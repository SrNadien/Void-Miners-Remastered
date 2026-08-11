package nadiendev.voidminersremastered.world.block.entity;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.config.MinerConfigLoader;
import nadiendev.voidminersremastered.init.ModDataComponents;
import nadiendev.voidminersremastered.init.ModRarities;
import nadiendev.voidminersremastered.world.block.ModifierBlock;
import nadiendev.voidminersremastered.common.energy.MinerEnergyStorage;
import nadiendev.voidminersremastered.init.ModBlockEntities;
import nadiendev.voidminersremastered.server.recipe.MinerRecipe;
import nadiendev.voidminersremastered.server.recipe.WeightedStack;
import nadiendev.voidminersremastered.util.ListUtil;
import nadiendev.voidminersremastered.util.MiscUtil;
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
import nadiendev.mangomultiblock.core.manager.MultiBlockManager;
import nadiendev.mangomultiblock.core.manager.RegisteredMultiBlockPattern;
import nadiendev.mangomultiblock.core.misc.MultiblockMatchResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public boolean enoughPower = false;

    public boolean enoughPowerForNextOperation = false;

    private HaltReason haltReason = HaltReason.NONE;

    private boolean dimensionOK = false;

    private int checkStructureTTL = 0;

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
                            "§f%d×", (int) getItemModifierMultiplier()))));
        }

        float progressPercent = getMaxProgress() == 0 ? 0F : (float) getProgress() / getMaxProgress();
        tooltip.add(Component.translatable("tooltip.voidminersremastered.controller.progress")
                .append(Component.literal(String.format(
                        "§r%s §f%.2f%%", getProgressBar(progressPercent), progressPercent * 100))));

        tooltip.add(getUpgradeInfoText());
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
        if(this.upgradeItem == Items.AIR) {
            return Component.literal("⚙ UPGRADE: ").withStyle(ChatFormatting.AQUA)
                    .append(Component.literal("No upgrades applied.").withStyle(ChatFormatting.WHITE));
        }

        return Component.literal("⚙ UPGRADE: ").withStyle(ChatFormatting.AQUA)
                .append(Component.translatable(this.upgradeItem.getDescriptionId()).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" (+" + this.upgradeItem.components().get(ModDataComponents.MAX_STORAGE_UPGRADE_SLOTS.get()) + " slots)").withStyle(ChatFormatting.GRAY));
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

        if (!MinerConfigLoader.getInstance().ALLOW_TICK_ACCELERATION) {
            if (this.lastProcessedGameTime == gameTime) return;
            this.lastProcessedGameTime = gameTime;
        }

        // only check dimension once
        if(!dimensionOK && haltReason != HaltReason.NO_RECIPES_IN_DIMENSION) {
            if (allRecipes().isEmpty()) {
                haltReason = HaltReason.NO_RECIPES_IN_DIMENSION;
                return;
            }
            dimensionOK = true;
        } else if (haltReason == HaltReason.NO_RECIPES_IN_DIMENSION) {
            return;
        }

        if(getStructure() == null) {
            setup(structure, name);
        }

        if(this.lastProcessedGameTime != gameTime) {
            if(checkStructureTTL == 0) {
                checkStructure(level, pPos);
                checkStructureTTL = 20; // only check structure every 20 ticks even if tick accelerated
                sync();
            }
            checkStructureTTL--;
        }

        if (!foundStructure) {
            haltReason = HaltReason.STRUCTURE_NOT_FOUND;
            return;
        }

        if (showStructure) {
            updateShowStructure();
        }

        canSeeBedrockOrVoid = hasViewOnBedrockOrVoid(pPos);
        sync();

        if(!canSeeBedrockOrVoid) {
            haltReason = HaltReason.NO_BEDROCK_OR_VOID_VIEW;
            return;
        }

        if(!MinerConfigLoader.getInstance().MINERS_AUTO_EXPORT_INSTEAD_OF_FILLING_THEIR_OWN_INVENTORY) {
            int itemModMultMultiplier = (int) getItemModifierMultiplier();

            if (!MinerConfigLoader.getInstance().MINERS_FILL_ALL_SLOTS) {
                if (itemModMultMultiplier > itemHandler.size() * 64) {
                    haltReason = HaltReason.TOO_MUCH_ITEM_MULTIPLIER;
                    return;
                }
                if(!hasEnoughEmptySlots(itemModMultMultiplier / 64)) {
                    haltReason = HaltReason.NOT_ENOUGH_EMPTY_SLOTS;
                    return;
                }
            }

            if(isItemHandlerFull()) {
                haltReason = HaltReason.NOT_ENOUGH_EMPTY_SLOTS;
                return;
            }
        }

        int rfPerTick = getRFPerTick();

        enoughPower = rfPerTick <= energyHandler.getEnergyStored();
        enoughPowerForNextOperation = rfPerTick * 2 <= energyHandler.getEnergyStored();
        sync();

        if (!enoughPower) {
            haltReason = HaltReason.NOT_ENOUGH_POWER;
            return;
        }

        haltReason = HaltReason.NONE;

        progress++;
        energyHandler.removeEnergy(rfPerTick);
        sync();

        if (progress < getMaxProgress()) {
            return;
        }

        List<WeightedStack> allOutputs = new ArrayList<>();

        for (MinerRecipe recipe : allRecipes()) {
            allOutputs.add(recipe.output().copy());
        }

        ItemStack output = getBoostedStack(getWeightedItem(allOutputs, level.getRandom()));

        if(!MinerConfigLoader.getInstance().MINERS_AUTO_EXPORT_INSTEAD_OF_FILLING_THEIR_OWN_INVENTORY) {
            for (int i = 0; i < itemHandler.size(); i++) {
                if (output.isEmpty()) break;
                if (!isItemValid(output, ItemUtil.getStack(itemHandler, i))) continue;
                // Opens (and commits) its own root transaction; returns what did not fit.
                output = ItemUtil.insertItemReturnRemaining(itemHandler, i, output, false, null);
            }
        } else {
            pushItemsToNeighbors(output);
        }

        progress = 0;
        sync();
    }

    private void pushItemsToNeighbors(ItemStack outputStack) {
        if (level == null || level.isClientSide()) return;
        if (outputStack.isEmpty()) return;

        int remainingCount = outputStack.getCount();
        ItemResource resource = ItemResource.of(outputStack);

        for (Direction dir : Direction.values()) {
            if (remainingCount <= 0) break;

            BlockPos neighborPos = worldPosition.relative(dir);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);
            if (neighbor == null) continue;

            ResourceHandler<ItemResource> receiver = level.getCapability(
                    Capabilities.Item.BLOCK,
                    neighborPos,
                    dir.getOpposite()
            );

            if (receiver == null) continue;

            // insertStacking with a null transaction opens a root transaction and commits it,
            // which is the 26.1.2 equivalent of ItemHandlerHelper.insertItemStacked(handler, stack, false).
            int inserted = ResourceHandlerUtil.insertStacking(receiver, resource, remainingCount, null);

            remainingCount -= inserted;
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
        float mod = 1;

        for (Map.Entry<BlockInWorld, MinerConfigLoader.ModifierConfig> entry : modifierMap.entrySet()) {
            mod *= entry.getValue().energy();
        }

        return (int) (MinerConfigLoader.getInstance().getConfig(name).energyConsumptionPerTick() * mod);
    }

    public int getMaxProgress() {
        float mod = 1;

        for (Map.Entry<BlockInWorld, MinerConfigLoader.ModifierConfig> entry : modifierMap.entrySet()) {
            mod *= entry.getValue().speed();
        }

        return (int) (MinerConfigLoader.getInstance().getConfig(name).duration() / mod);
    }

    public float getItemModifierMultiplier() {
        float mod = 1;
        for (Map.Entry<BlockInWorld, MinerConfigLoader.ModifierConfig> entry : modifierMap.entrySet()) {
            mod *= entry.getValue().item();
        }
        return mod;
    }

    public float getEnergyModifierMultiplier() {
        float mod = 1;
        for (Map.Entry<BlockInWorld, MinerConfigLoader.ModifierConfig> entry : modifierMap.entrySet()) {
            mod *= entry.getValue().energy();
        }
        return mod;
    }

    public float getSpeedModifierMultiplier() {
        float mod = 1;
        for (Map.Entry<BlockInWorld, MinerConfigLoader.ModifierConfig> entry : modifierMap.entrySet()) {
            mod *= entry.getValue().speed();
        }
        return mod;
    }

    public ItemStack getBoostedStack(ItemStack base) {
        int count = (base.getCount() * (int) getItemModifierMultiplier());
        return base.copyWithCount(count);
    }

    private boolean hasViewOnBedrockOrVoid(BlockPos pos) {
        for (int i = 0; i < 320; i++) {
            BlockPos check = pos.below(i + 1);

            assert level != null;
            if(level.getBlockState(check).is(Blocks.BEDROCK)) return true;

            // propagatesSkylightDown is now precomputed on the BlockState (BlockBehaviour.java:545) and takes no arguments.
            if (level.getBlockState(check).propagatesSkylightDown() || level.isFluidAtPosition(check, (fluidState -> !fluidState.isEmpty()))) continue;

            return false;
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
        // Level#getRecipeManager() no longer exists; the recipe map lives behind
        // ServerLevel#recipeAccess() (ServerLevel.java:1475) -> RecipeManager#recipeMap()
        // (RecipeManager.java:263) -> RecipeMap#byType (RecipeMap.java:55).
        // The client no longer receives the full recipe list, hence the server-only guard (unchanged behaviour).
        if (!(level instanceof ServerLevel serverLevel)) {
            return new ArrayList<>();
        }

        if (structure == null) {
            return new ArrayList<>();
        }

        return serverLevel.recipeAccess().recipeMap().byType(MinerRecipe.TYPE)
                .stream()
                .map(RecipeHolder::value)
                .filter(recipe -> {
                    if (recipe.allowHigherTiers()) {
                        return recipe.minTier() <= MiscUtil.tierMap.get(structure.getPath());
                    } else {
                        return recipe.minTier() == MiscUtil.tierMap.get(structure.getPath());
                    }
                })
                .filter(recipe -> recipe.dimension().equals(this.level.dimension()))
                .toList();
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
                    MinerConfigLoader.ModifierConfig modifier = MinerConfigLoader.getInstance().getModifierConfig(block.getState().getBlock());
                    if (!modifierMap.containsKey(block)) {
                        modifierMap.put(block, modifier);
                    }
                });
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
