package nadiendev.voidminersremastered.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.init.CrystalSet;
import nadiendev.voidminersremastered.init.SolarSet;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {
    public ModItemTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, BlockTagsProvider blocks, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blocks.contentsGetter(),VoidMinersRemastered.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        for (CrystalSet set : CrystalSet.sets()) {
            if (set.CRYSTAL != null) {
                this.tag(common("gems/" + set.name)).add(set.CRYSTAL.get());
            }
        }

        for (SolarSet set : SolarSet.sets()) {
            if (set.CRYSTAL != null) {
                this.tag(common("gems/" + "solar_" + set.name)).add(set.CRYSTAL.get());
            }
        }
    }

    private static TagKey<Item> common(String path) {
        return tag("c", path);
    }

    private static TagKey<Item> voidminers(String path) {
        return tag(VoidMinersRemastered.MODID, path);
    }

    private static TagKey<Item> tag(String namespace, String name) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(namespace, name));
    }
}
