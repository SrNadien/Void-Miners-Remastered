package nadiendev.voidminersremastered.world.block;

import nadiendev.voidminersremastered.util.ColorUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.block.Block;

public class ColoredBlock extends Block {
    private final ColorUtil color;

    public ColoredBlock(Properties properties, ColorUtil color) {
        super(properties);
        this.color = color;
    }

    @Override
    public MutableComponent getName() {
        return Component.literal(super.getName().getString())
                .setStyle(Style.EMPTY.withColor(color.getTextColor()));
    }

    public ColorUtil getColor() {
        return color;
    }
}