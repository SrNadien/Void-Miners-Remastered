package nadiendev.voidminersremastered.world.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public final class MultiBlockPattern {
    public record Offset(char character, BlockPos pos) {
    }

    private final List<Offset> offsets;
    private final Map<Character, Predicate<BlockInWorld>> predicates;

    MultiBlockPattern(List<Offset> offsets, Map<Character, Predicate<BlockInWorld>> predicates) {
        this.offsets = List.copyOf(offsets);
        this.predicates = Map.copyOf(predicates);
    }

    public MultiBlockMatchResult matchesWithResult(Level level, BlockPos origin, Rotation rotation) {
        List<BlockInWorld> blocks = new ArrayList<>(offsets.size());

        for (Offset offset : offsets) {
            Predicate<BlockInWorld> predicate = predicates.get(offset.character());
            BlockInWorld block = new BlockInWorld(level, origin.offset(offset.pos().rotate(rotation)), false);

            if (predicate == null || !predicate.test(block)) {
                return null;
            }

            blocks.add(block);
        }

        return new MultiBlockMatchResult(List.copyOf(blocks));
    }
}
