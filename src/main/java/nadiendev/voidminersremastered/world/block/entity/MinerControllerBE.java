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
import org.mangorage.mangomultiblock.core.manager.MultiBlockManager;
import org.mangorage.mangomultiblock.core.manager.RegisteredMultiBlockPattern;
import org.mangorage.mangomultiblock.core.misc.MultiblockMatchResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinerControllerBE extends BlockEntity {

    public static final int ENERGY_CAPACITY = Integer.MAX_VALUE;
    private static final int BASE_OUTPUT_SLOTS = 9;
    private Item upgradeItem = Items.AIR;

    private MinerEnergyStorage energyHandler = new MinerEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, 0, 0);
    private ItemStackHandler itemHandler = createItemHandler();

    public boolean foundStructure = false;
    private int progress = 0;

    public boolean showStructure = false;

    private final Map<BlockInWorld, MinerConfigLoader.ModifierConfig> modifierMap = new HashMap<>();

    private ResourceLocation structure;
    private String name;

    public boolean canSeeBedrockOrVoid = false;
    public boolean enoughPower = false;

    public boolean enoughPowerForNextOperation = false;

    private HaltReason haltReason = HaltReason.NONE;

    private boolean dimensionOK = false;

    private int checkStructureTTL = 0;

    private static ItemStackHandler createItemHandler() {
        return new ItemStackHandler(BASE_OUTPUT_SLOTS) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
            }
        };
    }

    private void recalculateStorageFromUpgrades() {
        if(this.upgradeItem == Items.AIR) return;

        int extraSlots = this.upgradeItem.components().get(ModDataComponents.MAX_STORAGE_UPGRADE_SLOTS.get());

        int desiredSlots = BASE_OUTPUT_SLOTS + extraSlots;
        if (desiredSlots != itemHandler.getSlots()) {
            replaceItemHandler(desiredSlots);
        }
    }

    private void replaceItemHandler(int newSlots) {
        ItemStackHandler newHandler = new ItemStackHandler(newSlots) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
            }
        };

        // Copy existing stacks into new handler
        int copySlots = Math.min(itemHandler.getSlots(), newHandler.getSlots());
        for (int i = 0; i < copySlots; i++) {
            newHandler.setStackInSlot(i, itemHandler.getStackInSlot(i));
        }

        this.itemHandler = newHandler;
    }

    public MinerControllerBE(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.MINER_CONTROLLER_BASE_BE.get(), pPos, pBlockState);
    }

    public void setup(ResourceLocation structure, String name) {
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
            energyHandler = new MinerEnergyStorage(storage, Integer.MAX_VALUE, 0, currentEnergy);
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

        MutableComponent status = Component.translatable("tooltip.voidminers.controller.status.status").withStyle(ChatFormatting.GOLD);

        switch (haltReason) {
            case NONE:
                if(enoughPowerForNextOperation) {
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

                MiscUtil.getNeededBlocks(MiscUtil.structureMap.get(structure.toString())).forEach((string, integer) -> {
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
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);

        CompoundTag data = new CompoundTag();
        if (energyHandler != null) data.put("energy", energyHandler.serializeNBT(pRegistries));
        data.put("items", itemHandler.serializeNBT(pRegistries));
        data.putString("upgradeItem", this.upgradeItem.toString());
        data.putInt("progress", this.progress);
        if (name != null) data.putString("name", this.name);
        if (structure != null) data.putString("structure", structure.toString());
        data.putBoolean("showStructure", showStructure);
        data.putBoolean("canSeeBedrockOrVoid", canSeeBedrockOrVoid);
        data.putBoolean("foundStructure", foundStructure);
        data.putBoolean("enoughPower", enoughPower);

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
                this.upgradeItem = BuiltInRegistries.ITEM.get(itemId);
            } else {
                this.upgradeItem = Items.AIR;
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
        this.loadAdditional(tag, pRegistries);
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
                if (itemModMultMultiplier > itemHandler.getSlots() * 64) {
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

        ItemStack output = getBoostedStack(getWeightedItem(allOutputs, level.random));

        if(!MinerConfigLoader.getInstance().MINERS_AUTO_EXPORT_INSTEAD_OF_FILLING_THEIR_OWN_INVENTORY) {
            ItemStack remaining;

            for (int i = 0; i < itemHandler.getSlots(); i++) {
                if (!isItemValid(output, itemHandler.getStackInSlot(i))) continue;
                remaining = itemHandler.insertItem(i, output.copy(), false);
                if (remaining.isEmpty()) break;
                output = remaining;
            }
        } else {
            pushItemsToNeighbors(output);
        }

        progress = 0;
        sync();
    }

    private void pushItemsToNeighbors(ItemStack outputStack) {
        if (level == null || level.isClientSide) return;
        if (outputStack.isEmpty()) return;

        int remainingCount = outputStack.getCount();

        for (Direction dir : Direction.values()) {
            if (remainingCount <= 0) break;

            BlockPos neighborPos = worldPosition.relative(dir);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);
            if (neighbor == null) continue;

            IItemHandler receiver = level.getCapability(
                    Capabilities.ItemHandler.BLOCK,
                    neighborPos,
                    dir.getOpposite()
            );

            if (receiver == null) continue;

            ItemStack toPush = outputStack.copyWithCount(remainingCount);
            ItemStack remaining = ItemHandlerHelper.insertItemStacked(
                    receiver,
                    toPush,
                    false
            );

            remainingCount = remaining.getCount();
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

            if (level.getBlockState(check).propagatesSkylightDown(level, check) || level.isFluidAtPosition(check, (fluidState -> !fluidState.isEmpty()))) continue;

            return false;
        }

        return true;
    }

    private boolean isItemHandlerFull() {
        if(itemHandler.getStackInSlot(itemHandler.getSlots() - 1).getCount() == 0) return false;

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (itemHandler.getStackInSlot(i).getCount() < itemHandler.getStackInSlot(i).getMaxStackSize()) {
                return false;
            }
        }

        return true;
    }

    private List<MinerRecipe> allRecipes() {
        if (level == null || level.isClientSide) {
            return new ArrayList<>();
        }

        if (structure == null) {
            return new ArrayList<>();
        }

        return level.getRecipeManager().getAllRecipesFor(MinerRecipe.Type.INSTANCE)
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
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            container.addItem(itemHandler.getStackInSlot(i));
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
                return item.stack;
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

    public ResourceLocation getStructure() {
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