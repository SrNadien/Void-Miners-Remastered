package nadiendev.voidminersremastered.common.compat.jade;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.util.MiscUtil;
import nadiendev.voidminersremastered.world.block.entity.HaltReason;
import nadiendev.voidminersremastered.world.block.entity.SolarControllerBE;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

// Jade forbids one class implementing both IServerDataProvider and IBlockComponentProvider
// since Minecraft 1.21.6, so the server-data and client-tooltip halves live in separate classes.
public enum SolarClientProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof SolarControllerBE)) {
            return;
        }

        CompoundTag serverData = accessor.getServerData();

        if (serverData.contains("Tier")) {
            int tier = serverData.getIntOr("Tier", 0);
            tooltip.add(Component.translatable("jade.voidminersremastered.tier", tier));
        }

        if (serverData.contains("Energy") && serverData.contains("MaxEnergy")) {
            long energy = serverData.getLongOr("Energy", 0L);
            long maxEnergy = serverData.getLongOr("MaxEnergy", 0L);
            tooltip.add(Component.translatable("jade.voidminersremastered.energy",
                    String.format("%,d", energy),
                    String.format("%,d", maxEnergy)));
        }

        if (serverData.contains("HaltReason")) {
            int haltReasonInt = serverData.getIntOr("HaltReason", 0);
            HaltReason haltReason = HaltReason.getHaltReasonFromInt(haltReasonInt);
            Component reason;

            if (haltReason != HaltReason.NONE) {
                tooltip.add(Component.translatable("jade.voidminersremastered.status.idle"));

                switch (haltReason) {
                    case STRUCTURE_NOT_FOUND -> reason = Component.translatable("jade.voidminersremastered.halt_reason.structure_not_found");
                    case NO_SKY_VIEW -> reason = Component.translatable("jade.voidminersremastered.halt_reason.no_sky_view");
                    case POWER_FULL -> reason = Component.translatable("jade.voidminersremastered.halt_reason.power_full");
                    default -> reason = Component.translatable("jade.voidminersremastered.halt_reason.halt_reason_not_found");
                }
            } else {
                reason = Component.translatable("jade.voidminersremastered.status.working");
            }

            tooltip.add(reason);
        }
    }

    @Override
    public Identifier getUid() {
        return Identifier.fromNamespaceAndPath(VoidMinersRemastered.MODID, "solars");
    }
}