package nadiendev.voidminersremastered.world.item;

import nadiendev.voidminersremastered.init.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class MaxStorageUpgradeItem extends Item {
    private final int addedSlots;
    public MaxStorageUpgradeItem(Integer addedSlots, Properties pProperties) {
        super(pProperties.component(ModDataComponents.MAX_STORAGE_UPGRADE_SLOTS, addedSlots));
        this.addedSlots = addedSlots;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.voidminers.max_storage_upgrades", this.addedSlots));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
