package nadiendev.voidminersremastered.init;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.world.block.entity.MinerControllerBE;
import nadiendev.voidminersremastered.world.block.entity.SolarControllerBE;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, VoidMinersRemastered.MODID);


    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MinerControllerBE>> MINER_CONTROLLER_BASE_BE =
            BLOCK_ENTITIES.register("controller_base",
                    () -> new BlockEntityType<>(
                            MinerControllerBE::new,
                            CrystalSet.RUBETINE.CONTROLLER.get(),
                            CrystalSet.AURANTIUM.CONTROLLER.get(),
                            CrystalSet.CITRINETINE.CONTROLLER.get(),
                            CrystalSet.VERDIUM.CONTROLLER.get(),
                            CrystalSet.AZURINE.CONTROLLER.get(),
                            CrystalSet.CAERIUM.CONTROLLER.get(),
                            CrystalSet.AMETHYSTINE.CONTROLLER.get(),
                            CrystalSet.ROSARIUM.CONTROLLER.get(),
                            CrystalSet.ULTIMATE.CONTROLLER.get()
                    )
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarControllerBE>> SOLAR_BASE_BE =
            BLOCK_ENTITIES.register("solar_base",
                    () -> new BlockEntityType<>(
                            SolarControllerBE::new,
                            SolarSet.RUBETINE.CONTROLLER.get(),
                            SolarSet.AURANTIUM.CONTROLLER.get(),
                            SolarSet.CITRINETINE.CONTROLLER.get(),
                            SolarSet.VERDIUM.CONTROLLER.get(),
                            SolarSet.AZURINE.CONTROLLER.get(),
                            SolarSet.CAERIUM.CONTROLLER.get(),
                            SolarSet.AMETHYSTINE.CONTROLLER.get(),
                            SolarSet.ROSARIUM.CONTROLLER.get(),
                            SolarSet.ULTIMATE.CONTROLLER.get()
                    )
            );
}