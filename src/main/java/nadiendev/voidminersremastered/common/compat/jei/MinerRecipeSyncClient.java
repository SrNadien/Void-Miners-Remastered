package nadiendev.voidminersremastered.common.compat.jei;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.server.recipe.MinerRecipe;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

import java.util.List;

/**
 * Client half of {@link MinerRecipeSync}. Kept in its own {@code Dist.CLIENT} class because
 * {@code RecipesReceivedEvent} / {@code ClientPlayerNetworkEvent} only exist on the physical client.
 */
@EventBusSubscriber(modid = VoidMinersRemastered.MODID, value = Dist.CLIENT)
public final class MinerRecipeSyncClient {

    private MinerRecipeSyncClient() {}

    /**
     * Caches the miner recipes the server mirrored to us.
     *
     * <p>{@link EventPriority#HIGHEST} is deliberate: JEI restarts itself from this very event
     * (its {@code mezz.jei.neoforge.startup.StartEventObserver} lists
     * {@code net.neoforged.neoforge.client.event.RecipesReceivedEvent} among its required start events,
     * verified by disassembling {@code jei-26.1.2-neoforge}). JEI's plugin reload — and therefore
     * {@link JeiPlugin#registerRecipes} — can run synchronously inside this same dispatch, so the cache has
     * to be filled before JEI's own listener gets a turn.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRecipesReceived(RecipesReceivedEvent event) {
        List<MinerRecipe> recipes = event.getRecipeMap().byType(MinerRecipe.TYPE).stream()
                .map(holder -> {
                    MinerRecipe recipe = holder.value();
                    // RecipeHolder#id() is a ResourceKey<Recipe<?>> in 26.1.2, not an Identifier.
                    recipe.setId(holder.id());
                    return recipe;
                })
                .toList();

        MinerRecipeSync.setClientRecipes(recipes);

        if (recipes.isEmpty() && !event.getRecipeTypes().contains(MinerRecipe.TYPE)) {
            VoidMinersRemastered.LOGGER.warn(
                    "The server did not mirror any '{}' recipes; the JEI miner categories will be empty.",
                    MinerRecipe.TYPE);
        }
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        MinerRecipeSync.clearClientRecipes();
    }
}
