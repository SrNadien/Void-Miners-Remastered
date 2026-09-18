package nadiendev.voidminersremastered.world.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class FaceConfiguratorItem extends Item {
    public FaceConfiguratorItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.voidminers.face_configurator"));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
