package nadiendev.voidminersremastered.config;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import io.netty.buffer.ByteBuf;
import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.util.MapUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.*;

import static java.nio.file.Files.readAllBytes;

public class SolarConfigLoader {
    public static final String CONFIG_FILE = "voidminers-solars.json5";
    private static SolarConfigLoader INSTANCE = new SolarConfigLoader();

    private SolarConfigLoader() {}

    public static SolarConfigLoader getInstance() {
        return INSTANCE != null ? INSTANCE : new SolarConfigLoader();
    }

    @Expose
    public boolean ALLOW_TICK_ACCELERATION = true;

    @Expose
    public List<String> ALWAYS_DAY_DIMENSIONS = new ArrayList<>(Arrays.asList(
            "jamd:mining"
    ));

    public boolean isAlwaysDayDimension(String dimensionId) {
        return ALWAYS_DAY_DIMENSIONS != null &&
                ALWAYS_DAY_DIMENSIONS.contains(dimensionId);
    }

    @Expose
    public Map<String, Config> SOLAR_CONFIGS = MapUtil.of(
            MapUtil.createEntry("rubetine", new Config(5000000, 20,
                    MapUtil.of(
                            MapUtil.createEntry("efficiency", new ModifierConfig(1.15f, 1.0f)),
                            MapUtil.createEntry("weather_resistance", new ModifierConfig(1.0f, 1.15f))
                    )
            )),
            MapUtil.createEntry("aurantium", new Config(10000000, 40,
                    MapUtil.of(
                            MapUtil.createEntry("efficiency", new ModifierConfig(1.17f, 1.0f)),
                            MapUtil.createEntry("weather_resistance", new ModifierConfig(1.0f, 1.17f))
                    )
            )),
            MapUtil.createEntry("citrinetine", new Config(20000000, 80,
                    MapUtil.of(
                            MapUtil.createEntry("efficiency", new ModifierConfig(1.19f, 1.0f)),
                            MapUtil.createEntry("weather_resistance", new ModifierConfig(1.0f, 1.19f))
                    )
            )),
            MapUtil.createEntry("verdium", new Config(40000000, 160,
                    MapUtil.of(
                            MapUtil.createEntry("efficiency", new ModifierConfig(1.21f, 1.0f)),
                            MapUtil.createEntry("weather_resistance", new ModifierConfig(1.0f, 1.21f))
                    )
            )),
            MapUtil.createEntry("azurine", new Config(80000000, 320,
                    MapUtil.of(
                            MapUtil.createEntry("efficiency", new ModifierConfig(1.23f, 1.0f)),
                            MapUtil.createEntry("weather_resistance", new ModifierConfig(1.0f, 1.23f))
                    )
            )),
            MapUtil.createEntry("caerium", new Config(160000000, 640,
                    MapUtil.of(
                            MapUtil.createEntry("efficiency", new ModifierConfig(1.25f, 1.0f)),
                            MapUtil.createEntry("weather_resistance", new ModifierConfig(1.0f, 1.25f))
                    )
            )),
            MapUtil.createEntry("amethystine", new Config(320000000, 1280,
                    MapUtil.of(
                            MapUtil.createEntry("efficiency", new ModifierConfig(1.27f, 1.0f)),
                            MapUtil.createEntry("weather_resistance", new ModifierConfig(1.0f, 1.27f))
                    )
            )),
            MapUtil.createEntry("rosarium", new Config(640000000, 2560,
                    MapUtil.of(
                            MapUtil.createEntry("efficiency", new ModifierConfig(1.29f, 1.0f)),
                            MapUtil.createEntry("weather_resistance", new ModifierConfig(1.0f, 1.29f))
                    )
            )),
            MapUtil.createEntry("ultimate", new Config(2147483647, 5120,
                    MapUtil.of(
                            MapUtil.createEntry("efficiency", new ModifierConfig(1.35f, 1.0f)),
                            MapUtil.createEntry("weather_resistance", new ModifierConfig(1.0f, 1.35f))
                    )
            ))
    );

    public void load() {
        Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .create();

        Path configPath = FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE);
        File file = configPath.toFile();

        try {
            if (!file.exists()) {
               VoidMinersRemastered.LOGGER.info("Configuration file does not exist. Creating a new one.");
                saveDefaultConfig(file, gson);
            } else {
               VoidMinersRemastered.LOGGER.info("Configuration file exists. Merging the current file with a new one configurations exist to preserve changed configurations.");
                mergeDefaultConfig(file, gson);
            }
        } catch (JsonSyntaxException e) {
           VoidMinersRemastered.LOGGER.error("Invalid configuration file. Regenerating default config.");
            saveDefaultConfig(file, gson);
        }
    }

    private void saveDefaultConfig(File file, Gson gson) {
        try (FileWriter writer = new FileWriter(file)) {
            if(INSTANCE == null) INSTANCE = new SolarConfigLoader();

            gson.toJson(INSTANCE, SolarConfigLoader.class, writer);
           VoidMinersRemastered.LOGGER.info("Default configuration file created successfully.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create default configuration file.", e);
        }
    }

    private void mergeDefaultConfig(File file, Gson gson) {
        try {
            String existingContent = new String(readAllBytes(file.toPath()));

            JsonObject existingConfig = gson.fromJson(existingContent, JsonObject.class);

            // Create default config JSON from a fresh MinerConfigLoader instance
            StringWriter defaultConfigWriter = new StringWriter();
            gson.toJson(new SolarConfigLoader(), defaultConfigWriter);
            JsonObject defaultConfig = gson.fromJson(defaultConfigWriter.toString(), JsonObject.class);

            // Merge default config into existing config, only adding missing keys
            mergeJsonObjects(existingConfig, defaultConfig);

            // Convert merged config back to JSON string
            String mergedJson = gson.toJson(existingConfig);

            INSTANCE = gson.fromJson(mergedJson, SolarConfigLoader.class);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(mergedJson);
            }

           VoidMinersRemastered.LOGGER.info("Merged configuration file successfully.");
        } catch (IOException | JsonSyntaxException e) {
           VoidMinersRemastered.LOGGER.error("Failed to merge, regenerating default config." + e.getMessage());
            saveDefaultConfig(file, gson);
        }
    }

    private void mergeJsonObjects(JsonObject target, JsonObject source) {
        for (Map.Entry<String, JsonElement> entry : source.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if (value.isJsonObject()) {
                // If the value is a nested object, recursively merge
                JsonObject nestedTarget = target.has(key) && target.get(key).isJsonObject()
                        ? target.getAsJsonObject(key)
                        : null;
                if (nestedTarget == null) {
                    nestedTarget = new JsonObject();
                    target.add(key, nestedTarget);
                }
                mergeJsonObjects(nestedTarget, value.getAsJsonObject());
            } else {
                // For non-object values, only add the value if it's missing in the target
                if (!target.has(key)) {
                    target.add(key, value);
                }
            }
        }
    }

    public Config getConfig(String name) {
        return SOLAR_CONFIGS.getOrDefault(name, new Config(0, 0, Map.of()));
    }

    public ModifierConfig getModifierConfig(String modifierTierName, String type) {
        if (modifierTierName.equals("null")) return new ModifierConfig(1, 1);

        if(type.equals("weather")) type = "weather_resistance";

        return getConfig(modifierTierName).modifiers.getOrDefault(type, new ModifierConfig(100, 1));
    }

    public ModifierConfig getModifierConfig(Block block) {
        String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
        // blockName expected = solar_minerTier_modifierType
        String modifierTierName;
        String modifierType;

        if (blockName.equals("null_modifier")) {
            modifierTierName = "null";
            modifierType = "modifier";
        } else {
            modifierTierName = blockName.split("_")[1];
            modifierType = blockName.split("_")[2];
        }

        return getModifierConfig(modifierTierName, modifierType);
    }

    public record Config(@Expose long energyStorage, @Expose long energyGenerationPerTick, @Expose Map<String, ModifierConfig> modifiers) {
        public static final StreamCodec<ByteBuf, Config> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG,
            Config::energyStorage,
            ByteBufCodecs.VAR_LONG,
            Config::energyGenerationPerTick,
            ByteBufCodecs.map(
                HashMap::new,
                ByteBufCodecs.STRING_UTF8,
                ModifierConfig.STREAM_CODEC
            ),
            Config::modifiers,
            Config::new
        );
    }

    public record ModifierConfig(@Expose float efficiency, @Expose float weatherResistance) {
        public static final StreamCodec<ByteBuf, ModifierConfig> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            ModifierConfig::efficiency,
            ByteBufCodecs.FLOAT,
            ModifierConfig::weatherResistance,
            ModifierConfig::new
        );
    }
}