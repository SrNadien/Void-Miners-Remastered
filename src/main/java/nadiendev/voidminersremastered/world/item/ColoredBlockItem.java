package nadiendev.voidminersremastered.world.item;

import nadiendev.voidminersremastered.util.CustomColorUtil;
import nadiendev.voidminersremastered.world.block.ModifierBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class ColoredBlockItem extends BlockItem {
    private final CustomColorUtil color;

    public ColoredBlockItem(Block block, Properties properties, CustomColorUtil color) {
        super(block, properties);
        this.color = color;
    }

    // Block#appendHoverText no longer exists in 26.1.2, so the modifier stats tooltip is
    // forwarded from here -- this is the item actually registered for those blocks.
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        if (getBlock() instanceof ModifierBlock modifierBlock) {
            modifierBlock.appendModifierTooltip(tooltipAdder);
        }

        super.appendHoverText(stack, context, display, tooltipAdder, tooltipFlag);
    }

    @Override
    public Component getName(ItemStack stack) {
        String name = super.getName(stack).getString();
        return Component.literal(name)
                .setStyle(Style.EMPTY.withColor(color.getTextColor()));
    }
}