package nadiendev.voidminersremastered.datagen;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * 26.1.2: {@code GatherDataEvent} is abstract and split into {@code .Client} / {@code .Server}.
 * NeoForge itself registers everything (client <em>and</em> server data) from the {@code .Client}
 * event during a single {@code clientData} run -- see {@code ClientNeoForgeMod#onGatherData}.
 * <p>
 * {@code includeClient()}, {@code includeServer()}, {@code getPackOutput()} and
 * {@code getExistingFileHelper()} were all removed; providers are now built via
 * {@code event.createProvider(...)}.
 */
@EventBusSubscriber(modid = VoidMinersRemastered.MODID)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        event.createProvider(ModBlockStateProvider::new);
        event.createProvider(ModItemModelProvider::new);

        event.createProvider(ModRecipeProvider.Runner::new);

        event.createProvider(ModLootTableProvider::create);

        event.createProvider(ModBlockTagGenerator::new);

        event.createProvider(output -> new ModLanguageProvider(output, "en_us"));
        event.createProvider(output -> new ModLanguageProvider.EsEs(output, "es_es"));
        event.createProvider(output -> new ModLanguageProvider.EsEs(output, "es_ar"));
        event.createProvider(output -> new ModLanguageProvider.EsEs(output, "es_cl"));
        event.createProvider(output -> new ModLanguageProvider.EsEs(output, "es_mx"));
        event.createProvider(ModLanguageProvider.JaJp::new);
        event.createProvider(ModLanguageProvider.ZhCn::new);
        event.createProvider(ModLanguageProvider.RuRu::new);
    }
}
