package nadiendev.voidminersremastered.datagen;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.init.CrystalSet;
import nadiendev.voidminersremastered.init.ModBlocks;
import nadiendev.voidminersremastered.init.SolarSet;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 26.1.2: {@code net.neoforged.neoforge.client.model.generators.BlockStateProvider} no longer exists.
 * Block states + block models + block item models are now produced by
 * {@link ModelProvider} / {@link BlockModelGenerators}.
 * <p>
 * This provider owns every block of the mod namespace and every {@link BlockItem}; plain items are
 * handled by {@link ModItemModelProvider}.
 */
public class ModBlockStateProvider extends ModelProvider {

    /** {@link TextureSlot} only ships LAYER0..LAYER2, the mod's quad_layer template needs a fourth. */
    private static final TextureSlot LAYER3 = TextureSlot.create("layer3");

    private static final TextureSlot[] LAYER_SLOTS = {
            TextureSlot.LAYER0, TextureSlot.LAYER1, TextureSlot.LAYER2, LAYER3
    };

    private static final ModelTemplate DUAL_LAYER = layerTemplate("dual_layer", 2);
    private static final ModelTemplate TRIPLE_LAYER = layerTemplate("triple_layer", 3);
    private static final ModelTemplate QUAD_LAYER = layerTemplate("quad_layer", 4);

    public ModBlockStateProvider(PackOutput output) {
        super(output, VoidMinersRemastered.MODID);
    }

    @Override
    public String getName() {
        return "Block Model Definitions - " + this.modId;
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return BuiltInRegistries.ITEM.listElements()
                .filter(holder -> holder.getKey().identifier().getNamespace().equals(this.modId))
                .filter(holder -> holder.value() instanceof BlockItem);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        layered(blockModels, QUAD_LAYER, ModBlocks.FRAME_BASE,
                "block/null/frame", "block/_core/frame", "block/_core/cover", "block/null/core");

        layered(blockModels, DUAL_LAYER, ModBlocks.STRUCTURE_PANEL,
                "block/_core/panel", "block/_core/cover");

        // Hand-written model in src/main/resources.
        existingModel(blockModels, ModBlocks.GLASS_PANEL, "block/glass_panel");

        layered(blockModels, QUAD_LAYER, ModBlocks.NULL_MOD,
                "block/null/modifier", "block/_core/modifier", "block/_core/cover", "block/null/core");

        for (CrystalSet set : CrystalSet.sets()) {
            cubeAll(blockModels, set.CRYSTAL_BLOCK, "block/" + set.name + "/block");

            layered(blockModels, TRIPLE_LAYER, set.FRAME,
                    "block/" + set.name + "/frame", "block/_core/frame", "block/_core/cover");

            existingModel(blockModels, set.CONTROLLER, "block/" + set.name + "/miner");

            layered(blockModels, QUAD_LAYER, set.ENERGY_MOD,
                    "block/_core/energy", "block/_core/modifier", "block/_core/cover", "block/" + set.name + "/core");

            layered(blockModels, QUAD_LAYER, set.SPEED_MOD,
                    "block/_core/speed", "block/_core/modifier", "block/_core/cover", "block/" + set.name + "/core");

            layered(blockModels, QUAD_LAYER, set.ITEM_MOD,
                    "block/_core/item", "block/_core/modifier", "block/_core/cover", "block/" + set.name + "/core");
        }

        for (SolarSet set : SolarSet.sets()) {
            cubeAll(blockModels, set.CRYSTAL_BLOCK, "block/" + set.name + "/block");

            layered(blockModels, TRIPLE_LAYER, set.FRAME,
                    "block/" + set.name + "/frame", "block/_core/frame", "block/_core/cover");

            existingModel(blockModels, set.CONTROLLER, "block/" + set.name + "/solar_panel");

            layered(blockModels, QUAD_LAYER, set.EFFICIENCY_MOD,
                    "block/_core/energy", "block/_core/modifier", "block/_core/cover", "block/" + set.name + "/core");

            layered(blockModels, QUAD_LAYER, set.WEATHER_MOD,
                    "block/_core/speed", "block/_core/modifier", "block/_core/cover", "block/" + set.name + "/core");
        }
    }

    // ------------------------------------------------------------------------------------------

    /** Builds a template pointing at one of the mod's own {@code block/_template/*_layer} models. */
    private static ModelTemplate layerTemplate(String name, int layers) {
        TextureSlot[] slots = new TextureSlot[layers];
        System.arraycopy(LAYER_SLOTS, 0, slots, 0, layers);
        return new ModelTemplate(Optional.of(modLoc("block/_template/" + name)), Optional.empty(), slots);
    }

    private static Identifier modLoc(String path) {
        return Identifier.fromNamespaceAndPath(VoidMinersRemastered.MODID, path);
    }

    private static Material modTexture(String path) {
        return new Material(modLoc(path));
    }

    /** Old {@code simpleBlockWithItem(block, model)}: one variant + the block item pointing at it. */
    private static void simpleBlockWithItem(BlockModelGenerators blockModels, Block block, Identifier model) {
        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(model)));
        blockModels.registerSimpleItemModel(block, model);
    }

    /** Old {@code ModelFile.UncheckedModelFile}: reference a model this provider does not generate. */
    private static void existingModel(BlockModelGenerators blockModels,
                                      DeferredHolder<Block, ? extends Block> holder, String modelPath) {
        simpleBlockWithItem(blockModels, holder.get(), modLoc(modelPath));
    }

    private static void cubeAll(BlockModelGenerators blockModels,
                                DeferredHolder<Block, ? extends Block> holder, String texture) {
        Block block = holder.get();
        Identifier model = ModelTemplates.CUBE_ALL.create(
                block, TextureMapping.cube(modTexture(texture)), blockModels.modelOutput);
        simpleBlockWithItem(blockModels, block, model);
    }

    private static void layered(BlockModelGenerators blockModels, ModelTemplate template,
                                DeferredHolder<Block, ? extends Block> holder, String... layers) {
        Block block = holder.get();
        TextureMapping mapping = new TextureMapping();
        for (int i = 0; i < layers.length; i++) {
            mapping.put(LAYER_SLOTS[i], modTexture(layers[i]));
        }
        Identifier model = template.create(block, mapping, blockModels.modelOutput);
        simpleBlockWithItem(blockModels, block, model);
    }
}
