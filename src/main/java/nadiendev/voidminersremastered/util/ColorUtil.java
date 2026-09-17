package nadiendev.voidminersremastered.util;

import net.minecraft.network.chat.TextColor;

public class ColorUtil {
    private final String hexColor;
    private final TextColor textColor;
    private final int argb;

    public ColorUtil(String hexColor) {
        this.hexColor = hexColor;

        this.textColor = TextColor.parseColor(hexColor)
                .result()
                .orElse(TextColor.fromRgb(0xFFFFFF));

        this.argb = 0xFF000000 | textColor.getValue();
    }

    public String getHexColor() {
        return hexColor;
    }

    public TextColor getTextColor() {
        return textColor;
    }

    public int getARGB() {
        return argb;
    }

    public static ColorUtil fromHex(String hex) {
        return new ColorUtil(hex);
    }

    public static final ColorUtil RUBETINE_COLOR = ColorUtil.fromHex("#FF0000");
    public static final ColorUtil AURANTIUM_COLOR = ColorUtil.fromHex("#FFAA00");
    public static final ColorUtil CITRINETINE_COLOR = ColorUtil.fromHex("#FFFF00");
    public static final ColorUtil VERDIUM_COLOR = ColorUtil.fromHex("#00FF00");
    public static final ColorUtil AZURINE_COLOR = ColorUtil.fromHex("#00FFFF");
    public static final ColorUtil CAERIUM_COLOR = ColorUtil.fromHex("#0000FF");
    public static final ColorUtil AMETHYSTINE_COLOR = ColorUtil.fromHex("#AA00FF");
    public static final ColorUtil ROSARIUM_COLOR = ColorUtil.fromHex("#FF00FF");
    public static final ColorUtil ULTIMATE_COLOR = ColorUtil.fromHex("#FFD700");

    public static final ColorUtil NULL_COLOR = ColorUtil.fromHex("#FFFFFF");

    public static ColorUtil getColorForCrystal(String name) {
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
            default -> ColorUtil.fromHex("#FFFFFF");
        };
    }

    public static int getARGBForCrystal(String name) {
        return getColorForCrystal(name).getARGB();
    }
}