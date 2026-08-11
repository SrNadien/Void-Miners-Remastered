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

    /**
     * NeoForge 26.1.2 replaced the legacy capability holders:
     * {@code Capabilities.EnergyStorage} -> {@link Capabilities.Energy} (exposes
     * {@code net.neoforged.neoforge.transfer.energy.EnergyHandler}) and
     * {@code Capabilities.ItemHandler} -> {@link Capabilities.Item} (exposes
     * {@code ResourceHandler<ItemResource>}). {@code IEnergyStorage}/{@code IItemHandler} still exist but
     * are deprecated-for-removal and are no longer what the capabilities expose, and NeoForge only ships
     * new -> legacy wrappers ({@code IEnergyStorage#of}, {@code IItemHandler#of}), never the reverse.
     * <p>
     * Therefore {@code MinerControllerBE#getEnergyStorage()} / {@code SolarControllerBE#getEnergyStorage()}
     * must return an {@code EnergyHandler} and {@code MinerControllerBE#getItemHandler()} must return a
     * {@code ResourceHandler<ItemResource>} for these registrations to compile.
     */
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.Energy.BLOCK,
            ModBlockEntities.MINER_CONTROLLER_BASE_BE.get(),
            ((o, direction) -> o.getEnergyStorage())
        );

        event.registerBlockEntity(
            Capabilities.Item.BLOCK,
            ModBlockEntities.MINER_CONTROLLER_BASE_BE.get(),
            (be, side) -> be.getItemHandler()
        );

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ModBlockEntities.SOLAR_BASE_BE.get(),
                ((o, direction) -> o.getEnergyStorage())
        );
    }
}