package nadiendev.voidminersremastered.world.block;

import nadiendev.voidminersremastered.config.SolarConfigLoader;
import nadiendev.voidminersremastered.init.ModItems;
import nadiendev.voidminersremastered.util.CustomColorUtil;
import nadiendev.voidminersremastered.util.EnergyFormatUtil;
import nadiendev.voidminersremastered.world.block.entity.SolarControllerBE;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SolarControllerBlock extends ColoredBlock implements EntityBlock {
    final Identifier structure;
    final String name;

    public SolarControllerBlock(Properties pProperties, Identifier structure, String name, CustomColorUtil color) {
        super(pProperties, color);
        this.structure = structure;
        this.name = name;
    }

    public void appendControllerTooltip(Consumer<Component> tooltipAdder) {
        SolarConfigLoader.Config config = SolarConfigLoader.getInstance().getConfig(name);

        tooltipAdder.accept(Component.translatable("tooltip.voidminersremastered.controller.item.generation_per_tick", EnergyFormatUtil.format(config.energyGenerationPerTick())).withStyle(ChatFormatting.YELLOW));
        tooltipAdder.accept(Component.translatable("tooltip.voidminersremastered.controller.item.energy_capacity", EnergyFormatUtil.format(config.energyStorage())).withStyle(ChatFormatting.GOLD));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new SolarControllerBE(blockPos, blockState);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (pLevel.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (pStack.is(ModItems.FACE_CONFIGURATOR.get()) && pLevel.getBlockEntity(pPos) instanceof SolarControllerBE blockEntity) {
            Direction side = blockEntity.toggleExportSide(pHitResult.getDirection());
            if (pPlayer instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(side == null
                        ? Component.translatable("client_message.voidminersremastered.export.all_sides")
                        : Component.translatable("client_message.voidminersremastered.export.enabled",
                                Component.translatable("tooltip.voidminersremastered.controller.export.side." + side.getName())), true);
            }
            return InteractionResult.CONSUME;
        }

        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        SolarControllerBE blockEntity = (SolarControllerBE) pLevel.getBlockEntity(pPos);

        if (pLevel.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (pPlayer.isCrouching()) {
            if (blockEntity != null && !blockEntity.foundStructure) {
                blockEntity.updateShowStructure();
            } else if (blockEntity != null) {
                Direction side = blockEntity.toggleExportSide(pHit.getDirection());
                if (pPlayer instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(side == null
                        ? Component.translatable("client_message.voidminersremastered.export.all_sides")
                        : Component.translatable("client_message.voidminersremastered.export.enabled",
                                Component.translatable("tooltip.voidminersremastered.controller.export.side." + side.getName())), true);
                }
            }
            return InteractionResult.CONSUME;
        }

        if (blockEntity != null) {
            for (Component component : blockEntity.getInteractionTooltip()) {
                // Player#displayClientMessage is gone in 26.1.2; this path is always server-side.
                if (pPlayer instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(component, false);
                }
            }
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void setPlacedBy(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @Nullable LivingEntity pPlacer, @NotNull ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);

        SolarControllerBE controller = ((SolarControllerBE) pLevel.getBlockEntity(pPos));
        if (controller == null) {
            controller = ((SolarControllerBE) this.newBlockEntity(pPos, pState));
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
            if (be instanceof SolarControllerBE controllerBE) {
                controllerBE.tick(pLevel, blockPos, blockState, structure, name);
            }
        });
    }
}
