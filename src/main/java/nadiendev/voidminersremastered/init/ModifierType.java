package nadiendev.voidminersremastered.init;

public enum ModifierType {
    ENERGY("energy"),
    SPEED("speed"),
    ITEM("item"),
    EFFICIENCY("efficiency"),
    WEATHER("weather"),
    NULL("null");

    public final String type;

    ModifierType(String type) {
        this.type = type;
    }
}
