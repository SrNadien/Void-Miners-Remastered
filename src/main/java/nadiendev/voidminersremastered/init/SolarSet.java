package nadiendev.voidminersremastered.init;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.util.ColorUtil;
import nadiendev.voidminersremastered.world.block.*;
import nadiendev.voidminersremastered.world.item.ColoredItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;

public class SolarSet {
    public static SolarSet RUBETINE;
    public static SolarSet AURANTIUM;
    public static SolarSet CITRINETINE;
    public static SolarSet VERDIUM;
    public static SolarSet AZURINE;
    public static SolarSet CAERIUM;
    public static SolarSet AMETHYSTINE;
    public static SolarSet ROSARIUM;
    public static SolarSet ULTIMATE;

    public final String name;
    public final DeferredHolder<Item, Item> CRYSTAL;
    public final DeferredHolder<Block, Block> CRYSTAL_BLOCK;
    public final DeferredHolder<Block, ? extends Block> CONTROLLER;
    public final DeferredHolder<Block, Block> FRAME;
    public final DeferredHolder<Block, ? extends Block> EFFICIENCY_MOD;
    public final DeferredHolder<Block, ? extends Block> WEATHER_MOD;
    public final ColorUtil color;

    SolarSet(String name, DeferredHolder<Item, Item> solarCrystal, DeferredHolder<Block, Block> solarCrystalBlock,
             DeferredHolder<Block, ? extends Block> solarPanelController, DeferredHolder<Block, Block> solarFrame,
             DeferredHolder<Block, ? extends Block> efficiencyMod, DeferredHolder<Block, ? extends Block> weatherMod,
             ColorUtil color) {
        this.name = name;
        CRYSTAL = solarCrystal;
        CRYSTAL_BLOCK = solarCrystalBlock;
        CONTROLLER = solarPanelController;
        FRAME = solarFrame;
        EFFICIENCY_MOD = efficiencyMod;
        WEATHER_MOD = weatherMod;
        this.color = color;
    }

    public static DeferredHolder<Item, Item> fastCreateSolarItem(String name, Rarity rarity, ColorUtil color) {
        return ModItems.ITEMS.register("solar_" + name, () -> new ColoredItem(
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
        return ModBlocks.registerColoredBlock("solar_" + name + "_" + type.type + "_modifier",
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

    public static DeferredHolder<Block, SolarControllerBlock> fastCreateController(String name, float hardness, float resistance, Rarity rarity, ResourceLocation structure, ColorUtil color) {
        return ModBlocks.registerColoredBlock("solar_" + name + "_panel",
                () -> new SolarControllerBlock(
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

    public static SolarSet createSet(String name, Rarity rarity, ColorUtil color) {
        return new SolarSet(
                name,
                name.equals("ultimate") ? null : fastCreateSolarItem(name, rarity, color),
                fastCreateBlock("solar_" + name + "_block", 5, 6, rarity, color),
                fastCreateController(name, 5, 6, rarity, ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, "solar_" + name), color),
                fastCreateBlock("solar_" + name + "_frame", 5, 6, rarity, color),
                fastCreateModifier(name, 5, 6, rarity, ModifierType.EFFICIENCY, color),
                fastCreateModifier(name, 5, 6, rarity, ModifierType.WEATHER, color),
                color
        );
    }

    public static List<SolarSet> sets() {
        List<SolarSet> sets = new ArrayList<>();

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
