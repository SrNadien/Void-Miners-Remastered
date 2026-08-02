package nadiendev.voidminersremastered.world.block;

import nadiendev.voidminersremastered.init.ModDataComponents;
import nadiendev.voidminersremastered.util.CustomColorUtil;
import nadiendev.voidminersremastered.world.block.entity.MinerControllerBE;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class MinerControllerBlock extends ColoredBlock implements EntityBlock {
    final ResourceLocation structure;
    final String name;

    public MinerControllerBlock(Properties pProperties, ResourceLocation structure, String name, CustomColorUtil color) {
        super(pProperties, color);
        this.structure = structure;
        this.name = name;
    }

    @Override
    protected void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof MinerControllerBE controllerBE) {
                controllerBE.drops();
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pMoving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new MinerControllerBE(blockPos, blockState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        MinerControllerBE blockEntity = (MinerControllerBE) pLevel.getBlockEntity(pPos);

        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (pPlayer.isCrouching()) {
            if (blockEntity != null && !blockEntity.foundStructure) {
                blockEntity.updateShowStructure();
            }
            return InteractionResult.CONSUME;
        }

        if (blockEntity != null) {
            for (Component component : blockEntity.getInteractionTooltip()) {
                pPlayer.displayClientMessage(component, false);
            }
        }

        return InteractionResult.CONSUME;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        MinerControllerBE blockEntity = (MinerControllerBE) pLevel.getBlockEntity(pPos);

        if (pLevel.isClientSide) {
            return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
        }

        if(pStack.getItem().components().get(ModDataComponents.MAX_STORAGE_UPGRADE_SLOTS.get()) != null) {
            handleUpgrade(blockEntity, pPlayer, pStack, pHand, pLevel, pState, pPos);
            return ItemInteractionResult.CONSUME;
        }

        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }

    private void handleUpgrade(MinerControllerBE blockEntity, Player pPlayer, ItemStack pStack, InteractionHand pHand, Level pLevel, BlockState pState, BlockPos pPos) {
        Item currentUpgradeItem = blockEntity.getUpgradeItem();
        Item newUpgradeItem = pStack.getItem();

        if (currentUpgradeItem == newUpgradeItem) {
            pPlayer.displayClientMessage(Component.translatable("client_message.voidminers.max_storage_upgrades.upgrade_already_applied"), true);
            return;
        }

        int newAddedSlots = 0;

        if(newUpgradeItem.components().get(ModDataComponents.MAX_STORAGE_UPGRADE_SLOTS.get()) != null) {
            newAddedSlots = newUpgradeItem.components().get(ModDataComponents.MAX_STORAGE_UPGRADE_SLOTS.get());
        } else {
            return;
        }

        if(currentUpgradeItem != Items.AIR) {
            int currentAddedSlots = currentUpgradeItem.components().get(ModDataComponents.MAX_STORAGE_UPGRADE_SLOTS.get());

            if (currentAddedSlots > newAddedSlots) {
                pPlayer.displayClientMessage(Component.translatable("client_message.voidminers.max_storage_upgrades.upgrade_already_applied_is_higher_tier"), true);
                return;
            }
        }

        blockEntity.setAppliedUpgradeItem(newUpgradeItem);

        if (!pPlayer.getAbilities().instabuild) {
            pStack.shrink(1);
            pPlayer.setItemInHand(pHand, pStack);
        }

        if (!(currentUpgradeItem == Items.AIR)) {
            boolean added = pPlayer.getInventory().add(new ItemStack(currentUpgradeItem));
            if (!added) {
                ItemEntity drop = new ItemEntity(pLevel, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), new ItemStack(currentUpgradeItem));
                pLevel.addFreshEntity(drop);
            }
        }

        if (blockEntity.getLevel() != null) {
            blockEntity.getLevel().sendBlockUpdated(pPos, pState, pState, 3);
        }

        pPlayer.displayClientMessage(Component.translatable("client_message.voidminers.max_storage_upgrades.upgrade_applied", newAddedSlots), true);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);

        MinerControllerBE controller = ((MinerControllerBE) pLevel.getBlockEntity(pPos));
        if (controller == null) {
            controller = ((MinerControllerBE) this.newBlockEntity(pPos, pState));
        }

        if (controller != null) {
            controller.setup(structure, name);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }

        return ((level, blockPos, blockState, be) -> {
            if (be instanceof MinerControllerBE controllerBE) {
                controllerBE.tick(pLevel, blockPos, blockState, structure, name);
            }
        });
    }
}