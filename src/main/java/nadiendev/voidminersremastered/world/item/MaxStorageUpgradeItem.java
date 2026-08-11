package nadiendev.voidminersremastered.world.item;

import nadiendev.voidminersremastered.init.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class MaxStorageUpgradeItem extends Item {
    private final int addedSlots;
    public MaxStorageUpgradeItem(Integer addedSlots, Properties pProperties) {
        super(pProperties.component(ModDataComponents.MAX_STORAGE_UPGRADE_SLOTS, addedSlots));
        this.addedSlots = addedSlots;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        tooltipAdder.accept(Component.translatable("tooltip.voidminersremastered.max_storage_upgrades", this.addedSlots));

        super.appendHoverText(stack, context, display, tooltipAdder, tooltipFlag);
    }
}
