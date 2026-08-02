package nadiendev.voidminersremastered.common.compat.jade;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.util.MiscUtil;
import nadiendev.voidminersremastered.world.block.entity.MinerControllerBE;
import nadiendev.voidminersremastered.world.block.entity.HaltReason;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum MinerProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof MinerControllerBE)) {
            return;
        }

        CompoundTag serverData = accessor.getServerData();
        
        if (serverData.contains("Tier")) {
            int tier = serverData.getInt("Tier");
            tooltip.add(Component.translatable("jade.voidminers.tier", tier));
        }

        if (serverData.contains("Energy") && serverData.contains("MaxEnergy")) {
            int energy = serverData.getInt("Energy");
            int maxEnergy = serverData.getInt("MaxEnergy");
            tooltip.add(Component.translatable("jade.voidminers.energy",
                String.format("%,d", energy), 
                String.format("%,d", maxEnergy)));
        }

        if (serverData.contains("MaxStorageUpgradeItem")) {
            ResourceLocation itemId = ResourceLocation.tryParse(serverData.getString("MaxStorageUpgradeItem"));
            if (itemId != null && BuiltInRegistries.ITEM.get(itemId) != Items.AIR) {
                tooltip.add(Component.translatable("jade.voidminers.storage_upgrade", BuiltInRegistries.ITEM.get(itemId).getDescription()));
            }
        }

        if (serverData.contains("HaltReason")) {
            int haltReasonInt = serverData.getInt("HaltReason");
            HaltReason haltReason = HaltReason.getHaltReasonFromInt(haltReasonInt);
            Component reason;

            if (haltReason != HaltReason.NONE) {
                tooltip.add(Component.translatable("jade.voidminers.status.idle"));

                switch (haltReason) {
                    case NO_RECIPES_IN_DIMENSION -> reason = Component.translatable("jade.voidminers.halt_reason.no_recipes_in_dimension");
                    case STRUCTURE_NOT_FOUND -> reason = Component.translatable("jade.voidminers.halt_reason.structure_not_found");
                    case TOO_MUCH_ITEM_MULTIPLIER -> reason = Component.translatable("jade.voidminers.halt_reason.too_much_item_multiplier");
                    case NOT_ENOUGH_EMPTY_SLOTS -> reason = Component.translatable("jade.voidminers.halt_reason.not_enough_empty_slots");
                    case NO_BEDROCK_OR_VOID_VIEW -> reason = Component.translatable("jade.voidminers.halt_reason.no_bedrock_or_void_view");
                    case NOT_ENOUGH_POWER -> reason = Component.translatable("jade.voidminers.halt_reason.not_enough_power");
                    default -> reason = Component.translatable("jade.voidminers.halt_reason.halt_reason_not_found");
                }
            } else {
                reason = Component.translatable("jade.voidminers.status.working");
            }

            tooltip.add(reason);
        }

        if (serverData.contains("Progress") && serverData.contains("MaxProgress")) {
            int progress = serverData.getInt("Progress");
            int maxProgress = serverData.getInt("MaxProgress");
            
            if (maxProgress > 0) {
                int percentage = (int) ((progress / (float) maxProgress) * 100);
                tooltip.add(Component.translatable("jade.voidminers.progress", percentage));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof MinerControllerBE miner)) {
            return;
        }

        if (miner.getStructure() != null) {
            Integer tier = MiscUtil.tierMap.get(miner.getStructure().getPath());
            if (tier != null) {
                tag.putInt("Tier", tier);
            }
        }

        tag.putInt("Energy", miner.getEnergyStorage().getEnergyStored());
        tag.putInt("MaxEnergy", miner.getEnergyStorage().getMaxEnergyStored());

        tag.putString("MaxStorageUpgradeItem", miner.getUpgradeItem().toString());

        tag.putInt("HaltReason", HaltReason.getIntFromHaltReason(miner.getHaltReason()));

        tag.putInt("Progress", miner.getProgress());
        tag.putInt("MaxProgress", miner.getMaxProgress());
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, "miners");
    }
}