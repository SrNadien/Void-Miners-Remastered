package nadiendev.voidminersremastered.datagen;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.init.ModBlocks;
import nadiendev.voidminersremastered.init.ModItems;
import nadiendev.voidminersremastered.init.CrystalSet;
import nadiendev.voidminersremastered.init.SolarSet;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {

    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, VoidMinersRemastered.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        this.add("creativetab.voidminersremastered.title", "Void Miners Remastered");

        this.add(ModItems.STRUCTURE_BUILDER.get(), "Structure Builder");
        this.add(ModItems.ULTIMATE_STELLAR_CORE.get(), "Ultimate Stellar Core");

        this.add(ModItems.MAX_STORAGE_UPGRADE_T1.get(), "Max Storage Upgrade T1");
        this.add(ModItems.MAX_STORAGE_UPGRADE_T2.get(), "Max Storage Upgrade T2");
        this.add(ModItems.MAX_STORAGE_UPGRADE_T3.get(), "Max Storage Upgrade T3");

        this.add("tooltip.voidminersremastered.max_storage_upgrades", "Right-click a miner with this upgrade to apply it.\nIncreases the miner's output inventory by +%s slots.\nOnly the highest installed tier is applied\nNon-cumulative.\nConsumed on use.");

        this.add("gui.voidminersremastered.miner", "Tier %d Miner");
        this.add("gui.voidminersremastered.miners", "Miners");

        this.add("tooltip.voidminersremastered.controller.halt_reason.dimension_not_ok", "§eThis miner doesn't have any recipes available for this dimension : %s§d)§e.\nCheck JEI for available recipes / dimensions.");
        this.add("tooltip.voidminersremastered.controller.halt_reason.structure_not_found", "Structure is not correct, sneak + right-click the Controller for a guide.\nTotal needed blocks: ");
        this.add("tooltip.voidminersremastered.controller.halt_reason.too_much_item_multiplier", "§eItem Multiplier too high, remove some of the blocks that add Item Multiplier.\nMax Item Multiplier: %d×");
        this.add("tooltip.voidminersremastered.controller.halt_reason.not_enough_empty_slots", "All slots are full, the miner can't mine until it has been emptied.");
        this.add("tooltip.voidminersremastered.controller.halt_reason.no_bedrock_or_void_view", "Make sure that the miner can see the void / bedrock!\nThe distance does NOT matter, only that the center block can see void / bedrock.");

        this.add("tooltip.voidminersremastered.controller.halt_reason.no_sky_view", "Unable to see the sky, make sure there are no blocks above the Solar Panel\nDimension could also not have a \"day\" ie. The Nether / The End");

        this.add("tooltip.voidminersremastered.controller.progress", "§e⏳ PROGRESS: ");
        this.add("tooltip.voidminersremastered.controller.item_boost", "§d📦 ITEM BOOST: ");
        this.add("tooltip.voidminersremastered.controller.duration", "§9⏱ DURATION: ");
        this.add("tooltip.voidminersremastered.controller.consumption", "§c⚡ CONSUMPTION: ");
        this.add("tooltip.voidminersremastered.controller.energy", "§e⚡ ENERGY: ");
        this.add("tooltip.voidminersremastered.controller.max_storage_upgrade_tip", "If a better one is available, you could also add a better Max Storage Upgrade");

        this.add("tooltip.voidminersremastered.controller.generation", "⚡ GENERATION: ");
        this.add("tooltip.voidminersremastered.controller.efficiency", "☀ EFFICIENCY: ");

        this.add("tooltip.voidminersremastered.controller.status.status", "⚠ STATUS: ");

        this.add("tooltip.voidminersremastered.controller.status.mining_slow", "MINING SLOW");
        this.add("tooltip.voidminersremastered.controller.status.not_enough_power_for_next_operation", "Not enough power for next operation.");
        this.add("tooltip.voidminersremastered.controller.status.mining_impossible", "MINING IMPOSSIBLE");
        this.add("tooltip.voidminersremastered.controller.status.mining_stopped", "MINING STOPPED");
        this.add("tooltip.voidminersremastered.controller.status.not_enough_power", "Not enough power.");
        this.add("tooltip.voidminersremastered.controller.status.not_enough_power_buffer", "Not enough power buffer.\nEither use less Modifiers or use Energy Modifiers.");

        this.add("tooltip.voidminersremastered.controller.status.structure_incomplete", "STRUCTURE INCOMPLETE");
        this.add("tooltip.voidminersremastered.controller.status.working", "WORKING");
        this.add("tooltip.voidminersremastered.controller.status.power_full", "POWER BUFFER FULL");
        this.add("tooltip.voidminersremastered.controller.efficiency_limited_by_rain", "Efficiency limited because of Rain.");
        this.add("tooltip.voidminersremastered.controller.efficiency_limited_by_thunder", "Efficiency limited because of Thunder.");
        this.add("tooltip.voidminersremastered.controller.efficiency_limited_by_time_of_day", "Efficiency limited because of the current time.");

        this.add("tooltip.voidminersremastered.controller.status.no_sky_view", "NO SKY VIEW / DIMENSION DOES NOT HAVE A \"DAY\"");

        this.add("tooltip.voidminersremastered.structure.weight", "Weight: %d");

        this.add(ModBlocks.FRAME_BASE.get(), "Frame Base");

        this.add(ModBlocks.STRUCTURE_PANEL.get(), "Structure Panel");

        this.add(ModBlocks.GLASS_PANEL.get(), "Glass Panel");

        this.add(ModBlocks.NULL_MOD.get(), "Null Modifier");

        for (CrystalSet set : CrystalSet.sets()) {
            if (set.CRYSTAL != null) {
                this.add(set.CRYSTAL.get(), cFL(set.name));
                this.add(set.CRYSTAL_BLOCK.get(), cFL(set.name) + " Block");
                this.add(set.CONTROLLER.get(), cFL(set.name) + " Miner Controller");
                this.add(set.FRAME.get(), cFL(set.name) + " Frame");
                this.add(set.ENERGY_MOD.get(), cFL(set.name) + " Energy Modifier");
                this.add(set.SPEED_MOD.get(), cFL(set.name) + " Speed Modifier");
                this.add(set.ITEM_MOD.get(), cFL(set.name) + " Item Modifier");
            } else {
                this.add(CrystalSet.ULTIMATE.CRYSTAL_BLOCK.get(), cFL(CrystalSet.ULTIMATE.name) + " Block");
                this.add(CrystalSet.ULTIMATE.CONTROLLER.get(), cFL(CrystalSet.ULTIMATE.name) + " Miner Controller");
                this.add(CrystalSet.ULTIMATE.FRAME.get(), cFL(CrystalSet.ULTIMATE.name) + " Frame");
                this.add(CrystalSet.ULTIMATE.ENERGY_MOD.get(), cFL(CrystalSet.ULTIMATE.name) + " Energy Modifier");
                this.add(CrystalSet.ULTIMATE.SPEED_MOD.get(), cFL(CrystalSet.ULTIMATE.name) + " Speed Modifier");
                this.add(CrystalSet.ULTIMATE.ITEM_MOD.get(), cFL(CrystalSet.ULTIMATE.name) + " Item Modifier");
            }
        }

        for (SolarSet set : SolarSet.sets()) {
            if (set.CRYSTAL != null) {
                this.add(set.CRYSTAL.get(), "Solar " + cFL(set.name));
                this.add(set.CRYSTAL_BLOCK.get(), "Solar " + cFL(set.name) + " Block");
                this.add(set.CONTROLLER.get(), "Solar " + cFL(set.name) + " Panel Controller");
                this.add(set.FRAME.get(), "Solar " + cFL(set.name) + " Frame");
                this.add(set.WEATHER_MOD.get(), "Solar " + cFL(set.name) + " Weather Modifier");
                this.add(set.EFFICIENCY_MOD.get(), "Solar " + cFL(set.name) + " Efficiency Modifier");
            } else {
                this.add(SolarSet.ULTIMATE.CRYSTAL_BLOCK.get(), "Solar " + cFL(SolarSet.ULTIMATE.name) + " Block");
                this.add(SolarSet.ULTIMATE.CONTROLLER.get(), cFL(SolarSet.ULTIMATE.name) + " Solar Panel Controller");
                this.add(SolarSet.ULTIMATE.FRAME.get(), "Solar " + cFL(SolarSet.ULTIMATE.name) + " Frame");
                this.add(SolarSet.ULTIMATE.WEATHER_MOD.get(), "Solar " + cFL(SolarSet.ULTIMATE.name) + " Weather Modifier");
                this.add(SolarSet.ULTIMATE.EFFICIENCY_MOD.get(), "Solar " + cFL(SolarSet.ULTIMATE.name) + " Efficiency Modifier");
            }
        }

        this.add("tooltip.voidminersremastered.energy", "Energy Consumption: %s%%");
        this.add("tooltip.voidminersremastered.speed", "Mining Speed: %s%%");
        this.add("tooltip.voidminersremastered.item", "Item Multiplier: %s%%");
        this.add("tooltip.voidminersremastered.efficiency", "Solar Efficiency: %s%%");
        this.add("tooltip.voidminersremastered.weatherResistance", "Weather Resistance: %s%%");

        this.add("tooltip.voidminersremastered.structure_builder.instructions", "Sneak + right-click on a Controller to automatically build the Multiblock.");

        this.add("minecraft.overworld", "Overworld");
        this.add("minecraft.the_nether", "The Nether");
        this.add("minecraft.the_end", "The End");

        this.add("config.jade.plugin_voidminers.void_miner", "VoidMiners");
        this.add("config.jade.plugin_" + VoidMinersRemastered.MODID + ".miners", "Miners");
        this.add("config.jade.plugin_" + VoidMinersRemastered.MODID + ".solars", "Solar Panels");
        this.add("jade.voidminersremastered.halt_reason.halt_reason_not_found", "Reason not found");
        this.add("jade.voidminersremastered.halt_reason.not_enough_power", "Reason: Not enough power");
        this.add("jade.voidminersremastered.halt_reason.not_enough_power_buffer", "Reason: Not enough power buffer");
        this.add("jade.voidminersremastered.tier", "Tier: %s");
        this.add("jade.voidminersremastered.storage_upgrade", "Storage Upgrade: %s");
        this.add("jade.voidminersremastered.energy", "Energy: %s / %s FE");
        this.add("jade.voidminersremastered.status.working", "Status: Working");
        this.add("jade.voidminersremastered.status.idle", "Status: Idle");
        this.add("jade.voidminersremastered.halt_reason.no_recipes_in_dimension", "Reason: No recipes in dimension");
        this.add("jade.voidminersremastered.halt_reason.structure_not_found", "Reason: Structure not found");
        this.add("jade.voidminersremastered.halt_reason.too_much_item_multiplier", "Reason: Item Multiplier too high");
        this.add("jade.voidminersremastered.halt_reason.not_enough_empty_slots", "Reason: Not enough empty slots");
        this.add("jade.voidminersremastered.halt_reason.no_bedrock_or_void_view", "Reason: Can't see Bedrock or Void");

        this.add("jade.voidminersremastered.halt_reason.no_sky_view", "Reason: Can't see the sky");
        this.add("jade.voidminersremastered.halt_reason.power_full", "Reason: Power buffer full");

        this.add("jade.voidminersremastered.progress", "Progress: %s%%");

        this.add("client_message.voidminersremastered.max_storage_upgrades.upgrade_already_applied", "Same Upgrade already applied");
        this.add("client_message.voidminersremastered.max_storage_upgrades.upgrade_already_applied_is_higher_tier", "Cannot apply an upgrade with less slots then the current upgrade.");
        this.add("client_message.voidminersremastered.max_storage_upgrades.upgrade_applied", "Upgrade applied, added slots : %d");

        this.add("tooltip.voidminersremastered.structure_builder.unable_to_place_multiblock.1", "Unable to place the multiblock because other blocks are in the way.");
        this.add("tooltip.voidminersremastered.structure_builder.unable_to_place_multiblock.2", "Please clear the area where the multiblock will be made, you can sneak + right-click the Controller for a guide.");
        this.add("tooltip.voidminersremastered.structure_builder.missing_block_in_inventory", "Unable to place some blocks because they are not in your inventory.\nMissing Blocks:");
    }


    /**
     * Capitalizes first letter of a string
     *
     * @param input the string to capitalize e.g. "alpha"
     * @return the string capitalized e.g. "Alpha"
     */
    public static String cFL(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }


   public static class EsEs extends LanguageProvider {
        public EsEs(PackOutput output, String locale) {
            super(output, VoidMinersRemastered.MODID, locale);
        }

        @Override
        protected void addTranslations() {
            this.add("block.voidminersremastered.amethystine_block", "Bloque de Amethystine");
            this.add("block.voidminersremastered.amethystine_energy_modifier", "Modificador de Energía de Amethystine");
            this.add("block.voidminersremastered.amethystine_frame", "Estructura de Amethystine");
            this.add("block.voidminersremastered.amethystine_item_modifier", "Modificador de Objetos de Amethystine");
            this.add("block.voidminersremastered.amethystine_miner", "Minero de Amethystine");
            this.add("block.voidminersremastered.amethystine_speed_modifier", "Modificador de Velocidad de Amethystine");
            this.add("block.voidminersremastered.aurantium_block", "Bloque de Aurantium");
            this.add("block.voidminersremastered.aurantium_energy_modifier", "Modificador de Energía de Aurantium");
            this.add("block.voidminersremastered.aurantium_frame", "Estructura de Aurantium");
            this.add("block.voidminersremastered.aurantium_item_modifier", "Modificador de Objetos de Aurantium");
            this.add("block.voidminersremastered.aurantium_miner", "Minero de Aurantium");
            this.add("block.voidminersremastered.aurantium_speed_modifier", "Modificador de Velocidad de Aurantium");
            this.add("block.voidminersremastered.azurine_block", "Bloque de Azurine");
            this.add("block.voidminersremastered.azurine_energy_modifier", "Modificador de Energía de Azurine");
            this.add("block.voidminersremastered.azurine_frame", "Estructura de Azurine");
            this.add("block.voidminersremastered.azurine_item_modifier", "Modificador de Objetos de Azurine");
            this.add("block.voidminersremastered.azurine_miner", "Minero de Azurine");
            this.add("block.voidminersremastered.azurine_speed_modifier", "Modificador de Velocidad de Azurine");
            this.add("block.voidminersremastered.caerium_block", "Bloque de Caerium");
            this.add("block.voidminersremastered.caerium_energy_modifier", "Modificador de Energía de Caerium");
            this.add("block.voidminersremastered.caerium_frame", "Estructura de Caerium");
            this.add("block.voidminersremastered.caerium_item_modifier", "Modificador de Objetos de Caerium");
            this.add("block.voidminersremastered.caerium_miner", "Minero de Caerium");
            this.add("block.voidminersremastered.caerium_speed_modifier", "Modificador de Velocidad de Caerium");
            this.add("block.voidminersremastered.citrinetine_block", "Bloque de Citrinetine");
            this.add("block.voidminersremastered.citrinetine_energy_modifier", "Modificador de Energía de Citrinetine");
            this.add("block.voidminersremastered.citrinetine_frame", "Estructura de Citrinetine");
            this.add("block.voidminersremastered.citrinetine_item_modifier", "Modificador de Objetos de Citrinetine");
            this.add("block.voidminersremastered.citrinetine_miner", "Minero de Citrinetine");
            this.add("block.voidminersremastered.citrinetine_speed_modifier", "Modificador de Velocidad de Citrinetine");
            this.add("block.voidminersremastered.frame_base", "Base de Estructura");
            this.add("block.voidminersremastered.glass_panel", "Panel de Cristal");
            this.add("block.voidminersremastered.null_modifier", "Modificador Nulo");
            this.add("block.voidminersremastered.rosarium_block", "Bloque de Rosarium");
            this.add("block.voidminersremastered.rosarium_energy_modifier", "Modificador de Energía de Rosarium");
            this.add("block.voidminersremastered.rosarium_frame", "Estructura de Rosarium");
            this.add("block.voidminersremastered.rosarium_item_modifier", "Modificador de Objetos de Rosarium");
            this.add("block.voidminersremastered.rosarium_miner", "Minero de Rosarium");
            this.add("block.voidminersremastered.rosarium_speed_modifier", "Modificador de Velocidad de Rosarium");
            this.add("block.voidminersremastered.rubetine_block", "Bloque de Rubetine");
            this.add("block.voidminersremastered.rubetine_energy_modifier", "Modificador de Energía de Rubetine");
            this.add("block.voidminersremastered.rubetine_frame", "Estructura de Rubetine");
            this.add("block.voidminersremastered.rubetine_item_modifier", "Modificador de Objetos de Rubetine");
            this.add("block.voidminersremastered.rubetine_miner", "Minero de Rubetine");
            this.add("block.voidminersremastered.rubetine_speed_modifier", "Modificador de Velocidad de Rubetine");
            this.add("block.voidminersremastered.solar_amethystine_block", "Bloque Solar de Amethystine");
            this.add("block.voidminersremastered.solar_amethystine_efficiency_modifier", "Modificador de Eficiencia Solar de Amethystine");
            this.add("block.voidminersremastered.solar_amethystine_frame", "Estructura Solar de Amethystine");
            this.add("block.voidminersremastered.solar_amethystine_panel", "Panel Solar de Amethystine");
            this.add("block.voidminersremastered.solar_amethystine_weather_modifier", "Modificador Climático Solar de Amethystine");
            this.add("block.voidminersremastered.solar_aurantium_block", "Bloque Solar de Aurantium");
            this.add("block.voidminersremastered.solar_aurantium_efficiency_modifier", "Modificador de Eficiencia Solar de Aurantium");
            this.add("block.voidminersremastered.solar_aurantium_frame", "Estructura Solar de Aurantium");
            this.add("block.voidminersremastered.solar_aurantium_panel", "Panel Solar de Aurantium");
            this.add("block.voidminersremastered.solar_aurantium_weather_modifier", "Modificador Climático Solar de Aurantium");
            this.add("block.voidminersremastered.solar_azurine_block", "Bloque Solar de Azurine");
            this.add("block.voidminersremastered.solar_azurine_efficiency_modifier", "Modificador de Eficiencia Solar de Azurine");
            this.add("block.voidminersremastered.solar_azurine_frame", "Estructura Solar de Azurine");
            this.add("block.voidminersremastered.solar_azurine_panel", "Panel Solar de Azurine");
            this.add("block.voidminersremastered.solar_azurine_weather_modifier", "Modificador Climático Solar de Azurine");
            this.add("block.voidminersremastered.solar_caerium_block", "Bloque Solar de Caerium");
            this.add("block.voidminersremastered.solar_caerium_efficiency_modifier", "Modificador de Eficiencia Solar de Caerium");
            this.add("block.voidminersremastered.solar_caerium_frame", "Estructura Solar de Caerium");
            this.add("block.voidminersremastered.solar_caerium_panel", "Panel Solar de Caerium");
            this.add("block.voidminersremastered.solar_caerium_weather_modifier", "Modificador Climático Solar de Caerium");
            this.add("block.voidminersremastered.solar_citrinetine_block", "Bloque Solar de Citrinetine");
            this.add("block.voidminersremastered.solar_citrinetine_efficiency_modifier", "Modificador de Eficiencia Solar de Citrinetine");
            this.add("block.voidminersremastered.solar_citrinetine_frame", "Estructura Solar de Citrinetine");
            this.add("block.voidminersremastered.solar_citrinetine_panel", "Panel Solar de Citrinetine");
            this.add("block.voidminersremastered.solar_citrinetine_weather_modifier", "Modificador Climático Solar de Citrinetine");
            this.add("block.voidminersremastered.solar_rosarium_block", "Bloque Solar de Rosarium");
            this.add("block.voidminersremastered.solar_rosarium_efficiency_modifier", "Modificador de Eficiencia Solar de Rosarium");
            this.add("block.voidminersremastered.solar_rosarium_frame", "Estructura Solar de Rosarium");
            this.add("block.voidminersremastered.solar_rosarium_panel", "Panel Solar de Rosarium");
            this.add("block.voidminersremastered.solar_rosarium_weather_modifier", "Modificador Climático Solar de Rosarium");
            this.add("block.voidminersremastered.solar_rubetine_block", "Bloque Solar de Rubetine");
            this.add("block.voidminersremastered.solar_rubetine_efficiency_modifier", "Modificador de Eficiencia Solar de Rubetine");
            this.add("block.voidminersremastered.solar_rubetine_frame", "Estructura Solar de Rubetine");
            this.add("block.voidminersremastered.solar_rubetine_panel", "Panel Solar de Rubetine");
            this.add("block.voidminersremastered.solar_rubetine_weather_modifier", "Modificador Climático Solar de Rubetine");
            this.add("block.voidminersremastered.solar_ultimate_block", "Bloque Solar Definitivo");
            this.add("block.voidminersremastered.solar_ultimate_efficiency_modifier", "Modificador de Eficiencia Solar Definitivo");
            this.add("block.voidminersremastered.solar_ultimate_frame", "Estructura Solar Definitiva");
            this.add("block.voidminersremastered.solar_ultimate_panel", "Panel Solar Definitivo");
            this.add("block.voidminersremastered.solar_ultimate_weather_modifier", "Modificador Climático Solar Definitivo");
            this.add("block.voidminersremastered.solar_verdium_block", "Bloque Solar de Verdium");
            this.add("block.voidminersremastered.solar_verdium_efficiency_modifier", "Modificador de Eficiencia Solar de Verdium");
            this.add("block.voidminersremastered.solar_verdium_frame", "Estructura Solar de Verdium");
            this.add("block.voidminersremastered.solar_verdium_panel", "Panel Solar de Verdium");
            this.add("block.voidminersremastered.solar_verdium_weather_modifier", "Modificador Climático Solar de Verdium");
            this.add("block.voidminersremastered.structure_panel", "Panel de Estructura");
            this.add("block.voidminersremastered.ultimate_block", "Bloque Definitivo");
            this.add("block.voidminersremastered.ultimate_energy_modifier", "Modificador de Energía Definitivo");
            this.add("block.voidminersremastered.ultimate_frame", "Estructura Definitiva");
            this.add("block.voidminersremastered.ultimate_item_modifier", "Modificador de Objetos Definitivo");
            this.add("block.voidminersremastered.ultimate_miner", "Minero Definitivo");
            this.add("block.voidminersremastered.ultimate_speed_modifier", "Modificador de Velocidad Definitivo");
            this.add("block.voidminersremastered.verdium_block", "Bloque de Verdium");
            this.add("block.voidminersremastered.verdium_energy_modifier", "Modificador de Energía de Verdium");
            this.add("block.voidminersremastered.verdium_frame", "Estructura de Verdium");
            this.add("block.voidminersremastered.verdium_item_modifier", "Modificador de Objetos de Verdium");
            this.add("block.voidminersremastered.verdium_miner", "Minero de Verdium");
            this.add("block.voidminersremastered.verdium_speed_modifier", "Modificador de Velocidad de Verdium");
            this.add("client_message.voidminersremastered.max_storage_upgrades.upgrade_already_applied", "Ya se ha aplicado la misma mejora");
            this.add("client_message.voidminersremastered.max_storage_upgrades.upgrade_already_applied_is_higher_tier", "No se puede aplicar una mejora de nivel inferior mientras haya instalada una de nivel superior");
            this.add("client_message.voidminersremastered.max_storage_upgrades.upgrade_applied", "Mejora aplicada, Nivel: %d");
            this.add("config.jade.plugin_voidminers.void_miner", "VoidMiners");
            this.add("config.jade.plugin_voidminersremastered.miners", "Mineros");
            this.add("config.jade.plugin_voidminersremastered.solars", "Paneles Solares");
            this.add("creativetab.voidminersremastered.title", "Void Miners Remastered");
            this.add("gui.voidminersremastered.miner", "Minero de Nivel %d");
            this.add("gui.voidminersremastered.miners", "Mineros");
            this.add("item.voidminersremastered.amethystine", "Amethystine");
            this.add("item.voidminersremastered.aurantium", "Aurantium");
            this.add("item.voidminersremastered.azurine", "Azurine");
            this.add("item.voidminersremastered.caerium", "Caerium");
            this.add("item.voidminersremastered.citrinetine", "Citrinetine");
            this.add("item.voidminersremastered.max_storage_upgrade_t1", "Mejora de Almacenamiento Máximo T1");
            this.add("item.voidminersremastered.max_storage_upgrade_t2", "Mejora de Almacenamiento Máximo T2");
            this.add("item.voidminersremastered.max_storage_upgrade_t3", "Mejora de Almacenamiento Máximo T3");
            this.add("item.voidminersremastered.rosarium", "Rosarium");
            this.add("item.voidminersremastered.rubetine", "Rubetine");
            this.add("item.voidminersremastered.solar_amethystine", "Amethystine Solar");
            this.add("item.voidminersremastered.solar_aurantium", "Aurantium Solar");
            this.add("item.voidminersremastered.solar_azurine", "Azurine Solar");
            this.add("item.voidminersremastered.solar_caerium", "Caerium Solar");
            this.add("item.voidminersremastered.solar_citrinetine", "Citrinetine Solar");
            this.add("item.voidminersremastered.solar_rosarium", "Rosarium Solar");
            this.add("item.voidminersremastered.solar_rubetine", "Rubetine Solar");
            this.add("item.voidminersremastered.solar_verdium", "Verdium Solar");
            this.add("item.voidminersremastered.structure_builder", "Constructor de Estructuras");
            this.add("item.voidminersremastered.ultimate_stellar_core", "Núcleo Estelar Definitivo");
            this.add("item.voidminersremastered.verdium", "Verdium");
            this.add("jade.voidminersremastered.energy", "Energía: %s / %s FE");
            this.add("jade.voidminersremastered.halt_reason.halt_reason_not_found", "Motivo no encontrado");
            this.add("jade.voidminersremastered.halt_reason.no_bedrock_or_void_view", "Motivo: No puede ver el Lecho de Roca ni el Vacío");
            this.add("jade.voidminersremastered.halt_reason.no_recipes_in_dimension", "Motivo: No hay recetas en esta dimensión");
            this.add("jade.voidminersremastered.halt_reason.no_sky_view", "Motivo: No puede ver el cielo");
            this.add("jade.voidminersremastered.halt_reason.not_enough_empty_slots", "Motivo: No hay suficientes espacios vacíos");
            this.add("jade.voidminersremastered.halt_reason.not_enough_power", "Motivo: No hay suficiente energía");
            this.add("jade.voidminersremastered.halt_reason.not_enough_power_buffer", "Motivo: No hay suficiente almacenamiento de energía");
            this.add("jade.voidminersremastered.halt_reason.power_full", "Motivo: Almacenamiento de energía lleno");
            this.add("jade.voidminersremastered.halt_reason.structure_not_found", "Motivo: Estructura no encontrada");
            this.add("jade.voidminersremastered.halt_reason.too_much_item_multiplier", "Motivo: Multiplicador de Objetos demasiado alto");
            this.add("jade.voidminersremastered.progress", "Progreso: %s%%");
            this.add("jade.voidminersremastered.status.idle", "Estado: Inactivo");
            this.add("jade.voidminersremastered.status.working", "Estado: Trabajando");
            this.add("jade.voidminersremastered.storage_upgrade", "Mejora de Almacenamiento: %s");
            this.add("jade.voidminersremastered.tier", "Nivel: %s");
            this.add("minecraft.overworld", "Overworld");
            this.add("minecraft.the_end", "El End");
            this.add("minecraft.the_nether", "El Nether");
            this.add("tooltip.voidminersremastered.controller.consumption", "§c⚡ CONSUMO: ");
            this.add("tooltip.voidminersremastered.controller.duration", "Duración: %d ticks");
            this.add("tooltip.voidminersremastered.controller.efficiency", "☀ EFICIENCIA: ");
            this.add("tooltip.voidminersremastered.controller.efficiency_limited_by_rain", "Eficiencia limitada debido a la lluvia.");
            this.add("tooltip.voidminersremastered.controller.efficiency_limited_by_thunder", "Eficiencia limitada debido a la tormenta.");
            this.add("tooltip.voidminersremastered.controller.efficiency_limited_by_time_of_day", "Eficiencia limitada debido a la hora actual.");
            this.add("tooltip.voidminersremastered.controller.energy", "Energía: %d rf/t");
            this.add("tooltip.voidminersremastered.controller.generation", "⚡ GENERACIÓN: ");
            this.add("tooltip.voidminersremastered.controller.halt_reason.dimension_not_ok", "§eEste minero no tiene ninguna receta disponible para esta dimensión: %s§d)§e.\nConsulta JEI para ver las recetas / dimensiones disponibles.");
            this.add("tooltip.voidminersremastered.controller.halt_reason.no_bedrock_or_void_view", "¡Asegúrate de que el minero pueda ver el vacío / lecho de roca!\nLa distancia NO importa, solo que el bloque central pueda ver el vacío / lecho de roca.");
            this.add("tooltip.voidminersremastered.controller.halt_reason.no_sky_view", "No se puede ver el cielo, asegúrate de que no haya bloques encima del Panel Solar\nLa dimensión también podría no tener \"día\", como El Nether / El End");
            this.add("tooltip.voidminersremastered.controller.halt_reason.not_enough_empty_slots", "Todos los espacios están llenos, el minero no puede seguir minando hasta que se vacíe.");
            this.add("tooltip.voidminersremastered.controller.halt_reason.structure_not_found", "La estructura no es correcta, agáchate y haz clic derecho en el Minero para ver una guía.\nBloques totales necesarios: ");
            this.add("tooltip.voidminersremastered.controller.halt_reason.too_much_item_multiplier", "§eMultiplicador de Objetos demasiado alto, elimina algunos de los bloques que añaden Multiplicador de Objetos.\nMultiplicador de Objetos máximo: %d×");
            this.add("tooltip.voidminersremastered.controller.item_boost", "§d📦 IMPULSO DE OBJETOS: ");
            this.add("tooltip.voidminersremastered.controller.max_storage_upgrade_tip", "También puedes añadir una Mejora de Almacenamiento Máximo mejor");
            this.add("tooltip.voidminersremastered.controller.progress", "§e⏳ PROGRESO: ");
            this.add("tooltip.voidminersremastered.controller.status.mining_impossible", "MINADO IMPOSIBLE");
            this.add("tooltip.voidminersremastered.controller.status.mining_slow", "MINADO LENTO");
            this.add("tooltip.voidminersremastered.controller.status.mining_stopped", "MINADO DETENIDO");
            this.add("tooltip.voidminersremastered.controller.status.no_sky_view", "SIN VISIÓN DEL CIELO / LA DIMENSIÓN NO TIENE \"DÍA\"");
            this.add("tooltip.voidminersremastered.controller.status.not_enough_power", "No hay suficiente energía.");
            this.add("tooltip.voidminersremastered.controller.status.not_enough_power_buffer", "No hay suficiente almacenamiento de energía.\nUsa menos Modificadores o utiliza Modificadores de Energía.");
            this.add("tooltip.voidminersremastered.controller.status.not_enough_power_for_next_operation", "No hay suficiente energía para la próxima operación.");
            this.add("tooltip.voidminersremastered.controller.status.power_full", "ALMACENAMIENTO DE ENERGÍA LLENO");
            this.add("tooltip.voidminersremastered.controller.status.status", "⚠ ESTADO: ");
            this.add("tooltip.voidminersremastered.controller.status.structure_incomplete", "ESTRUCTURA INCOMPLETA");
            this.add("tooltip.voidminersremastered.controller.status.working", "TRABAJANDO");
            this.add("tooltip.voidminersremastered.efficiency", "Eficiencia Solar: %s%%");
            this.add("tooltip.voidminersremastered.energy", "Modificador de Energía: %dx");
            this.add("tooltip.voidminersremastered.item", "Modificador de Cantidad de Objetos: %dx");
            this.add("tooltip.voidminersremastered.max_storage_upgrades", "Haz clic derecho en un minero con esta mejora para aplicarla.\nAumenta el inventario de salida del minero en +%s espacios.\nSolo se aplica el nivel más alto instalado.\nNo acumulable.\nSe consume al usarse.");
            this.add("tooltip.voidminersremastered.speed", "Modificador de Duración: %dx");
            this.add("tooltip.voidminersremastered.structure.weight", "Peso: %d");
            this.add("tooltip.voidminersremastered.structure_builder.instructions", "Agáchate y haz clic derecho sobre un Controlador para construir automáticamente el Multibloque.");
            this.add("tooltip.voidminersremastered.structure_builder.missing_block_in_inventory", "No se pueden colocar los bloques porque no están en tu inventario.\nBloques faltantes:");
            this.add("tooltip.voidminersremastered.structure_builder.unable_to_place_multiblock.1", "No se puede colocar el multibloque porque hay otros bloques en el camino.");
            this.add("tooltip.voidminersremastered.structure_builder.unable_to_place_multiblock.2", "Por favor, despeja el área donde se construirá el multibloque; también puedes agacharte y hacer clic derecho en el Controlador para ver una guía.");
            this.add("tooltip.voidminersremastered.weatherResistance", "Resistencia al Clima: %s%%");
        }
    }

    public static class JaJp extends LanguageProvider {
        public JaJp(PackOutput output) {
            super(output, VoidMinersRemastered.MODID, "ja_jp");
        }

        @Override
        protected void addTranslations() {
            this.add("block.voidminersremastered.amethystine_block", "アメジスチンブロック");
            this.add("block.voidminersremastered.amethystine_energy_modifier", "アメジスチンのエネルギー効率強化");
            this.add("block.voidminersremastered.amethystine_frame", "アメジスチンの外枠");
            this.add("block.voidminersremastered.amethystine_item_modifier", "アメジスチンのアイテム採掘強化");
            this.add("block.voidminersremastered.amethystine_miner", "アメジスチンの採掘機");
            this.add("block.voidminersremastered.amethystine_speed_modifier", "アメジスチンの採掘速度強化");
            this.add("block.voidminersremastered.aurantium_block", "オーランティウムブロック");
            this.add("block.voidminersremastered.aurantium_energy_modifier", "オーランティウムのエネルギー効率強化");
            this.add("block.voidminersremastered.aurantium_frame", "オーランティウムの外枠");
            this.add("block.voidminersremastered.aurantium_item_modifier", "オーランティウムのアイテム採掘強化");
            this.add("block.voidminersremastered.aurantium_miner", "オーランティウムの採掘機");
            this.add("block.voidminersremastered.aurantium_speed_modifier", "オーランティウムの採掘速度強化");
            this.add("block.voidminersremastered.azurine_block", "アズラインブロック");
            this.add("block.voidminersremastered.azurine_energy_modifier", "アズラインのエネルギー効率強化");
            this.add("block.voidminersremastered.azurine_frame", "アズラインの外枠");
            this.add("block.voidminersremastered.azurine_item_modifier", "アズラインのアイテム採掘強化");
            this.add("block.voidminersremastered.azurine_miner", "アズラインの採掘機");
            this.add("block.voidminersremastered.azurine_speed_modifier", "アズラインの採掘速度強化");
            this.add("block.voidminersremastered.caerium_block", "カエリウムブロック");
            this.add("block.voidminersremastered.caerium_energy_modifier", "カエリウムのエネルギー効率強化");
            this.add("block.voidminersremastered.caerium_frame", "カエリウムの外枠");
            this.add("block.voidminersremastered.caerium_item_modifier", "カエリウムのアイテム採掘強化");
            this.add("block.voidminersremastered.caerium_miner", "カエリウムの採掘機");
            this.add("block.voidminersremastered.caerium_speed_modifier", "カエリウムの採掘速度強化");
            this.add("block.voidminersremastered.citrinetine_block", "シトリネタインブロック");
            this.add("block.voidminersremastered.citrinetine_energy_modifier", "シトリネタインのエネルギー効率強化");
            this.add("block.voidminersremastered.citrinetine_frame", "シトリネタインの外枠");
            this.add("block.voidminersremastered.citrinetine_item_modifier", "シトリネタインのアイテム採掘強化");
            this.add("block.voidminersremastered.citrinetine_miner", "シトリネタインの採掘機");
            this.add("block.voidminersremastered.citrinetine_speed_modifier", "シトリネタインの採掘速度強化");
            this.add("block.voidminersremastered.frame_base", "外枠基礎");
            this.add("block.voidminersremastered.glass_panel", "採掘機用ガラス");
            this.add("block.voidminersremastered.null_modifier", "無効力強化");
            this.add("block.voidminersremastered.rosarium_block", "ローザリウムブロック");
            this.add("block.voidminersremastered.rosarium_energy_modifier", "ローザリウムのエネルギー効率強化");
            this.add("block.voidminersremastered.rosarium_frame", "ローザリウムの外枠");
            this.add("block.voidminersremastered.rosarium_item_modifier", "ローザリウムのアイテム採掘強化");
            this.add("block.voidminersremastered.rosarium_miner", "ローザリウムの採掘機");
            this.add("block.voidminersremastered.rosarium_speed_modifier", "ローザリウムの採掘速度強化");
            this.add("block.voidminersremastered.rubetine_block", "ルベタインブロック");
            this.add("block.voidminersremastered.rubetine_energy_modifier", "ルベタインのエネルギー効率強化");
            this.add("block.voidminersremastered.rubetine_frame", "ルベタインの外枠");
            this.add("block.voidminersremastered.rubetine_item_modifier", "ルベタインのアイテム採掘強化");
            this.add("block.voidminersremastered.rubetine_miner", "ルベタインの採掘機");
            this.add("block.voidminersremastered.rubetine_speed_modifier", "ルベタインの採掘速度強化");
            this.add("block.voidminersremastered.solar_amethystine_block", "Solar Amethystine Block");
            this.add("block.voidminersremastered.solar_amethystine_efficiency_modifier", "Solar Amethystine Efficiency Modifier");
            this.add("block.voidminersremastered.solar_amethystine_frame", "Solar Amethystine Frame");
            this.add("block.voidminersremastered.solar_amethystine_panel", "Solar Amethystine Panel");
            this.add("block.voidminersremastered.solar_amethystine_weather_modifier", "Solar Amethystine Weather Modifier");
            this.add("block.voidminersremastered.solar_aurantium_block", "Solar Aurantium Block");
            this.add("block.voidminersremastered.solar_aurantium_efficiency_modifier", "Solar Aurantium Efficiency Modifier");
            this.add("block.voidminersremastered.solar_aurantium_frame", "Solar Aurantium Frame");
            this.add("block.voidminersremastered.solar_aurantium_panel", "Solar Aurantium Panel");
            this.add("block.voidminersremastered.solar_aurantium_weather_modifier", "Solar Aurantium Weather Modifier");
            this.add("block.voidminersremastered.solar_azurine_block", "Solar Azurine Block");
            this.add("block.voidminersremastered.solar_azurine_efficiency_modifier", "Solar Azurine Efficiency Modifier");
            this.add("block.voidminersremastered.solar_azurine_frame", "Solar Azurine Frame");
            this.add("block.voidminersremastered.solar_azurine_panel", "Solar Azurine Panel");
            this.add("block.voidminersremastered.solar_azurine_weather_modifier", "Solar Azurine Weather Modifier");
            this.add("block.voidminersremastered.solar_caerium_block", "Solar Caerium Block");
            this.add("block.voidminersremastered.solar_caerium_efficiency_modifier", "Solar Caerium Efficiency Modifier");
            this.add("block.voidminersremastered.solar_caerium_frame", "Solar Caerium Frame");
            this.add("block.voidminersremastered.solar_caerium_panel", "Solar Caerium Panel");
            this.add("block.voidminersremastered.solar_caerium_weather_modifier", "Solar Caerium Weather Modifier");
            this.add("block.voidminersremastered.solar_citrinetine_block", "Solar Citrinetine Block");
            this.add("block.voidminersremastered.solar_citrinetine_efficiency_modifier", "Solar Citrinetine Efficiency Modifier");
            this.add("block.voidminersremastered.solar_citrinetine_frame", "Solar Citrinetine Frame");
            this.add("block.voidminersremastered.solar_citrinetine_panel", "Solar Citrinetine Panel");
            this.add("block.voidminersremastered.solar_citrinetine_weather_modifier", "Solar Citrinetine Weather Modifier");
            this.add("block.voidminersremastered.solar_rosarium_block", "Solar Rosarium Block");
            this.add("block.voidminersremastered.solar_rosarium_efficiency_modifier", "Solar Rosarium Efficiency Modifier");
            this.add("block.voidminersremastered.solar_rosarium_frame", "Solar Rosarium Frame");
            this.add("block.voidminersremastered.solar_rosarium_panel", "Solar Rosarium Panel");
            this.add("block.voidminersremastered.solar_rosarium_weather_modifier", "Solar Rosarium Weather Modifier");
            this.add("block.voidminersremastered.solar_rubetine_block", "Solar Rubetine Block");
            this.add("block.voidminersremastered.solar_rubetine_efficiency_modifier", "Solar Rubetine Efficiency Modifier");
            this.add("block.voidminersremastered.solar_rubetine_frame", "Solar Rubetine Frame");
            this.add("block.voidminersremastered.solar_rubetine_panel", "Solar Rubetine Panel");
            this.add("block.voidminersremastered.solar_rubetine_weather_modifier", "Solar Rubetine Weather Modifier");
            this.add("block.voidminersremastered.solar_ultimate_block", "Solar Ultimate Block");
            this.add("block.voidminersremastered.solar_ultimate_efficiency_modifier", "Solar Ultimate Efficiency Modifier");
            this.add("block.voidminersremastered.solar_ultimate_frame", "Solar Ultimate Frame");
            this.add("block.voidminersremastered.solar_ultimate_panel", "Ultimate Solar Panel");
            this.add("block.voidminersremastered.solar_ultimate_weather_modifier", "Solar Ultimate Weather Modifier");
            this.add("block.voidminersremastered.solar_verdium_block", "Solar Verdium Block");
            this.add("block.voidminersremastered.solar_verdium_efficiency_modifier", "Solar Verdium Efficiency Modifier");
            this.add("block.voidminersremastered.solar_verdium_frame", "Solar Verdium Frame");
            this.add("block.voidminersremastered.solar_verdium_panel", "Solar Verdium Panel");
            this.add("block.voidminersremastered.solar_verdium_weather_modifier", "Solar Verdium Weather Modifier");
            this.add("block.voidminersremastered.structure_panel", "構造基礎");
            this.add("block.voidminersremastered.ultimate_block", "Ultimate Block");
            this.add("block.voidminersremastered.ultimate_energy_modifier", "Ultimate Energy Modifier");
            this.add("block.voidminersremastered.ultimate_frame", "Ultimate Frame");
            this.add("block.voidminersremastered.ultimate_item_modifier", "Ultimate Item Modifier");
            this.add("block.voidminersremastered.ultimate_miner", "Ultimate Miner");
            this.add("block.voidminersremastered.ultimate_speed_modifier", "Ultimate Speed Modifier");
            this.add("block.voidminersremastered.verdium_block", "ヴェルディウムブロック");
            this.add("block.voidminersremastered.verdium_energy_modifier", "ヴェルディウムのエネルギー効率強化");
            this.add("block.voidminersremastered.verdium_frame", "ヴェルディウムの外枠");
            this.add("block.voidminersremastered.verdium_item_modifier", "ヴェルディウムのアイテム採掘強化");
            this.add("block.voidminersremastered.verdium_miner", "ヴェルディウムの採掘機");
            this.add("block.voidminersremastered.verdium_speed_modifier", "ヴェルディウムの採掘速度強化");
            this.add("client_message.voidminersremastered.max_storage_upgrades.upgrade_already_applied", "Same Upgrade already applied");
            this.add("client_message.voidminersremastered.max_storage_upgrades.upgrade_already_applied_is_higher_tier", "Cannot apply lower-tier upgrade while a higher-tier upgrade is installed");
            this.add("client_message.voidminersremastered.max_storage_upgrades.upgrade_applied", "Upgrade applied, Tier : %d");
            this.add("config.jade.plugin_voidminers.void_miner", "VoidMiners");
            this.add("config.jade.plugin_voidminersremastered.miners", "採掘機");
            this.add("config.jade.plugin_voidminersremastered.solars", "ソーラーパネル");
            this.add("creativetab.voidminersremastered.title", "Void Miners Remastered");
            this.add("gui.voidminersremastered.miner", "ティア%dの採掘機");
            this.add("gui.voidminersremastered.miners", "Miners");
            this.add("item.voidminersremastered.amethystine", "アメジスチン");
            this.add("item.voidminersremastered.aurantium", "オーランティウム");
            this.add("item.voidminersremastered.azurine", "アズライン");
            this.add("item.voidminersremastered.caerium", "カエリウム");
            this.add("item.voidminersremastered.citrinetine", "シトリネタイン");
            this.add("item.voidminersremastered.max_storage_upgrade_t1", "Max Storage Upgrade T1");
            this.add("item.voidminersremastered.max_storage_upgrade_t2", "Max Storage Upgrade T2");
            this.add("item.voidminersremastered.max_storage_upgrade_t3", "Max Storage Upgrade T3");
            this.add("item.voidminersremastered.rosarium", "ローザリウム");
            this.add("item.voidminersremastered.rubetine", "ルベタイン");
            this.add("item.voidminersremastered.solar_amethystine", "Solar Amethystine");
            this.add("item.voidminersremastered.solar_aurantium", "Solar Aurantium");
            this.add("item.voidminersremastered.solar_azurine", "Solar Azurine");
            this.add("item.voidminersremastered.solar_caerium", "Solar Caerium");
            this.add("item.voidminersremastered.solar_citrinetine", "Solar Citrinetine");
            this.add("item.voidminersremastered.solar_rosarium", "Solar Rosarium");
            this.add("item.voidminersremastered.solar_rubetine", "Solar Rubetine");
            this.add("item.voidminersremastered.solar_verdium", "Solar Verdium");
            this.add("item.voidminersremastered.structure_builder", "Structure Builder");
            this.add("item.voidminersremastered.ultimate_stellar_core", "Ultimate Stellar Core");
            this.add("item.voidminersremastered.verdium", "ヴェルディウム");
            this.add("jade.voidminersremastered.energy", "Energy: %s / %s FE");
            this.add("jade.voidminersremastered.halt_reason.halt_reason_not_found", "Reason not found");
            this.add("jade.voidminersremastered.halt_reason.no_bedrock_or_void_view", "Reason: Can't see Bedrock or Void");
            this.add("jade.voidminersremastered.halt_reason.no_recipes_in_dimension", "Reason: No recipes in dimension");
            this.add("jade.voidminersremastered.halt_reason.no_sky_view", "Reason: Can't see the sky");
            this.add("jade.voidminersremastered.halt_reason.not_enough_empty_slots", "Reason: Not enough empty slots");
            this.add("jade.voidminersremastered.halt_reason.not_enough_power", "Reason: Not enough power");
            this.add("jade.voidminersremastered.halt_reason.not_enough_power_buffer", "Reason: Not enough power buffer");
            this.add("jade.voidminersremastered.halt_reason.power_full", "Reason: Power buffer full");
            this.add("jade.voidminersremastered.halt_reason.structure_not_found", "Reason: Structure not found");
            this.add("jade.voidminersremastered.halt_reason.too_much_item_multiplier", "Reason: Item Multiplier too high");
            this.add("jade.voidminersremastered.progress", "Progress: %s%%");
            this.add("jade.voidminersremastered.status.idle", "Status: Idle");
            this.add("jade.voidminersremastered.status.working", "Status: Working");
            this.add("jade.voidminersremastered.storage_upgrade", "Storage Upgrade: %s");
            this.add("jade.voidminersremastered.tier", "Tier: %s");
            this.add("minecraft.overworld", "オーバーワールド");
            this.add("minecraft.the_end", "ジ・エンド");
            this.add("minecraft.the_nether", "ネザー");
            this.add("tooltip.voidminersremastered.controller.consumption", "§c⚡ CONSUMPTION: ");
            this.add("tooltip.voidminersremastered.controller.duration", "掘削間隔: %d ティック");
            this.add("tooltip.voidminersremastered.controller.efficiency", "☀ EFFICIENCY: ");
            this.add("tooltip.voidminersremastered.controller.efficiency_limited_by_rain", "Efficiency limited because of Rain.");
            this.add("tooltip.voidminersremastered.controller.efficiency_limited_by_thunder", "Efficiency limited because of Thunder.");
            this.add("tooltip.voidminersremastered.controller.efficiency_limited_by_time_of_day", "Efficiency limited because of the current time.");
            this.add("tooltip.voidminersremastered.controller.energy", "エネルギー: %d RF/t");
            this.add("tooltip.voidminersremastered.controller.generation", "⚡ GENERATION: ");
            this.add("tooltip.voidminersremastered.controller.halt_reason.dimension_not_ok", "§eThis miner doesn't have any recipes available for this dimension : %s§d)§e.\nCheck JEI for available recipes / dimensions.");
            this.add("tooltip.voidminersremastered.controller.halt_reason.no_bedrock_or_void_view", "Make sure that the miner can see the void / bedrock!\nThe distance does NOT matter, only that the center block can see void / bedrock.");
            this.add("tooltip.voidminersremastered.controller.halt_reason.no_sky_view", "Unable to see the sky, make sure there are no blocks above the Solar Panel\nDimension could also not have a \"day\" ie. The Nether / The End");
            this.add("tooltip.voidminersremastered.controller.halt_reason.not_enough_empty_slots", "All slots are full, the miner can't mine until it has been emptied.");
            this.add("tooltip.voidminersremastered.controller.halt_reason.structure_not_found", "Structure is not correct, sneak + right-click the Miner for a guide.\nTotal needed blocks: ");
            this.add("tooltip.voidminersremastered.controller.halt_reason.too_much_item_multiplier", "§eItem Multiplier too high, remove some of the blocks that add Item Multiplier.\nMax Item Multiplier: %d×");
            this.add("tooltip.voidminersremastered.controller.item_boost", "§d📦 ITEM BOOST: ");
            this.add("tooltip.voidminersremastered.controller.max_storage_upgrade_tip", "You can also add a better Max Storage Upgrade");
            this.add("tooltip.voidminersremastered.controller.progress", "§e⏳ PROGRESS: ");
            this.add("tooltip.voidminersremastered.controller.status.mining_impossible", "MINING IMPOSSIBLE");
            this.add("tooltip.voidminersremastered.controller.status.mining_slow", "MINING SLOW");
            this.add("tooltip.voidminersremastered.controller.status.mining_stopped", "MINING STOPPED");
            this.add("tooltip.voidminersremastered.controller.status.no_sky_view", "NO SKY VIEW / DIMENSION DOES NOT HAVE A \"DAY\"");
            this.add("tooltip.voidminersremastered.controller.status.not_enough_power", "Not enough power.");
            this.add("tooltip.voidminersremastered.controller.status.not_enough_power_buffer", "Not enough power buffer.\nEither use less Modifiers or use Energy Modifiers.");
            this.add("tooltip.voidminersremastered.controller.status.not_enough_power_for_next_operation", "Not enough power for next operation.");
            this.add("tooltip.voidminersremastered.controller.status.power_full", "POWER BUFFER FULL");
            this.add("tooltip.voidminersremastered.controller.status.status", "⚠ STATUS: ");
            this.add("tooltip.voidminersremastered.controller.status.structure_incomplete", "STRUCTURE INCOMPLETE");
            this.add("tooltip.voidminersremastered.controller.status.working", "WORKING");
            this.add("tooltip.voidminersremastered.efficiency", "Solar Efficiency: %s%%");
            this.add("tooltip.voidminersremastered.energy", "エネルギー倍率: %dx");
            this.add("tooltip.voidminersremastered.item", "アイテム倍率: %dx");
            this.add("tooltip.voidminersremastered.max_storage_upgrades", "Right-click a miner with this upgrade to apply it.\nIncreases the miner's output inventory by +%s slots.\nOnly the highest installed tier is applied\nNon-cumulative.\nConsumed on use.");
            this.add("tooltip.voidminersremastered.speed", "掘削間隔倍率: %dx");
            this.add("tooltip.voidminersremastered.structure.weight", "確率: %d");
            this.add("tooltip.voidminersremastered.structure_builder.instructions", "Sneak + right-click on a Controller to automatically build the Multiblock.");
            this.add("tooltip.voidminersremastered.structure_builder.missing_block_in_inventory", "Unable to place blocks because they are not in your inventory.\nMissing Blocks:");
            this.add("tooltip.voidminersremastered.structure_builder.unable_to_place_multiblock.1", "Unable to place the multiblock because other blocks are in the way.");
            this.add("tooltip.voidminersremastered.structure_builder.unable_to_place_multiblock.2", "Please clear the area where the multiblock will be made, you can sneak + right-click the Controller for a guide.");
            this.add("tooltip.voidminersremastered.weatherResistance", "Weather Resistance: %s%%");
        }
    }

    public static class ZhCn extends LanguageProvider {
        public ZhCn(PackOutput output) {
            super(output, VoidMinersRemastered.MODID, "zh_cn");
        }

        @Override
        protected void addTranslations() {
            this.add("block.voidminersremastered.amethystine_block", "紫晶石块");
            this.add("block.voidminersremastered.amethystine_energy_modifier", "紫晶石能量强化部件");
            this.add("block.voidminersremastered.amethystine_frame", "紫晶石框架");
            this.add("block.voidminersremastered.amethystine_item_modifier", "紫晶石物品强化部件");
            this.add("block.voidminersremastered.amethystine_miner", "紫晶石矿机");
            this.add("block.voidminersremastered.amethystine_speed_modifier", "紫晶石速度强化部件");
            this.add("block.voidminersremastered.aurantium_block", "奥金块");
            this.add("block.voidminersremastered.aurantium_energy_modifier", "奥金能量强化部件");
            this.add("block.voidminersremastered.aurantium_frame", "奥金框架");
            this.add("block.voidminersremastered.aurantium_item_modifier", "奥金物品强化部件");
            this.add("block.voidminersremastered.aurantium_miner", "奥金矿机");
            this.add("block.voidminersremastered.aurantium_speed_modifier", "奥金速度强化部件");
            this.add("block.voidminersremastered.azurine_block", "蔚蓝石块");
            this.add("block.voidminersremastered.azurine_energy_modifier", "蔚蓝石能量强化部件");
            this.add("block.voidminersremastered.azurine_frame", "蔚蓝石框架");
            this.add("block.voidminersremastered.azurine_item_modifier", "蔚蓝石物品强化部件");
            this.add("block.voidminersremastered.azurine_miner", "蔚蓝石矿机");
            this.add("block.voidminersremastered.azurine_speed_modifier", "蔚蓝石速度强化部件");
            this.add("block.voidminersremastered.caerium_block", "凯靛块");
            this.add("block.voidminersremastered.caerium_energy_modifier", "凯靛能量强化部件");
            this.add("block.voidminersremastered.caerium_frame", "凯靛框架");
            this.add("block.voidminersremastered.caerium_item_modifier", "凯靛物品强化部件");
            this.add("block.voidminersremastered.caerium_miner", "凯靛矿机");
            this.add("block.voidminersremastered.caerium_speed_modifier", "凯靛速度强化部件");
            this.add("block.voidminersremastered.citrinetine_block", "璨金石块");
            this.add("block.voidminersremastered.citrinetine_energy_modifier", "璨金石能量强化部件");
            this.add("block.voidminersremastered.citrinetine_frame", "璨金石框架");
            this.add("block.voidminersremastered.citrinetine_item_modifier", "璨金石物品强化部件");
            this.add("block.voidminersremastered.citrinetine_miner", "璨金石矿机");
            this.add("block.voidminersremastered.citrinetine_speed_modifier", "璨金石速度强化部件");
            this.add("block.voidminersremastered.frame_base", "基础框架");
            this.add("block.voidminersremastered.glass_panel", "玻璃板");
            this.add("block.voidminersremastered.null_modifier", "无效化强化部件");
            this.add("block.voidminersremastered.rosarium_block", "玫珞块");
            this.add("block.voidminersremastered.rosarium_energy_modifier", "玫珞能量强化部件");
            this.add("block.voidminersremastered.rosarium_frame", "玫珞框架");
            this.add("block.voidminersremastered.rosarium_item_modifier", "玫珞物品强化部件");
            this.add("block.voidminersremastered.rosarium_miner", "玫珞矿机");
            this.add("block.voidminersremastered.rosarium_speed_modifier", "玫珞速度强化部件");
            this.add("block.voidminersremastered.rubetine_block", "赤瑛石块");
            this.add("block.voidminersremastered.rubetine_energy_modifier", "赤瑛石能量强化部件");
            this.add("block.voidminersremastered.rubetine_frame", "赤瑛石框架");
            this.add("block.voidminersremastered.rubetine_item_modifier", "赤瑛石物品强化部件");
            this.add("block.voidminersremastered.rubetine_miner", "赤瑛石矿机");
            this.add("block.voidminersremastered.rubetine_speed_modifier", "赤瑛石速度强化部件");
            this.add("block.voidminersremastered.solar_amethystine_block", "Solar Amethystine Block");
            this.add("block.voidminersremastered.solar_amethystine_efficiency_modifier", "Solar Amethystine Efficiency Modifier");
            this.add("block.voidminersremastered.solar_amethystine_frame", "Solar Amethystine Frame");
            this.add("block.voidminersremastered.solar_amethystine_panel", "Solar Amethystine Panel");
            this.add("block.voidminersremastered.solar_amethystine_weather_modifier", "Solar Amethystine Weather Modifier");
            this.add("block.voidminersremastered.solar_aurantium_block", "Solar Aurantium Block");
            this.add("block.voidminersremastered.solar_aurantium_efficiency_modifier", "Solar Aurantium Efficiency Modifier");
            this.add("block.voidminersremastered.solar_aurantium_frame", "Solar Aurantium Frame");
            this.add("block.voidminersremastered.solar_aurantium_panel", "Solar Aurantium Panel");
            this.add("block.voidminersremastered.solar_aurantium_weather_modifier", "Solar Aurantium Weather Modifier");
            this.add("block.voidminersremastered.solar_azurine_block", "Solar Azurine Block");
            this.add("block.voidminersremastered.solar_azurine_efficiency_modifier", "Solar Azurine Efficiency Modifier");
            this.add("block.voidminersremastered.solar_azurine_frame", "Solar Azurine Frame");
            this.add("block.voidminersremastered.solar_azurine_panel", "Solar Azurine Panel");
            this.add("block.voidminersremastered.solar_azurine_weather_modifier", "Solar Azurine Weather Modifier");
            this.add("block.voidminersremastered.solar_caerium_block", "Solar Caerium Block");
            this.add("block.voidminersremastered.solar_caerium_efficiency_modifier", "Solar Caerium Efficiency Modifier");
            this.add("block.voidminersremastered.solar_caerium_frame", "Solar Caerium Frame");
            this.add("block.voidminersremastered.solar_caerium_panel", "Solar Caerium Panel");
            this.add("block.voidminersremastered.solar_caerium_weather_modifier", "Solar Caerium Weather Modifier");
            this.add("block.voidminersremastered.solar_citrinetine_block", "Solar Citrinetine Block");
            this.add("block.voidminersremastered.solar_citrinetine_efficiency_modifier", "Solar Citrinetine Efficiency Modifier");
            this.add("block.voidminersremastered.solar_citrinetine_frame", "Solar Citrinetine Frame");
            this.add("block.voidminersremastered.solar_citrinetine_panel", "Solar Citrinetine Panel");
            this.add("block.voidminersremastered.solar_citrinetine_weather_modifier", "Solar Citrinetine Weather Modifier");
            this.add("block.voidminersremastered.solar_rosarium_block", "Solar Rosarium Block");
            this.add("block.voidminersremastered.solar_rosarium_efficiency_modifier", "Solar Rosarium Efficiency Modifier");
            this.add("block.voidminersremastered.solar_rosarium_frame", "Solar Rosarium Frame");
            this.add("block.voidminersremastered.solar_rosarium_panel", "Solar Rosarium Panel");
            this.add("block.voidminersremastered.solar_rosarium_weather_modifier", "Solar Rosarium Weather Modifier");
            this.add("block.voidminersremastered.solar_rubetine_block", "Solar Rubetine Block");
            this.add("block.voidminersremastered.solar_rubetine_efficiency_modifier", "Solar Rubetine Efficiency Modifier");
            this.add("block.voidminersremastered.solar_rubetine_frame", "Solar Rubetine Frame");
            this.add("block.voidminersremastered.solar_rubetine_panel", "Solar Rubetine Panel");
            this.add("block.voidminersremastered.solar_rubetine_weather_modifier", "Solar Rubetine Weather Modifier");
            this.add("block.voidminersremastered.solar_ultimate_block", "Solar Ultimate Block");
            this.add("block.voidminersremastered.solar_ultimate_efficiency_modifier", "Solar Ultimate Efficiency Modifier");
            this.add("block.voidminersremastered.solar_ultimate_frame", "Solar Ultimate Frame");
            this.add("block.voidminersremastered.solar_ultimate_panel", "Ultimate Solar Panel");
            this.add("block.voidminersremastered.solar_ultimate_weather_modifier", "Solar Ultimate Weather Modifier");
            this.add("block.voidminersremastered.solar_verdium_block", "Solar Verdium Block");
            this.add("block.voidminersremastered.solar_verdium_efficiency_modifier", "Solar Verdium Efficiency Modifier");
            this.add("block.voidminersremastered.solar_verdium_frame", "Solar Verdium Frame");
            this.add("block.voidminersremastered.solar_verdium_panel", "Solar Verdium Panel");
            this.add("block.voidminersremastered.solar_verdium_weather_modifier", "Solar Verdium Weather Modifier");
            this.add("block.voidminersremastered.structure_panel", "结构板");
            this.add("block.voidminersremastered.ultimate_block", "Ultimate Block");
            this.add("block.voidminersremastered.ultimate_energy_modifier", "Ultimate Energy Modifier");
            this.add("block.voidminersremastered.ultimate_frame", "Ultimate Frame");
            this.add("block.voidminersremastered.ultimate_item_modifier", "Ultimate Item Modifier");
            this.add("block.voidminersremastered.ultimate_miner", "Ultimate Miner");
            this.add("block.voidminersremastered.ultimate_speed_modifier", "Ultimate Speed Modifier");
            this.add("block.voidminersremastered.verdium_block", "翠珀块");
            this.add("block.voidminersremastered.verdium_energy_modifier", "翠珀能量强化部件");
            this.add("block.voidminersremastered.verdium_frame", "翠珀框架");
            this.add("block.voidminersremastered.verdium_item_modifier", "翠珀物品强化部件");
            this.add("block.voidminersremastered.verdium_miner", "翠珀矿机");
            this.add("block.voidminersremastered.verdium_speed_modifier", "翠珀速度强化部件");
            this.add("client_message.voidminersremastered.max_storage_upgrades.upgrade_already_applied", "Same Upgrade already applied");
            this.add("client_message.voidminersremastered.max_storage_upgrades.upgrade_already_applied_is_higher_tier", "Cannot apply lower-tier upgrade while a higher-tier upgrade is installed");
            this.add("client_message.voidminersremastered.max_storage_upgrades.upgrade_applied", "Upgrade applied, Tier : %d");
            this.add("config.jade.plugin_voidminers.void_miner", "VoidMiners");
            this.add("config.jade.plugin_voidminersremastered.miners", "矿机");
            this.add("config.jade.plugin_voidminersremastered.solars", "太阳能板");
            this.add("creativetab.voidminersremastered.title", "Void Miners Remastered");
            this.add("gui.voidminersremastered.miner", "第%d阶矿机");
            this.add("gui.voidminersremastered.miners", "Miners");
            this.add("item.voidminersremastered.amethystine", "紫晶石");
            this.add("item.voidminersremastered.aurantium", "奥金");
            this.add("item.voidminersremastered.azurine", "蔚蓝石");
            this.add("item.voidminersremastered.caerium", "凯靛");
            this.add("item.voidminersremastered.citrinetine", "璨金石");
            this.add("item.voidminersremastered.max_storage_upgrade_t1", "Max Storage Upgrade T1");
            this.add("item.voidminersremastered.max_storage_upgrade_t2", "Max Storage Upgrade T2");
            this.add("item.voidminersremastered.max_storage_upgrade_t3", "Max Storage Upgrade T3");
            this.add("item.voidminersremastered.rosarium", "玫珞");
            this.add("item.voidminersremastered.rubetine", "赤瑛石");
            this.add("item.voidminersremastered.solar_amethystine", "Solar Amethystine");
            this.add("item.voidminersremastered.solar_aurantium", "Solar Aurantium");
            this.add("item.voidminersremastered.solar_azurine", "Solar Azurine");
            this.add("item.voidminersremastered.solar_caerium", "Solar Caerium");
            this.add("item.voidminersremastered.solar_citrinetine", "Solar Citrinetine");
            this.add("item.voidminersremastered.solar_rosarium", "Solar Rosarium");
            this.add("item.voidminersremastered.solar_rubetine", "Solar Rubetine");
            this.add("item.voidminersremastered.solar_verdium", "Solar Verdium");
            this.add("item.voidminersremastered.structure_builder", "Structure Builder");
            this.add("item.voidminersremastered.ultimate_stellar_core", "Ultimate Stellar Core");
            this.add("item.voidminersremastered.verdium", "翠珀");
            this.add("jade.voidminersremastered.energy", "Energy: %s / %s FE");
            this.add("jade.voidminersremastered.halt_reason.halt_reason_not_found", "Reason not found");
            this.add("jade.voidminersremastered.halt_reason.no_bedrock_or_void_view", "Reason: Can't see Bedrock or Void");
            this.add("jade.voidminersremastered.halt_reason.no_recipes_in_dimension", "Reason: No recipes in dimension");
            this.add("jade.voidminersremastered.halt_reason.no_sky_view", "Reason: Can't see the sky");
            this.add("jade.voidminersremastered.halt_reason.not_enough_empty_slots", "Reason: Not enough empty slots");
            this.add("jade.voidminersremastered.halt_reason.not_enough_power", "Reason: Not enough power");
            this.add("jade.voidminersremastered.halt_reason.not_enough_power_buffer", "Reason: Not enough power buffer");
            this.add("jade.voidminersremastered.halt_reason.power_full", "Reason: Power buffer full");
            this.add("jade.voidminersremastered.halt_reason.structure_not_found", "Reason: Structure not found");
            this.add("jade.voidminersremastered.halt_reason.too_much_item_multiplier", "Reason: Item Multiplier too high");
            this.add("jade.voidminersremastered.progress", "Progress: %s%%");
            this.add("jade.voidminersremastered.status.idle", "Status: Idle");
            this.add("jade.voidminersremastered.status.working", "Status: Working");
            this.add("jade.voidminersremastered.storage_upgrade", "Storage Upgrade: %s");
            this.add("jade.voidminersremastered.tier", "Tier: %s");
            this.add("minecraft.overworld", "主世界");
            this.add("minecraft.the_end", "末地");
            this.add("minecraft.the_nether", "下界");
            this.add("tooltip.voidminersremastered.controller.consumption", "§c⚡ CONSUMPTION: ");
            this.add("tooltip.voidminersremastered.controller.duration", "持续时间: %d ticks");
            this.add("tooltip.voidminersremastered.controller.efficiency", "☀ EFFICIENCY: ");
            this.add("tooltip.voidminersremastered.controller.efficiency_limited_by_rain", "Efficiency limited because of Rain.");
            this.add("tooltip.voidminersremastered.controller.efficiency_limited_by_thunder", "Efficiency limited because of Thunder.");
            this.add("tooltip.voidminersremastered.controller.efficiency_limited_by_time_of_day", "Efficiency limited because of the current time.");
            this.add("tooltip.voidminersremastered.controller.energy", "能量: %d rf/t");
            this.add("tooltip.voidminersremastered.controller.generation", "⚡ GENERATION: ");
            this.add("tooltip.voidminersremastered.controller.halt_reason.dimension_not_ok", "§eThis miner doesn't have any recipes available for this dimension : %s§d)§e.\nCheck JEI for available recipes / dimensions.");
            this.add("tooltip.voidminersremastered.controller.halt_reason.no_bedrock_or_void_view", "Make sure that the miner can see the void / bedrock!\nThe distance does NOT matter, only that the center block can see void / bedrock.");
            this.add("tooltip.voidminersremastered.controller.halt_reason.no_sky_view", "Unable to see the sky, make sure there are no blocks above the Solar Panel\nDimension could also not have a \"day\" ie. The Nether / The End");
            this.add("tooltip.voidminersremastered.controller.halt_reason.not_enough_empty_slots", "All slots are full, the miner can't mine until it has been emptied.");
            this.add("tooltip.voidminersremastered.controller.halt_reason.structure_not_found", "Structure is not correct, sneak + right-click the Miner for a guide.\nTotal needed blocks: ");
            this.add("tooltip.voidminersremastered.controller.halt_reason.too_much_item_multiplier", "§eItem Multiplier too high, remove some of the blocks that add Item Multiplier.\nMax Item Multiplier: %d×");
            this.add("tooltip.voidminersremastered.controller.item_boost", "§d📦 ITEM BOOST: ");
            this.add("tooltip.voidminersremastered.controller.max_storage_upgrade_tip", "You can also add a better Max Storage Upgrade");
            this.add("tooltip.voidminersremastered.controller.progress", "§e⏳ PROGRESS: ");
            this.add("tooltip.voidminersremastered.controller.status.mining_impossible", "MINING IMPOSSIBLE");
            this.add("tooltip.voidminersremastered.controller.status.mining_slow", "MINING SLOW");
            this.add("tooltip.voidminersremastered.controller.status.mining_stopped", "MINING STOPPED");
            this.add("tooltip.voidminersremastered.controller.status.no_sky_view", "NO SKY VIEW / DIMENSION DOES NOT HAVE A \"DAY\"");
            this.add("tooltip.voidminersremastered.controller.status.not_enough_power", "Not enough power.");
            this.add("tooltip.voidminersremastered.controller.status.not_enough_power_buffer", "Not enough power buffer.\nEither use less Modifiers or use Energy Modifiers.");
            this.add("tooltip.voidminersremastered.controller.status.not_enough_power_for_next_operation", "Not enough power for next operation.");
            this.add("tooltip.voidminersremastered.controller.status.power_full", "POWER BUFFER FULL");
            this.add("tooltip.voidminersremastered.controller.status.status", "⚠ STATUS: ");
            this.add("tooltip.voidminersremastered.controller.status.structure_incomplete", "STRUCTURE INCOMPLETE");
            this.add("tooltip.voidminersremastered.controller.status.working", "WORKING");
            this.add("tooltip.voidminersremastered.efficiency", "Solar Efficiency: %s%%");
            this.add("tooltip.voidminersremastered.energy", "能量强化部件: %dx");
            this.add("tooltip.voidminersremastered.item", "物品数量强化部件: %dx");
            this.add("tooltip.voidminersremastered.max_storage_upgrades", "Right-click a miner with this upgrade to apply it.\nIncreases the miner's output inventory by +%s slots.\nOnly the highest installed tier is applied\nNon-cumulative.\nConsumed on use.");
            this.add("tooltip.voidminersremastered.speed", "持续时间强化部件: %dx");
            this.add("tooltip.voidminersremastered.structure.weight", "重量: %d");
            this.add("tooltip.voidminersremastered.structure_builder.instructions", "Sneak + right-click on a Controller to automatically build the Multiblock.");
            this.add("tooltip.voidminersremastered.structure_builder.missing_block_in_inventory", "Unable to place blocks because they are not in your inventory.\nMissing Blocks:");
            this.add("tooltip.voidminersremastered.structure_builder.unable_to_place_multiblock.1", "Unable to place the multiblock because other blocks are in the way.");
            this.add("tooltip.voidminersremastered.structure_builder.unable_to_place_multiblock.2", "Please clear the area where the multiblock will be made, you can sneak + right-click the Controller for a guide.");
            this.add("tooltip.voidminersremastered.weatherResistance", "Weather Resistance: %s%%");
        }
    }

    public static class RuRu extends LanguageProvider {
        public RuRu(PackOutput output) {
            super(output, VoidMinersRemastered.MODID, "ru_ru");
        }

        @Override
        protected void addTranslations() {
            this.add("block.voidminersremastered.amethystine_block", "Аметистиновый блок");
            this.add("block.voidminersremastered.amethystine_energy_modifier", "Аметистиновое улучшение энергии");
            this.add("block.voidminersremastered.amethystine_frame", "Аметистиновый каркас");
            this.add("block.voidminersremastered.amethystine_item_modifier", "Аметистиновое предметное улучшение");
            this.add("block.voidminersremastered.amethystine_miner", "Аметистиновый шахтёр");
            this.add("block.voidminersremastered.amethystine_speed_modifier", "Аметистиновое улучшение скорости");
            this.add("block.voidminersremastered.aurantium_block", "Аурантиевый блок");
            this.add("block.voidminersremastered.aurantium_energy_modifier", "Аурантиевое улучшение энергии");
            this.add("block.voidminersremastered.aurantium_frame", "Аурантиевый каркас");
            this.add("block.voidminersremastered.aurantium_item_modifier", "Аурантиевое предметное улучшение");
            this.add("block.voidminersremastered.aurantium_miner", "Аурантиевый шахтёр");
            this.add("block.voidminersremastered.aurantium_speed_modifier", "Аурантиевое улучшение скорости");
            this.add("block.voidminersremastered.azurine_block", "Азуриновый блок");
            this.add("block.voidminersremastered.azurine_energy_modifier", "Азуриновое улучшение энергии");
            this.add("block.voidminersremastered.azurine_frame", "Азуриновый каркас");
            this.add("block.voidminersremastered.azurine_item_modifier", "Азуриновое предметное улучшение");
            this.add("block.voidminersremastered.azurine_miner", "Азуриновый шахтёр");
            this.add("block.voidminersremastered.azurine_speed_modifier", "Азуриновое улучшение скорости");
            this.add("block.voidminersremastered.caerium_block", "Каэриевый блок");
            this.add("block.voidminersremastered.caerium_energy_modifier", "Каэриевое улучшение энергии");
            this.add("block.voidminersremastered.caerium_frame", "Каэриевый каркас");
            this.add("block.voidminersremastered.caerium_item_modifier", "Каэриевое предметное улучшение");
            this.add("block.voidminersremastered.caerium_miner", "Каэриевый шахтёр");
            this.add("block.voidminersremastered.caerium_speed_modifier", "Каэриевое улучшение скорости");
            this.add("block.voidminersremastered.citrinetine_block", "Цитринетиновый блок");
            this.add("block.voidminersremastered.citrinetine_energy_modifier", "Цитринетиновое улучшение энергии");
            this.add("block.voidminersremastered.citrinetine_frame", "Цитринетиновый каркас");
            this.add("block.voidminersremastered.citrinetine_item_modifier", "Цитринетиновое предметное улучшение");
            this.add("block.voidminersremastered.citrinetine_miner", "Цитринетиновый шахтёр");
            this.add("block.voidminersremastered.citrinetine_speed_modifier", "Цитринетиновое улучшение скорости");
            this.add("block.voidminersremastered.frame_base", "Базовый каркас");
            this.add("block.voidminersremastered.glass_panel", "Стеклянная панель");
            this.add("block.voidminersremastered.null_modifier", "Нулевой модификатор");
            this.add("block.voidminersremastered.rosarium_block", "Розариевый блок");
            this.add("block.voidminersremastered.rosarium_energy_modifier", "Розариевое улучшение энергии");
            this.add("block.voidminersremastered.rosarium_frame", "Розариевый каркас");
            this.add("block.voidminersremastered.rosarium_item_modifier", "Розариевый предметное улучшение Modifier");
            this.add("block.voidminersremastered.rosarium_miner", "Розариевый шахтёр");
            this.add("block.voidminersremastered.rosarium_speed_modifier", "Розариевое улучшение скорости");
            this.add("block.voidminersremastered.rubetine_block", "Рубетиновый блок");
            this.add("block.voidminersremastered.rubetine_energy_modifier", "Рубетиновое улучшение энергии");
            this.add("block.voidminersremastered.rubetine_frame", "Рубетиновый каркас");
            this.add("block.voidminersremastered.rubetine_item_modifier", "Рубетиновое предметное улучшение");
            this.add("block.voidminersremastered.rubetine_miner", "Рубетиновый шахтёр");
            this.add("block.voidminersremastered.rubetine_speed_modifier", "Рубетиновое улучшение скорости");
            this.add("block.voidminersremastered.solar_amethystine_block", "Solar Amethystine Block");
            this.add("block.voidminersremastered.solar_amethystine_efficiency_modifier", "Solar Amethystine Efficiency Modifier");
            this.add("block.voidminersremastered.solar_amethystine_frame", "Solar Amethystine Frame");
            this.add("block.voidminersremastered.solar_amethystine_panel", "Solar Amethystine Panel");
            this.add("block.voidminersremastered.solar_amethystine_weather_modifier", "Solar Amethystine Weather Modifier");
            this.add("block.voidminersremastered.solar_aurantium_block", "Solar Aurantium Block");
            this.add("block.voidminersremastered.solar_aurantium_efficiency_modifier", "Solar Aurantium Efficiency Modifier");
            this.add("block.voidminersremastered.solar_aurantium_frame", "Solar Aurantium Frame");
            this.add("block.voidminersremastered.solar_aurantium_panel", "Solar Aurantium Panel");
            this.add("block.voidminersremastered.solar_aurantium_weather_modifier", "Solar Aurantium Weather Modifier");
            this.add("block.voidminersremastered.solar_azurine_block", "Solar Azurine Block");
            this.add("block.voidminersremastered.solar_azurine_efficiency_modifier", "Solar Azurine Efficiency Modifier");
            this.add("block.voidminersremastered.solar_azurine_frame", "Solar Azurine Frame");
            this.add("block.voidminersremastered.solar_azurine_panel", "Solar Azurine Panel");
            this.add("block.voidminersremastered.solar_azurine_weather_modifier", "Solar Azurine Weather Modifier");
            this.add("block.voidminersremastered.solar_caerium_block", "Solar Caerium Block");
            this.add("block.voidminersremastered.solar_caerium_efficiency_modifier", "Solar Caerium Efficiency Modifier");
            this.add("block.voidminersremastered.solar_caerium_frame", "Solar Caerium Frame");
            this.add("block.voidminersremastered.solar_caerium_panel", "Solar Caerium Panel");
            this.add("block.voidminersremastered.solar_caerium_weather_modifier", "Solar Caerium Weather Modifier");
            this.add("block.voidminersremastered.solar_citrinetine_block", "Solar Citrinetine Block");
            this.add("block.voidminersremastered.solar_citrinetine_efficiency_modifier", "Solar Citrinetine Efficiency Modifier");
            this.add("block.voidminersremastered.solar_citrinetine_frame", "Solar Citrinetine Frame");
            this.add("block.voidminersremastered.solar_citrinetine_panel", "Solar Citrinetine Panel");
            this.add("block.voidminersremastered.solar_citrinetine_weather_modifier", "Solar Citrinetine Weather Modifier");
            this.add("block.voidminersremastered.solar_rosarium_block", "Solar Rosarium Block");
            this.add("block.voidminersremastered.solar_rosarium_efficiency_modifier", "Solar Rosarium Efficiency Modifier");
            this.add("block.voidminersremastered.solar_rosarium_frame", "Solar Rosarium Frame");
            this.add("block.voidminersremastered.solar_rosarium_panel", "Solar Rosarium Panel");
            this.add("block.voidminersremastered.solar_rosarium_weather_modifier", "Solar Rosarium Weather Modifier");
            this.add("block.voidminersremastered.solar_rubetine_block", "Solar Rubetine Block");
            this.add("block.voidminersremastered.solar_rubetine_efficiency_modifier", "Solar Rubetine Efficiency Modifier");
            this.add("block.voidminersremastered.solar_rubetine_frame", "Solar Rubetine Frame");
            this.add("block.voidminersremastered.solar_rubetine_panel", "Solar Rubetine Panel");
            this.add("block.voidminersremastered.solar_rubetine_weather_modifier", "Solar Rubetine Weather Modifier");
            this.add("block.voidminersremastered.solar_ultimate_block", "Solar Ultimate Block");
            this.add("block.voidminersremastered.solar_ultimate_efficiency_modifier", "Solar Ultimate Efficiency Modifier");
            this.add("block.voidminersremastered.solar_ultimate_frame", "Solar Ultimate Frame");
            this.add("block.voidminersremastered.solar_ultimate_panel", "Ultimate Solar Panel");
            this.add("block.voidminersremastered.solar_ultimate_weather_modifier", "Solar Ultimate Weather Modifier");
            this.add("block.voidminersremastered.solar_verdium_block", "Solar Verdium Block");
            this.add("block.voidminersremastered.solar_verdium_efficiency_modifier", "Solar Verdium Efficiency Modifier");
            this.add("block.voidminersremastered.solar_verdium_frame", "Solar Verdium Frame");
            this.add("block.voidminersremastered.solar_verdium_panel", "Solar Verdium Panel");
            this.add("block.voidminersremastered.solar_verdium_weather_modifier", "Solar Verdium Weather Modifier");
            this.add("block.voidminersremastered.structure_panel", "Структурная панель");
            this.add("block.voidminersremastered.ultimate_block", "Ultimate Block");
            this.add("block.voidminersremastered.ultimate_energy_modifier", "Ultimate Energy Modifier");
            this.add("block.voidminersremastered.ultimate_frame", "Ultimate Frame");
            this.add("block.voidminersremastered.ultimate_item_modifier", "Ultimate Item Modifier");
            this.add("block.voidminersremastered.ultimate_miner", "Ultimate Miner");
            this.add("block.voidminersremastered.ultimate_speed_modifier", "Ultimate Speed Modifier");
            this.add("block.voidminersremastered.verdium_block", "Вердиумовый блок");
            this.add("block.voidminersremastered.verdium_energy_modifier", "Вердиумовое улучшение энергии");
            this.add("block.voidminersremastered.verdium_frame", "Вердиумовый каркас");
            this.add("block.voidminersremastered.verdium_item_modifier", "Вердиумовое предметное улучшение");
            this.add("block.voidminersremastered.verdium_miner", "Вердиумовый шахтёр");
            this.add("block.voidminersremastered.verdium_speed_modifier", "Вердиумовое улучшение скорости");
            this.add("client_message.voidminersremastered.max_storage_upgrades.upgrade_already_applied", "Same Upgrade already applied");
            this.add("client_message.voidminersremastered.max_storage_upgrades.upgrade_already_applied_is_higher_tier", "Cannot apply lower-tier upgrade while a higher-tier upgrade is installed");
            this.add("client_message.voidminersremastered.max_storage_upgrades.upgrade_applied", "Upgrade applied, Tier : %d");
            this.add("config.jade.plugin_voidminers.void_miner", "VoidMiners");
            this.add("config.jade.plugin_voidminersremastered.miners", "Майнеры");
            this.add("config.jade.plugin_voidminersremastered.solars", "Солнечные панели");
            this.add("creativetab.voidminersremastered.title", "Void Miners Remastered");
            this.add("gui.voidminersremastered.miner", "Шахтёр %d уровня");
            this.add("gui.voidminersremastered.miners", "Шахтёры");
            this.add("item.voidminersremastered.amethystine", "Аметистин");
            this.add("item.voidminersremastered.aurantium", "Аурантиум");
            this.add("item.voidminersremastered.azurine", "Азурин");
            this.add("item.voidminersremastered.caerium", "Каэрий");
            this.add("item.voidminersremastered.citrinetine", "Цитринетин");
            this.add("item.voidminersremastered.max_storage_upgrade_t1", "Max Storage Upgrade T1");
            this.add("item.voidminersremastered.max_storage_upgrade_t2", "Max Storage Upgrade T2");
            this.add("item.voidminersremastered.max_storage_upgrade_t3", "Max Storage Upgrade T3");
            this.add("item.voidminersremastered.rosarium", "Розарий");
            this.add("item.voidminersremastered.rubetine", "Рубетин");
            this.add("item.voidminersremastered.solar_amethystine", "Solar Amethystine");
            this.add("item.voidminersremastered.solar_aurantium", "Solar Aurantium");
            this.add("item.voidminersremastered.solar_azurine", "Solar Azurine");
            this.add("item.voidminersremastered.solar_caerium", "Solar Caerium");
            this.add("item.voidminersremastered.solar_citrinetine", "Solar Citrinetine");
            this.add("item.voidminersremastered.solar_rosarium", "Solar Rosarium");
            this.add("item.voidminersremastered.solar_rubetine", "Solar Rubetine");
            this.add("item.voidminersremastered.solar_verdium", "Solar Verdium");
            this.add("item.voidminersremastered.structure_builder", "Structure Builder");
            this.add("item.voidminersremastered.ultimate_stellar_core", "Ultimate Stellar Core");
            this.add("item.voidminersremastered.verdium", "Вердиум");
            this.add("jade.voidminersremastered.energy", "Energy: %s / %s FE");
            this.add("jade.voidminersremastered.halt_reason.halt_reason_not_found", "Reason not found");
            this.add("jade.voidminersremastered.halt_reason.no_bedrock_or_void_view", "Reason: Can't see Bedrock or Void");
            this.add("jade.voidminersremastered.halt_reason.no_recipes_in_dimension", "Reason: No recipes in dimension");
            this.add("jade.voidminersremastered.halt_reason.no_sky_view", "Reason: Can't see the sky");
            this.add("jade.voidminersremastered.halt_reason.not_enough_empty_slots", "Reason: Not enough empty slots");
            this.add("jade.voidminersremastered.halt_reason.not_enough_power", "Reason: Not enough power");
            this.add("jade.voidminersremastered.halt_reason.not_enough_power_buffer", "Reason: Not enough power buffer");
            this.add("jade.voidminersremastered.halt_reason.power_full", "Reason: Power buffer full");
            this.add("jade.voidminersremastered.halt_reason.structure_not_found", "Reason: Structure not found");
            this.add("jade.voidminersremastered.halt_reason.too_much_item_multiplier", "Reason: Item Multiplier too high");
            this.add("jade.voidminersremastered.progress", "Progress: %s%%");
            this.add("jade.voidminersremastered.status.idle", "Status: Idle");
            this.add("jade.voidminersremastered.status.working", "Status: Working");
            this.add("jade.voidminersremastered.storage_upgrade", "Storage Upgrade: %s");
            this.add("jade.voidminersremastered.tier", "Tier: %s");
            this.add("minecraft.overworld", "Верхний мир");
            this.add("minecraft.the_end", "Эндер мир");
            this.add("minecraft.the_nether", "Нижний мир");
            this.add("tooltip.voidminersremastered.controller.consumption", "§c⚡ CONSUMPTION: ");
            this.add("tooltip.voidminersremastered.controller.duration", "Каждые: %d тиков");
            this.add("tooltip.voidminersremastered.controller.efficiency", "☀ EFFICIENCY: ");
            this.add("tooltip.voidminersremastered.controller.efficiency_limited_by_rain", "Efficiency limited because of Rain.");
            this.add("tooltip.voidminersremastered.controller.efficiency_limited_by_thunder", "Efficiency limited because of Thunder.");
            this.add("tooltip.voidminersremastered.controller.efficiency_limited_by_time_of_day", "Efficiency limited because of the current time.");
            this.add("tooltip.voidminersremastered.controller.energy", "Энергия: %d rf/t");
            this.add("tooltip.voidminersremastered.controller.generation", "⚡ GENERATION: ");
            this.add("tooltip.voidminersremastered.controller.halt_reason.dimension_not_ok", "§eThis miner doesn't have any recipes available for this dimension : %s§d)§e.\nCheck JEI for available recipes / dimensions.");
            this.add("tooltip.voidminersremastered.controller.halt_reason.no_bedrock_or_void_view", "Make sure that the miner can see the void / bedrock!\nThe distance does NOT matter, only that the center block can see void / bedrock.");
            this.add("tooltip.voidminersremastered.controller.halt_reason.no_sky_view", "Unable to see the sky, make sure there are no blocks above the Solar Panel\nDimension could also not have a \"day\" ie. The Nether / The End");
            this.add("tooltip.voidminersremastered.controller.halt_reason.not_enough_empty_slots", "All slots are full, the miner can't mine until it has been emptied.");
            this.add("tooltip.voidminersremastered.controller.halt_reason.structure_not_found", "Structure is not correct, sneak + right-click the Miner for a guide.\nTotal needed blocks: ");
            this.add("tooltip.voidminersremastered.controller.halt_reason.too_much_item_multiplier", "§eItem Multiplier too high, remove some of the blocks that add Item Multiplier.\nMax Item Multiplier: %d×");
            this.add("tooltip.voidminersremastered.controller.item_boost", "§d📦 ITEM BOOST: ");
            this.add("tooltip.voidminersremastered.controller.max_storage_upgrade_tip", "You can also add a better Max Storage Upgrade");
            this.add("tooltip.voidminersremastered.controller.progress", "§e⏳ PROGRESS: ");
            this.add("tooltip.voidminersremastered.controller.status.mining_impossible", "MINING IMPOSSIBLE");
            this.add("tooltip.voidminersremastered.controller.status.mining_slow", "MINING SLOW");
            this.add("tooltip.voidminersremastered.controller.status.mining_stopped", "MINING STOPPED");
            this.add("tooltip.voidminersremastered.controller.status.no_sky_view", "NO SKY VIEW / DIMENSION DOES NOT HAVE A \"DAY\"");
            this.add("tooltip.voidminersremastered.controller.status.not_enough_power", "Not enough power.");
            this.add("tooltip.voidminersremastered.controller.status.not_enough_power_buffer", "Not enough power buffer.\nEither use less Modifiers or use Energy Modifiers.");
            this.add("tooltip.voidminersremastered.controller.status.not_enough_power_for_next_operation", "Not enough power for next operation.");
            this.add("tooltip.voidminersremastered.controller.status.power_full", "POWER BUFFER FULL");
            this.add("tooltip.voidminersremastered.controller.status.status", "⚠ STATUS: ");
            this.add("tooltip.voidminersremastered.controller.status.structure_incomplete", "STRUCTURE INCOMPLETE");
            this.add("tooltip.voidminersremastered.controller.status.working", "WORKING");
            this.add("tooltip.voidminersremastered.efficiency", "Solar Efficiency: %s%%");
            this.add("tooltip.voidminersremastered.energy", "Модификатор энергии: %dx");
            this.add("tooltip.voidminersremastered.item", "Модификатор количества предметов: %dx");
            this.add("tooltip.voidminersremastered.max_storage_upgrades", "Right-click a miner with this upgrade to apply it.\nIncreases the miner's output inventory by +%s slots.\nOnly the highest installed tier is applied\nNon-cumulative.\nConsumed on use.");
            this.add("tooltip.voidminersremastered.speed", "Модификатор скорости: %dx");
            this.add("tooltip.voidminersremastered.structure.weight", "Масса: %d");
            this.add("tooltip.voidminersremastered.structure_builder.instructions", "Sneak + right-click on a Controller to automatically build the Multiblock.");
            this.add("tooltip.voidminersremastered.structure_builder.missing_block_in_inventory", "Unable to place blocks because they are not in your inventory.\nMissing Blocks:");
            this.add("tooltip.voidminersremastered.structure_builder.unable_to_place_multiblock.1", "Unable to place the multiblock because other blocks are in the way.");
            this.add("tooltip.voidminersremastered.structure_builder.unable_to_place_multiblock.2", "Please clear the area where the multiblock will be made, you can sneak + right-click the Controller for a guide.");
            this.add("tooltip.voidminersremastered.weatherResistance", "Weather Resistance: %s%%");
        }
    }

}