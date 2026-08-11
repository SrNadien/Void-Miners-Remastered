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
public enum SolarProvider implements IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof SolarControllerBE solar)) {
            return;
        }

        if (solar.getStructure() != null) {
            Integer tier = MiscUtil.tierMap.get(solar.getStructure().getPath());
            if (tier != null) {
                tag.putInt("Tier", tier);
            }
        }

        tag.putLong("Energy", solar.getEnergyStorage().getLongEnergyStored());
        tag.putLong("MaxEnergy", solar.getEnergyStorage().getLongMaxEnergyStored());

        tag.putInt("HaltReason", HaltReason.getIntFromHaltReason(solar.getHaltReason()));
    }

    @Override
    public Identifier getUid() {
        return Identifier.fromNamespaceAndPath(VoidMinersRemastered.MODID, "solars");
    }
}