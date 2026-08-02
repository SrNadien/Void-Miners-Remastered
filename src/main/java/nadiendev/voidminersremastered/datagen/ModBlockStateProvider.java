package nadiendev.voidminersremastered.datagen;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.init.ModBlocks;
import nadiendev.voidminersremastered.init.CrystalSet;
import nadiendev.voidminersremastered.init.SolarSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockStateProvider extends BlockStateProvider {

    ExistingFileHelper existingFileHelper;

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, VoidMinersRemastered.MODID, exFileHelper);
        this.existingFileHelper = exFileHelper;
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockItem(ModBlocks.FRAME_BASE);

        quadLayerBlockWithItem(ModBlocks.FRAME_BASE, "voidminersremastered:block/null/frame", "voidminersremastered:block/_core/frame", "voidminersremastered:block/_core/cover", "voidminersremastered:block/null/core");

        dualLayerBlockWithItem(ModBlocks.STRUCTURE_PANEL, "voidminersremastered:block/_core/panel", "voidminersremastered:block/_core/cover");

        simpleBlockWithItem(ModBlocks.GLASS_PANEL);

        quadLayerBlockWithItem(ModBlocks.NULL_MOD, "voidminersremastered:block/null/modifier", "voidminersremastered:block/_core/modifier", "voidminersremastered:block/_core/cover", "voidminersremastered:block/null/core");

        for (CrystalSet set : CrystalSet.sets()) {
            simpleAllCubeWithItem(
                set.CRYSTAL_BLOCK,
                set.name
            );

            tripleLayerBlockWithItem(set.FRAME, "voidminersremastered:block/" + set.name + "/frame", "voidminersremastered:block/_core/frame", "voidminersremastered:block/_core/cover");

            simpleBlockWithItem(set.CONTROLLER.get(),
                    new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, "block/" + set.name + "/miner")));

            quadLayerBlockWithItem(set.ENERGY_MOD, "voidminersremastered:block/_core/energy", "voidminersremastered:block/_core/modifier", "voidminersremastered:block/_core/cover", "voidminersremastered:block/" + set.name + "/core");

            quadLayerBlockWithItem(set.SPEED_MOD, "voidminersremastered:block/_core/speed", "voidminersremastered:block/_core/modifier", "voidminersremastered:block/_core/cover", "voidminersremastered:block/" + set.name + "/core");

            quadLayerBlockWithItem(set.ITEM_MOD, "voidminersremastered:block/_core/item", "voidminersremastered:block/_core/modifier", "voidminersremastered:block/_core/cover", "voidminersremastered:block/" + set.name + "/core");
        }

        for (SolarSet set : SolarSet.sets()) {
            simpleAllCubeWithItem(
                    set.CRYSTAL_BLOCK,
                    set.name
            );

            tripleLayerBlockWithItem(set.FRAME, "voidminersremastered:block/" + set.name + "/frame", "voidminersremastered:block/_core/frame", "voidminersremastered:block/_core/cover");

            simpleBlockWithItem(set.CONTROLLER.get(),
                    new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, "block/" + set.name + "/solar_panel")));

            quadLayerBlockWithItem(set.EFFICIENCY_MOD, "voidminersremastered:block/_core/energy", "voidminersremastered:block/_core/modifier", "voidminersremastered:block/_core/cover", "voidminersremastered:block/" + set.name + "/core");

            quadLayerBlockWithItem(set.WEATHER_MOD, "voidminersremastered:block/_core/speed", "voidminersremastered:block/_core/modifier", "voidminersremastered:block/_core/cover", "voidminersremastered:block/" + set.name + "/core");
        }
    }

    private void simpleBlockWithItem(DeferredHolder<Block, ? extends Block> block) {
        simpleBlockWithItem(block.get(), new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, "block/" + block.getId().getPath())));
    }

    private void simpleBlockWithItem(DeferredHolder<Block, ? extends Block> block, String name) {
        simpleBlockWithItem(block.get(), new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, "block/" + name)));
    }

    private void simpleBlockItem(DeferredHolder<Block, ? extends Block> block) {
        simpleBlockItem(block.get(), new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, "block/" + block.getId().getPath())));
    }

    private void dualLayerBlockWithItem(DeferredHolder<Block, ? extends Block> block, String layer0, String layer1) {
        ModelFile.UncheckedModelFile parent = new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, "block/_template/dual_layer"));

        BlockModelBuilder model = models().getBuilder(block.getId().getPath())
            .parent(parent)
            .texture("layer0", layer0)
            .texture("layer1", layer1);

        simpleBlockWithItem(block.get(), model);
    }

    private void tripleLayerBlockWithItem(DeferredHolder<Block, ? extends Block> block, String layer0, String layer1, String layer2) {
        ModelFile.UncheckedModelFile parent = new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, "block/_template/triple_layer"));

        BlockModelBuilder model = models().getBuilder(block.getId().getPath())
            .parent(parent)
            .texture("layer0", layer0)
            .texture("layer1", layer1)
            .texture("layer2", layer2);

        simpleBlockWithItem(block.get(), model);
    }

    private void quadLayerBlockWithItem(DeferredHolder<Block, ? extends Block> block, String layer0, String layer1, String layer2, String layer3) {
        ModelFile.UncheckedModelFile parent = new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, "block/_template/quad_layer"));

        BlockModelBuilder model = models().getBuilder(block.getId().getPath())
            .parent(parent)
            .texture("layer0", layer0)
            .texture("layer1", layer1)
            .texture("layer2", layer2)
            .texture("layer3", layer3);

        simpleBlockWithItem(block.get(), model);
    }

    private void simpleAllCubeWithItem(DeferredHolder<Block, Block> block, String name) {
        simpleBlockWithItem(block.get(), models().cubeAll(name(block.get()), ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, "block/" + name + "/block")));
    }

    private String name(Block block) {
        return key(block).getPath();
    }

    private ResourceLocation key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }
}