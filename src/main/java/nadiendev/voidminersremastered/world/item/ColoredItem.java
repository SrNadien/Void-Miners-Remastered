package nadiendev.voidminersremastered.world.item;

import nadiendev.voidminersremastered.util.ColorUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class ColoredItem extends Item {
    private final ColorUtil color;

    public ColoredItem(Properties properties, ColorUtil color) {
        super(properties);
        this.color = color;
    }

    @Override
    public Component getName(ItemStack stack) {
        String name = super.getName(stack).getString();
        return Component.literal(name)
                .setStyle(Style.EMPTY.withColor(color.getTextColor()));
    }
}