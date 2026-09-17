package nadiendev.voidminersremastered.server.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public final class BlockRequirement {
    private final String raw;
    private final Identifier blockId;
    private final TagKey<Block> tag;

    private BlockRequirement(String raw, Identifier blockId, TagKey<Block> tag) {
        this.raw = raw;
        this.blockId = blockId;
        this.tag = tag;
    }

    public static BlockRequirement parse(String raw) {
        if (raw == null || raw.isEmpty()) {
            throw new IllegalArgumentException("blockUnderneath must not be empty");
        }

        if (raw.startsWith("#")) {
            Identifier tagId = Identifier.parse(raw.substring(1));
            return new BlockRequirement(raw, null, TagKey.create(Registries.BLOCK, tagId));
        }

        return new BlockRequirement(raw, Identifier.parse(raw), null);
    }

    public String raw() {
        return raw;
    }

    public boolean isTag() {
        return tag != null;
    }

    public boolean matches(BlockState state) {
        if (tag != null) {
            return state.is(tag);
        }
        return BuiltInRegistries.BLOCK.getKey(state.getBlock()).equals(blockId);
    }

    public List<Block> resolveBlocks() {
        List<Block> blocks = new ArrayList<>();
        if (tag != null) {
            BuiltInRegistries.BLOCK.getTagOrEmpty(tag).forEach(holder -> blocks.add(holder.value()));
        } else if (blockId != null) {
            BuiltInRegistries.BLOCK.getOptional(blockId).ifPresent(blocks::add);
        }
        return blocks;
    }

    @Override
    public String toString() {
        return raw;
    }
}
