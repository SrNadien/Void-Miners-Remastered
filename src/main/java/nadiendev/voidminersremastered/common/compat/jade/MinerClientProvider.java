package nadiendev.voidminersremastered.common.compat.jade;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.util.MiscUtil;
import nadiendev.voidminersremastered.world.block.entity.MinerControllerBE;
import nadiendev.voidminersremastered.world.block.entity.HaltReason;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

// Jade forbids one class implementing both IServerDataProvider and IBlockComponentProvider
// since Minecraft 1.21.6, so the server-data and client-tooltip halves live in separate classes.
public enum MinerClientProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof MinerControllerBE)) {
            return;
        }

        CompoundTag serverData = accessor.getServerData();
        
        if (serverData.contains("Tier")) {
            int tier = serverData.getIntOr("Tier", 0);
            tooltip.add(Component.translatable("jade.voidminersremastered.tier", tier));
        }

        if (serverData.contains("Energy") && serverData.contains("MaxEnergy")) {
            int energy = serverData.getIntOr("Energy", 0);
            int maxEnergy = serverData.getIntOr("MaxEnergy", 0);
            tooltip.add(Component.translatable("jade.voidminersremastered.energy",
                String.format("%,d", energy), 
                String.format("%,d", maxEnergy)));
        }

        if (serverData.contains("MaxStorageUpgradeItem")) {
            Identifier itemId = Identifier.tryParse(serverData.getStringOr("MaxStorageUpgradeItem", ""));
            if (itemId != null && BuiltInRegistries.ITEM.getValue(itemId) != Items.AIR) {
                // Item#getDescription() is gone in 26.1.2; build the component from its description id.
                tooltip.add(Component.translatable("jade.voidminersremastered.storage_upgrade",
                        Component.translatable(BuiltInRegistries.ITEM.getValue(itemId).getDescriptionId())));
            }
        }

        if (serverData.contains("HaltReason")) {
            int haltReasonInt = serverData.getIntOr("HaltReason", 0);
            HaltReason haltReason = HaltReason.getHaltReasonFromInt(haltReasonInt);
            Component reason;

            if (haltReason != HaltReason.NONE) {
                tooltip.add(Component.translatable("jade.voidminersremastered.status.idle"));

                switch (haltReason) {
                    case NO_RECIPES_IN_DIMENSION -> reason = Component.translatable("jade.voidminersremastered.halt_reason.no_recipes_in_dimension");
                    case STRUCTURE_NOT_FOUND -> reason = Component.translatable("jade.voidminersremastered.halt_reason.structure_not_found");
                    case TOO_MUCH_ITEM_MULTIPLIER -> reason = Component.translatable("jade.voidminersremastered.halt_reason.too_much_item_multiplier");
                    case NOT_ENOUGH_EMPTY_SLOTS -> reason = Component.translatable("jade.voidminersremastered.halt_reason.not_enough_empty_slots");
                    case NO_BEDROCK_OR_VOID_VIEW -> reason = Component.translatable("jade.voidminersremastered.halt_reason.no_bedrock_or_void_view");
                    case NOT_ENOUGH_POWER -> reason = Component.translatable("jade.voidminersremastered.halt_reason.not_enough_power");
                    default -> reason = Component.translatable("jade.voidminersremastered.halt_reason.halt_reason_not_found");
                }
            } else {
                reason = Component.translatable("jade.voidminersremastered.status.working");
            }

            tooltip.add(reason);
        }

        if (serverData.contains("Progress") && serverData.contains("MaxProgress")) {
            int progress = serverData.getIntOr("Progress", 0);
            int maxProgress = serverData.getIntOr("MaxProgress", 0);
            
            if (maxProgress > 0) {
                int percentage = (int) ((progress / (float) maxProgress) * 100);
                tooltip.add(Component.translatable("jade.voidminersremastered.progress", percentage));
            }
        }
    }
    @Override
    public Identifier getUid() {
        return Identifier.fromNamespaceAndPath(VoidMinersRemastered.MODID, "miners");
    }
}