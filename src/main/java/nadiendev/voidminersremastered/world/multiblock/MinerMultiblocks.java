package nadiendev.voidminersremastered.world.multiblock;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.datagen.ModBlockTagGenerator;
import nadiendev.voidminersremastered.init.ModBlocks;
import nadiendev.voidminersremastered.init.CrystalSet;
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

public class MinerMultiblocks {

    public static final MultiBlockManager MANAGER = MultiBlockManager.getOrCreate(VoidMinersRemastered.MODID, "voidminers_miners");

    // IMPORTANT: variable to stop duplicate registration
    private static boolean INITIALIZED = false;

    public static final SimpleMultiBlockAislePatternBuilder RUBETINE = createAccessiblePattern(
            VoidMinersRemastered.MODID + ":miner_rubetine",
            List.of(
                    List.of(
                            "     ",
                            "     ",
                            "  *  ",
                            "     ",
                            "     "
                    ),
                    List.of(
                            "     ",
                            "  F  ",
                            " F F ",
                            "  F  ",
                            "     "
                    ),
                    List.of(
                            "  F  ",
                            "     ",
                            "F   F",
                            "     ",
                            "  F  "
                    ),
                    List.of(
                            " FFF ",
                            "FPPPF",
                            "FPPPF",
                            "FPPPF",
                            " FFF "
                    )
            ),
            Map.of(
                    '*', a -> a.getState().is(CrystalSet.RUBETINE.CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlocks.GLASS_PANEL.get()),
                    'F', a -> a.getState().is(CrystalSet.RUBETINE.FRAME.get())
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', CrystalSet.RUBETINE.FRAME.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder AURANTIUM = createAccessiblePattern(
            VoidMinersRemastered.MODID + ":miner_aurantium",
            List.of(
                    List.of(
                            "       ",
                            "       ",
                            "       ",
                            "   *   ",
                            "       ",
                            "       ",
                            "       "
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
                            " FFFFF ",
                            "FPPMPPF",
                            "FPPPPPF",
                            "FPPPPPF",
                            "FPPPPPF",
                            "FPPMPPF",
                            " FFFFF "
                    )
            ),
            Map.of(
                    '*', a -> a.getState().is(CrystalSet.AURANTIUM.CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlocks.GLASS_PANEL.get()),
                    'F', a -> a.getState().is(CrystalSet.AURANTIUM.FRAME.get()),
                    'M', a -> a.getState().is(ModBlockTagGenerator.MINER_MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', CrystalSet.AURANTIUM.FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder CITRINETINE = createAccessiblePattern(
            VoidMinersRemastered.MODID + ":miner_citrinetine",
            List.of(
                    List.of(
                            "       ",
                            "       ",
                            "       ",
                            "   *   ",
                            "       ",
                            "       ",
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
                            "   F   ",
                            "       ",
                            " F   F ",
                            "       ",
                            "   F   ",
                            "       "
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
                            " FFFFF ",
                            "FMPPPMF",
                            "FPPPPPF",
                            "FPPPPPF",
                            "FPPPPPF",
                            "FMPPPMF",
                            " FFFFF "
                    )
            ),
            Map.of(
                    '*', a -> a.getState().is(CrystalSet.CITRINETINE.CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlocks.GLASS_PANEL.get()),
                    'F', a -> a.getState().is(CrystalSet.CITRINETINE.FRAME.get()),
                    'M', a -> a.getState().is(ModBlockTagGenerator.MINER_MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', CrystalSet.CITRINETINE.FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder VERDIUM = createAccessiblePattern(
            VoidMinersRemastered.MODID + ":miner_verdium",
            List.of(
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
                            "  FFFFF  ",
                            " FMPMPMF ",
                            "FPPPPPPPF",
                            "FPPPPPPPF",
                            "FPPPPPPPF",
                            "FPPPPPPPF",
                            "FPPPPPPPF",
                            " FMPMPMF ",
                            "  FFFFF  "
                    )
            ),
            Map.of(
                    '*', a -> a.getState().is(CrystalSet.VERDIUM.CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlocks.GLASS_PANEL.get()),
                    'F', a -> a.getState().is(CrystalSet.VERDIUM.FRAME.get()),
                    'M', a -> a.getState().is(ModBlockTagGenerator.MINER_MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', CrystalSet.VERDIUM.FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder AZURINE = createAccessiblePattern(
            VoidMinersRemastered.MODID + ":miner_azurine",
            List.of(
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
                            "  FFFFF  ",
                            " FMPPPMF ",
                            "FMPPPPPMF",
                            "FPPPPPPPF",
                            "FPPPPPPPF",
                            "FPPPPPPPF",
                            "FMPPPPPMF",
                            " FMPPPMF ",
                            "  FFFFF  "
                    )
            ),
            Map.of(
                    '*', a -> a.getState().is(CrystalSet.AZURINE.CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlocks.GLASS_PANEL.get()),
                    'F', a -> a.getState().is(CrystalSet.AZURINE.FRAME.get()),
                    'M', a -> a.getState().is(ModBlockTagGenerator.MINER_MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', CrystalSet.AZURINE.FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder CAERIUM = createAccessiblePattern(
            VoidMinersRemastered.MODID + ":miner_caerium",
            List.of(
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
                            "  FFFFF  ",
                            " FMMMMMF ",
                            "FPPPPPPPF",
                            "FPPPPPPPF",
                            "FPPPPPPPF",
                            "FPPPPPPPF",
                            "FPPPPPPPF",
                            " FMMMMMF ",
                            "  FFFFF  "
                    )
            ),
            Map.of(
                    '*', a -> a.getState().is(CrystalSet.CAERIUM.CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlocks.GLASS_PANEL.get()),
                    'F', a -> a.getState().is(CrystalSet.CAERIUM.FRAME.get()),
                    'M', a -> a.getState().is(ModBlockTagGenerator.MINER_MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', CrystalSet.CAERIUM.FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder AMETHYSTINE = createAccessiblePattern(
            VoidMinersRemastered.MODID + ":miner_amethystine",
            List.of(
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
                            "   FFF   ",
                            "  FMMMF  ",
                            " FPPPPPF ",
                            "FMPPPPPMF",
                            "FMPPPPPMF",
                            "FMPPPPPMF",
                            " FPPPPPF ",
                            "  FMMMF  ",
                            "   FFF   "
                    )
            ),
            Map.of(
                    '*', a -> a.getState().is(CrystalSet.AMETHYSTINE.CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlocks.GLASS_PANEL.get()),
                    'F', a -> a.getState().is(CrystalSet.AMETHYSTINE.FRAME.get()),
                    'M', a -> a.getState().is(ModBlockTagGenerator.MINER_MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', CrystalSet.AMETHYSTINE.FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder ROSARIUM = createAccessiblePattern(
            VoidMinersRemastered.MODID + ":miner_rosarium",
            List.of(
                    List.of(
                            "       ",
                            "       ",
                            "       ",
                            "   *   ",
                            "       ",
                            "       ",
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
                            "   F   ",
                            "       ",
                            " F   F ",
                            "       ",
                            "   F   ",
                            "       "
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
                            "  FFF  ",
                            " FPMPF ",
                            "FPMPMPF",
                            "FMPPPMF",
                            "FPMPMPF",
                            " FPMPF ",
                            "  FFF  "
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
                            "F     F",
                            "       ",
                            "       ",
                            "       ",
                            "       ",
                            "       ",
                            "F     F"
                    ),
                    List.of(
                            " FFFFF ",
                            "FMPMPMF",
                            "FPPPPPF",
                            "FMPPPMF",
                            "FPPPPPF",
                            "FMPMPMF",
                            " FFFFF "
                    )
            ),
            Map.of(
                    '*', a -> a.getState().is(CrystalSet.ROSARIUM.CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlocks.GLASS_PANEL.get()),
                    'F', a -> a.getState().is(CrystalSet.ROSARIUM.FRAME.get()),
                    'M', a -> a.getState().is(ModBlockTagGenerator.MINER_MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', CrystalSet.ROSARIUM.FRAME.get()::defaultBlockState,
                    'M', ModBlocks.NULL_MOD.get()::defaultBlockState
            )
    );

    public static final SimpleMultiBlockAislePatternBuilder ULTIMATE = createAccessiblePattern(
            VoidMinersRemastered.MODID + ":miner_ultimate",
            List.of(
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
                    )
            ),
            Map.of(
                    '*', a -> a.getState().is(CrystalSet.ULTIMATE.CONTROLLER.get()),
                    'P', a -> a.getState().is(ModBlocks.GLASS_PANEL.get()),
                    'F', a -> a.getState().is(CrystalSet.ULTIMATE.FRAME.get()),
                    'M', a -> a.getState().is(ModBlockTagGenerator.MINER_MODIFIERS)
            ),
            Map.of(
                    'P', ModBlocks.GLASS_PANEL.get()::defaultBlockState,
                    'F', CrystalSet.ULTIMATE.FRAME.get()::defaultBlockState,
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
           VoidMinersRemastered.LOGGER.warn("MinerMultiblocks already initialized, skipping...");
            return;
        }

       VoidMinersRemastered.LOGGER.info("Initializing MinerMultiblocks...");

        MANAGER.register("miner_rubetine", RUBETINE.build());
        MANAGER.register("miner_aurantium", AURANTIUM.build());
        MANAGER.register("miner_citrinetine", CITRINETINE.build());
        MANAGER.register("miner_verdium", VERDIUM.build());
        MANAGER.register("miner_azurine", AZURINE.build());
        MANAGER.register("miner_caerium", CAERIUM.build());
        MANAGER.register("miner_amethystine", AMETHYSTINE.build());
        MANAGER.register("miner_rosarium", ROSARIUM.build());
        MANAGER.register("miner_ultimate", ULTIMATE.build());

        INITIALIZED = true;
       VoidMinersRemastered.LOGGER.info("MinerMultiblocks initialized successfully!");
    }
}