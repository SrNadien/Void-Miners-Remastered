package nadiendev.voidminersremastered.datagen;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.init.ModBlocks;
import nadiendev.voidminersremastered.init.CrystalSet;
import nadiendev.voidminersremastered.init.SolarSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, VoidMinersRemastered.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(
                        ModBlocks.FRAME_BASE.get(),
                        ModBlocks.STRUCTURE_PANEL.get(),
                        ModBlocks.GLASS_PANEL.get(),
                        ModBlocks.NULL_MOD.get()
                );

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(
                        ModBlocks.FRAME_BASE.get(),
                        ModBlocks.STRUCTURE_PANEL.get(),
                        ModBlocks.GLASS_PANEL.get(),
                        ModBlocks.NULL_MOD.get()
                );

        List<CrystalSet> allCrystalSets = CrystalSet.sets();
        for (CrystalSet set : allCrystalSets) {
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .add(
                            set.CONTROLLER.get(),
                            set.CRYSTAL_BLOCK.get(),
                            set.FRAME.get(),
                            set.ENERGY_MOD.get(),
                            set.SPEED_MOD.get(),
                            set.ITEM_MOD.get()
                    );

            this.tag(BlockTags.NEEDS_STONE_TOOL)
                    .add(
                            set.CONTROLLER.get(),
                            set.CRYSTAL_BLOCK.get(),
                            set.FRAME.get(),
                            set.ENERGY_MOD.get(),
                            set.SPEED_MOD.get(),
                            set.ITEM_MOD.get()
                    );

            this.tag(MINER_MODIFIERS)
                    .add(
                            set.ENERGY_MOD.get(),
                            set.SPEED_MOD.get(),
                            set.ITEM_MOD.get()
                    );
        }

        List<SolarSet> allSolarSets = SolarSet.sets();
        for (SolarSet set : allSolarSets) {
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .add(
                            set.CONTROLLER.get(),
                            set.CRYSTAL_BLOCK.get(),
                            set.FRAME.get(),
                            set.EFFICIENCY_MOD.get(),
                            set.WEATHER_MOD.get()
                    );

            this.tag(BlockTags.NEEDS_STONE_TOOL)
                    .add(
                            set.CONTROLLER.get(),
                            set.CRYSTAL_BLOCK.get(),
                            set.FRAME.get(),
                            set.EFFICIENCY_MOD.get(),
                            set.WEATHER_MOD.get()
                    );

            this.tag(SOLAR_MODIFIERS)
                    .add(
                            set.EFFICIENCY_MOD.get(),
                            set.WEATHER_MOD.get()
                    );
        }

        this.tag(MINER_MODIFIERS).add(ModBlocks.NULL_MOD.get());

        this.tag(SOLAR_MODIFIERS).add(ModBlocks.NULL_MOD.get());
    }

    public static final TagKey<Block> MINER_MODIFIERS = create("miner_modifiers");
    public static final TagKey<Block> SOLAR_MODIFIERS = create("solar_modifiers");

    private static TagKey<Block> create(String pName) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, pName));
    }
}
