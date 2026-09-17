package nadiendev.voidminersremastered.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import nadiendev.voidminersremastered.VoidMinersRemastered;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModConnectedTexturesProvider implements DataProvider {
    public static final String FUSION_PACK = "fusion";

    private static final String NS = VoidMinersRemastered.MODID + ":block/";

    private static final Set<String> CONNECTED_TEXTURES = Set.of(
            NS + "_core/frame",
            NS + "_core/cover",
            NS + "null/frame",
            NS + "rubetine/frame",
            NS + "aurantium/frame",
            NS + "citrinetine/frame",
            NS + "verdium/frame",
            NS + "azurine/frame",
            NS + "caerium/frame",
            NS + "amethystine/frame",
            NS + "rosarium/frame",
            NS + "ultimate/frame"
    );

    private static final Map<String, ModelEntry> MODELS = new LinkedHashMap<>();

    private record ModelEntry(String parent, Map<String, String> textures) {}

    public static void record(String name, String parent, Map<String, String> textures) {
        MODELS.put(name, new ModelEntry(parent, new LinkedHashMap<>(textures)));
    }

    private final Path root;

    public ModConnectedTexturesProvider(PackOutput output) {
        this.root = output.getOutputFolder().resolve("resourcepacks");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        Map<String, Set<String>> usersByTexture = new LinkedHashMap<>();
        MODELS.forEach((name, entry) -> entry.textures().values().forEach(texture ->
                usersByTexture.computeIfAbsent(texture, t -> new LinkedHashSet<>()).add(name)));

        MODELS.forEach((name, entry) -> {
            boolean connected = entry.textures().values().stream().anyMatch(CONNECTED_TEXTURES::contains);
            if (!connected) {
                return;
            }

            JsonObject fusion = baseModel(entry);
            fusion.addProperty("loader", "fusion:model");
            fusion.addProperty("type", "fusion:connecting");
            JsonObject connections = new JsonObject();
            entry.textures().forEach((key, texture) -> connections.add(key, connectionPredicate(texture, usersByTexture)));
            fusion.add("connections", connections);
            futures.add(DataProvider.saveStable(output, fusion, modelPath(FUSION_PACK, name)));
        });

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private static final Map<String, List<String>> TEMPLATE_LAYERS = Map.of(
            "dual_layer", List.of("layer0", "layer1"),
            "triple_layer", List.of("layer0", "layer1", "layer2"),
            "quad_layer", List.of("layer0", "layer3", "layer1", "layer2"),
            "cube_all", List.of("all")
    );

    private static JsonObject connectionPredicate(String texture, Map<String, Set<String>> usersByTexture) {
        Set<String> users = usersByTexture.getOrDefault(texture, Set.of());
        JsonObject predicate = new JsonObject();
        if (!CONNECTED_TEXTURES.contains(texture) || users.size() <= 1) {
            predicate.addProperty("type", "fusion:is_same_block");
            return predicate;
        }

        JsonArray predicates = new JsonArray();
        for (String user : users) {
            JsonObject match = new JsonObject();
            match.addProperty("type", "fusion:match_block");
            match.addProperty("block", VoidMinersRemastered.MODID + ":" + user);
            predicates.add(match);
        }
        predicate.addProperty("type", "fusion:or");
        predicate.add("predicates", predicates);
        return predicate;
    }

    private static JsonObject baseModel(ModelEntry entry) {
        String template = entry.parent().substring(entry.parent().lastIndexOf('/') + 1);
        List<String> layers = TEMPLATE_LAYERS.get(template);
        if (layers == null) {
            throw new IllegalStateException("Unknown model template " + entry.parent());
        }

        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:block/block");

        JsonObject textures = new JsonObject();
        entry.textures().forEach(textures::addProperty);
        textures.addProperty("particle", "#" + layers.getFirst());
        json.add("textures", textures);

        JsonArray elements = new JsonArray();
        for (String layer : layers) {
            elements.add(fullCube("#" + layer));
        }
        json.add("elements", elements);
        return json;
    }

    private static JsonObject fullCube(String texture) {
        JsonObject element = new JsonObject();
        element.add("from", vector(0, 0, 0));
        element.add("to", vector(16, 16, 16));

        JsonObject faces = new JsonObject();
        for (String side : new String[]{"north", "east", "south", "west", "up", "down"}) {
            JsonObject face = new JsonObject();
            JsonArray uv = new JsonArray();
            uv.add(0);
            uv.add(0);
            uv.add(16);
            uv.add(16);
            face.add("uv", uv);
            face.addProperty("texture", texture);
            face.addProperty("cullface", side);
            faces.add(side, face);
        }
        element.add("faces", faces);
        return element;
    }

    private static JsonArray vector(int x, int y, int z) {
        JsonArray array = new JsonArray();
        array.add(x);
        array.add(y);
        array.add(z);
        return array;
    }

    private Path modelPath(String pack, String name) {
        return root.resolve(pack).resolve("assets").resolve(VoidMinersRemastered.MODID)
                .resolve("models").resolve("block").resolve(name + ".json");
    }

    @Override
    public String getName() {
        return "Void Miners connected texture packs";
    }
}
