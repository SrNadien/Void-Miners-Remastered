package nadiendev.voidminersremastered.init;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.config.MinerConfigLoader;
import nadiendev.voidminersremastered.world.item.FaceConfiguratorItem;
import nadiendev.voidminersremastered.world.item.MaxStorageUpgradeItem;
import nadiendev.voidminersremastered.world.item.StructureBuilderItem;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import net.neoforged.neoforge.registries.DeferredItem;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(VoidMinersRemastered.MODID);

    public static final DeferredItem<Item> STRUCTURE_BUILDER = ITEMS.register("structure_builder",
        () -> new StructureBuilderItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> ULTIMATE_STELLAR_CORE = ITEMS.register("ultimate_stellar_core",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    public static final DeferredItem<Item> FACE_CONFIGURATOR = ITEMS.register("face_configurator",
            () -> new FaceConfiguratorItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> STORAGE_UPGRADE_MK1 = ITEMS.register("storage_upgrade_mk1",
            () -> new MaxStorageUpgradeItem(MinerConfigLoader.getInstance().UPGRADE_T1_SLOTS, new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<Item> STORAGE_UPGRADE_MK2 = ITEMS.register("storage_upgrade_mk2",
            () -> new MaxStorageUpgradeItem(MinerConfigLoader.getInstance().UPGRADE_T2_SLOTS, new Item.Properties().rarity(Rarity.RARE)));
    public static final DeferredItem<Item> STORAGE_UPGRADE_MK3 = ITEMS.register("storage_upgrade_mk3",
            () -> new MaxStorageUpgradeItem(MinerConfigLoader.getInstance().UPGRADE_T3_SLOTS, new Item.Properties().rarity(Rarity.EPIC)));


    /** Old ids kept as aliases so saved miners and stored items survive the rename. */
    public static final Map<String, String> RENAMED_ITEMS = Map.of(
            "max_storage_upgrade_t1", "storage_upgrade_mk1",
            "max_storage_upgrade_t2", "storage_upgrade_mk2",
            "max_storage_upgrade_t3", "storage_upgrade_mk3"
    );

    public static void registerAliases() {
        RENAMED_ITEMS.forEach((oldId, newId) -> ITEMS.addAlias(
                ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, oldId),
                ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, newId)
        ));
    }
}