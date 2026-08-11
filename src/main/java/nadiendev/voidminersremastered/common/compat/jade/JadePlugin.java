package nadiendev.voidminersremastered.common.compat.jade;

import nadiendev.voidminersremastered.world.block.entity.MinerControllerBE;
import nadiendev.voidminersremastered.world.block.entity.SolarControllerBE;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {
    
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(MinerProvider.INSTANCE, MinerControllerBE.class);
        registration.registerBlockDataProvider(SolarProvider.INSTANCE, SolarControllerBE.class);
    }
    
    // Since Minecraft 1.21.6 Jade rejects a data provider that also implements IComponentProvider,
    // so the client tooltips come from separate *ClientProvider classes.
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(MinerClientProvider.INSTANCE, Block.class);
        registration.registerBlockComponent(SolarClientProvider.INSTANCE, Block.class);
    }
}