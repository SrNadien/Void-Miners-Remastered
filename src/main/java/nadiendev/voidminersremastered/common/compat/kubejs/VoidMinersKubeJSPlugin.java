package nadiendev.voidminersremastered.common.compat.kubejs;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import dev.latvian.mods.kubejs.plugin.ClassFilter;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import net.minecraft.resources.Identifier;

public class VoidMinersKubeJSPlugin implements KubeJSPlugin {
    
    @Override
    public void registerClasses(ClassFilter filter) {
        filter.allow("nadiendev.voidminersremastered.common.compat.kubejs");
    }
    
    @Override
    public void registerRecipeSchemas(RecipeSchemaRegistry event) {
        event.register(Identifier.fromNamespaceAndPath(VoidMinersRemastered.MODID, "miner"), VoidMinerRecipeSchema.SCHEMA);
    }
}