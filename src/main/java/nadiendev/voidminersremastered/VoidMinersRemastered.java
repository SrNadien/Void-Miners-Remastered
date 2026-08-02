package nadiendev.voidminersremastered;

import nadiendev.voidminersremastered.config.MinerConfigLoader;
import nadiendev.voidminersremastered.config.SolarConfigLoader;
import nadiendev.voidminersremastered.init.*;
import nadiendev.voidminersremastered.world.multiblock.MinerMultiblocks;
import nadiendev.voidminersremastered.common.network.ModNetwork;
import com.mojang.logging.LogUtils;
import nadiendev.voidminersremastered.world.multiblock.SolarMultiblocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(VoidMinersRemastered.MODID)
public class VoidMinersRemastered {
    public static final String MODID = "voidminersremastered";
    public static final Logger LOGGER = LogUtils.getLogger();
    
    public VoidMinersRemastered(IEventBus modEventBus) {
        MinerConfigLoader.getInstance().load();
        SolarConfigLoader.getInstance().load();
        CrystalSet.initSets();
        SolarSet.initSets();

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModRecipes.SERIALIZERS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModDataComponents.DATA_COMPONENT_TYPES.register(modEventBus);

        modEventBus.addListener(this::registerPayloads);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::commonSetup);
    }

    private void registerPayloads(net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent event) {
        ModNetwork.register(event);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(this::setupRenders);
    }

    private void setupRenders() {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.GLASS_PANEL.get(), RenderType.translucent());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModRarities.init();
            MinerMultiblocks.init();
            SolarMultiblocks.init();
            LOGGER.info("Multiblocks initialized successfully!");
        });
    }
}