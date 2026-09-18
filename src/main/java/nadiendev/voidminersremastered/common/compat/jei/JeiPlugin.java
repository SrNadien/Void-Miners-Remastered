package nadiendev.voidminersremastered.common.compat.jei;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.init.CrystalSet;
import nadiendev.voidminersremastered.server.recipe.MinerRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {
    List<MinerCategory> tiers = new ArrayList<>();

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(VoidMinersRemastered.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        List<CrystalSet> sets = CrystalSet.sets();
        tiers = new ArrayList<>();
        for (int i = 0; i < sets.size(); i++) {
            CrystalSet set = sets.get(i);
            tiers.add(
                new MinerCategory(
                    registration.getJeiHelpers().getGuiHelper(),
                    set.CONTROLLER.get(),
                    i + 1
                )
            );
        }

        registration.addRecipeCategories(
            tiers.toArray(new MinerCategory[]{})
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // 26.1.2: there is no client-side recipe manager any more (Level#getRecipeManager and
        // RecipeManager#getAllRecipesFor were both removed). The miner recipes reach the client through
        // NeoForge's opt-in recipe sync; see MinerRecipeSync / MinerRecipeSyncClient.
        List<MinerRecipe> minerRecipes = MinerRecipeSync.clientRecipes();

        if (minerRecipes.isEmpty()) {
            VoidMinersRemastered.LOGGER.warn(
                    "No miner recipes were mirrored to the client, JEI miner categories will be empty.");
        }

        for (int i = 0; i < tiers.size(); i++) {
            addRecipeToTier(i, minerRecipes, registration);
        }
    }

    public void addRecipeToTier(int tier, List<MinerRecipe> recipes, IRecipeRegistration registration) {

        Comparator<MinerRecipe> alphabetical = Comparator.comparing(MinerRecipe::dimension);
        Comparator<MinerRecipe> weight = Comparator.comparing((recipe) -> recipe.output().weight);
        weight = weight.reversed();

        List<MinerRecipe> foundRecipes = recipes.stream().filter(
            recipe -> {
                if(recipe.allowHigherTiers()){
                    return recipe.minTier() <= tier + 1;
                } else {
                    return recipe.minTier() == tier + 1;
                }
            }
        ).sorted(
            alphabetical.thenComparing(weight))
            .toList();

        registration.addRecipes(
            tiers.get(tier).getRecipeType(),
            foundRecipes
        );
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        List<CrystalSet> allSets = CrystalSet.sets();
        for (int i = 0; i < allSets.size(); i++) {
            CrystalSet set = allSets.get(i);
            registration.addRecipeCatalyst(
                set.CONTROLLER.get().asItem().getDefaultInstance(),
                tiers.get(i).getRecipeType()
            );
        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
    }
}
