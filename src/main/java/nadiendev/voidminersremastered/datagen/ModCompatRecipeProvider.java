package nadiendev.voidminersremastered.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import nadiendev.voidminersremastered.VoidMinersRemastered;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModCompatRecipeProvider implements DataProvider {
    public static final String OVERWORLD = "minecraft:overworld";
    public static final String NETHER = "minecraft:the_nether";
    public static final String END = "minecraft:the_end";
    public static final String MINING = "allthemodium:mining";
    public static final String OTHER = "allthemodium:the_other";
    public static final String MOON = "ad_astra:moon";
    public static final String MARS = "ad_astra:mars";
    public static final String VENUS = "ad_astra:venus";
    public static final String MERCURY = "ad_astra:mercury";
    public static final String GLACIO = "ad_astra:glacio";

    private static final String[] ATO_METALS = {
            "aluminum", "cinnabar", "fluorite", "iridium", "lead", "nickel", "osmium", "peridot", "platinum",
            "ruby", "salt", "sapphire", "silver", "sulfur", "tin", "uranium", "zinc"
    };

    private static final Map<String, Float> ATO_WEIGHTS = Map.ofEntries(
            Map.entry("aluminum", 8f), Map.entry("cinnabar", 4f), Map.entry("fluorite", 4f), Map.entry("iridium", 1f),
            Map.entry("lead", 6f), Map.entry("nickel", 6f), Map.entry("osmium", 6f), Map.entry("peridot", 2f),
            Map.entry("platinum", 3f), Map.entry("ruby", 2f), Map.entry("salt", 6f), Map.entry("sapphire", 2f),
            Map.entry("silver", 5f), Map.entry("sulfur", 4f), Map.entry("tin", 8f), Map.entry("uranium", 3f),
            Map.entry("zinc", 6f)
    );

    private final PackOutput.PathProvider pathProvider;
    private final Map<ResourceLocation, JsonObject> recipes = new LinkedHashMap<>();

    public ModCompatRecipeProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
    }

    private void buildRecipes() {
        mekanism();
        allTheModium();
        allTheOres();
        oritech();
        create();
        immersiveEngineering();
        otherMods();
        adAstra();
    }

    private void mekanism() {
        withDeepslate("mekanism", "mekanism:osmium_ore", "mekanism:deepslate_osmium_ore", 8f, 1);
        withDeepslate("mekanism", "mekanism:tin_ore", "mekanism:deepslate_tin_ore", 8f, 1);
        withDeepslate("mekanism", "mekanism:lead_ore", "mekanism:deepslate_lead_ore", 6f, 1);
        withDeepslate("mekanism", "mekanism:uranium_ore", "mekanism:deepslate_uranium_ore", 4f, 1);
        withDeepslate("mekanism", "mekanism:fluorite_ore", "mekanism:deepslate_fluorite_ore", 4f, 1);
        withDeepslate("mekmm", "mekmm:silver_ore", "mekmm:deepslate_silver_ore", 5f, 1);
        overworldAndMining("mekanism_extras", "mekanism_extras:naquadah_ore", 1f, 4);
        ore("mekanism_extras", "mekanism_extras:end_naquadah_ore", 1.5f, 4, END);
    }

    private void allTheModium() {
        ore("allthemodium", "allthemodium:allthemodium_ore", 0.5f, 5, OVERWORLD);
        ore("allthemodium", "allthemodium:vibranium_ore", 0.5f, 6, NETHER);
        ore("allthemodium", "allthemodium:unobtainium_ore", 0.3f, 7, END);

        ore("allthemodium", "allthemodium:allthemodium_ore", 1f, 5, OTHER);
        ore("allthemodium", "allthemodium:other_vibranium_ore", 1f, 6, OTHER);

        ore("allthemodium", "allthemodium:allthemodium_ore", 1f, 5, MINING);
        ore("allthemodium", "allthemodium:allthemodium_slate_ore", 1f, 5, MINING);
        ore("allthemodium", "allthemodium:vibranium_ore", 0.75f, 6, MINING);
        ore("allthemodium", "allthemodium:other_vibranium_ore", 0.75f, 6, MINING);
        ore("allthemodium", "allthemodium:unobtainium_ore", 0.5f, 7, MINING);
    }

    private void allTheOres() {
        for (String metal : ATO_METALS) {
            float weight = ATO_WEIGHTS.get(metal);
            int tier = metal.equals("iridium") ? 3 : 1;

            withDeepslate("alltheores", "alltheores:" + metal + "_ore", "alltheores:deepslate_" + metal + "_ore", weight, tier);
            ore("alltheores", "alltheores:nether_" + metal + "_ore", weight, tier, NETHER);
            ore("alltheores", "alltheores:end_" + metal + "_ore", weight, tier, END);
            ore("alltheores", "alltheores:other_" + metal + "_ore", weight, tier, OTHER);
        }
    }

    private void oritech() {
        withDeepslate("oritech", "oritech:nickel_ore", "oritech:deepslate_nickel_ore", 6f, 1);
        overworldAndMining("oritech", "oritech:deepslate_platinum_ore", 3f, 1);
        overworldAndMining("oritech", "oritech:deepslate_uranium_ore", 3f, 1);
        ore("oritech", "oritech:endstone_platinum_ore", 3f, 1, END);
    }

    private void create() {
        withDeepslate("create", "create:zinc_ore", "create:deepslate_zinc_ore", 8f, 1);
    }

    private void immersiveEngineering() {
        withDeepslate("immersiveengineering", "immersiveengineering:ore_aluminum", "immersiveengineering:deepslate_ore_aluminum", 8f, 1);
        withDeepslate("immersiveengineering", "immersiveengineering:ore_lead", "immersiveengineering:deepslate_ore_lead", 6f, 1);
        withDeepslate("immersiveengineering", "immersiveengineering:ore_silver", "immersiveengineering:deepslate_ore_silver", 5f, 1);
        withDeepslate("immersiveengineering", "immersiveengineering:ore_nickel", "immersiveengineering:deepslate_ore_nickel", 6f, 1);
        withDeepslate("immersiveengineering", "immersiveengineering:ore_uranium", "immersiveengineering:deepslate_ore_uranium", 3f, 1);
    }

    private void otherMods() {
        overworldAndMining("actuallyadditions", "actuallyadditions:black_quartz_ore", 4f, 1);
        withDeepslate("biggerreactors", "biggerreactors:uranium_ore", "biggerreactors:deepslate_uranium_ore", 4f, 1);
        overworldAndMining("create_better_motors", "create_better_motors:deepslate_reggarfonite_ore", 2f, 2);
        withDeepslate("createnuclear", "createnuclear:lead_ore", "createnuclear:deepslate_lead_ore", 6f, 1);
        withDeepslate("createnuclear", "createnuclear:uranium_ore", "createnuclear:deepslate_uranium_ore", 3f, 1);
        withDeepslate("enderio_evolution", "enderio_evolution:silver_ore", "enderio_evolution:deepslate_silver_ore", 5f, 1);
        withDeepslate("energizedpower", "energizedpower:tin_ore", "energizedpower:deepslate_tin_ore", 8f, 1);
        withDeepslate("occultism", "occultism:silver_ore", "occultism:silver_ore_deepslate", 5f, 1);
        withDeepslate("powah", "powah:uraninite_ore", "powah:deepslate_uraninite_ore", 5f, 1);
        withDeepslate("powah", "powah:uraninite_ore_dense", "powah:deepslate_uraninite_ore_dense", 1f, 2);
        withDeepslate("smallprogressions", "smallprogressions:stone_ender_ore", "smallprogressions:deepslate_ender_ore", 2f, 2);
        withDeepslate("mysticalagriculture", "mysticalagriculture:inferium_ore", "mysticalagriculture:deepslate_inferium_ore", 6f, 1);
        withDeepslate("mysticalagriculture", "mysticalagriculture:prosperity_ore", "mysticalagriculture:deepslate_prosperity_ore", 4f, 1);
        withDeepslate("malum", "malum:soulstone_ore", "malum:deepslate_soulstone_ore", 3f, 1);
        withDeepslate("malum", "malum:natural_quartz_ore", "malum:deepslate_quartz_ore", 3f, 1);
        overworldAndMining("malum", "malum:cthonic_gold_ore", 0.5f, 2);
        withDeepslate("xycraft_world", "xycraft_world:aluminum_ore_stone", "xycraft_world:aluminum_ore_deepslate", 6f, 1);
        for (String color : new String[]{"blue", "dark", "green", "light", "red"}) {
            withDeepslate("xycraft_world", "xycraft_world:xychorium_ore_stone_" + color, "xycraft_world:xychorium_ore_deepslate_" + color, 3f, 1);
        }
        withDeepslate("ultimatefoods", "ultimatefoods:joanfoite_ore", "ultimatefoods:deepslate_joanfoite_ore", 4f, 1);
        withDeepslate("ultimatefoods", "ultimatefoods:mushashite_ore", "ultimatefoods:deepslate_mushashite_ore", 4f, 1);
        withDeepslate("ultimatefoods", "ultimatefoods:nadienite_ore", "ultimatefoods:deepslate_nadienite_ore", 4f, 1);

        withDeepslate("draconicevolution", "draconicevolution:overworld_draconium_ore", "draconicevolution:deepslate_draconium_ore", 1f, 3);
        ore("draconicevolution", "draconicevolution:nether_draconium_ore", 1.5f, 3, NETHER);
        ore("draconicevolution", "draconicevolution:end_draconium_ore", 2f, 3, END);

        ore("smallprogressions", "smallprogressions:netherrack_ender_ore", 2f, 2, NETHER);
        ore("smallprogressions", "smallprogressions:endstone_ender_ore", 3f, 2, END);

        ore("mysticalagriculture", "mysticalagriculture:soulium_ore", 4f, 1, NETHER, MINING);
        ore("mysticalagradditions", "mysticalagradditions:nether_inferium_ore", 4f, 1, NETHER);
        ore("mysticalagradditions", "mysticalagradditions:nether_prosperity_ore", 3f, 1, NETHER);
        ore("mysticalagradditions", "mysticalagradditions:end_inferium_ore", 4f, 1, END);
        ore("mysticalagradditions", "mysticalagradditions:end_prosperity_ore", 3f, 1, END);

        ore("occultism", "occultism:iesnium_ore", 2f, 2, NETHER, MINING);
        ore("malum", "malum:blazing_quartz_ore", 4f, 1, NETHER, MINING);
        ore("modernfoundry", "modernfoundry:cobalt_ore", 3f, 2, NETHER, MINING);
    }

    private void adAstra() {
        ore("ad_astra", "ad_astra:moon_iron_ore", 8f, 1, MOON);
        ore("ad_astra", "ad_astra:moon_desh_ore", 5f, 2, MOON);
        ore("ad_astra", "ad_astra:moon_ice_shard_ore", 4f, 1, MOON);
        ore("ad_astra", "ad_astra:moon_cheese_ore", 3f, 1, MOON);

        ore("ad_astra", "ad_astra:mars_iron_ore", 8f, 1, MARS);
        ore("ad_astra", "ad_astra:mars_ostrum_ore", 5f, 3, MARS);
        ore("ad_astra", "ad_astra:mars_ice_shard_ore", 4f, 1, MARS);
        ore("ad_astra", "ad_astra:mars_diamond_ore", 2f, 1, MARS);

        ore("ad_astra", "ad_astra:venus_coal_ore", 12f, 1, VENUS);
        ore("ad_astra", "ad_astra:venus_gold_ore", 4f, 1, VENUS);
        ore("ad_astra", "ad_astra:venus_calorite_ore", 5f, 4, VENUS);
        ore("ad_astra", "ad_astra:venus_diamond_ore", 2f, 1, VENUS);

        ore("ad_astra", "ad_astra:mercury_iron_ore", 8f, 1, MERCURY);

        ore("ad_astra", "ad_astra:glacio_coal_ore", 12f, 1, GLACIO);
        ore("ad_astra", "ad_astra:glacio_copper_ore", 10f, 1, GLACIO);
        ore("ad_astra", "ad_astra:glacio_iron_ore", 8f, 1, GLACIO);
        ore("ad_astra", "ad_astra:glacio_lapis_ore", 6f, 1, GLACIO);
        ore("ad_astra", "ad_astra:glacio_ice_shard_ore", 4f, 1, GLACIO);
    }

    private void withDeepslate(String modId, String stoneItem, String deepslateItem, float weight, int minTier) {
        overworldAndMining(modId, stoneItem, weight, minTier);
        overworldAndMining(modId, deepslateItem, weight / 2f, minTier);
    }

    private void overworldAndMining(String modId, String item, float weight, int minTier) {
        ore(modId, item, weight, minTier, OVERWORLD, MINING);
    }

    private void ore(String modId, String item, float weight, int minTier, String... dimensions) {
        for (String dimension : dimensions) {
            ResourceLocation itemId = ResourceLocation.parse(item);
            ResourceLocation dimensionId = ResourceLocation.parse(dimension);

            Set<String> requiredMods = new LinkedHashSet<>();
            requiredMods.add(modId);
            if (!dimensionId.getNamespace().equals("minecraft")) {
                requiredMods.add(dimensionId.getNamespace());
            }

            JsonArray conditions = new JsonArray();
            for (String required : requiredMods) {
                JsonObject condition = new JsonObject();
                condition.addProperty("type", "neoforge:mod_loaded");
                condition.addProperty("modid", required);
                conditions.add(condition);
            }

            JsonObject stack = new JsonObject();
            stack.addProperty("count", 1);
            stack.addProperty("id", itemId.toString());

            JsonObject output = new JsonObject();
            output.add("stack", stack);
            output.addProperty("weight", weight);

            JsonObject json = new JsonObject();
            json.add("neoforge:conditions", conditions);
            json.addProperty("type", VoidMinersRemastered.MODID + ":miner");
            json.addProperty("dimension", dimensionId.toString());
            json.addProperty("minTier", minTier);
            json.add("output", output);

            ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(
                    VoidMinersRemastered.MODID,
                    "compat/" + dimensionId.getPath() + "/tier" + minTier + "_miner/" + itemId.getNamespace() + "/" + itemId.getPath()
            );

            if (recipes.put(recipeId, json) != null) {
                throw new IllegalStateException("Duplicate compat miner recipe " + recipeId);
            }
        }
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        recipes.clear();
        buildRecipes();

        List<CompletableFuture<?>> futures = new ArrayList<>();
        recipes.forEach((id, json) -> futures.add(DataProvider.saveStable(output, json, pathProvider.json(id))));
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Void Miners compat miner recipes";
    }
}
