package nadiendev.voidminersremastered.init;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VoidMinersRemastered.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> VOIDMINERS =
        CREATIVE_MODE_TABS.register("voidminers", () ->
            CreativeModeTab.builder()
                .title(Component.translatable("creativetab.voidminers.title"))
                .icon(() -> new ItemStack(ModBlocks.FRAME_BASE.get()))
                .displayItems((parameters, output) -> {
         
                    ModItems.ITEMS.getEntries().forEach(entry -> {
                        output.accept(entry.get());
                    });
            
                    ModBlocks.BLOCKS.getEntries().forEach(entry -> {
                        output.accept(entry.get());
                    });
                })
                .build()
        );
}