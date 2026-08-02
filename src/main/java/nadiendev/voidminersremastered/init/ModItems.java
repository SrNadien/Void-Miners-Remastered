package nadiendev.voidminersremastered.init;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.config.MinerConfigLoader;
import nadiendev.voidminersremastered.world.item.MaxStorageUpgradeItem;
import nadiendev.voidminersremastered.world.item.StructureBuilderItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(VoidMinersRemastered.MODID);

    public static final DeferredItem<Item> STRUCTURE_BUILDER = ITEMS.register("structure_builder",
        () -> new StructureBuilderItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> ULTIMATE_STELLAR_CORE = ITEMS.register("ultimate_stellar_core",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    public static final DeferredItem<Item> MAX_STORAGE_UPGRADE_T1 = ITEMS.register("max_storage_upgrade_t1",
            () -> new MaxStorageUpgradeItem(MinerConfigLoader.getInstance().UPGRADE_T1_SLOTS, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> MAX_STORAGE_UPGRADE_T2 = ITEMS.register("max_storage_upgrade_t2",
            () -> new MaxStorageUpgradeItem(MinerConfigLoader.getInstance().UPGRADE_T2_SLOTS, new Item.Properties().rarity(Rarity.RARE)));
    public static final DeferredItem<Item> MAX_STORAGE_UPGRADE_T3 = ITEMS.register("max_storage_upgrade_t3",
            () -> new MaxStorageUpgradeItem(MinerConfigLoader.getInstance().UPGRADE_T3_SLOTS, new Item.Properties().rarity(Rarity.EPIC)));
}