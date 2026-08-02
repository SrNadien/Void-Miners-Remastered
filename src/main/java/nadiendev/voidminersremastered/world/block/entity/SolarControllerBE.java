package nadiendev.voidminersremastered.world.block.entity;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.common.energy.SolarEnergyStorage;
import nadiendev.voidminersremastered.config.SolarConfigLoader;
import nadiendev.voidminersremastered.init.ModRarities;
import nadiendev.voidminersremastered.world.block.ModifierBlock;
import nadiendev.voidminersremastered.init.ModBlockEntities;
import nadiendev.voidminersremastered.util.MiscUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;
import org.mangorage.mangomultiblock.core.manager.MultiBlockManager;
import org.mangorage.mangomultiblock.core.manager.RegisteredMultiBlockPattern;
import org.mangorage.mangomultiblock.core.misc.MultiblockMatchResult;

import java.util.*;

public class SolarControllerBE extends BlockEntity {
    private SolarEnergyStorage energyHandler = new SolarEnergyStorage(Long.MAX_VALUE, 0, Long.MAX_VALUE, 0);

    public boolean foundStructure = false;

    public boolean showStructure = false;

    private final Map<BlockInWorld, SolarConfigLoader.ModifierConfig> modifierMap = new HashMap<>();

    private ResourceLocation structure;
    private String name;

    private long lastProcessedGameTime = Long.MIN_VALUE;

    public boolean canSeeSky = false;

    private HaltReason haltReason = HaltReason.NONE;

    private int checkStructureTTL = 0;

    public SolarControllerBE(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SOLAR_BASE_BE.get(), pPos, pBlockState);
    }

    public void setup(ResourceLocation structure, String name) {
        this.structure = structure;
        this.name = name;
        setupEnergyStorage();
    }

    public void setupEnergyStorage() {
        long storage = SolarConfigLoader.getInstance().getConfig(name).energyStorage();

        long currentEnergy = energyHandler != null ? energyHandler.getLongEnergyStored() : 0;

        if (energyHandler != null) {
            energyHandler.setLongCapacity(storage);
        } else {
            energyHandler = new SolarEnergyStorage(storage, 0, Long.MAX_VALUE, currentEnergy);
        }
    }

    public int getBeamColor() {
        return MiscUtil.colorMap.getOrDefault(name, 0xFFFFFFFF);
    }

    public List<Component> getInteractionTooltip() {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.literal("═══ ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(name.replace("solar_", "").toUpperCase() + " SOLAR PANEL").withColor(Integer.parseInt(ModRarities.getColorForCrystal(name.replace("solar_", "")).getHexColor().substring(1), 16))
                        .append(Component.literal(" ═══").withStyle(ChatFormatting.GRAY))));

        MutableComponent status = Component.translatable("tooltip.voidminers.controller.status.status").withStyle(ChatFormatting.GOLD);

        switch (haltReason) {
            case NONE:
                status.append(Component.translatable("tooltip.voidminers.controller.status.working").withStyle(ChatFormatting.GREEN));
                tooltip.add(status);

                addSolarInfo(tooltip);

                break;
            case STRUCTURE_NOT_FOUND: {
                status.append(Component.translatable("tooltip.voidminers.controller.status.structure_incomplete").withStyle(ChatFormatting.RED));
                tooltip.add(status);

                tooltip.add(Component.translatable("tooltip.voidminers.controller.halt_reason.structure_not_found").withStyle(ChatFormatting.YELLOW));

                MiscUtil.getNeededBlocks(MiscUtil.structureMap.get(structure.toString())).forEach((string, integer) -> {
                    tooltip.add(Component.literal("• ").withStyle(ChatFormatting.GRAY)
                            .append(Component.literal(string.contains("Null") ? "Modifier" : string).withStyle(ChatFormatting.WHITE))
                            .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                            .append(Component.literal(String.valueOf(integer)).withStyle(ChatFormatting.RED)));
                });
                break;
            }
            case NO_SKY_VIEW: {
                status.append(Component.translatable("tooltip.voidminers.controller.status.no_sky_view").withStyle(ChatFormatting.RED));
                tooltip.add(status);

                tooltip.add(Component.translatable("tooltip.voidminers.controller.halt_reason.no_sky_view").withStyle(ChatFormatting.YELLOW));
                break;
            }
            case POWER_FULL: {
                status.append(Component.translatable("tooltip.voidminers.controller.status.power_full").withStyle(ChatFormatting.YELLOW));
                tooltip.add(status);

                addSolarInfo(tooltip);
                break;
            }
        }

        return tooltip;
    }

    private void addSolarInfo(List<Component> tooltip) {
        String energyBar = getEnergyBar(energyHandler.getLongEnergyStored(), energyHandler.getLongMaxEnergyStored());
        tooltip.add(Component.translatable("tooltip.voidminers.controller.energy")
                .append(Component.literal(String.format(
                        "%s §f%,d §7/ §f%,d RF", energyBar, energyHandler.getLongEnergyStored(), energyHandler.getLongMaxEnergyStored()))));

        tooltip.add(Component.translatable("tooltip.voidminers.controller.generation").withStyle(net.minecraft.ChatFormatting.GREEN)
                .append(Component.literal(String.format("%,d RF/tick", getRFPerTick(getSolarEfficiency()))).withStyle(net.minecraft.ChatFormatting.WHITE)));

        tooltip.add(Component.translatable("tooltip.voidminers.controller.efficiency").withStyle(net.minecraft.ChatFormatting.YELLOW)
                .append(Component.literal(String.format("%.2f%%", getSolarEfficiency() * 100)).withStyle(net.minecraft.ChatFormatting.WHITE)));

        if(getSolarEfficiency() != 1.0f) {
            assert level != null;
            if(level.isRaining() && level.isThundering()) {
                tooltip.add(Component.translatable("tooltip.voidminers.controller.efficiency_limited_by_thunder").withStyle(net.minecraft.ChatFormatting.YELLOW));
            } else if (level.isRaining()) {
                tooltip.add(Component.translatable("tooltip.voidminers.controller.efficiency_limited_by_rain").withStyle(net.minecraft.ChatFormatting.YELLOW));
            } else {
                tooltip.add(Component.translatable("tooltip.voidminers.controller.efficiency_limited_by_time_of_day").withStyle(net.minecraft.ChatFormatting.YELLOW));
            }
        }
    }

    private String getEnergyBar(long current, long max) {
        float percent = (float) current / max;
        int bars = (int) (percent * 16);
        StringBuilder bar = new StringBuilder("§a");

        for (int i = 0; i < 16; i++) {
            if (i < bars) {
                bar.append("▌");
            } else if (i == bars && current + getRFPerTick(getSolarEfficiency()) + 1 > max) {
                bar.append("▌");
            } else {
                bar.append("§8▌");
            }
        }
        return bar + "§r";
    }

    public void updateShowStructure() {
        showStructure = !showStructure;
        sync();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);

        CompoundTag data = new CompoundTag();
        if (energyHandler != null) data.put("energy", energyHandler.serializeNBT(pRegistries));
        if (name != null) data.putString("name", this.name);
        if (structure != null) data.putString("structure", structure.toString());
        data.putBoolean("showStructure", showStructure);
        data.putBoolean("foundStructure", foundStructure);
        data.putBoolean("canSeeSky", canSeeSky);

        pTag.put(VoidMinersRemastered.MODID, data);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        CompoundTag data = pTag.getCompound(VoidMinersRemastered.MODID);
        if (data.isEmpty())
            return;

        if (data.contains("energy")) {
            energyHandler.deserializeNBT(pRegistries, data.get("energy"));
        }

        if (data.contains("name")) {
            name = data.getString("name");
        }

        if (data.contains("structure")) {
            structure = ResourceLocation.parse(data.getString("structure"));
        }

        if (data.contains("showStructure")) {
            showStructure = data.getBoolean("showStructure");
        }

        if (data.contains("foundStructure")) {
            foundStructure = data.getBoolean("foundStructure");
        }

        if (data.contains("canSeeSky")) {
            canSeeSky = data.getBoolean("canSeeSky");
        }

        sync();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        setupEnergyStorage();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = super.getUpdateTag(pRegistries);
        saveAdditional(tag, pRegistries);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState, ResourceLocation structure, String name) {
        if (level == null || level.isClientSide) return;

        long gameTime = level.getGameTime();

        if (!SolarConfigLoader.getInstance().ALLOW_TICK_ACCELERATION) {
            if (this.lastProcessedGameTime == gameTime) return;
            this.lastProcessedGameTime = gameTime;
        }

        if(getStructure() == null) {
            setup(structure, name);
        }

        if(this.lastProcessedGameTime != gameTime) {
            if(checkStructureTTL == 0) {
                checkStructure(level, pPos);
                checkStructureTTL = 20; // only check structure every 20 ticks even if tick accelerated
            }
            checkStructureTTL--;
        }
        sync();

        if (!foundStructure) {
            haltReason = HaltReason.STRUCTURE_NOT_FOUND;
            return;
        }

        if (showStructure) {
            updateShowStructure();
        }

        canSeeSky = level.canSeeSky(pPos.above());

        if(!canSeeSky) {
            haltReason = HaltReason.NO_SKY_VIEW;
            return;
        }

        pushEnergyToNeighbors();

        boolean energyFull = isEnergyHandlerFull();

        if (energyFull) {
            haltReason = HaltReason.POWER_FULL;
            return;
        }

        energyHandler.addLongEnergy(getRFPerTick(getSolarEfficiency()));

        haltReason = HaltReason.NONE;

        sync();
    }

    private void pushEnergyToNeighbors() {
        if (level == null || level.isClientSide) return;
        if (energyHandler == null) return;

        long available = energyHandler.getLongEnergyStored();
        if (available <= 0) return;

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(dir);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);
            if (neighbor == null) continue;

            IEnergyStorage receiver = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, dir.getOpposite());

            if (receiver == null) continue;

            long availableNow = energyHandler.getLongEnergyStored();
            if (availableNow <= 0) break;

            long remaining = energyHandler.getLongEnergyStored();
            while (remaining > 0) {
                int toSend = (int) Math.min(remaining, Integer.MAX_VALUE);
                int accepted = receiver.receiveEnergy(toSend, false);
                if (accepted <= 0) break;
                energyHandler.removeLongEnergy(accepted);
                remaining -= accepted;
                if (accepted < toSend) break;
            }
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.handleUpdateTag(tag, pRegistries);
        this.loadAdditional(tag, pRegistries);
    }

    public SolarEnergyStorage getEnergyStorage() {
        return energyHandler;
    }

    private void sync() {
        if(level != null) {
            setChanged(level, getBlockPos(), getBlockState());
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public long getRFPerTick(float efficiency) {
        SolarConfigLoader cfg = SolarConfigLoader.getInstance();

        long rfPerTick = cfg.SOLAR_CONFIGS.get(name).energyGenerationPerTick();

        float efficiencyModifier = 1.0f;

        for (Map.Entry<BlockInWorld, SolarConfigLoader.ModifierConfig> entry : modifierMap.entrySet()) {
            efficiencyModifier *= entry.getValue().efficiency();
        }

        rfPerTick *= efficiencyModifier;

        rfPerTick *= efficiency;

        return rfPerTick;
    }

    public float getSolarEfficiency() {
        assert level != null;

        if (SolarConfigLoader.getInstance().isAlwaysDayDimension(level.dimension().location().toString())) {
            return 1.0f;
        }

        long timeOfDay = level.getDayTime() % 24000L;
        float efficiency;

        if (timeOfDay < 5000) {
            // Linear from 0 to 5000: 0 -> 1.0
            efficiency = (float) timeOfDay / 5000f;
        } else if (timeOfDay <= 7000) {
            // Flat at 1.0 from 5000 to 7000
            efficiency = 1.0f;
        } else if (timeOfDay <= 12000) {
            // Linear from 7000 to 12000: 1.0 -> 0
            efficiency = 1.0f - ((float) (timeOfDay - 7000) / 5000f);
        } else {
            // After 12000, it's night
            return 0.0f;
        }

        // Apply weather penalties and modifier protections
        float weatherPenalty = 1.0f;

        if (level.isRaining()) {
            weatherPenalty = 0.3f;

            if (level.isThundering()) weatherPenalty = 0.15f;

            for (Map.Entry<BlockInWorld, SolarConfigLoader.ModifierConfig> entry : modifierMap.entrySet()) {
                weatherPenalty += entry.getValue().weatherResistance() - 1;
            }

            if(weatherPenalty > 1.0f) weatherPenalty = 1.0f;
        }

        efficiency *= weatherPenalty;

        return efficiency;
    }

    private boolean isEnergyHandlerFull() {
        if (energyHandler.getLongEnergyStored() == Long.MAX_VALUE) return true;

        return energyHandler.getLongEnergyStored() >= energyHandler.getLongMaxEnergyStored();
    }

    public void checkStructure(Level pLevel, BlockPos pPos) {
        RegisteredMultiBlockPattern pattern = MultiBlockManager.findAnyStructure(pLevel, pPos, Rotation.NONE);

        if (pattern == null) {
            foundStructure = false;
            return;
        }

        MultiblockMatchResult result = pattern.pattern().matchesWithResult(pLevel, pPos, Rotation.NONE);

        if (result == null) {
            foundStructure = false;
            return;
        }

        if (!pattern.ID().equals(structure)) {
            foundStructure = false;
            return;
        }

        modifierMap.clear();
        foundStructure = true;
        result.blocks().stream()
                .filter(block -> block.getState().getBlock() instanceof ModifierBlock)
                .forEach(block -> {
                    SolarConfigLoader.ModifierConfig modifier = SolarConfigLoader.getInstance().getModifierConfig(block.getState().getBlock());
                    if (!modifierMap.containsKey(block)) {
                        modifierMap.put(block, modifier);
                    }
                });
    }

    public ResourceLocation getStructure() {
        return structure;
    }

    public HaltReason getHaltReason() {
        return haltReason;
    }
}
