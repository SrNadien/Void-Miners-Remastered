package nadiendev.voidminersremastered.world.block.entity;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.config.MinerConfigLoader;
import nadiendev.voidminersremastered.init.ModDataComponents;
import nadiendev.voidminersremastered.init.ModRarities;
import nadiendev.voidminersremastered.world.block.ModifierBlock;
import nadiendev.voidminersremastered.common.energy.MinerEnergyStorage;
import nadiendev.voidminersremastered.init.ModBlockEntities;
import nadiendev.voidminersremastered.server.recipe.BlockRequirement;
import nadiendev.voidminersremastered.server.recipe.MinerRecipe;
import nadiendev.voidminersremastered.server.recipe.WeightedStack;
import nadiendev.voidminersremastered.util.ListUtil;
import nadiendev.voidminersremastered.util.MiscUtil;
import nadiendev.voidminersremastered.world.multiblock.MinerMultiblocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import org.jetbrains.annotations.Nullable;
import nadiendev.mangomultiblock.core.manager.RegisteredMultiBlockPattern;
import nadiendev.mangomultiblock.core.misc.MultiblockMatchResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class MinerControllerBE extends BlockEntity {

    public static final int ENERGY_CAPACITY = Integer.MAX_VALUE;
    private static final int BASE_OUTPUT_SLOTS = 9;
    private Item upgradeItem = Items.AIR;

    private MinerEnergyStorage energyHandler = createEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, 0, 0);
    private ItemStacksResourceHandler itemHandler = createItemHandler(BASE_OUTPUT_SLOTS);

    public boolean foundStructure = false;
    private int progress = 0;

    public boolean showStructure = false;

    private final Map<BlockInWorld, MinerConfigLoader.ModifierConfig> modifierMap = new HashMap<>();

    private Identifier structure;
    private String name;

    public boolean canSeeBedrockOrVoid = false;
    public boolean blockBeforeGlass = false;
    public boolean enoughPower = false;

    public boolean enoughPowerForNextOperation = false;

    private HaltReason haltReason = HaltReason.NONE;

    private boolean dimensionOK = false;

    private int checkStructureTTL = 0;
    private int bedrockCheckTTL = 0;
    private int blockBeforeGlassTTL = 0;
    private int recipeCheckTTL = 0;

    private int tickAcceleratedTicks = 1;

    private float cachedEnergyMod = 1f;
    private float cachedSpeedMod = 1f;
    private float cachedItemMod = 1f;

    @Nullable
    private BlockState blockUnderneathState = null;
    public int beamLength = 0;

    @Nullable
    private Direction exportSide = null;
    private int exportCooldown = 0;

    /**
     * Output inventory of the miner.
     *
     * <p>{@code ItemStackHandler} is deprecated in 26.1.2 and {@code Capabilities.Item.BLOCK} resolves to
     * {@code ResourceHandler<ItemResource>}, so the handler is now an {@link ItemStacksResourceHandler}.
     * {@link #serialize}/{@link #deserialize} are overridden to keep writing the exact NBT layout the
     * 1.21.1 {@code ItemStackHandler} produced ({@code {Items:[{Slot:i, id:.., count:.., components:{}}], Size:n}}),
     * so already-built miners keep their inventory when the world is upgraded.
     */
    public static class MinerItemHandler extends ItemStacksResourceHandler {
        public MinerItemHandler(int size) {
            super(size);
        }

        @Override
        @SuppressWarnings("deprecation") // ValueOutput#store(MapCodec, T) is the only way to merge a stack into a compound
        public void serialize(ValueOutput output) {
            ValueOutput.ValueOutputList items = output.childrenList("Items");
            for (int i = 0; i < size(); i++) {
                ItemStack stack = ItemUtil.getStack(this, i);
                if (!stack.isEmpty()) {
                    ValueOutput entry = items.addChild();
                    entry.putInt("Slot", i);
                    entry.store(ItemStack.MAP_CODEC, stack);
                }
            }
            output.putInt("Size", size());
        }

        @Override
        @SuppressWarnings("deprecation") // ValueInput#read(MapCodec) mirrors the write above
        public void deserialize(ValueInput input) {
            int storedSize = input.getIntOr("Size", size());
            if (storedSize <= 0) storedSize = size();

            NonNullList<ItemStack> loaded = NonNullList.withSize(storedSize, ItemStack.EMPTY);
            input.childrenList("Items").ifPresent(list -> {
                for (ValueInput entry : list) {
                    int slot = entry.getIntOr("Slot", -1);
                    if (slot >= 0 && slot < loaded.size()) {
                        entry.read(ItemStack.MAP_CODEC).ifPresent(stack -> loaded.set(slot, stack));
                    }
                }
            });
            setStacks(loaded);
        }
    }

    private MinerEnergyStorage createEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        return new MinerEnergyStorage(capacity, maxReceive, maxExtract, energy) {
            @Override
            protected void onEnergyChanged(int previousAmount) {
                MinerControllerBE.this.setChanged();
            }
        };
    }

    private ItemStacksResourceHandler createItemHandler(int slots) {
        return new MinerItemHandler(slots) {
            @Override
            protected void onContentsChanged(int index, ItemStack previousContents) {
                MinerControllerBE.this.setChanged();
            }
        };
    }

    private void recalculateStorageFromUpgrades() {
        if(this.upgradeItem == Items.AIR) return;

        Integer extraSlots = this.upgradeItem.components().get(ModDataComponents.MAX_STORAGE_UPGRADE_SLOTS.get());
        if (extraSlots == null) return;

        int desiredSlots = BASE_OUTPUT_SLOTS + extraSlots;
        if (desiredSlots != itemHandler.size()) {
            replaceItemHandler(desiredSlots);
        }
    }

    private void replaceItemHandler(int newSlots) {
        ItemStacksResourceHandler newHandler = createItemHandler(newSlots);

        // Copy existing stacks into new handler
        NonNullList<ItemStack> previous = itemHandler.copyToList();
        int copySlots = Math.min(previous.size(), newSlots);
        for (int i = 0; i < copySlots; i++) {
            ItemStack stack = previous.get(i);
            if (!stack.isEmpty()) {
                newHandler.set(i, ItemResource.of(stack), stack.getCount());
            }
        }

        this.itemHandler = newHandler;

        // The capability instance changed: tell the level so neighbours re-resolve it.
        invalidateCapabilities();
    }

    public MinerControllerBE(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.MINER_CONTROLLER_BASE_BE.get(), pPos, pBlockState);
    }

    public void setup(Identifier structure, String name) {
        this.structure = structure;
        this.name = name;
        setupEnergyStorage();
    }

    public void setupEnergyStorage() {
        int storage = MinerConfigLoader.getInstance().getConfig(name).energyStorage();

        if (!MinerConfigLoader.getInstance().ALLOW_NO_ENERGY_MINERS && storage <= 0) storage = ENERGY_CAPACITY;

        int currentEnergy = energyHandler != null ? energyHandler.getEnergyStored() : 0;

        if (energyHandler != null) {
            energyHandler.setCapacity(storage);
            if (currentEnergy > storage) {
                energyHandler.setEnergy(storage);
            }
        } else {
            energyHandler = createEnergyStorage(storage, Integer.MAX_VALUE, 0, currentEnergy);
        }
    }

    public int getBeamColor() {
        return MiscUtil.colorMap.getOrDefault(name, 0xFFFFFFFF);
    }

    public List<Component> getInteractionTooltip() {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.literal("═══ ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(name.toUpperCase() + " MINER").withColor(Integer.parseInt(ModRarities.getColorForCrystal(name).getHexColor().substring(1), 16))
                        .append(Component.literal(" ═══").withStyle(ChatFormatting.GRAY))));

        MutableComponent status = Component.translatable("tooltip.voidminersremastered.controller.status.status").withStyle(ChatFormatting.GOLD);

        switch (haltReason) {
            case NONE:
                if(enoughPowerForNextOperation) {
                    status.append(Component.translatable("tooltip.voidminersremastered.controller.status.working").withStyle(ChatFormatting.GREEN));
                    tooltip.add(status);
                } else {
                    status.append(Component.translatable("tooltip.voidminersremastered.controller.status.mining_slow").withStyle(ChatFormatting.RED));
                    tooltip.add(status);
                    tooltip.add(Component.translatable("tooltip.voidminersremastered.controller.status.not_enough_power_for_next_operation").withStyle(ChatFormatting.YELLOW));
                }

                addMinerInfo(tooltip);
                break;
            case NO_RECIPES_IN_DIMENSION:
                status.append(Component.translatable("tooltip.voidminersremastered.controller.status.mining_impossible").withStyle(ChatFormatting.RED));
                tooltip.add(status);

                assert level != null;
                String dimName = Component.translatable("dimension." +  level.dimension().identifier().toLanguageKey()).getString();

                tooltip.add(Component.translatable("tooltip.voidminersremastered.controller.halt_reason.dimension_not_ok", dimName));
                break;
            case STRUCTURE_NOT_FOUND: {
                status.append(Component.translatable("tooltip.voidminersremastered.controller.status.structure_incomplete").withStyle(ChatFormatting.RED));
                tooltip.add(status);

                tooltip.add(Component.translatable("tooltip.voidminersremastered.controller.halt_reason.structure_not_found").withStyle(ChatFormatting.YELLOW));

                MiscUtil.getNeededBlocks(MiscUtil.structureMap.get(structure.toString())).forEach((string, integer) -> {
                    tooltip.add(Component.literal("• ").withStyle(ChatFormatting.GRAY)
                            .append(Component.literal(string.contains("Null") ? "Modifier" : string).withStyle(ChatFormatting.WHITE))
                            .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                            .append(Component.literal(String.valueOf(integer)).withStyle(ChatFormatting.RED)));
                });
                break;
            }
            case TOO_MUCH_ITEM_MULTIPLIER:
                status.append(Component.translatable("tooltip.voidminersremastered.controller.status.mining_stopped").withStyle(ChatFormatting.RED));
                tooltip.add(status);

                tooltip.add(Component.translatable("tooltip.voidminersremastered.controller.halt_reason.too_much_item_multiplier", itemHandler.size() * 64).withStyle(ChatFormatting.YELLOW));

                tooltip.add(Component.translatable("tooltip.voidminersremastered.controller.max_storage_upgrade_tip").withStyle(ChatFormatting.YELLOW));

                addMinerInfo(tooltip);
                break;
            case NOT_ENOUGH_EMPTY_SLOTS:
                status.append(Component.translatable("tooltip.voidminersremastered.controller.status.mining_stopped").withStyle(ChatFormatting.RED));
                tooltip.add(status);
                tooltip.add(Component.translatable("tooltip.voidminersremastered.controller.halt_reason.not_enough_empty_slots").withStyle(ChatFormatting.YELLOW));

                addMinerInfo(tooltip);
                break;
            case BLOCK_BEFORE_GLASS:
                status.append(Component.translatable("tooltip.voidminersremastered.controller.status.mining_stopped").withStyle(ChatFormatting.RED));
                tooltip.add(status);
                tooltip.add(Component.translatable("tooltip.voidminersremastered.controller.halt_reason.block_before_glass").withStyle(ChatFormatting.YELLOW));

                addMinerInfo(tooltip);
                break;
            case NO_BEDROCK_OR_VOID_VIEW:
                status.append(Component.translatable("tooltip.voidminersremastered.controller.status.mining_stopped").withStyle(ChatFormatting.RED));
                tooltip.add(status);
                tooltip.add(Component.translatable("tooltip.voidminersremastered.controller.halt_reason.no_bedrock_or_void_view").withStyle(ChatFormatting.YELLOW));

                addMinerInfo(tooltip);
                break;
            case NOT_ENOUGH_POWER:
                status.append(Component.translatable("tooltip.voidminersremastered.controller.status.mining_stopped").withStyle(ChatFormatting.RED));
                tooltip.add(status);
                tooltip.add(Component.translatable("tooltip.voidminersremastered.controller.status.not_enough_power").withStyle(ChatFormatting.YELLOW));

                addMinerInfo(tooltip);
                break;
        }

        return tooltip;
    }

    private void addMinerInfo(List<Component> tooltip) {
        String energyBar = getEnergyBar(energyHandler.getEnergyStored(), energyHandler.getMaxEnergyStored());
        tooltip.add(Component.translatable("tooltip.voidminersremastered.controller.energy")
                .append(Component.literal(String.format(
                        "%s §f%,d §7/ §f%,d RF", energyBar, energyHandler.getEnergyStored(), energyHandler.getMaxEnergyStored()))));

        tooltip.add(Component.translatable("tooltip.voidminersremastered.controller.consumption")
                .append(Component.literal(String.format(
                        "§f%,d RF/tick §b(%.2f×)", getRFPerTick(), getEnergyModifierMultiplier()))));

        tooltip.add(Component.translatable("tooltip.voidminersremastered.controller.duration")
                .append(Component.literal(String.format(
                        "§f%d ticks §b(%.2f×)", getMaxProgress(), getSpeedModifierMultiplier()))));

        if (getItemModifierMultiplier() != 1.0f) {
            tooltip.add(Component.translatable("tooltip.voidminersremastered.controller.item_boost")
                    .append(Component.literal(String.format(
                            "§f%.2f×", getItemModifierMultiplier()))));
        }

        float progressPercent = getMaxProgress() == 0 ? 0F : (float) getProgress() / getMaxProgress();
        tooltip.add(Component.translatable("tooltip.voidminersremastered.controller.progress")
                .append(Component.literal(String.format(
                        "§r%s §f%.2f%%", getProgressBar(progressPercent), progressPercent * 100))));

        tooltip.add(getUpgradeInfoText());

        tooltip.add(Component.translatable("tooltip.voidminersremastered.controller.export").withStyle(ChatFormatting.AQUA)
                .append(exportSide == null
                        ? Component.translatable("tooltip.voidminersremastered.controller.export.none").withStyle(ChatFormatting.WHITE)
                        : Component.translatable("tooltip.voidminersremastered.controller.export.side." + exportSide.getName()).withStyle(ChatFormatting.WHITE)));

        if (MinerConfigLoader.getInstance().MINERS_AUTO_EXPORT_INSTEAD_OF_FILLING_THEIR_OWN_INVENTORY) {
            tooltip.add(Component.translatable("tooltip.voidminersremastered.controller.information").withStyle(ChatFormatting.GOLD)
                    .append(Component.translatable("tooltip.voidminersremastered.controller.information.auto_export")));
        }
    }

    private String getEnergyBar(int current, int max) {
        float percent = (float) current / max;
        int bars = (int) (percent * 16);
        StringBuilder bar = new StringBuilder("§a");

        for (int i = 0; i < 16; i++) {
            if (i < bars) {
                bar.append("▌");
            } else if (i == bars && (long) current + getRFPerTick() + 1 > max) {
                bar.append("▌");
            } else {
                bar.append("§8▌");
            }
        }
        return bar + "§r";
    }

    private String getProgressBar(float percent) {
        int bars = (int) (percent * 16);
        StringBuilder bar = new StringBuilder("§e");

        for (int i = 0; i < 16; i++) {
            if (i < bars) {
                bar.append("▌");
            } else if (i == bars && percent * 16 - bars > 0.5) {
                bar.append("▌");
            } else {
                bar.append("§8▌");
            }
        }
        return bar + "§r";
    }

    private Component getUpgradeInfoText() {
        MutableComponent base = Component.translatable("tooltip.voidminersremastered.controller.upgrade").withStyle(ChatFormatting.AQUA);

        if (this.upgradeItem == Items.AIR) {
            return base.append(Component.translatable("tooltip.voidminersremastered.controller.upgrade.no_upgrade").withStyle(ChatFormatting.WHITE));
        }

        return base.append(Component.translatable(this.upgradeItem.getDescriptionId()).withStyle(ChatFormatting.WHITE))
                .append(Component.translatable("tooltip.voidminersremastered.controller.upgrade.slots",
                        this.upgradeItem.components().get(ModDataComponents.MAX_STORAGE_UPGRADE_SLOTS.get())).withStyle(ChatFormatting.WHITE));
    }

    public void updateShowStructure() {
        showStructure = !showStructure;
        sync();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        ValueOutput data = output.child(VoidMinersRemastered.MODID);
        // 1.21.1 wrote EnergyStorage#serializeNBT, which was a bare IntTag under "energy" -> same NBT.
        if (energyHandler != null) data.putInt("energy", energyHandler.getEnergyStored());
        itemHandler.serialize(data.child("items"));
        data.putString("upgradeItem", this.upgradeItem.toString());
        data.putInt("progress", this.progress);
        if (name != null) data.putString("name", this.name);
        if (structure != null) data.putString("structure", structure.toString());
        data.putBoolean("showStructure", showStructure);
        data.putBoolean("canSeeBedrockOrVoid", canSeeBedrockOrVoid);
        data.putBoolean("foundStructure", foundStructure);
        data.putBoolean("enoughPower", enoughPower);
        data.putInt("beamLength", beamLength);
        if (exportSide != null) data.putString("exportSide", exportSide.getName());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        Optional<ValueInput> maybeData = input.child(VoidMinersRemastered.MODID);
        if (maybeData.isEmpty())
            return;

        ValueInput data = maybeData.get();

        data.getInt("energy").ifPresent(stored -> energyHandler.setEnergy(stored));

        data.child("items").ifPresent(itemHandler::deserialize);

        data.getString("upgradeItem").ifPresent(raw -> {
            Identifier itemId = Identifier.tryParse(raw);
            Item item = itemId != null ? BuiltInRegistries.ITEM.getValue(itemId) : null;
            this.upgradeItem = item != null ? item : Items.AIR;
        });

        this.progress = data.getIntOr("progress", this.progress);

        data.getString("name").ifPresent(loaded -> this.name = loaded);

        data.getString("structure").ifPresent(loaded -> this.structure = Identifier.parse(loaded));

        this.showStructure = data.getBooleanOr("showStructure", this.showStructure);
        this.canSeeBedrockOrVoid = data.getBooleanOr("canSeeBedrockOrVoid", this.canSeeBedrockOrVoid);
        this.foundStructure = data.getBooleanOr("foundStructure", this.foundStructure);
        // NOTE: key mismatch preserved from 1.21.1 -- saved as "enoughPower", read back as "enoughRF".
        this.enoughPower = data.getBooleanOr("enoughRF", this.enoughPower);
        this.beamLength = data.getIntOr("beamLength", this.beamLength);
        this.exportSide = data.getString("exportSide").map(Direction::byName).orElse(null);

        recalculateStorageFromUpgrades();

        sync();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        setupEnergyStorage();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        // getUpdateTag still returns a CompoundTag, while saveAdditional now takes a ValueOutput.
        // saveCustomOnly(Provider) is the vanilla bridge (BlockEntity.java:160-169): it builds a
        // TagValueOutput, runs saveAdditional on it and returns the resulting tag.
        return saveCustomOnly(pRegistries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // handleUpdateTag(ValueInput) is no longer overridden: the NeoForge default
    // (IBlockEntityExtension.java:47) already routes to loadWithComponents -> loadAdditional.

    public MinerEnergyStorage getEnergyStorage() {
        return energyHandler;
    }

    public ItemStacksResourceHandler getItemHandler() {
        return itemHandler;
    }

    public int getProgress() {
        return progress;
    }

    private long lastProcessedGameTime = Long.MIN_VALUE;

    public void tick(Level pLevel, BlockPos pPos, BlockState pState, Identifier structure, String name) {
        if (level == null || level.isClientSide()) return;

        long gameTime = level.getGameTime();

        if (!MinerConfigLoader.getInstance().ALLOW_TICK_ACCELERATION && this.lastProcessedGameTime == gameTime) {
            return;
        }

        if (getStructure() == null) {
            setup(structure, name);
        }

        if (haltReason == HaltReason.NO_RECIPES_IN_DIMENSION) {
            if (recipeCheckTTL > 0) {
                recipeCheckTTL--;
                return;
            }
            if (allRecipes().isEmpty()) {
                recipeCheckTTL = 100;
                return;
            }
            dimensionOK = true;
            haltReason = HaltReason.NONE;
        } else if (!dimensionOK) {
            if (allRecipes().isEmpty()) {
                haltReason = HaltReason.NO_RECIPES_IN_DIMENSION;
                recipeCheckTTL = 100;
                return;
            }
            dimensionOK = true;
        }

        if (this.lastProcessedGameTime == gameTime) {
            tickAcceleratedTicks++;
            return;
        }

        if (exportSide != null) {
            if (exportCooldown <= 0) {
                exportToSide();
                exportCooldown = Math.max(1, MinerConfigLoader.getInstance().EXPORT_INTERVAL_TICKS);
            } else {
                exportCooldown--;
            }
        }

        if (checkStructureTTL <= 0) {
            checkStructure(level, pPos);
            sync();
            checkStructureTTL = 20;
        } else {
            checkStructureTTL--;
        }

        if (!foundStructure) {
            haltReason = HaltReason.STRUCTURE_NOT_FOUND;
            beforeReturn();
            return;
        }

        if (showStructure) {
            updateShowStructure();
        }

        if (blockBeforeGlassTTL <= 0) {
            blockBeforeGlass = hasBlockBeforeGlass(pPos);
            blockBeforeGlassTTL = 20;
        } else {
            blockBeforeGlassTTL--;
        }

        if (blockBeforeGlass) {
            haltReason = HaltReason.BLOCK_BEFORE_GLASS;
            canSeeBedrockOrVoid = false;
            beforeReturn();
            return;
        }

        if (bedrockCheckTTL <= 0) {
            canSeeBedrockOrVoid = hasViewOnBedrockOrVoid(pPos);
            sync();
            bedrockCheckTTL = 20;
        } else {
            bedrockCheckTTL--;
        }

        if (!canSeeBedrockOrVoid) {
            haltReason = HaltReason.NO_BEDROCK_OR_VOID_VIEW;
            beforeReturn();
            return;
        }

        if (!MinerConfigLoader.getInstance().MINERS_AUTO_EXPORT_INSTEAD_OF_FILLING_THEIR_OWN_INVENTORY) {
            int itemModMultMultiplier = (int) Math.ceil(getItemModifierMultiplier() * getMaxOutputCount());

            if (!MinerConfigLoader.getInstance().MINERS_FILL_ALL_SLOTS) {
                if (itemModMultMultiplier > itemHandler.size() * 64) {
                    haltReason = HaltReason.TOO_MUCH_ITEM_MULTIPLIER;
                    beforeReturn();
                    return;
                }
                if (!hasEnoughEmptySlots(itemModMultMultiplier / 64)) {
                    haltReason = HaltReason.NOT_ENOUGH_EMPTY_SLOTS;
                    beforeReturn();
                    return;
                }
            }

            if (isItemHandlerFull()) {
                haltReason = HaltReason.NOT_ENOUGH_EMPTY_SLOTS;
                beforeReturn();
                return;
            }
        }

        long rfPerTick = (long) getRFPerTick() * tickAcceleratedTicks;

        enoughPower = rfPerTick <= energyHandler.getEnergyStored();
        enoughPowerForNextOperation = rfPerTick * 2 <= energyHandler.getEnergyStored();

        if (!enoughPower) {
            haltReason = HaltReason.NOT_ENOUGH_POWER;
            beforeReturn();
            return;
        }

        haltReason = HaltReason.NONE;
        int tickMultiplier = tickAcceleratedTicks <= 2 ? tickAcceleratedTicks : (tickAcceleratedTicks - 1) * 2;
        progress += tickMultiplier;
        energyHandler.removeEnergy((int) rfPerTick);
        sync();

        int maxProgress = getMaxProgress();

        if (maxProgress <= 0 || progress < maxProgress) {
            beforeReturn();
            return;
        }

        List<WeightedStack> allOutputs = new ArrayList<>();
        for (MinerRecipe recipe : allRecipes()) {
            allOutputs.add(recipe.output().copy());
        }

        List<ItemStack> outputs = new ArrayList<>();
        while (progress >= maxProgress) {
            ItemStack output = getBoostedStack(getWeightedItem(allOutputs, level.getRandom()), level.getRandom());
            if (!output.isEmpty()) {
                outputs.add(output);
            }
            progress -= maxProgress;
        }

        if (!MinerConfigLoader.getInstance().MINERS_AUTO_EXPORT_INSTEAD_OF_FILLING_THEIR_OWN_INVENTORY) {
            for (ItemStack output : outputs) {
                ItemStack remaining = output;
                for (int i = 0; i < itemHandler.size(); i++) {
                    if (remaining.isEmpty()) break;
                    if (!isItemValid(remaining, ItemUtil.getStack(itemHandler, i))) continue;
                    remaining = ItemUtil.insertItemReturnRemaining(itemHandler, i, remaining, false, null);
                }
            }
            if (exportSide != null) {
                exportToSide();
            }
        } else {
            pushItemsToNeighbors(outputs);
        }

        sync();
        beforeReturn();
    }

    public int getMinerTier() {
        if (structure == null) return 0;
        return MiscUtil.tierMap.getOrDefault(structure.getPath(), 0);
    }

    private int getMaxOutputCount() {
        int max = 1;
        for (MinerRecipe recipe : allRecipes()) {
            max = Math.max(max, recipe.output().stack().getCount());
        }
        return max;
    }

    @Nullable
    public Direction getExportSide() {
        return exportSide;
    }

    public Direction toggleExportSide(Direction side) {
        exportSide = exportSide == side ? null : side;
        exportCooldown = 0;
        sync();
        return exportSide;
    }

    private void exportToSide() {
        if (level == null || level.isClientSide() || exportSide == null) return;

        ResourceHandler<ItemResource> target = level.getCapability(
                Capabilities.Item.BLOCK,
                worldPosition.relative(exportSide),
                exportSide.getOpposite()
        );
        if (target == null) return;

        int moved = ResourceHandlerUtil.moveStacking(itemHandler, target, resource -> true, Integer.MAX_VALUE, null);
        if (moved > 0) {
            setChanged();
        }
    }

    private void beforeReturn() {
        if (level != null) {
            lastProcessedGameTime = level.getGameTime();
        }
        tickAcceleratedTicks = 1;
    }

    private void pushItemsToNeighbors(List<ItemStack> outputStacks) {
        if (level == null || level.isClientSide()) return;
        if (outputStacks.isEmpty()) return;

        List<ResourceHandler<ItemResource>> receivers = new ArrayList<>();

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(dir);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);
            if (neighbor == null) continue;

            ResourceHandler<ItemResource> receiver = level.getCapability(
                    Capabilities.Item.BLOCK,
                    neighborPos,
                    dir.getOpposite()
            );

            if (receiver != null) {
                receivers.add(receiver);
            }
        }

        for (ItemStack output : outputStacks) {
            if (receivers.isEmpty()) break;

            int remainingCount = output.getCount();
            ItemResource resource = ItemResource.of(output);

            Iterator<ResourceHandler<ItemResource>> iterator = receivers.iterator();
            while (iterator.hasNext() && remainingCount > 0) {
                ResourceHandler<ItemResource> receiver = iterator.next();
                int inserted = ResourceHandlerUtil.insertStacking(receiver, resource, remainingCount, null);
                if (inserted <= 0) {
                    iterator.remove();
                    continue;
                }
                remainingCount -= inserted;
            }
        }
    }

    private boolean hasEnoughEmptySlots(int neededSlots) {
        int emptyCount = 0;
        for (int i = 0; i < itemHandler.size(); i++) {
            if (itemHandler.getResource(i).isEmpty()) {
                emptyCount++;
                if (emptyCount >= neededSlots) {
                    return true;
                }
            }
        }
        return false;
    }

    private void sync() {
        if(level != null) {
            setChanged(level, getBlockPos(), getBlockState());
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public int getRFPerTick() {
        return (int) (MinerConfigLoader.getInstance().getConfig(name).energyConsumptionPerTick() * cachedEnergyMod);
    }

    public int getMaxProgress() {
        return (int) (MinerConfigLoader.getInstance().getConfig(name).duration() / cachedSpeedMod);
    }

    public float getItemModifierMultiplier() {
        return cachedItemMod;
    }

    public float getEnergyModifierMultiplier() {
        return cachedEnergyMod;
    }

    public float getSpeedModifierMultiplier() {
        return cachedSpeedMod;
    }

    public ItemStack getBoostedStack(ItemStack base, RandomSource random) {
        if (base.isEmpty()) return ItemStack.EMPTY;

        float multiplier = getItemModifierMultiplier();
        int wholeMultiplier = (int) multiplier;
        float fractionalMultiplier = multiplier - wholeMultiplier;

        int count = base.getCount() * wholeMultiplier;

        if (fractionalMultiplier > 0f && random.nextFloat() < fractionalMultiplier) {
            count += base.getCount();
        }

        return base.copyWithCount(count);
    }

    private boolean hasBlockBeforeGlass(BlockPos pos) {
        if (level == null || structure == null) return false;

        int glassHeight = MiscUtil.GLASS_HEIGHT.getOrDefault(structure.getPath(), 0);

        for (int i = 0; i < glassHeight; i++) {
            BlockPos check = pos.below(i + 1);
            BlockState state = level.getBlockState(check);

            if (state.propagatesSkylightDown() || level.isFluidAtPosition(check, fluidState -> !fluidState.isEmpty())) continue;

            return true;
        }

        return false;
    }

    private boolean hasViewOnBedrockOrVoid(BlockPos pos) {
        if (level == null) return false;
        blockUnderneathState = null;

        for (int i = 0; i < level.getHeight(); i++) {
            BlockPos check = pos.below(i + 1);

            if (check.getY() < level.getMinY()) {
                beamLength = pos.below().getY() - check.getY();
                return true;
            }

            BlockState state = level.getBlockState(check);

            if (state.is(Blocks.BEDROCK)) {
                beamLength = pos.below().getY() - check.getY();
                return true;
            }

            if (state.propagatesSkylightDown() || level.isFluidAtPosition(check, fluidState -> !fluidState.isEmpty())) continue;

            blockUnderneathState = state;
            beamLength = pos.below().getY() - check.getY();

            return hasRecipeRequiring(state);
        }

        return true;
    }

    private boolean isItemHandlerFull() {
        int size = itemHandler.size();
        if (size == 0) return true;

        if (itemHandler.getAmountAsLong(size - 1) == 0) return false;

        for (int i = 0; i < size; i++) {
            if (itemHandler.getAmountAsLong(i) < itemHandler.getCapacityAsLong(i, itemHandler.getResource(i))) {
                return false;
            }
        }

        return true;
    }

    private List<MinerRecipe> allRecipes() {
        List<MinerRecipe> candidates = recipesForTierAndDimension().toList();

        List<MinerRecipe> specific = candidates.stream()
                .filter(recipe -> recipe.blockUnderneath() != null)
                .filter(recipe -> blockUnderneathState != null && recipe.blockUnderneath().matches(blockUnderneathState))
                .toList();

        if (!specific.isEmpty()) {
            return specific;
        }

        return candidates.stream()
                .filter(recipe -> recipe.blockUnderneath() == null)
                .toList();
    }

    private Stream<MinerRecipe> recipesForTierAndDimension() {
        if (!(level instanceof ServerLevel serverLevel) || structure == null) {
            return Stream.empty();
        }

        Integer tier = MiscUtil.tierMap.get(structure.getPath());
        if (tier == null) {
            return Stream.empty();
        }

        return serverLevel.recipeAccess().recipeMap().byType(MinerRecipe.TYPE)
                .stream()
                .map(RecipeHolder::value)
                .filter(recipe -> recipe.allowHigherTiers() ? recipe.minTier() <= tier : recipe.minTier() == tier)
                .filter(recipe -> recipe.dimension().equals(serverLevel.dimension()));
    }

    private boolean hasRecipeRequiring(BlockState state) {
        return recipesForTierAndDimension()
                .anyMatch(recipe -> {
                    BlockRequirement requirement = recipe.blockUnderneath();
                    return requirement != null && requirement.matches(state);
                });
    }

    private boolean isItemValid(ItemStack stack, ItemStack handler) {
        return handler.isEmpty() || handler.is(stack.getItem());
    }

    public void drops() {
        SimpleContainer container = new SimpleContainer(itemHandler.size());

        for (int i = 0; i < itemHandler.size(); i++) {
            container.addItem(ItemUtil.getStack(itemHandler, i));
        }

        container.addItem(new ItemStack(this.upgradeItem));

        assert level != null;
        Containers.dropContents(level, worldPosition, container);
    }

    public ItemStack getWeightedItem(List<WeightedStack> items, RandomSource random) {
        float totalWeight = ListUtil.getTotalWeight(items);

        float randomValue = random.nextFloat() * totalWeight;

        for (WeightedStack item : items) {
            randomValue -= item.weight;
            if (randomValue <= 0) {
                return item.stack();
            }
        }

        return ItemStack.EMPTY;
    }

    public void checkStructure(Level pLevel, BlockPos pPos) {
        foundStructure = false;

        if (structure == null) {
            return;
        }

        RegisteredMultiBlockPattern pattern = MinerMultiblocks.MANAGER.getStructure(structure);
        if (pattern == null) {
            return;
        }

        MultiblockMatchResult result = pattern.pattern().matchesWithResult(pLevel, pPos, Rotation.NONE);
        if (result == null) {
            result = pattern.pattern().matchesWithResult(pLevel, pPos, Rotation.CLOCKWISE_90);
        }

        if (result == null) {
            return;
        }

        modifierMap.clear();
        foundStructure = true;
        result.blocks().stream()
                .filter(block -> block.getState().getBlock() instanceof ModifierBlock)
                .forEach(block -> {
                    MinerConfigLoader.ModifierConfig modifier = MinerConfigLoader.getInstance().getModifierConfig(block.getState().getBlock());
                    modifierMap.putIfAbsent(block, modifier);
                });

        calculateCachedModifiers();
    }

    private void calculateCachedModifiers() {
        float energyMod = 1f, speedMod = 1f, itemMod = 1f;
        for (MinerConfigLoader.ModifierConfig cfg : modifierMap.values()) {
            energyMod *= cfg.energy();
            speedMod *= cfg.speed();
            itemMod *= cfg.item();
        }
        cachedEnergyMod = energyMod;
        cachedSpeedMod = speedMod;
        cachedItemMod = itemMod;
    }

    public Identifier getStructure() {
        return structure;
    }

    public void setAppliedUpgradeItem(Item item) {
        this.upgradeItem = item;
        recalculateStorageFromUpgrades();
        sync();
    }

    public Item getUpgradeItem() {
        return upgradeItem;
    }

    public HaltReason getHaltReason() {
        return haltReason;
    }
}
