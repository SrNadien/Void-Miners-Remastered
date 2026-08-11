package nadiendev.voidminersremastered.init;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.world.block.*;
import nadiendev.voidminersremastered.world.block.MinerControllerBlock;
import nadiendev.voidminersremastered.util.CustomColorUtil;
import nadiendev.voidminersremastered.world.item.ColoredItem;
import net.minecraft.resources.Identifier;
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
    public final CustomColorUtil color;

    CrystalSet(String name, DeferredHolder<Item, Item> crystal, DeferredHolder<Block, Block> crystalBlock,
               DeferredHolder<Block, ? extends Block> minerController, DeferredHolder<Block, Block> frame,
               DeferredHolder<Block, ? extends Block> energyMod, DeferredHolder<Block, ? extends Block> speedMod,
               DeferredHolder<Block, ? extends Block> itemMod, CustomColorUtil color) {
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

    public static DeferredHolder<Item, Item> fastCreateItem(String name, Rarity rarity, CustomColorUtil color) {
        return ModItems.ITEMS.<Item>registerItem(name,
                p -> new ColoredItem(p, color),
                () -> new Item.Properties().rarity(rarity));
    }

    public static DeferredHolder<Block, Block> fastCreateBlock(String name, float hardness, float resistance, Rarity rarity, CustomColorUtil color) {
        return ModBlocks.<Block>registerColoredBlock(name,
                p -> new ColoredBlock(p, color),
                () -> BlockBehaviour.Properties.of()
                        .strength(hardness, resistance)
                        .requiresCorrectToolForDrops(),
                rarity,
                color
        );
    }

    public static DeferredHolder<Block, ModifierBlock> fastCreateModifier(String name, float hardness, float resistance, Rarity rarity, ModifierType type, CustomColorUtil color) {
        return ModBlocks.registerColoredBlock(name + "_" + type.type + "_modifier",
                p -> new ModifierBlock(p, name, color, type),
                () -> BlockBehaviour.Properties.of()
                        .strength(hardness, resistance)
                        .requiresCorrectToolForDrops(),
                rarity,
                color
        );
    }

    public static DeferredHolder<Block, MinerControllerBlock> fastCreateController(String name, float hardness, float resistance, Rarity rarity, Identifier structure, CustomColorUtil color) {
        return ModBlocks.registerColoredBlock(name + "_miner",
                p -> new MinerControllerBlock(p, structure, name, color),
                () -> BlockBehaviour.Properties.of()
                        .strength(hardness, resistance)
                        .requiresCorrectToolForDrops()
                        .noOcclusion(),
                rarity,
                color
        );
    }

    public static void initSets() {
        RUBETINE = createSet("rubetine", ModRarities.RUBETINE, ModRarities.RUBETINE_COLOR);
        AURANTIUM = createSet("aurantium", ModRarities.AURANTIUM, ModRarities.AURANTIUM_COLOR);
        CITRINETINE = createSet("citrinetine", ModRarities.CITRINETINE, ModRarities.CITRINETINE_COLOR);
        VERDIUM = createSet("verdium", ModRarities.VERDIUM, ModRarities.VERDIUM_COLOR);
        AZURINE = createSet("azurine", ModRarities.AZURINE, ModRarities.AZURINE_COLOR);
        CAERIUM = createSet("caerium", ModRarities.CAERIUM, ModRarities.CAERIUM_COLOR);
        AMETHYSTINE = createSet("amethystine", ModRarities.AMETHYSTINE, ModRarities.AMETHYSTINE_COLOR);
        ROSARIUM = createSet("rosarium", ModRarities.ROSARIUM, ModRarities.ROSARIUM_COLOR);
        ULTIMATE = createSet("ultimate", ModRarities.ULTIMATE, ModRarities.ULTIMATE_COLOR);
    }

    public static CrystalSet createSet(String name, Rarity rarity, CustomColorUtil color) {
        return new CrystalSet(
                name,
                name.equals("ultimate") ? null : fastCreateItem(name, rarity, color),
                fastCreateBlock(name + "_block", 5, 6, rarity, color),
                fastCreateController(name, 5, 6, rarity, Identifier.fromNamespaceAndPath(VoidMinersRemastered.MODID, "miner_" + name), color),
                fastCreateBlock(name + "_frame", 5, 6, rarity, color),
                fastCreateModifier(name, 5, 6, rarity, ModifierType.ENERGY, color),
                fastCreateModifier(name, 5, 6, rarity, ModifierType.SPEED, color),
                fastCreateModifier(name, 5, 6, rarity, ModifierType.ITEM, color),
                color
        );
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