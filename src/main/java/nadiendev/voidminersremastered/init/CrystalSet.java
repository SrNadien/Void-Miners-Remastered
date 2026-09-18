package nadiendev.voidminersremastered.init;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.world.block.*;
import nadiendev.voidminersremastered.world.block.MinerControllerBlock;
import nadiendev.voidminersremastered.util.ColorUtil;
import nadiendev.voidminersremastered.world.item.ColoredItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;

public class CrystalSet {
    public static CrystalSet RUBETINE;
    public static CrystalSet AURANTIUM;
    public static CrystalSet CITRINETINE;
    public static CrystalSet VERDIUM;
    public static CrystalSet AZURINE;
    public static CrystalSet CAERIUM;
    public static CrystalSet AMETHYSTINE;
    public static CrystalSet ROSARIUM;
    public static CrystalSet ULTIMATE;

    public final String name;
    public final DeferredHolder<Item, Item> CRYSTAL;
    public final DeferredHolder<Block, Block> CRYSTAL_BLOCK;
    public final DeferredHolder<Block, ? extends Block> CONTROLLER;
    public final DeferredHolder<Block, Block> FRAME;
    public final DeferredHolder<Block, ? extends Block> SPEED_MOD;
    public final DeferredHolder<Block, ? extends Block> ENERGY_MOD;
    public final DeferredHolder<Block, ? extends Block> ITEM_MOD;
    public final ColorUtil color;

    CrystalSet(String name, DeferredHolder<Item, Item> crystal, DeferredHolder<Block, Block> crystalBlock,
               DeferredHolder<Block, ? extends Block> minerController, DeferredHolder<Block, Block> frame,
               DeferredHolder<Block, ? extends Block> energyMod, DeferredHolder<Block, ? extends Block> speedMod,
               DeferredHolder<Block, ? extends Block> itemMod, ColorUtil color) {
        this.name = name;
        CRYSTAL = crystal;
        CRYSTAL_BLOCK = crystalBlock;
        CONTROLLER = minerController;
        FRAME = frame;
        SPEED_MOD = speedMod;
        ENERGY_MOD = energyMod;
        ITEM_MOD = itemMod;
        this.color = color;
    }

    public static DeferredHolder<Item, Item> fastCreateItem(String name, Rarity rarity, ColorUtil color) {
        return ModItems.ITEMS.register(name, () -> new ColoredItem(
                new Item.Properties().rarity(rarity), color
        ));
    }

    public static DeferredHolder<Block, Block> fastCreateBlock(String name, float hardness, float resistance, Rarity rarity, ColorUtil color) {
        return ModBlocks.registerColoredBlock(name,
                () -> new ColoredBlock(
                        BlockBehaviour.Properties.of()
                                .strength(hardness, resistance)
                                .requiresCorrectToolForDrops(),
                        color
                ),
                rarity,
                color
        );
    }

    public static DeferredHolder<Block, ModifierBlock> fastCreateModifier(String name, float hardness, float resistance, Rarity rarity, ModifierType type, ColorUtil color) {
        return ModBlocks.registerColoredBlock(name + "_" + type.type + "_modifier",
                () -> new ModifierBlock(
                        BlockBehaviour.Properties.of()
                                .strength(hardness, resistance)
                                .requiresCorrectToolForDrops(),
                        name,
                        color,
                        type
                ),
                rarity,
                color
        );
    }

    public static DeferredHolder<Block, MinerControllerBlock> fastCreateController(String name, float hardness, float resistance, Rarity rarity, ResourceLocation structure, ColorUtil color) {
        return ModBlocks.registerColoredBlock(name + "_miner",
                () -> new MinerControllerBlock(
                        BlockBehaviour.Properties.of()
                                .strength(hardness, resistance)
                                .requiresCorrectToolForDrops()
                                .noOcclusion(),
                        structure,
                        name,
                        color
                ),
                rarity,
                color
        );
    }

    public static void initSets() {
        RUBETINE = createSet("rubetine", ModRarities.RUBETINE, ColorUtil.RUBETINE_COLOR);
        AURANTIUM = createSet("aurantium", ModRarities.AURANTIUM, ColorUtil.AURANTIUM_COLOR);
        CITRINETINE = createSet("citrinetine", ModRarities.CITRINETINE, ColorUtil.CITRINETINE_COLOR);
        VERDIUM = createSet("verdium", ModRarities.VERDIUM, ColorUtil.VERDIUM_COLOR);
        AZURINE = createSet("azurine", ModRarities.AZURINE, ColorUtil.AZURINE_COLOR);
        CAERIUM = createSet("caerium", ModRarities.CAERIUM, ColorUtil.CAERIUM_COLOR);
        AMETHYSTINE = createSet("amethystine", ModRarities.AMETHYSTINE, ColorUtil.AMETHYSTINE_COLOR);
        ROSARIUM = createSet("rosarium", ModRarities.ROSARIUM, ColorUtil.ROSARIUM_COLOR);
        ULTIMATE = createSet("ultimate", ModRarities.ULTIMATE, ColorUtil.ULTIMATE_COLOR);
    }

    public static CrystalSet createSet(String name, Rarity rarity, ColorUtil color) {
        return new CrystalSet(
                name,
                name.equals("ultimate") ? null : fastCreateItem(name, rarity, color),
                fastCreateBlock(name + "_block", 5, 6, rarity, color),
                fastCreateController(name, 5, 6, rarity, ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, "miner_" + name), color),
                fastCreateBlock(name + "_frame", 5, 6, rarity, color),
                fastCreateModifier(name, 5, 6, rarity, ModifierType.ENERGY, color),
                fastCreateModifier(name, 5, 6, rarity, ModifierType.SPEED, color),
                fastCreateModifier(name, 5, 6, rarity, ModifierType.ITEM, color),
                color
        );
    }

    public static boolean isGem(net.minecraft.world.item.Item item) {
        for (CrystalSet set : sets()) {
            if (set.CRYSTAL != null && set.CRYSTAL.get() == item) {
                return true;
            }
        }
        return false;
    }

    public static List<CrystalSet> sets() {
        List<CrystalSet> sets = new ArrayList<>();
        sets.add(RUBETINE);
        sets.add(AURANTIUM);
        sets.add(CITRINETINE);
        sets.add(VERDIUM);
        sets.add(AZURINE);
        sets.add(CAERIUM);
        sets.add(AMETHYSTINE);
        sets.add(ROSARIUM);
        sets.add(ULTIMATE);
        return sets;
    }
}