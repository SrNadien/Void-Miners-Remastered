package nadiendev.voidminersremastered.util;

public final class EnergyFormatUtil {
    private static final String[] UNITS = {"FE", "KFE", "MFE", "GFE", "TFE", "PFE", "EFE"};

    private EnergyFormatUtil() {}

    public static String format(long value) {
        if (value < 1000) {
            return value + " FE";
        }

        double scaled = value;
        int unitIndex = 0;

        while (scaled >= 1000.0 && unitIndex < UNITS.length - 1) {
            scaled /= 1000.0;
            unitIndex++;
        }

        double rounded = Math.round(scaled * 100.0) / 100.0;
        if (rounded >= 1000.0 && unitIndex < UNITS.length - 1) {
            scaled = rounded / 1000.0;
            unitIndex++;
        } else {
            scaled = rounded;
        }

        return String.format("%.2f %s", scaled, UNITS[unitIndex]);
    }
}
