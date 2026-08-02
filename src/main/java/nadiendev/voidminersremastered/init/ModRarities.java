package nadiendev.voidminersremastered.init;

import nadiendev.voidminersremastered.util.CustomColorUtil;
import net.minecraft.world.item.Rarity;

public class ModRarities {
    public static final Rarity RUBETINE = Rarity.COMMON;
    public static final Rarity AURANTIUM = Rarity.COMMON;
    public static final Rarity CITRINETINE = Rarity.COMMON;
    public static final Rarity VERDIUM = Rarity.UNCOMMON;
    public static final Rarity AZURINE = Rarity.UNCOMMON;
    public static final Rarity CAERIUM = Rarity.RARE;
    public static final Rarity AMETHYSTINE = Rarity.RARE;
    public static final Rarity ROSARIUM = Rarity.EPIC;
    public static final Rarity ULTIMATE = Rarity.EPIC;

    public static final CustomColorUtil RUBETINE_COLOR = CustomColorUtil.fromHex("#FF0000");
    public static final CustomColorUtil AURANTIUM_COLOR = CustomColorUtil.fromHex("#FFAA00");
    public static final CustomColorUtil CITRINETINE_COLOR = CustomColorUtil.fromHex("#FFFF00");
    public static final CustomColorUtil VERDIUM_COLOR = CustomColorUtil.fromHex("#00FF00");
    public static final CustomColorUtil AZURINE_COLOR = CustomColorUtil.fromHex("#00FFFF");
    public static final CustomColorUtil CAERIUM_COLOR = CustomColorUtil.fromHex("#0000FF");
    public static final CustomColorUtil AMETHYSTINE_COLOR = CustomColorUtil.fromHex("#AA00FF");
    public static final CustomColorUtil ROSARIUM_COLOR = CustomColorUtil.fromHex("#FF00FF");
    public static final CustomColorUtil ULTIMATE_COLOR = CustomColorUtil.fromHex("#FFD700");

    public static final CustomColorUtil NULL_COLOR = CustomColorUtil.fromHex("#FFFFFF");

    public static CustomColorUtil getColorForCrystal(String name) {
        return switch (name.toLowerCase()) {
            case "rubetine" -> RUBETINE_COLOR;
            case "aurantium" -> AURANTIUM_COLOR;
            case "citrinetine" -> CITRINETINE_COLOR;
            case "verdium" -> VERDIUM_COLOR;
            case "azurine" -> AZURINE_COLOR;
            case "caerium" -> CAERIUM_COLOR;
            case "amethystine" -> AMETHYSTINE_COLOR;
            case "rosarium" -> ROSARIUM_COLOR;
            case "ultimate" -> ULTIMATE_COLOR;
            default -> CustomColorUtil.fromHex("#FFFFFF");
        };
    }

    public static void init() {}
}