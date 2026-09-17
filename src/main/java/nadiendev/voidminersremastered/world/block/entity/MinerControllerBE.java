package nadiendev.voidminersremastered.world.block.entity;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.config.MinerConfigLoader;
import nadiendev.voidminersremastered.init.ModDataComponents;
import nadiendev.voidminersremastered.util.ColorUtil;
import nadiendev.voidminersremastered.world.block.ModifierBlock;
import nadiendev.voidminersremastered.common.energy.MinerEnergyStorage;
import nadiendev.voidminersremastered.init.ModBlockEntities;
import nadiendev.voidminersremastered.server.recipe.MinerRecipe;
import nadiendev.voidminersremastered.server.recipe.WeightedStack;
import nadiendev.voidminersremastered.util.ListUtil;
import nadiendev.voidminersremastered.util.MiscUtil;
import nadiendev.voidminersremastered.world.multiblock.MinerMultiblocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import org.mangorage.mangomultiblock.core.manager.RegisteredMultiBlockPattern;
import org.mangorage.mangomultiblock.core.misc.MultiblockMatchResult;

import java.util.*;
import java.util.stream.Stream;

public class MinerControllerBE extends BlockEntity {

    public static final int ENERGY_CAPACITY = Integer.MAX_VALUE;
    private static final int BASE_OUTPUT_SLOTS = 9;
    private Item upgradeItem = Items.AIR;

    private MinerEnergyStorage energyHandler = new MinerEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, 0, 0);
    private ItemStackHandler itemHandler = new ItemStackHandler(BASE_OUTPUT_SLOTS);

    public boolean foundStructure = false;
    private int progress = 0;

    public boolean showStructure = false;

    private final Map<BlockInWorld, MinerConfigLoader.ModifierConfig> modifierMap = new HashMap<>();

    private ResourceLocation structure;
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

    private BlockState blockUnderneathState = null;
    public int beamLength = 0;

    @Nullable
    private Direction exportSide = null;
    private int exportCooldown = 0;

    private void recalculateStorageFromUpgrades() {
        if (upgradeItem == Items.AIR) return;

        int extraSlots = upgradeItem.components().get(ModDataComponents.MAX_STORAGE_UPGRADE_SLOTS.get());

        int desiredSlots = BASE_OUTPUT_SLOTS + extraSlots;
        if (desiredSlots != itemHandler.getSlots()) {
            replaceItemHandler(desiredSlots);
        }
    }

    private void replaceItemHandler(int newSlots) {
        ItemStackHandler newHandler = new ItemStackHandler(newSlots);

        // Copy existing stacks into new handler
        int copySlots = Math.min(itemHandler.getSlots(), newHandler.getSlots());
        for (int i = 0; i < copySlots; i++) {
            newHandler.setStackInSlot(i, itemHandler.getStackInSlot(i));
        }

        itemHandler = newHandler;
    }

    public MinerControllerBE(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.MINER_CONTROLLER_BE.get(), pPos, pBlockState);
    }

    public void setup(ResourceLocation structure, String name) {
        this.structure = structure;
        this.name = name;
        setupEnergyStorage();
    }

    public void setupEnergyStorage() {
        int storage = MinerConfigLoader.getInstance().getControllerConfig(name).energyStorage();

        if (!MinerConfigLoader.getInstance().ALLOW_NO_ENERGY_MINERS && storage <= 0) storage = ENERGY_CAPACITY;

        int currentEnergy = energyHandler != null ? energyHandler.getEnergyStored() : 0;

        if (energyHandler != null) {
            energyHandler.setCapacity(storage);
            if (currentEnergy > storage) {
                energyHandler.setEnergy(storage);
            }
        } else {
            energyHandler = new MinerEnergyStorage(storage, Integer.MAX_VALUE, 0, currentEnergy);
        }
    }

    public int getBeamColor() {
        return ColorUtil.getARGBForCrystal(name);
    }

    public List<Component> getInteractionTooltip() {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.literal("═══ ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(name.toUpperCase() + " MINER").withColor(Integer.parseInt(ColorUtil.getColorForCrystal(name).getHexColor().substring(1), 16))
                        .append(Component.literal(" ═══").withStyle(ChatFormatting.GRAY))));

        MutableComponent status = Component.translatable("tooltip.voidminers.controller.status.status").withStyle(ChatFormatting.GOLD);

        switch (haltReason) {
            case NONE:
                if (enoughPowerForNextOperation) {
                    status.append(Component.translatable("tooltip.voidminers.controller.status.working").withStyle(ChatFormatting.GREEN));
                    tooltip.add(status);
                } else {
                    status.append(Component.translatable("tooltip.voidminers.controller.status.mining_slow").withStyle(ChatFormatting.RED));
                    tooltip.add(status);
                    tooltip.add(Component.translatable("tooltip.voidminers.controller.status.not_enough_power_for_next_operation").withStyle(ChatFormatting.YELLOW));
                }

                addMinerInfo(tooltip);
                break;
            case NO_RECIPES_IN_DIMENSION:
                status.append(Component.translatable("tooltip.voidminers.controller.status.mining_impossible").withStyle(ChatFormatting.RED));
                tooltip.add(status);

                assert level != null;
                String dimName = Component.translatable("dimension." +  level.dimension().location().toLanguageKey()).getString();

                tooltip.add(Component.translatable("tooltip.voidminers.controller.halt_reason.dimension_not_ok", dimName));
                break;
            case STRUCTURE_NOT_FOUND: {
                status.append(Component.translatable("tooltip.voidminers.controller.status.structure_incomplete").withStyle(ChatFormatting.RED));
                tooltip.add(status);

                tooltip.add(Component.translatable("tooltip.voidminers.controller.halt_reason.structure_not_found").withStyle(ChatFormatting.YELLOW));

                MiscUtil.getNeededBlocks(MiscUtil.structureMap.get(structure.toString()))
                        .forEach((string, integer) -> {
                    tooltip.add(Component.literal("• ").withStyle(ChatFormatting.GRAY)
                            .append(Component.literal(string.contains("Null") ? "Modifier" : string).withStyle(ChatFormatting.WHITE))
                            .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                            .append(Component.literal(String.valueOf(integer)).withStyle(ChatFormatting.RED)));
                });
                break;
            }
            case TOO_MUCH_ITEM_MULTIPLIER:
                status.append(Component.translatable("tooltip.voidminers.controller.status.mining_stopped").withStyle(ChatFormatting.RED));
                tooltip.add(status);

                tooltip.add(Component.translatable("tooltip.voidminers.controller.halt_reason.too_much_item_multiplier", itemHandler.getSlots() * 64).withStyle(ChatFormatting.YELLOW));

                tooltip.add(Component.translatable("tooltip.voidminers.controller.max_storage_upgrade_tip").withStyle(ChatFormatting.YELLOW));

                addMinerInfo(tooltip);
                break;
            case NOT_ENOUGH_EMPTY_SLOTS:
                status.append(Component.translatable("tooltip.voidminers.controller.status.mining_stopped").withStyle(ChatFormatting.RED));
                tooltip.add(status);
                tooltip.add(Component.translatable("tooltip.voidminers.controller.halt_reason.not_enough_empty_slots").withStyle(ChatFormatting.YELLOW));

                addMinerInfo(tooltip);
                break;
            case BLOCK_BEFORE_GLASS:
                status.append(Component.translatable("tooltip.voidminers.controller.status.mining_stopped").withStyle(ChatFormatting.RED));
                tooltip.add(status);
                tooltip.add(Component.translatable("tooltip.voidminers.controller.halt_reason.block_before_glass").withStyle(ChatFormatting.YELLOW));

                addMinerInfo(tooltip);
                break;
            case NO_BEDROCK_OR_VOID_VIEW:
                status.append(Component.translatable("tooltip.voidminers.controller.status.mining_stopped").withStyle(ChatFormatting.RED));
                tooltip.add(status);
                tooltip.add(Component.translatable("tooltip.voidminers.controller.halt_reason.no_bedrock_or_void_view").withStyle(ChatFormatting.YELLOW));

                addMinerInfo(tooltip);
                break;
            case NOT_ENOUGH_POWER:
                status.append(Component.translatable("tooltip.voidminers.controller.status.mining_stopped").withStyle(ChatFormatting.RED));
                tooltip.add(status);
                tooltip.add(Component.translatable("tooltip.voidminers.controller.status.not_enough_power").withStyle(ChatFormatting.YELLOW));

                addMinerInfo(tooltip);
                break;
        }

        return tooltip;
    }

    private void addMinerInfo(List<Component> tooltip) {
        String energyBar = getEnergyBar(energyHandler.getEnergyStored(), energyHandler.getMaxEnergyStored());
        tooltip.add(Component.translatable("tooltip.voidminers.controller.energy")
                .append(Component.literal(String.format(
                        "%s §f%,d §7/ §f%,d RF", energyBar, energyHandler.getEnergyStored(), energyHandler.getMaxEnergyStored()))));

        tooltip.add(Component.translatable("tooltip.voidminers.controller.consumption")
                .append(Component.literal(String.format(
                        "§f%,d RF/tick §b(%.2f×)", getRFPerTick(), getEnergyModifierMultiplier()))));

        tooltip.add(Component.translatable("tooltip.voidminers.controller.duration")
                .append(Component.literal(String.format(
                        "§f%d ticks §b(%.2f×)", getMaxProgress(), getSpeedModifierMultiplier()))));

        if (getItemModifierMultiplier() != 1.0f) {
            tooltip.add(Component.translatable("tooltip.voidminers.controller.item_boost")
                    .append(Component.literal(String.format(
                            "§f%d×", (int) getItemModifierMultiplier()))));
        }

        float progressPercent = getMaxProgress() == 0 ? 0F : (float) getProgress() / getMaxProgress();
        tooltip.add(Component.translatable("tooltip.voidminers.controller.progress")
                .append(Component.literal(String.format(
                        "§r%s §f%.2f%%", getProgressBar(progressPercent), progressPercent * 100))));

        tooltip.add(getUpgradeInfoText());

        tooltip.add(Component.translatable("tooltip.voidminers.controller.export").withStyle(ChatFormatting.AQUA)
                .append(exportSide == null
                        ? Component.translatable("tooltip.voidminers.controller.export.none").withStyle(ChatFormatting.WHITE)
                        : Component.translatable("tooltip.voidminers.controller.export.side." + exportSide.getName()).withStyle(ChatFormatting.WHITE)));

        if (MinerConfigLoader.getInstance().MINERS_AUTO_EXPORT_INSTEAD_OF_FILLING_THEIR_OWN_INVENTORY) {
            tooltip.add(Component.translatable("tooltip.voidminers.controller.information").withStyle(ChatFormatting.GOLD)
                    .append(Component.translatable("tooltip.voidminers.controller.information.auto_export")));
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
        MutableComponent base = Component.translatable("tooltip.voidminers.controller.upgrade").withStyle(ChatFormatting.AQUA);

        if (upgradeItem == Items.AIR) {
            return base.append(Component.translatable("tooltip.voidminers.controller.upgrade.no_upgrade").withStyle(ChatFormatting.WHITE));
        }

        return base.append(Component.translatable(upgradeItem.getDescriptionId()).withStyle(ChatFormatting.WHITE))
                .append(Component.translatable("tooltip.voidminers.controller.upgrade.slots",
                        upgradeItem.components().get(ModDataComponents.MAX_STORAGE_UPGRADE_SLOTS.get())).withStyle(ChatFormatting.WHITE));
    }

    public void updateShowStructure() {
        showStructure = !showStructure;
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

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.handleUpdateTag(tag, pRegistries);
        loadAdditional(tag, pRegistries);
    }

    public MinerEnergyStorage getEnergyStorage() {
        return energyHandler;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public int getProgress() {
        return progress;
    }

    private long lastProcessedGameTime = Long.MIN_VALUE;

    public void tick(Level pLevel, BlockPos pPos, BlockState pState, ResourceLocation structure, String name) {
        if (level == null || level.isClientSide) return;

        long gameTime = level.getGameTime();

        if (!MinerConfigLoader.getInstance().ALLOW_TICK_ACCELERATION && lastProcessedGameTime == gameTime) {
            return;
        }

        if (getStructure() == null) {
            setup(structure, name);
        }

        // check that the current dimension has a recipe
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

        // this will make it so when the block is tick accelerated, count how much it's tick accelerated then
        // on the real tick apply a multiplier based on the tick acceleration
        if (lastProcessedGameTime == gameTime) {
            tickAcceleratedTicks++;
        } else {
            if (exportSide != null) {
                if (exportCooldown <= 0) {
                    exportToSide();
                    exportCooldown = Math.max(1, MinerConfigLoader.getInstance().EXPORT_INTERVAL_TICKS);
                } else {
                    exportCooldown--;
                }
            }

            // only check the structure every 20 real ticks
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

            // only check void / bedrock view every 20 ticks
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
                    if (itemModMultMultiplier > itemHandler.getSlots() * 64) {
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

            long rfPerTick = getRFPerTick() * tickAcceleratedTicks;

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

            if (progress < maxProgress) {
                beforeReturn();
                return;
            }

            List<WeightedStack> allOutputs = new ArrayList<>();
            for (MinerRecipe recipe : allRecipes()) {
                allOutputs.add(recipe.output().copy());
            }

            List<ItemStack> outputs = new ArrayList<>();

            while (progress >= maxProgress) {
                outputs.add(getBoostedStack(getWeightedItem(allOutputs, level.random), level.random));
                progress -= maxProgress;
            }

            if (!MinerConfigLoader.getInstance().MINERS_AUTO_EXPORT_INSTEAD_OF_FILLING_THEIR_OWN_INVENTORY) {
                Iterator<ItemStack> iterator = outputs.iterator();
                while (iterator.hasNext()) {
                    ItemStack output = iterator.next();
                    ItemStack copy = output.copy();
                    ItemStack remaining = copy;

                    for (int i = 0; i < itemHandler.getSlots(); i++) {
                        if (!isItemValid(copy, itemHandler.getStackInSlot(i))) continue;
                        remaining = itemHandler.insertItem(i, remaining.copy(), false);
                        if (remaining.isEmpty()) break;
                    }

                    if (remaining.isEmpty()) {
                        iterator.remove();
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
    }

    public int getMinerTier() {
        if (structure == null) return 0;
        return MiscUtil.TIER_MAP.getOrDefault(structure.getPath(), 0);
    }

    private int getMaxOutputCount() {
        int max = 1;
        for (MinerRecipe recipe : allRecipes()) {
            max = Math.max(max, recipe.output().stack.getCount());
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
        if (level == null || level.isClientSide || exportSide == null) return;

        BlockPos targetPos = worldPosition.relative(exportSide);
        IItemHandler target = level.getCapability(Capabilities.ItemHandler.BLOCK, targetPos, exportSide.getOpposite());
        if (target == null) return;

        boolean moved = false;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            ItemStack remaining = ItemHandlerHelper.insertItemStacked(target, stack.copy(), false);
            if (remaining.getCount() != stack.getCount()) {
                itemHandler.setStackInSlot(i, remaining);
                moved = true;
            }
        }

        if (moved) {
            setChanged();
        }
    }

    private void beforeReturn() {
        assert level != null;
        lastProcessedGameTime = level.getGameTime();
        tickAcceleratedTicks = 1;
    }

    private void pushItemsToNeighbors(List<ItemStack> outputStacks) {
        if (level == null || level.isClientSide) return;

        List<IItemHandler> receivers = new ArrayList<>();

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(dir);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);
            if (neighbor == null) continue;
            IItemHandler receiver = level.getCapability(
                    Capabilities.ItemHandler.BLOCK,
                    neighborPos,
                    dir.getOpposite()
            );
            if (receiver != null) {
                receivers.add(receiver);
            }
        }

        if (receivers.isEmpty()) return;

        for (ItemStack output : outputStacks) {
            int remainingCount = output.getCount();
            Iterator<IItemHandler> receiverIterator = receivers.iterator();
            while (receiverIterator.hasNext()) {
                if (remainingCount <= 0) break;
                IItemHandler receiver = receiverIterator.next();
                ItemStack toPush = output.copyWithCount(remainingCount);
                ItemStack remaining = ItemHandlerHelper.insertItemStacked(
                        receiver,
                        toPush,
                        false
                );
                if (remaining.getCount() == remainingCount) {
                    receiverIterator.remove();
                    continue;
                }
                remainingCount = remaining.getCount();
            }
            if (receivers.isEmpty()) break;
        }
    }

    private boolean hasEnoughEmptySlots(int neededSlots) {
        int emptyCount = 0;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (itemHandler.getStackInSlot(i).isEmpty()) {
                emptyCount++;
                if (emptyCount >= neededSlots) {
                    return true;
                }
            }
        }
        return false;
    }

    private void sync() {
        if (level != null) {
            setChanged(level, getBlockPos(), getBlockState());
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    private boolean hasBlockBeforeGlass(BlockPos pos) {
        assert level != null;

        int glassHeight = MiscUtil.GLASS_HEIGHT.getOrDefault(structure.getPath(), 0);
        boolean blockFound = false;

        for (int i = 0; i < glassHeight; i++) {
            BlockPos check = pos.below(i + 1);
            BlockState state = level.getBlockState(check);

            if (state.propagatesSkylightDown(level, check) || level.isFluidAtPosition(check, (fluidState -> !fluidState.isEmpty()))) continue;

            blockFound = true;
        }

        return blockFound;
    }

    private boolean hasViewOnBedrockOrVoid(BlockPos pos) {
        assert level != null;
        blockUnderneathState = null;

        for (int i = 0; i < level.getMaxBuildHeight(); i++) {
            BlockPos check = pos.below(i + 1);
            BlockState state = level.getBlockState(check);

            if (state.is(Blocks.BEDROCK)) {
                beamLength = pos.below().getY() - check.getY();
                return true;
            }

            if (check.getY() < level.getMinBuildHeight()) {
                beamLength = pos.below().getY() - check.getY();
                return true;
            }

            if (state.propagatesSkylightDown(level, check) || level.isFluidAtPosition(check, (fluidState -> !fluidState.isEmpty()))) continue;

            blockUnderneathState = state;
            beamLength = pos.below().getY() - check.getY();

            return hasRecipeRequiring(state);
        }

        return true;
    }

    private boolean isItemHandlerFull() {
        if (itemHandler.getStackInSlot(itemHandler.getSlots() - 1).getCount() == 0) return false;

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (itemHandler.getStackInSlot(i).getCount() < itemHandler.getStackInSlot(i).getMaxStackSize()) {
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
        if (level == null || level.isClientSide || structure == null) {
            return Stream.empty();
        }

        return level.getRecipeManager().getAllRecipesFor(MinerRecipe.Type.INSTANCE)
                .stream()
                .map(RecipeHolder::value)
                .filter(recipe -> {
                    if (recipe.allowHigherTiers()) {
                        return recipe.minTier() <= MiscUtil.TIER_MAP.get(structure.getPath());
                    } else {
                        return recipe.minTier() == MiscUtil.TIER_MAP.get(structure.getPath());
                    }
                })
                .filter(recipe -> recipe.dimension().equals(level.dimension()));
    }

    private boolean hasRecipeRequiring(BlockState state) {
        return recipesForTierAndDimension()
                .anyMatch(recipe -> recipe.blockUnderneath() != null && recipe.blockUnderneath().matches(state));
    }

    private boolean isItemValid(ItemStack stack, ItemStack handler) {
        return handler.isEmpty() || handler.is(stack.getItem());
    }

    public void drops() {
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            container.addItem(itemHandler.getStackInSlot(i));
        }

        container.addItem(new ItemStack(upgradeItem));

        assert level != null;
        Containers.dropContents(level, worldPosition, container);
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
                    if (!modifierMap.containsKey(block)) {
                        modifierMap.put(block, modifier);
                    }
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

    public void setUpgradeItem(Item item) {
        upgradeItem = item;
        recalculateStorageFromUpgrades();
        sync();
    }

    public Item getUpgradeItem() {
        return upgradeItem;
    }

    public long getRFPerTick() {
        return (long) (MinerConfigLoader.getInstance().getControllerConfig(name).energyConsumptionPerTick() * cachedEnergyMod);
    }

    public int getMaxProgress() {
        return (int) (MinerConfigLoader.getInstance().getControllerConfig(name).duration() / cachedSpeedMod);
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

    public ItemStack getWeightedItem(List<WeightedStack> items, RandomSource random) {
        float totalWeight = ListUtil.getTotalWeight(items);

        float randomValue = random.nextFloat() * totalWeight;

        for (WeightedStack item : items) {
            randomValue -= item.weight;
            if (randomValue <= 0) {
                return item.stack;
            }
        }

        return ItemStack.EMPTY;
    }

    public ItemStack getBoostedStack(ItemStack base, RandomSource random) {
        float multiplier = getItemModifierMultiplier();
        int wholeMultiplier = (int) multiplier;
        float fractionalMultiplier = multiplier - wholeMultiplier;

        int count = base.getCount() * wholeMultiplier;

        if (fractionalMultiplier > 0f && random.nextFloat() < fractionalMultiplier) {
            count += base.getCount();
        }

        base.setCount(count);
        return base;
    }

    public ResourceLocation getStructure() {
        return structure;
    }

    public HaltReason getHaltReason() {
        return haltReason;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);

        CompoundTag data = new CompoundTag();
        if (energyHandler != null) data.put("energy", energyHandler.serializeNBT(pRegistries));
        data.put("items", itemHandler.serializeNBT(pRegistries));
        data.putString("upgradeItem", upgradeItem.toString());
        data.putInt("progress", progress);
        if (name != null) data.putString("name", name);
        if (structure != null) data.putString("structure", structure.toString());
        data.putBoolean("showStructure", showStructure);
        data.putBoolean("canSeeBedrockOrVoid", canSeeBedrockOrVoid);
        data.putBoolean("foundStructure", foundStructure);
        data.putBoolean("enoughPower", enoughPower);
        data.putInt("beamLength", beamLength);
        if (exportSide != null) data.putString("exportSide", exportSide.getName());

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

        if (data.contains("items")) {
            itemHandler.deserializeNBT(pRegistries, data.getCompound("items"));
        }

        if (data.contains("upgradeItem")) {
            ResourceLocation itemId = ResourceLocation.tryParse(data.getString("upgradeItem"));
            if (itemId != null) {
                upgradeItem = BuiltInRegistries.ITEM.get(itemId);
            } else {
                upgradeItem = Items.AIR;
            }
        }

        if (data.contains("progress")) {
            progress = data.getInt("progress");
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

        if (data.contains("canSeeBedrockOrVoid")) {
            canSeeBedrockOrVoid = data.getBoolean("canSeeBedrockOrVoid");
        }

        if (data.contains("foundStructure")) {
            foundStructure = data.getBoolean("foundStructure");
        }

        if (data.contains("enoughRF")) {
            enoughPower = data.getBoolean("enoughRF");
        }


        if (data.contains("beamLength")) {
            beamLength = data.getInt("beamLength");
        }

        exportSide = data.contains("exportSide") ? Direction.byName(data.getString("exportSide")) : null;

        recalculateStorageFromUpgrades();

        sync();
    }
}