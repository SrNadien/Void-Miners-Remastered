package nadiendev.voidminersremastered.world.block;

import nadiendev.voidminersremastered.config.MinerConfigLoader;
import nadiendev.voidminersremastered.config.SolarConfigLoader;
import nadiendev.voidminersremastered.init.ModifierType;
import nadiendev.voidminersremastered.util.CustomColorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ModifierBlock extends ColoredBlock {
    public String name;
    private final ModifierType type;

    public ModifierBlock(Properties pProperties, String name, CustomColorUtil color, ModifierType type) {
        super(pProperties, color);
        this.name = name;
        this.type = type;
    }

    public ModifierBlock(Properties pProperties, String name, ModifierType type) {
        super(pProperties);
        this.name = name;
        this.type = type;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        switch (type) {
            case ENERGY, SPEED, ITEM:
                MinerConfigLoader.ModifierConfig minerConfig = MinerConfigLoader.getInstance().getModifierConfig(this);

                final String energy = String.format(minerConfig.energy() >= 1 ? "+%.0f" : "%.0f", -(1 - minerConfig.energy()) * 100);
                final String speed = String.format(minerConfig.speed() >= 1 ? "+%.0f" : "%.0f", -(1 - minerConfig.speed()) * 100);
                final String item = String.format(minerConfig.item() >= 1 ? "+%.0f" : "%.0f",  -(1 - minerConfig.item()) * 100);

                if(minerConfig.speed() != 1f) {
                    tooltipComponents.add(Component.translatable("tooltip.voidminers.speed",
                            speed).withStyle(ChatFormatting.GREEN));
                }
                if(minerConfig.item() != 1f) {
                    tooltipComponents.add(Component.translatable("tooltip.voidminers.item",
                            item).withStyle(ChatFormatting.AQUA));
                }
                if(minerConfig.energy() != 1f) {
                    tooltipComponents.add(Component.translatable("tooltip.voidminers.energy",
                            energy).withStyle(ChatFormatting.GOLD));
                }
                break;
            case WEATHER, EFFICIENCY:
                SolarConfigLoader.ModifierConfig solarConfig = SolarConfigLoader.getInstance().getModifierConfig(this);
                final String efficiency = String.format(solarConfig.efficiency() >= 1 ? "+%.0f" : "%.0f", -(1 - solarConfig.efficiency()) * 100);
                final String weatherResistance = String.format(solarConfig.weatherResistance() >= 1 ? "+%.0f" : "%.0f", -(1 - solarConfig.weatherResistance()) * 100);

                if(solarConfig.efficiency() != 1f) {
                    tooltipComponents.add(Component.translatable("tooltip.voidminers.efficiency",
                            efficiency).withStyle(ChatFormatting.GREEN));
                }
                if(solarConfig.weatherResistance() != 1f) {
                    tooltipComponents.add(Component.translatable("tooltip.voidminers.weatherResistance",
                            weatherResistance).withStyle(ChatFormatting.AQUA));
                }
        }


        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
