package nadiendev.voidminersremastered.world.block.entity;

public enum HaltReason {
    // Miner reasons
    NO_RECIPES_IN_DIMENSION("no recipes in this dimension"),
    TOO_MUCH_ITEM_MULTIPLIER("too much item multiplier"),
    NOT_ENOUGH_EMPTY_SLOTS("not enough empty slots in inventory"),
    NO_BEDROCK_OR_VOID_VIEW("no bedrock or void view"),
    NOT_ENOUGH_POWER("insufficient stored energy"),

    // Shared reasons
    NONE("operational"),
    STRUCTURE_NOT_FOUND("multiblock structure not found"),

    // Solar reasons
    NO_SKY_VIEW("no sky view"),
    POWER_FULL("power full"),
    ;

    private final String description;

    HaltReason(String description) {
        this.description = description;
    }

    String description() {
        return description;
    }

    public static HaltReason getHaltReasonFromInt(int index) {
        return HaltReason.values()[index];
    }

    public static int getIntFromHaltReason(HaltReason reason) {
        return reason.ordinal();
    }
}
