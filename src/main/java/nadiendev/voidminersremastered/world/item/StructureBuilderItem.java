package nadiendev.voidminersremastered.world.item;

import nadiendev.voidminersremastered.datagen.ModBlockTagGenerator;
import nadiendev.voidminersremastered.init.ModBlocks;
import nadiendev.voidminersremastered.util.MiscUtil;
import nadiendev.voidminersremastered.world.block.entity.MinerControllerBE;
import nadiendev.voidminersremastered.world.block.entity.SolarControllerBE;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

import java.util.ArrayList;
import java.util.List;

public class StructureBuilderItem extends Item {
    public StructureBuilderItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if(pContext.getPlayer() == null) return InteractionResult.PASS;

        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();

        if (!(level instanceof ServerLevel)) {
            return InteractionResult.PASS;
        }

        BlockEntity entity = level.getBlockEntity(pos);

        if(entity instanceof MinerControllerBE minerController) {
            buildMultiblock(level, pos, player, minerController.getStructure().toString(), true);
        } else if (entity instanceof SolarControllerBE solarController) {
            buildMultiblock(level, pos, player, solarController.getStructure().toString(), false);
        } else {
            return InteractionResult.PASS;
        }

        return InteractionResult.CONSUME;
    }

    private static void buildMultiblock(Level pLevel, BlockPos controllerPos, Player pPlayer, String structureKey, boolean isMiner) {
        if (!MiscUtil.structureMap.containsKey(structureKey)) return;

        List<List<List<BlockState>>> blocks = MiscUtil.structureMap.get(structureKey);

        int xOffset = MiscUtil.structureMap.get(structureKey).getFirst().size() / 2;
        int yOffset = MiscUtil.structureMap.get(structureKey).size() - 1;

        BlockPos origin = controllerPos.offset(-xOffset,  isMiner ? 0 : yOffset, -xOffset);

        List<ItemStack> missingBlocks = new ArrayList<>(List.of());

        boolean missingBlocksInInventory = false;

        for (int x = 0; x < blocks.size(); x++) {
            List<List<BlockState>> b2 = blocks.get(x);
            for (int y = 0; y < b2.size(); y++) {
                List<BlockState> b3 = b2.get(y);
                for (int z = 0; z < b3.size(); z++) {
                    BlockState block = b3.get(z);

                    Block targetBlock = block.getBlock();

                    if (targetBlock != Blocks.AIR) {
                        BlockPos targetPos = origin.offset(y, -x, z);

                        // if player in creative, build and skip
                        if(pPlayer.getAbilities().instabuild) {
                            pLevel.setBlock(targetPos, block, 3);
                            continue;
                        }

                        // if same block already placed in world, skip
                        if(block == pLevel.getBlockState(targetPos)) {
                            continue;
                        }

                        // if another block already placed in world (NULL_MOD gets special treatment), skip
                        if(pLevel.getBlockState(targetPos).getBlock() != Blocks.AIR) {
                            if(targetBlock == ModBlocks.NULL_MOD.get() &&
                                    pLevel.getBlockState(targetPos).getBlock().builtInRegistryHolder().is(isMiner ? ModBlockTagGenerator.MINER_MODIFIERS : ModBlockTagGenerator.SOLAR_MODIFIERS)) {
                                continue;
                            }
                            generateTooltip(pPlayer, true, false, new ArrayList<>(List.of()));
                            return;
                        }

                        // check the player's inventory for blocks that can replace NULL_MOD
                        if(targetBlock == ModBlocks.NULL_MOD.get()) {
                            boolean foundMatchingItem = false;
                            for (int slot = 0; slot < Inventory.INVENTORY_SIZE; slot++) {
                                ItemStack stack = pPlayer.getInventory().getItem(slot);
                                if (!stack.isEmpty()) {
                                    Block stackBlock = Block.byItem(stack.getItem());
                                    if (stackBlock != null && stackBlock != Blocks.AIR) {
                                        if (stackBlock.builtInRegistryHolder().is(isMiner ? ModBlockTagGenerator.MINER_MODIFIERS : ModBlockTagGenerator.SOLAR_MODIFIERS)) {
                                            stack.setCount(stack.getCount() - 1);
                                            pLevel.setBlock(targetPos, stackBlock.defaultBlockState(), 3);
                                            foundMatchingItem = true;
                                            break;
                                        }
                                    }
                                }
                            }

                            if (!foundMatchingItem) {
                                missingBlocksInInventory = true;

                                boolean blockAlreadyInArray = false;
                                for (ItemStack stack : missingBlocks) {
                                    if(stack.getItem() == targetBlock.asItem()) {
                                        blockAlreadyInArray = true;
                                        stack.setCount(stack.getCount() + 1);
                                        break;
                                    }
                                }

                                if(!blockAlreadyInArray) {
                                    missingBlocks.add(new ItemStack(targetBlock));
                                }
                            }
                            continue;
                        }

                        if(pPlayer.getInventory().contains(new ItemStack(targetBlock))) {
                            int slot = pPlayer.getInventory().findSlotMatchingItem(new ItemStack(targetBlock));
                            pPlayer.getSlot(slot).get().setCount(pPlayer.getSlot(slot).get().getCount() - 1);
                            pLevel.setBlock(targetPos, block, 3);
                        } else {
                            missingBlocksInInventory = true;

                            boolean blockAlreadyInArray = false;
                            for (ItemStack stack : missingBlocks) {
                                if(stack.getItem() == targetBlock.asItem()) {
                                    blockAlreadyInArray = true;
                                    stack.setCount(stack.getCount() + 1);
                                    break;
                                }
                            }

                            if(!blockAlreadyInArray) {
                                missingBlocks.add(new ItemStack(targetBlock));
                            }
                        }
                    }
                }
            }
        }
        if(missingBlocksInInventory) {
            generateTooltip(pPlayer, false, true, missingBlocks);
        }
    }

    private static void generateTooltip(Player pPlayer, boolean blocksInTheWay, boolean missingBlocksInInventory, List<ItemStack> missingBlocks) {
        List<Component> tooltip = new ArrayList<>();

        if(blocksInTheWay) {
            tooltip.add(Component.translatable("tooltip.voidminersremastered.structure_builder.unable_to_place_multiblock.1").withStyle(ChatFormatting.RED));
            tooltip.add(Component.translatable("tooltip.voidminersremastered.structure_builder.unable_to_place_multiblock.2").withStyle(ChatFormatting.YELLOW));
        }

        if(missingBlocksInInventory) {
            tooltip.add(Component.translatable("tooltip.voidminersremastered.structure_builder.missing_block_in_inventory").withStyle(ChatFormatting.RED));

            for (ItemStack stack : missingBlocks) {
                // ItemStack#getDescriptionId is gone in 26.1.2; the id now lives on the Item.
                String blockName = Language.getInstance().getOrDefault(stack.getItem().getDescriptionId());
                if(blockName.contains("Null")) blockName = "Modifier";
                tooltip.add(Component.literal(stack.getCount() + "x " + blockName).withStyle(ChatFormatting.YELLOW));
            }
        }

        for (Component component : tooltip) {
            // Player#displayClientMessage is gone in 26.1.2; this path is always server-side.
            if (pPlayer instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(component, false);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        tooltipAdder.accept(Component.translatable("tooltip.voidminersremastered.structure_builder.instructions").withStyle(ChatFormatting.LIGHT_PURPLE));
        super.appendHoverText(stack, context, display, tooltipAdder, tooltipFlag);
    }
}
