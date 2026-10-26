package nadiendev.voidminersremastered.world.multiblock;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public final class MultiBlockManager {
    private final String modId;
    private final Map<ResourceLocation, MultiBlockPattern> patterns = new HashMap<>();

    public MultiBlockManager(String modId) {
        this.modId = modId;
    }

    public MultiBlockPattern register(String name, MultiBlockPattern pattern) {
        patterns.put(ResourceLocation.fromNamespaceAndPath(modId, name), pattern);
        return pattern;
    }

    public MultiBlockPattern getStructure(ResourceLocation id) {
        return patterns.get(id);
    }
}
