package nadiendev.voidminersremastered.common.compat.jei;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.server.recipe.MinerRecipe;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.List;

/**
 * Client-side mirror of the {@link MinerRecipe} list.
 *
 * <p>Rationale: in 26.1.2 the client no longer holds the full recipe list. {@code Level#getRecipeManager()}
 * was removed ({@code Level} now only exposes {@code recipeAccess()} returning a {@code RecipeAccess}, which
 * has no generic queries), and {@code ClientLevel#recipeAccess()} is backed by a container that only knows
 * about the few recipe kinds vanilla still ships to the client. {@code RecipeManager#getAllRecipesFor(...)}
 * is gone as well.
 *
 * <p>NeoForge replaces it with an explicit opt-in: the server declares which recipe types it wants mirrored
 * through {@code OnDatapackSyncEvent#sendRecipes(RecipeType...)} (verified in
 * {@code net/neoforged/neoforge/event/OnDatapackSyncEvent.java:69}), NeoForge then ships them in its
 * {@code neoforge:recipe_content} payload and re-posts them on the client as
 * {@code RecipesReceivedEvent} (fired from {@code ClientPayloadHandler#handle(RecipeContentPayload, ...)},
 * {@code nf-sources/.../client/network/ClientPayloadHandler.java:185-188}).
 *
 * <p>This class holds the server half plus the cache; {@link MinerRecipeSyncClient} holds the client half.
 * It intentionally has <b>no</b> JEI imports so it loads (and syncs) even when JEI is absent — it merely
 * lives next to the JEI plugin because that is the only consumer today.
 */
@EventBusSubscriber(modid = VoidMinersRemastered.MODID)
public final class MinerRecipeSync {

    private static volatile List<MinerRecipe> clientRecipes = List.of();

    private MinerRecipeSync() {}

    /**
     * Server side. Fires on player join and on {@code /reload}, before recipes are sent to the client.
     * Asking for {@link MinerRecipe#TYPE} here is what makes {@code RecipesReceivedEvent} carry our recipes
     * at all; without it the client receives an empty map and the JEI category would show up empty.
     */
    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        event.sendRecipes(MinerRecipe.TYPE);
    }

    /**
     * The miner recipes the server mirrored to this client, in load order.
     * Empty while not in a world, on a non-NeoForge server, or before the sync packet arrives.
     */
    public static List<MinerRecipe> clientRecipes() {
        return clientRecipes;
    }

    static void setClientRecipes(List<MinerRecipe> recipes) {
        clientRecipes = List.copyOf(recipes);
    }

    static void clearClientRecipes() {
        clientRecipes = List.of();
    }
}
