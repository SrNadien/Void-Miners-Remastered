package nadiendev.voidminersremastered.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import nadiendev.voidminersremastered.VoidMinersRemastered;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

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
    private final Map<Identifier, JsonObject> recipes = new LinkedHashMap<>();

    public ModCompatRecipeProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
    }

    private void buildRecipes() {
        allTheModium();
        allTheOres();
        oritech();
        otherMods();
    }

    private void allTheModium() {
        ore("allthemodium", "allthemodium:allthemodium_ore", 0.5f, 5, OVERWORLD);
        ore("allthemodium", "allthemodium:vibranium_ore", 0.5f, 6, NETHER);
        ore("allthemodium", "allthemodium:unobtainium_ore", 0.3f, 7, END);

        ore("allthemodium", "allthemodium:allthemodium_ore", 1f, 5, OTHER);
        ore("allthemodium", "allthemodium:other_vibranium_ore", 1f, 6, OTHER);

        ore("allthemodium", "allthemodium:allthemodium_ore", 1f, 5, MINING);
        ore("allthemodium", "allthemodium:deepslate_allthemodium_ore", 1f, 5, MINING);
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

    private void otherMods() {
        withDeepslate("biggerreactors", "biggerreactors:uranium_ore", "biggerreactors:deepslate_uranium_ore", 4f, 1);
        withDeepslate("energizedpower", "energizedpower:tin_ore", "energizedpower:deepslate_tin_ore", 8f, 1);
        withDeepslate("occultism", "occultism:silver_ore", "occultism:silver_ore_deepslate", 5f, 1);
        withDeepslate("powah", "powah:uraninite_ore", "powah:deepslate_uraninite_ore", 5f, 1);
        withDeepslate("powah", "powah:uraninite_ore_dense", "powah:deepslate_uraninite_ore_dense", 1f, 2);
        withDeepslate("mysticalagriculture", "mysticalagriculture:inferium_ore", "mysticalagriculture:deepslate_inferium_ore", 6f, 1);
        withDeepslate("mysticalagriculture", "mysticalagriculture:prosperity_ore", "mysticalagriculture:deepslate_prosperity_ore", 4f, 1);
        withDeepslate("xycraft_world", "xycraft_world:aluminum_ore_stone", "xycraft_world:aluminum_ore_deepslate", 6f, 1);
        for (String color : new String[]{"blue", "dark", "green", "light", "red"}) {
            withDeepslate("xycraft_world", "xycraft_world:xychorium_ore_stone_" + color, "xycraft_world:xychorium_ore_deepslate_" + color, 3f, 1);
        }
        withDeepslate("ultimatefoods", "ultimatefoods:joanfoite_ore", "ultimatefoods:deepslate_joanfoite_ore", 4f, 1);
        withDeepslate("ultimatefoods", "ultimatefoods:mushashite_ore", "ultimatefoods:deepslate_mushashite_ore", 4f, 1);
        withDeepslate("ultimatefoods", "ultimatefoods:nadienite_ore", "ultimatefoods:deepslate_nadienite_ore", 4f, 1);

        ore("mysticalagriculture", "mysticalagriculture:soulium_ore", 4f, 1, NETHER, MINING);
        ore("mysticalagradditions", "mysticalagradditions:nether_inferium_ore", 4f, 1, NETHER);
        ore("mysticalagradditions", "mysticalagradditions:nether_prosperity_ore", 3f, 1, NETHER);
        ore("mysticalagradditions", "mysticalagradditions:end_inferium_ore", 4f, 1, END);
        ore("mysticalagradditions", "mysticalagradditions:end_prosperity_ore", 3f, 1, END);

        ore("occultism", "occultism:iesnium_ore", 2f, 2, NETHER, MINING);
        ore("modernfoundry", "modernfoundry:cobalt_ore", 3f, 2, NETHER, MINING);
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
            Identifier itemId = Identifier.parse(item);
            Identifier dimensionId = Identifier.parse(dimension);

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

            Identifier recipeId = Identifier.fromNamespaceAndPath(
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
