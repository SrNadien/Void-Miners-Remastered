package nadiendev.voidminersremastered.datagen;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.init.ModBlocks;
import nadiendev.voidminersremastered.init.CrystalSet;
import nadiendev.voidminersremastered.init.SolarSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

        for (CrystalSet set : CrystalSet.sets()) {
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

            if (set.CRYSTAL != null) {
                this.tag(common("storage_blocks/" + set.name + "_block")).add(set.CRYSTAL_BLOCK.get());
            }
        }

        for (SolarSet set : SolarSet.sets()) {
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

            if (set.CRYSTAL != null) {
                this.tag(common("storage_blocks/" + "solar_" + set.name + "_block")).add(set.CRYSTAL_BLOCK.get());
            }
        }

        this.tag(MINER_MODIFIERS).add(ModBlocks.NULL_MOD.get());

        this.tag(SOLAR_MODIFIERS).add(ModBlocks.NULL_MOD.get());
    }

    public static final TagKey<Block> MINER_MODIFIERS = voidminers("miner_modifiers");
    public static final TagKey<Block> SOLAR_MODIFIERS = voidminers("solar_modifiers");

    private static TagKey<Block> common(String path) {
        return tag("c", path);
    }

    private static TagKey<Block> voidminers(String path) {
        return tag(VoidMinersRemastered.MODID, path);
    }

    private static TagKey<Block> tag(String namespace, String name) {
        return BlockTags.create(ResourceLocation.fromNamespaceAndPath(namespace, name));
    }
}
