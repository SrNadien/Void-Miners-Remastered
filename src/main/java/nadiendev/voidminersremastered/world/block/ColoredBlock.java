package nadiendev.voidminersremastered.world.block;

import nadiendev.voidminersremastered.init.ModRarities;
import nadiendev.voidminersremastered.util.CustomColorUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.block.Block;

public class ColoredBlock extends Block {
    private final CustomColorUtil color;

    public ColoredBlock(Properties properties, CustomColorUtil color) {
        super(properties);
        this.color = color;
    }

    public ColoredBlock(Properties properties) {
        super(properties);
        this.color = ModRarities.NULL_COLOR;
    }

    @Override
    public MutableComponent getName() {
        return Component.literal(super.getName().getString())
                .setStyle(Style.EMPTY.withColor(color.getTextColor()));
    }

    public CustomColorUtil getColor() {
        return color;
    }
}