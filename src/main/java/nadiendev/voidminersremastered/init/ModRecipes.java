package nadiendev.voidminersremastered.init;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.server.recipe.MinerRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, VoidMinersRemastered.MODID);

    /**
     * Recipe types must be registered since 26.1.2: NeoForge's {@code RecipeContentPayload} encodes them
     * with {@code ByteBufCodecs.registry(Registries.RECIPE_TYPE)}, so an unregistered singleton would
     * throw while syncing recipes to the client.
     */
    public static final DeferredRegister<RecipeType<?>> TYPES =
        DeferredRegister.create(Registries.RECIPE_TYPE, VoidMinersRemastered.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MinerRecipe>> MINER_RECIPE_SERIALIZER =
        SERIALIZERS.register("miner", () -> MinerRecipe.SERIALIZER);

    public static final DeferredHolder<RecipeType<?>, RecipeType<MinerRecipe>> MINER_TYPE =
        TYPES.register("miner", () -> MinerRecipe.TYPE);
}
