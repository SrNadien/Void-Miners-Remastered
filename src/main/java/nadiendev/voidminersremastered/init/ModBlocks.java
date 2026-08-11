package nadiendev.voidminersremastered.init;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.util.CustomColorUtil;
import nadiendev.voidminersremastered.world.block.ModifierBlock;
import nadiendev.voidminersremastered.world.item.ColoredBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class ModBlocks {
    // 26.1.2 requires Block.Properties to carry its registry id. DeferredRegister.Blocks#registerBlock
    // calls setId for us, which a plain DeferredRegister<Block>#register(Supplier) cannot do -- hence
    // the helpers below take a Function<Properties, T> instead of a Supplier<T>.
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(VoidMinersRemastered.MODID);

    public static final DeferredHolder<Block, Block> FRAME_BASE = registerBlock("frame_base",
            Block::new,
            () -> BlockBehaviour.Properties.of()
                    .strength(5, 6)
                    .requiresCorrectToolForDrops()
    );

    public static final DeferredHolder<Block, Block> STRUCTURE_PANEL = registerBlock("structure_panel",
            Block::new,
            () -> BlockBehaviour.Properties.of()
                    .strength(5, 6)
                    .requiresCorrectToolForDrops()
    );

    public static final DeferredHolder<Block, TransparentBlock> GLASS_PANEL = registerBlock("glass_panel",
            TransparentBlock::new,
            () -> BlockBehaviour.Properties.of()
                    .strength(10, 5)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.GLASS)
                    .noOcclusion()
    );

    public static final DeferredHolder<Block, ModifierBlock> NULL_MOD = registerBlock("null_modifier",
            p -> new ModifierBlock(p, "null", ModifierType.NULL),
            () -> BlockBehaviour.Properties.of()
                    .strength(5, 6)
                    .requiresCorrectToolForDrops()
    );

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> block,
                                                                  Supplier<BlockBehaviour.Properties> properties) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, block, properties);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> block,
                                                                  Supplier<BlockBehaviour.Properties> properties, Rarity rarity) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, block, properties);
        registerBlockItem(name, toReturn, rarity);
        return toReturn;
    }

    public static <T extends Block> DeferredBlock<T> registerColoredBlock(String name, Function<BlockBehaviour.Properties, T> block,
                                                                         Supplier<BlockBehaviour.Properties> properties,
                                                                         Rarity rarity, CustomColorUtil color) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, block, properties);
        registerColoredBlockItem(name, toReturn, rarity, color);
        return toReturn;
    }

    // useBlockDescriptionPrefix() keeps the existing "block.voidminersremastered.*" translation keys.
    private static <T extends Block> DeferredHolder<Item, BlockItem> registerBlockItem(String name, DeferredHolder<Block, T> block) {
        return ModItems.ITEMS.registerItem(name,
                p -> new BlockItem(block.get(), p),
                () -> new Item.Properties().useBlockDescriptionPrefix());
    }

    private static <T extends Block> DeferredHolder<Item, BlockItem> registerBlockItem(String name, DeferredHolder<Block, T> block, Rarity rarity) {
        return ModItems.ITEMS.registerItem(name,
                p -> new BlockItem(block.get(), p),
                () -> new Item.Properties().useBlockDescriptionPrefix().rarity(rarity));
    }

    private static <T extends Block> DeferredHolder<Item, BlockItem> registerColoredBlockItem(String name, DeferredHolder<Block, T> block, Rarity rarity, CustomColorUtil color) {
        return ModItems.ITEMS.<BlockItem>registerItem(name,
                p -> new ColoredBlockItem(block.get(), p, color),
                () -> new Item.Properties().useBlockDescriptionPrefix().rarity(rarity));
    }
}
