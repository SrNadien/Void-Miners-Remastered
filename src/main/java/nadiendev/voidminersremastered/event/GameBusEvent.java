package nadiendev.voidminersremastered.event;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.config.MinerConfigLoader;
import nadiendev.voidminersremastered.config.ConfigReloadListener;
import nadiendev.voidminersremastered.common.network.ModNetwork;
import nadiendev.voidminersremastered.common.network.packet.SyncConfigS2CPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = VoidMinersRemastered.MODID)
public class GameBusEvent {
    /**
     * {@code AddReloadListenerEvent} was removed in 26.1.2; its replacement is
     * {@link AddServerReloadListenersEvent}, whose {@code addListener} now requires an
     * {@link Identifier} key used for dependency sorting.
     */
    @SubscribeEvent
    public static void addReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(
                Identifier.fromNamespaceAndPath(VoidMinersRemastered.MODID, "config_reload"),
                new ConfigReloadListener());
    }

    @SubscribeEvent
    public static void onReload(LevelTickEvent.Post event) {
        if(!(event.getLevel() instanceof ServerLevel sLevel)) return;

        int ticks = sLevel.getServer().getTickCount();
        if(ticks % 100 != 0) return;

        ModNetwork.sendToAllPlayers(
            new SyncConfigS2CPacket(
                MinerConfigLoader.getInstance().MINER_CONFIGS
            )
        );
    }
}