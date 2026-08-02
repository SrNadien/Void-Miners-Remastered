package nadiendev.voidminersremastered.event;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.init.ModBlockEntities;
import nadiendev.voidminersremastered.world.multiblock.MinerMultiblocks;
import nadiendev.voidminersremastered.world.multiblock.SolarMultiblocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = VoidMinersRemastered.MODID)
public class ModBusEvent {
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        MinerMultiblocks.init();
        SolarMultiblocks.init();
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK,
            ModBlockEntities.MINER_CONTROLLER_BASE_BE.get(),
            ((o, direction) -> o.getEnergyStorage())
        );

        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.MINER_CONTROLLER_BASE_BE.get(),
            (be, side) -> be.getItemHandler()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.SOLAR_BASE_BE.get(),
                ((o, direction) -> o.getEnergyStorage())
        );
    }
}