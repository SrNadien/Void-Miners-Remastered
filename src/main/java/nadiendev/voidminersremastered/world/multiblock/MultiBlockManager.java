package nadiendev.voidminersremastered.world.multiblock;

import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public final class MultiBlockManager {
    private final String modId;
    private final Map<Identifier, MultiBlockPattern> patterns = new HashMap<>();

    public MultiBlockManager(String modId) {
        this.modId = modId;
    }

    public MultiBlockPattern register(String name, MultiBlockPattern pattern) {
        patterns.put(Identifier.fromNamespaceAndPath(modId, name), pattern);
        return pattern;
    }

    public MultiBlockPattern getStructure(Identifier id) {
        return patterns.get(id);
    }
}
