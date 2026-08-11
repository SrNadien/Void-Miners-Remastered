package nadiendev.voidminersremastered.datagen;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.init.CrystalSet;
import nadiendev.voidminersremastered.init.ModItems;
import nadiendev.voidminersremastered.init.SolarSet;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.stream.Stream;

/**
 * 26.1.2: {@code net.neoforged.neoforge.client.model.generators.ItemModelProvider} no longer exists.
 * Item models are produced through {@link ItemModelGenerators}, and each item additionally gets a
 * client item definition under {@code assets/<ns>/items/}.
 * <p>
 * Only plain items live here; {@link BlockItem}s are covered by {@link ModBlockStateProvider}.
 */
public class ModItemModelProvider extends ModelProvider {

    public ModItemModelProvider(PackOutput output) {
        super(output, VoidMinersRemastered.MODID);
    }

    @Override
    public String getName() {
        return "Item Model Definitions - " + this.modId;
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return BuiltInRegistries.ITEM.listElements()
                .filter(holder -> holder.getKey().identifier().getNamespace().equals(this.modId))
                .filter(holder -> !(holder.value() instanceof BlockItem));
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        simpleItem(itemModels, ModItems.STRUCTURE_BUILDER);
        simpleItem(itemModels, ModItems.ULTIMATE_STELLAR_CORE);

        simpleItem(itemModels, ModItems.MAX_STORAGE_UPGRADE_T1);
        simpleItem(itemModels, ModItems.MAX_STORAGE_UPGRADE_T2);
        simpleItem(itemModels, ModItems.MAX_STORAGE_UPGRADE_T3);

        for (CrystalSet set : CrystalSet.sets()) {
            if (set.CRYSTAL != null) {
                simpleItem(itemModels, set.CRYSTAL);
            }
        }

        for (SolarSet set : SolarSet.sets()) {
            if (set.CRYSTAL != null) {
                // Solar crystals reuse the plain crystal texture of the same colour.
                simpleItemWithTexture(itemModels, set.CRYSTAL, set.name);
            }
        }
    }

    // ------------------------------------------------------------------------------------------

    /** Old {@code withExistingParent(name, "item/generated").texture("layer0", "<mod>:item/<name>")}. */
    private static void simpleItem(ItemModelGenerators itemModels, DeferredHolder<Item, ? extends Item> item) {
        itemModels.generateFlatItem(item.get(), ModelTemplates.FLAT_ITEM);
    }

    private static void simpleItemWithTexture(ItemModelGenerators itemModels,
                                              DeferredHolder<Item, ? extends Item> item, String textureName) {
        Item value = item.get();
        Material texture = new Material(
                Identifier.fromNamespaceAndPath(VoidMinersRemastered.MODID, "item/" + textureName));
        Identifier model = ModelTemplates.FLAT_ITEM.create(
                ModelLocationUtils.getModelLocation(value), TextureMapping.layer0(texture), itemModels.modelOutput);
        itemModels.itemModelOutput.accept(value, ItemModelUtils.plainModel(model));
    }
}
