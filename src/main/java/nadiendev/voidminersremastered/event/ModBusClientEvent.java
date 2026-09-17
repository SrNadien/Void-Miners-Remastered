package nadiendev.voidminersremastered.event;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.client.render.block.MinerControllerBER;
import nadiendev.voidminersremastered.client.render.block.SolarControllerBER;
import nadiendev.voidminersremastered.init.ModBlockEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
        event.registerBlockEntityRenderer(ModBlockEntities.MINER_CONTROLLER_BE.get(), MinerControllerBER::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SOLAR_CONTROLLER_BE.get(), SolarControllerBER::new);
    }

    @SubscribeEvent
    public static void addConnectedTexturePacks(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) {
            return;
        }

        if (ModList.get().isLoaded("fusion")) {
            addBuiltinPack(event, "fusion", "Void Miners Remastered - Fusion");
        }
    }

    private static void addBuiltinPack(AddPackFindersEvent event, String pack, String title) {
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, "resourcepacks/" + pack),
                PackType.CLIENT_RESOURCES,
                Component.literal(title),
                PackSource.BUILT_IN,
                true,
                Pack.Position.TOP
        );
    }
}
