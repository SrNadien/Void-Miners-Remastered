package nadiendev.voidminersremastered.world.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public final class MultiBlockAislePatternBuilder {
    private static final char CONTROLLER = '*';

    private final List<String[]> aisles = new ArrayList<>();
    private final Map<Character, Predicate<BlockInWorld>> lookup = new HashMap<>();
    private int height;
    private int width;

    private MultiBlockAislePatternBuilder() {
    }

    public static MultiBlockAislePatternBuilder start() {
        return new MultiBlockAislePatternBuilder();
    }

    public MultiBlockAislePatternBuilder aisle(String... rows) {
        if (rows == null || rows.length == 0 || rows[0].isEmpty()) {
            throw new IllegalArgumentException("Empty pattern for aisle");
        }

        if (aisles.isEmpty()) {
            height = rows.length;
            width = rows[0].length();
        } else if (rows.length != height) {
            throw new IllegalArgumentException("Expected aisle with height of " + height + ", but was given one with a height of " + rows.length + ")");
        }

        for (String row : rows) {
            if (row.length() != width) {
                throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + width + ", found one with " + row.length() + ")");
            }
        }

        aisles.add(rows);
        return this;
    }

    public MultiBlockAislePatternBuilder where(char character, Predicate<BlockInWorld> predicate) {
        lookup.put(character, predicate);
        return this;
    }

    public MultiBlockPattern build() {
        List<String[]> ordered = new ArrayList<>(aisles);
        Collections.reverse(ordered);

        int controllerRow = -1;
        int controllerAisle = -1;
        int controllerColumn = -1;

        for (int aisle = 0; aisle < ordered.size() && controllerRow == -1; aisle++) {
            String[] rows = ordered.get(aisle);
            for (int row = 0; row < rows.length && controllerRow == -1; row++) {
                int column = rows[row].indexOf(CONTROLLER);
                if (column != -1) {
                    controllerRow = row;
                    controllerAisle = aisle;
                    controllerColumn = column;
                }
            }
        }

        if (controllerRow == -1) {
            throw new IllegalArgumentException("Failed to build pattern due to having more or less than one \"*\" defined! Have: 0 Expected: 1");
        }

        Map<Character, List<MultiBlockPattern.Offset>> grouped = new LinkedHashMap<>();
        for (int aisle = 0; aisle < ordered.size(); aisle++) {
            String[] rows = ordered.get(aisle);
            for (int row = 0; row < rows.length; row++) {
                for (int column = 0; column < rows[row].length(); column++) {
                    char character = rows[row].charAt(column);
                    if (lookup.containsKey(character)) {
                        grouped.computeIfAbsent(character, key -> new ArrayList<>()).add(new MultiBlockPattern.Offset(
                                character,
                                new BlockPos(row - controllerRow, aisle - controllerAisle, column - controllerColumn)
                        ));
                    }
                }
            }
        }

        if (grouped.get(CONTROLLER).size() != 1) {
            throw new IllegalArgumentException("Failed to build pattern due to having more or less than one \"*\" defined! Have: " + grouped.get(CONTROLLER).size() + " Expected: 1");
        }

        List<MultiBlockPattern.Offset> offsets = new ArrayList<>();
        grouped.values().forEach(offsets::addAll);

        return new MultiBlockPattern(offsets, lookup);
    }
}
