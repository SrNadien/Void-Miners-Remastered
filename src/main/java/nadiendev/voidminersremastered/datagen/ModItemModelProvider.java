package nadiendev.voidminersremastered.datagen;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.init.ModItems;
import nadiendev.voidminersremastered.init.CrystalSet;
import nadiendev.voidminersremastered.init.SolarSet;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, VoidMinersRemastered.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.STRUCTURE_BUILDER);
        simpleItem(ModItems.ULTIMATE_STELLAR_CORE);

        simpleItem(ModItems.MAX_STORAGE_UPGRADE_T1);
        simpleItem(ModItems.MAX_STORAGE_UPGRADE_T2);
        simpleItem(ModItems.MAX_STORAGE_UPGRADE_T3);

        for (CrystalSet set : CrystalSet.sets()) {
            if (set.CRYSTAL != null) {
                simpleItem(set.CRYSTAL);
            }
        }

        for (SolarSet set : SolarSet.sets()) {
            if (set.CRYSTAL != null) {
                simpleItemWithTexture(set.CRYSTAL, set.name);
            }
        }
    }

    private void simpleItem(DeferredHolder<Item, ? extends Item> item) {
        simpleItem(item.getId().getPath());
    }

    private void simpleItem(String name) {
        withExistingParent(name,
            ResourceLocation.parse("item/generated")).texture("layer0",
            ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, "item/" + name));
    }

    private void simpleItemWithTexture(DeferredHolder<Item, ? extends Item> item, String textureName) {
        withExistingParent(item.getId().getPath(),
                ResourceLocation.parse("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, "item/" + textureName));
    }
}