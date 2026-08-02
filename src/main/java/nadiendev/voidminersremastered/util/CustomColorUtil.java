package nadiendev.voidminersremastered.util;

import net.minecraft.network.chat.TextColor;

public class CustomColorUtil {
    private final String hexColor;
    private final TextColor textColor;

    public CustomColorUtil(String hexColor) {
        this.hexColor = hexColor;

        this.textColor = TextColor.parseColor(hexColor)
                .result()
                .orElse(TextColor.fromRgb(0xFFFFFF));
    }

    public String getHexColor() {
        return hexColor;
    }

    public TextColor getTextColor() {
        return textColor;
    }

    public static CustomColorUtil fromHex(String hex) {
        return new CustomColorUtil(hex);
    }
}