package nadiendev.voidminersremastered.event;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.client.render.block.MinerControllerBER;
import nadiendev.voidminersremastered.client.render.block.SolarControllerBER;
import nadiendev.voidminersremastered.init.ModBlockEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@EventBusSubscriber(modid = VoidMinersRemastered.MODID, value = Dist.CLIENT)
public class ModBusClientEvent {

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.MINER_CONTROLLER_BASE_BE.get(), MinerControllerBER::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SOLAR_BASE_BE.get(), SolarControllerBER::new);
    }

    @SubscribeEvent
    public static void addConnectedTexturePack(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES || !ModList.get().isLoaded("fusion")) {
            return;
        }

        event.addPackFinders(
                Identifier.fromNamespaceAndPath(VoidMinersRemastered.MODID, "resourcepacks/fusion"),
                PackType.CLIENT_RESOURCES,
                Component.literal("Void Miners Remastered - Fusion"),
                PackSource.BUILT_IN,
                true,
                Pack.Position.TOP
        );
    }
}
