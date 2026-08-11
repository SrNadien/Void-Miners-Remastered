package nadiendev.voidminersremastered.world.multiblock;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.datagen.ModBlockTagGenerator;
import nadiendev.voidminersremastered.init.SolarSet;
import nadiendev.voidminersremastered.init.ModBlocks;
import nadiendev.voidminersremastered.util.MiscUtil;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import nadiendev.mangomultiblock.core.SimpleMultiBlockAislePatternBuilder;
import nadiendev.mangomultiblock.core.manager.MultiBlockManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SolarMultiblocks {

    public static final MultiBlockManager MANAGER = MultiBlockManager.getOrCreate(VoidMinersRemastered.MODID, "voidminers_solars");

    // IMPORTANT: variable to stop duplicate registration
    private static boolean INITIALIZED = false;

    public static final SimpleMultiBlockAislePatternBuilder RUBETINE = createAccessiblePattern(
            VoidMinersRemastered.MODID + ":solar_rubetine",
            List.of(
                    List.of(
                            " FFF ",
                            "FPPPF",
                            "FPPPF",
                            "FPPPF",
                            " FFF "
                    ),
                    List.of(
                            "  F  ",
                            "     ",
                            "F   F",
                            "     ",
                            "  F  "
                    ),
                    List.of(
                            "     ",
                            "  F  ",
                            " F F ",
                            "  F  ",
                            "     "
                    ),
                    List.of(
                            "     ",
                            "     ",
                            "  *  ",
                            "     ",
                            "     "
                    )
            ),
            Map.of(
                    '*', a -> a.getState().is(SolarSet.RUBETINE.CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlocks.GLASS_PANEL.get()),
                    'F', a -> a.getState().is(SolarSet.RUBETINE.FRAME.get())
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', SolarSet.RUBETINE.FRAME.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder AURANTIUM = createAccessiblePattern(
            VoidMinersRemastered.MODID + ":solar_aurantium",
            List.of(
                    List.of(
                            " FFFFF ",
                            "FPPMPPF",
                            "FPPPPPF",
                            "FPPPPPF",
                            "FPPPPPF",
                            "FPPMPPF",
                            " FFFFF "
                    ),
                    List.of(
                            "   F   ",
                            "       ",
                            "       ",
                            "F     F",
                            "       ",
                            "       ",
                            "   F   "
                    ),
                    List.of(
                            "   F   ",
                            "       ",
                            "       ",
                            "F     F",
                            "       ",
                            "       ",
                            "   F   "
                    ),
                    List.of(
                            "       ",
                            "   F   ",
                            "   F   ",
                            " FF FF ",
                            "   F   ",
                            "   F   ",
                            "       "
                    ),
                    List.of(
                            "       ",
                            "       ",
                            "       ",
                            "   *   ",
                            "       ",
                            "       ",
                            "       "
                    )
            ),
            Map.of(
                    '*', a -> a.getState().is(SolarSet.AURANTIUM.CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlocks.GLASS_PANEL.get()),
                    'F', a -> a.getState().is(SolarSet.AURANTIUM.FRAME.get()),
                    'M', a -> a.getState().is(ModBlockTagGenerator.SOLAR_MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', SolarSet.AURANTIUM.FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder CITRINETINE = createAccessiblePattern(
            VoidMinersRemastered.MODID + ":solar_citrinetine",
            List.of(
                    List.of(
                            " FFFFF ",
                            "FMPPPMF",
                            "FPPPPPF",
                            "FPPPPPF",
                            "FPPPPPF",
                            "FMPPPMF",
                            " FFFFF "
                    ),
                    List.of(
                            "   F   ",
                            "       ",
                            "       ",
                            "F     F",
                            "       ",
                            "       ",
                            "   F   "
                    ),
                    List.of(
                            "   F   ",
                            "       ",
                            "       ",
                            "F     F",
                            "       ",
                            "       ",
                            "   F   "
                    ),
                    List.of(
                            "       ",
                            "   F   ",
                            "       ",
                            " F   F ",
                            "       ",
                            "   F   ",
                            "       "
                    ),
                    List.of(
                            "       ",
                            "       ",
                            "   F   ",
                            "  F F  ",
                            "   F   ",
                            "       ",
                            "       "
                    ),
                    List.of(
                            "       ",
                            "       ",
                            "       ",
                            "   *   ",
                            "       ",
                            "       ",
                            "       "
                    )
            ),
            Map.of(
                    '*', a -> a.getState().is(SolarSet.CITRINETINE.CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlocks.GLASS_PANEL.get()),
                    'F', a -> a.getState().is(SolarSet.CITRINETINE.FRAME.get()),
                    'M', a -> a.getState().is(ModBlockTagGenerator.SOLAR_MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', SolarSet.CITRINETINE.FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder VERDIUM = createAccessiblePattern(
            VoidMinersRemastered.MODID + ":solar_verdium",
            List.of(
                    List.of(
                            "  FFFFF  ",
                            " FMPMPMF ",
                            "FPPPPPPPF",
                            "FPPPPPPPF",
                            "FPPPPPPPF",
                            "FPPPPPPPF",
                            "FPPPPPPPF",
                            " FMPMPMF ",
                            "  FFFFF  "
                    ),
                    List.of(
                            "    F    ",
                            "         ",
                            "         ",
                            "         ",
                            "F       F",
                            "         ",
                            "         ",
                            "         ",
                            "    F    "
                    ),
                    List.of(
                            "    F    ",
                            "         ",
                            "         ",
                            "         ",
                            "F       F",
                            "         ",
                            "         ",
                            "         ",
                            "    F    "
                    ),
                    List.of(
                            "         ",
                            "    F    ",
                            "         ",
                            "         ",
                            " F     F ",
                            "         ",
                            "         ",
                            "    F    ",
                            "         "
                    ),
                    List.of(
                            "         ",
                            "         ",
                            "    F    ",
                            "    F    ",
                            "  FF FF  ",
                            "    F    ",
                            "    F    ",
                            "         ",
                            "         "
                    ),
                    List.of(
                            "         ",
                            "         ",
                            "         ",
                            "         ",
                            "    *    ",
                            "         ",
                            "         ",
                            "         ",
                            "         "
                    )
            ),
            Map.of(
                    '*', a -> a.getState().is(SolarSet.VERDIUM.CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlocks.GLASS_PANEL.get()),
                    'F', a -> a.getState().is(SolarSet.VERDIUM.FRAME.get()),
                    'M', a -> a.getState().is(ModBlockTagGenerator.SOLAR_MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', SolarSet.VERDIUM.FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder AZURINE = createAccessiblePattern(
            VoidMinersRemastered.MODID + ":solar_azurine",
            List.of(
                    List.of(
                            "  FFFFF  ",
                            " FMPPPMF ",
                            "FMPPPPPMF",
                            "FPPPPPPPF",
                            "FPPPPPPPF",
                            "FPPPPPPPF",
                            "FMPPPPPMF",
                            " FMPPPMF ",
                            "  FFFFF  "
                    ),
                    List.of(
                            "    F    ",
                            "         ",
                            "         ",
                            "         ",
                            "F       F",
                            "         ",
                            "         ",
                            "         ",
                            "    F    "
                    ),
                    List.of(
                            "    F    ",
                            "         ",
                            "         ",
                            "         ",
                            "F       F",
                            "         ",
                            "         ",
                            "         ",
                            "    F    "
                    ),
                    List.of(
                            "    F    ",
                            "         ",
                            "         ",
                            "         ",
                            "F       F",
                            "         ",
                            "         ",
                            "         ",
                            "    F    "
                    ),
                    List.of(
                            "         ",
                            "    F    ",
                            "    F    ",
                            "    F    ",
                            " FFF FFF ",
                            "    F    ",
                            "    F    ",
                            "    F    ",
                            "         "
                    ),
                    List.of(
                            "         ",
                            "         ",
                            "         ",
                            "         ",
                            "    *    ",
                            "         ",
                            "         ",
                            "         ",
                            "         "
                    )
            ),
            Map.of(
                    '*', a -> a.getState().is(SolarSet.AZURINE.CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlocks.GLASS_PANEL.get()),
                    'F', a -> a.getState().is(SolarSet.AZURINE.FRAME.get()),
                    'M', a -> a.getState().is(ModBlockTagGenerator.SOLAR_MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', SolarSet.AZURINE.FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder CAERIUM = createAccessiblePattern(
            VoidMinersRemastered.MODID + ":solar_caerium",
            List.of(
                    List.of(
                            "  FFFFF  ",
                            " FMMMMMF ",
                            "FPPPPPPPF",
                            "FPPPPPPPF",
                            "FPPPPPPPF",
                            "FPPPPPPPF",
                            "FPPPPPPPF",
                            " FMMMMMF ",
                            "  FFFFF  "
                    ),
                    List.of(
                            "    F    ",
                            "         ",
                            "         ",
                            "         ",
                            "F       F",
                            "         ",
                            "         ",
                            "         ",
                            "    F    "
                    ),
                    List.of(
                            "    F    ",
                            "         ",
                            "         ",
                            "         ",
                            "F       F",
                            "         ",
                            "         ",
                            "         ",
                            "    F    "
                    ),
                    List.of(
                            "         ",
                            "    F    ",
                            "         ",
                            "         ",
                            " F     F ",
                            "         ",
                            "         ",
                            "    F    ",
                            "         "
                    ),
                    List.of(
                            "         ",
                            "         ",
                            "    F    ",
                            "         ",
                            "  F   F  ",
                            "         ",
                            "    F    ",
                            "         ",
                            "         "
                    ),
                    List.of(
                            "         ",
                            "         ",
                            "         ",
                            "    F    ",
                            "   F F   ",
                            "    F    ",
                            "         ",
                            "         ",
                            "         "
                    ),
                    List.of(
                            "         ",
                            "         ",
                            "         ",
                            "         ",
                            "    *    ",
                            "         ",
                            "         ",
                            "         ",
                            "         "
                    )
            ),
            Map.of(
                    '*', a -> a.getState().is(SolarSet.CAERIUM.CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlocks.GLASS_PANEL.get()),
                    'F', a -> a.getState().is(SolarSet.CAERIUM.FRAME.get()),
                    'M', a -> a.getState().is(ModBlockTagGenerator.SOLAR_MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', SolarSet.CAERIUM.FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder AMETHYSTINE = createAccessiblePattern(
            VoidMinersRemastered.MODID + ":solar_amethystine",
            List.of(
                    List.of(
                            "   FFF   ",
                            "  FMMMF  ",
                            " FPPPPPF ",
                            "FMPPPPPMF",
                            "FMPPPPPMF",
                            "FMPPPPPMF",
                            " FPPPPPF ",
                            "  FMMMF  ",
                            "   FFF   "
                    ),
                    List.of(
                            "    F    ",
                            "         ",
                            "         ",
                            "         ",
                            "F       F",
                            "         ",
                            "         ",
                            "         ",
                            "    F    "
                    ),
                    List.of(
                            "    F    ",
                            "         ",
                            "         ",
                            "         ",
                            "F       F",
                            "         ",
                            "         ",
                            "         ",
                            "    F    "
                    ),
                    List.of(
                            "         ",
                            "    F    ",
                            "         ",
                            "         ",
                            " F     F ",
                            "         ",
                            "         ",
                            "    F    ",
                            "         "
                    ),
                    List.of(
                            "         ",
                            "    F    ",
                            "         ",
                            "         ",
                            " F     F ",
                            "         ",
                            "         ",
                            "    F    ",
                            "         "
                    ),
                    List.of(
                            "         ",
                            "         ",
                            "    F    ",
                            "    F    ",
                            "  FF FF  ",
                            "    F    ",
                            "    F    ",
                            "         ",
                            "         "
                    ),
                    List.of(
                            "         ",
                            "         ",
                            "         ",
                            "         ",
                            "    *    ",
                            "         ",
                            "         ",
                            "         ",
                            "         "
                    )
            ),
            Map.of(
                    '*', a -> a.getState().is(SolarSet.AMETHYSTINE.CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlocks.GLASS_PANEL.get()),
                    'F', a -> a.getState().is(SolarSet.AMETHYSTINE.FRAME.get()),
                    'M', a -> a.getState().is(ModBlockTagGenerator.SOLAR_MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', SolarSet.AMETHYSTINE.FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder ROSARIUM = createAccessiblePattern(
            VoidMinersRemastered.MODID + ":solar_rosarium",
            List.of(
                    List.of(
                            " FFFFF ",
                            "FMPMPMF",
                            "FPPPPPF",
                            "FMPPPMF",
                            "FPPPPPF",
                            "FMPMPMF",
                            " FFFFF "
                    ),
                    List.of(
                            "F     F",
                            "       ",
                            "       ",
                            "       ",
                            "       ",
                            "       ",
                            "F     F"
                    ),
                    List.of(
                            " F   F ",
                            "F     F",
                            "       ",
                            "       ",
                            "       ",
                            "F     F",
                            " F   F "
                    ),
                    List.of(
                            "  FFF  ",
                            " FPMPF ",
                            "FPMPMPF",
                            "FMPPPMF",
                            "FPMPMPF",
                            " FPMPF ",
                            "  FFF  "
                    ),
                    List.of(
                            "   F   ",
                            "       ",
                            "       ",
                            "F     F",
                            "       ",
                            "       ",
                            "   F   "
                    ),
                    List.of(
                            "       ",
                            "   F   ",
                            "       ",
                            " F   F ",
                            "       ",
                            "   F   ",
                            "       "
                    ),
                    List.of(
                            "       ",
                            "       ",
                            "   F   ",
                            "  F F  ",
                            "   F   ",
                            "       ",
                            "       "
                    ),
                    List.of(
                            "       ",
                            "       ",
                            "       ",
                            "   *   ",
                            "       ",
                            "       ",
                            "       "
                    )
            ),
            Map.of(
                    '*', a -> a.getState().is(SolarSet.ROSARIUM.CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlocks.GLASS_PANEL.get()),
                    'F', a -> a.getState().is(SolarSet.ROSARIUM.FRAME.get()),
                    'M', a -> a.getState().is(ModBlockTagGenerator.SOLAR_MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', SolarSet.ROSARIUM.FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder ULTIMATE = createAccessiblePattern(
            VoidMinersRemastered.MODID + ":solar_ultimate",
            List.of(
                    List.of(
                            " F       F ",
                            "F         F",
                            "           ",
                            "           ",
                            "           ",
                            "           ",
                            "           ",
                            "           ",
                            "           ",
                            "F         F",
                            " F       F "
                    ),
                    List.of(
                            "   FFFFF   ",
                            "  FMMMMMF  ",
                            " FMPPPPPMF ",
                            "FMPPPPPPPMF",
                            "FMPPPPPPPMF",
                            "FMPPPPPPPMF",
                            "FMPPPPPPPMF",
                            "FMPPPPPPPMF",
                            " FMPPPPPMF ",
                            "  FMMMMMF  ",
                            "   FFFFF   "
                    ),
                    List.of(
                            "     F     ",
                            "           ",
                            "           ",
                            "           ",
                            "           ",
                            "F         F",
                            "           ",
                            "           ",
                            "           ",
                            "           ",
                            "     F     "
                    ),
                    List.of(
                            "     F     ",
                            "           ",
                            "           ",
                            "           ",
                            "           ",
                            "F         F",
                            "           ",
                            "           ",
                            "           ",
                            "           ",
                            "     F     "
                    ),
                    List.of(
                            "     F     ",
                            "           ",
                            "           ",
                            "           ",
                            "           ",
                            "F         F",
                            "           ",
                            "           ",
                            "           ",
                            "           ",
                            "     F     "
                    ),
                    List.of(
                            "           ",
                            "     F     ",
                            "           ",
                            "           ",
                            "           ",
                            " F       F ",
                            "           ",
                            "           ",
                            "           ",
                            "     F     ",
                            "           "
                    ),
                    List.of(
                            "           ",
                            "           ",
                            "     F     ",
                            "           ",
                            "           ",
                            "  F     F  ",
                            "           ",
                            "           ",
                            "     F     ",
                            "           ",
                            "           "
                    ),
                    List.of(
                            "           ",
                            "           ",
                            "           ",
                            "     F     ",
                            "     F     ",
                            "   FF FF   ",
                            "     F     ",
                            "     F     ",
                            "           ",
                            "           ",
                            "           "
                    ),
                    List.of(
                            "           ",
                            "           ",
                            "           ",
                            "           ",
                            "           ",
                            "     *     ",
                            "           ",
                            "           ",
                            "           ",
                            "           ",
                            "           "
                    )
            ),
            Map.of(
                    '*', a -> a.getState().is(SolarSet.ULTIMATE.CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlocks.GLASS_PANEL.get()),
                    'F', a -> a.getState().is(SolarSet.ULTIMATE.FRAME.get()),
                    'M', a -> a.getState().is(ModBlockTagGenerator.SOLAR_MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', SolarSet.ULTIMATE.FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static SimpleMultiBlockAislePatternBuilder createAccessiblePattern(String structure, List<List<String>> stringPattern, Map<Character, Predicate<BlockInWorld>> lookup, Map<Character, Supplier<BlockState>> blockProvider) {
        SimpleMultiBlockAislePatternBuilder pattern = SimpleMultiBlockAislePatternBuilder.start();
        List<List<List<BlockState>>> blocks = new ArrayList<>();

        for (List<String> strings : stringPattern) {
            pattern.aisle(strings.toArray(new String[]{}));

            List<List<BlockState>> blockForAisle = new ArrayList<>();

            for (String s : strings) {
                blockForAisle.add(
                        getStatesForString(s, blockProvider)
                );
            }

            blocks.add(blockForAisle);
        }

        MiscUtil.structureMap.put(
                structure,
                blocks
        );

        lookup.forEach(pattern::where);

        blockProvider.forEach(pattern::block);

        return pattern;
    }

    private static List<BlockState> getStatesForString(String s, Map<Character, Supplier<BlockState>> map) {
        List<BlockState> toReturn = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == ' ') {
                toReturn.add(
                        Blocks.AIR.defaultBlockState()
                );
            } else {
                if (map.containsKey(c)) {
                    toReturn.add(
                            map.get(c).get()
                    );
                }
            }
        }

        return toReturn;
    }


    public static void init() {
        if (INITIALIZED) {
           VoidMinersRemastered.LOGGER.warn("SolarMultiblocks already initialized, skipping...");
            return;
        }

       VoidMinersRemastered.LOGGER.info("Initializing SolarMultiblocks...");

        MANAGER.register("solar_rubetine", RUBETINE.build());
        MANAGER.register("solar_aurantium", AURANTIUM.build());
        MANAGER.register("solar_citrinetine", CITRINETINE.build());
        MANAGER.register("solar_verdium", VERDIUM.build());
        MANAGER.register("solar_azurine", AZURINE.build());
        MANAGER.register("solar_caerium", CAERIUM.build());
        MANAGER.register("solar_amethystine", AMETHYSTINE.build());
        MANAGER.register("solar_rosarium", ROSARIUM.build());
        MANAGER.register("solar_ultimate", ULTIMATE.build());

        INITIALIZED = true;
       VoidMinersRemastered.LOGGER.info("SolarMultiblocks initialized successfully!");
    }
}