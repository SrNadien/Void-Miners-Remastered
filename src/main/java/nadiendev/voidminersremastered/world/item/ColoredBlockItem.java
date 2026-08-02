package nadiendev.voidminersremastered.world.item;

import nadiendev.voidminersremastered.util.CustomColorUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class ColoredBlockItem extends BlockItem {
    private final CustomColorUtil color;

    public ColoredBlockItem(Block block, Properties properties, CustomColorUtil color) {
        super(block, properties);
        this.color = color;
    }

    @Override
    public Component getName(ItemStack stack) {
        String name = super.getName(stack).getString();
        return Component.literal(name)
                .setStyle(Style.EMPTY.withColor(color.getTextColor()));
    }
}