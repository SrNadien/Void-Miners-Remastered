package nadiendev.voidminersremastered.init;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.util.ColorUtil;
import nadiendev.voidminersremastered.world.block.ModifierBlock;
import nadiendev.voidminersremastered.world.item.ColoredBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(VoidMinersRemastered.MODID);

    public static final DeferredHolder<Block, Block> FRAME_BASE = registerBlock("frame_base",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(5, 6)
                            .requiresCorrectToolForDrops()
            )
    );

    public static final DeferredHolder<Block, Block> STRUCTURE_PANEL = registerBlock("structure_panel",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(5, 6)
                            .requiresCorrectToolForDrops()
            )
    );

    public static final DeferredHolder<Block, TransparentBlock> GLASS_PANEL = registerBlock("glass_panel",
            () -> new TransparentBlock(
                    BlockBehaviour.Properties.of()
                            .strength(0.3F)
                            .sound(SoundType.GLASS)
                            .noOcclusion()
                            .isValidSpawn((state, getter, pos, type) -> false)
                            .isRedstoneConductor((state, getter, pos) -> false)
                            .isSuffocating((state, getter, pos) -> false)
                            .isViewBlocking((state, getter, pos) -> false)
            )
    );

    public static final DeferredHolder<Block, ModifierBlock> NULL_MOD = registerBlock("null_modifier",
            () -> new ModifierBlock(
                    BlockBehaviour.Properties.of()
                            .strength(5, 6)
                            .requiresCorrectToolForDrops(),
                    "null",
                    ColorUtil.NULL_COLOR,
                    ModifierType.NULL
            )
    );

    public static <T extends Block> DeferredHolder<Block, T> registerBlock(String name, Supplier<T> block) {
        DeferredHolder<Block, T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    public static <T extends Block> DeferredHolder<Block, T> registerColoredBlock(String name, Supplier<T> block, Rarity rarity, ColorUtil color) {
        DeferredHolder<Block, T> toReturn = BLOCKS.register(name, block);
        registerColoredBlockItem(name, toReturn, rarity, color);
        return toReturn;
    }

    private static <T extends Block> DeferredHolder<Item, BlockItem> registerBlockItem(String name, DeferredHolder<Block, T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Block> DeferredHolder<Item, BlockItem> registerColoredBlockItem(String name, DeferredHolder<Block, T> block, Rarity rarity, ColorUtil color) {
        return ModItems.ITEMS.register(name, () -> new ColoredBlockItem(block.get(), new Item.Properties().rarity(rarity), color));
    }
}