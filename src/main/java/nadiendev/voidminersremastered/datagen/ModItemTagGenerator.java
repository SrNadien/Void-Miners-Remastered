package nadiendev.voidminersremastered.datagen;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.init.CrystalSet;
import nadiendev.voidminersremastered.init.SolarSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {
    public ModItemTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, VoidMinersRemastered.MODID);
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
                this.tag(common("gems/solar_" + set.name)).add(set.CRYSTAL.get());
            }
        }
    }

    private static TagKey<Item> common(String path) {
        return ItemTags.create(Identifier.fromNamespaceAndPath("c", path));
    }
}
