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
public enum MinerProvider implements IServerDataProvider<BlockAccessor> {
    INSTANCE;

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
    public Identifier getUid() {
        return Identifier.fromNamespaceAndPath(VoidMinersRemastered.MODID, "miners");
    }
}