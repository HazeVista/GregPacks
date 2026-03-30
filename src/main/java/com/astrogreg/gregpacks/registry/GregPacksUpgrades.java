package com.astrogreg.gregpacks.registry;

import com.astrogreg.gregpacks.GregPacks;
import com.astrogreg.gregpacks.item.UpgradeItem;
import com.astrogreg.gregpacks.item.UpgradeType;
import com.tterrag.registrate.util.entry.ItemEntry;

@SuppressWarnings("all")
public class GregPacksUpgrades {

    // Item Capacity
    public static final ItemEntry<UpgradeItem> ITEM_CAPACITY_I = GregPacks.REGISTRATE
            .item("item_module_1", p -> new UpgradeItem(UpgradeType.ITEM_CAPACITY_I, "item.gregpacks.item_module_1.tooltip"))
            .lang("Item Capacity Module I").register();

    public static final ItemEntry<UpgradeItem> ITEM_CAPACITY_II = GregPacks.REGISTRATE
            .item("item_module_2", p -> new UpgradeItem(UpgradeType.ITEM_CAPACITY_II, "item.gregpacks.item_module_2.tooltip"))
            .lang("Item Capacity Module II").register();

    public static final ItemEntry<UpgradeItem> ITEM_CAPACITY_III = GregPacks.REGISTRATE
            .item("item_module_3", p -> new UpgradeItem(UpgradeType.ITEM_CAPACITY_III, "item.gregpacks.item_module_3.tooltip"))
            .lang("Item Capacity Module III").register();

    // Fluid Capacity
    public static final ItemEntry<UpgradeItem> FLUID_CAPACITY_I = GregPacks.REGISTRATE
            .item("fluid_module_1", p -> new UpgradeItem(UpgradeType.FLUID_CAPACITY_I, "item.gregpacks.fluid_module_1.tooltip"))
            .lang("Fluid Capacity Module I").register();

    public static final ItemEntry<UpgradeItem> FLUID_CAPACITY_II = GregPacks.REGISTRATE
            .item("fluid_module_2", p -> new UpgradeItem(UpgradeType.FLUID_CAPACITY_II, "item.gregpacks.fluid_module_2.tooltip"))
            .lang("Fluid Capacity Module II").register();

    public static final ItemEntry<UpgradeItem> FLUID_CAPACITY_III = GregPacks.REGISTRATE
            .item("fluid_module_3", p -> new UpgradeItem(UpgradeType.FLUID_CAPACITY_III, "item.gregpacks.fluid_module_3.tooltip"))
            .lang("Fluid Capacity Module III").register();

    // Energy Capacity
    public static final ItemEntry<UpgradeItem> ENERGY_CAPACITY_I = GregPacks.REGISTRATE
            .item("energy_module_1", p -> new UpgradeItem(UpgradeType.ENERGY_CAPACITY_I, "item.gregpacks.energy_module_1.tooltip"))
            .lang("Energy Capacity Module I").register();

    public static final ItemEntry<UpgradeItem> ENERGY_CAPACITY_II = GregPacks.REGISTRATE
            .item("energy_module_2", p -> new UpgradeItem(UpgradeType.ENERGY_CAPACITY_II, "item.gregpacks.energy_module_2.tooltip"))
            .lang("Energy Capacity Module II").register();

    public static final ItemEntry<UpgradeItem> ENERGY_CAPACITY_III = GregPacks.REGISTRATE
            .item("energy_module_3", p -> new UpgradeItem(UpgradeType.ENERGY_CAPACITY_III, "item.gregpacks.energy_module_3.tooltip"))
            .lang("Energy Capacity Module III").register();

    // Utility
    public static final ItemEntry<UpgradeItem> CRAFTING_MODULE = GregPacks.REGISTRATE
            .item("crafting_module", p -> new UpgradeItem(UpgradeType.CRAFTING, "item.gregpacks.crafting_module.tooltip"))
            .lang("Crafting Module").register();

    public static final ItemEntry<UpgradeItem> FEEDING_MODULE = GregPacks.REGISTRATE
            .item("feeding_module", p -> new UpgradeItem(UpgradeType.FEEDING, "item.gregpacks.feeding_module.tooltip"))
            .lang("Feeding Module").register();

    public static final ItemEntry<UpgradeItem> JETPACK_MODULE_I = GregPacks.REGISTRATE
            .item("jetpack_module_1", p -> new UpgradeItem(UpgradeType.JETPACK_I, "item.gregpacks.jetpack_module.tooltip"))
            .lang("Jetpack Module I").register();

    public static final ItemEntry<UpgradeItem> JETPACK_MODULE_II = GregPacks.REGISTRATE
            .item("jetpack_module_2", p -> new UpgradeItem(UpgradeType.JETPACK_II, "item.gregpacks.jetpack_module.tooltip"))
            .lang("Jetpack Module II").register();

    public static final ItemEntry<UpgradeItem> MAINTENANCE_MODULE = GregPacks.REGISTRATE
            .item("maintenance_module", p -> new UpgradeItem(UpgradeType.MAINTENANCE, "item.gregpacks.maintenance_module.tooltip"))
            .lang("Maintenance Module").register();

    public static final ItemEntry<UpgradeItem> MAGNET_MODULE_I = GregPacks.REGISTRATE
            .item("magnet_module_1", p -> new UpgradeItem(UpgradeType.MAGNET_I, "item.gregpacks.magnet_module_1.tooltip"))
            .lang("Magnet Module I").register();

    public static final ItemEntry<UpgradeItem> MAGNET_MODULE_II = GregPacks.REGISTRATE
            .item("magnet_module_2", p -> new UpgradeItem(UpgradeType.MAGNET_II, "item.gregpacks.magnet_module_2.tooltip"))
            .lang("Magnet Module II").register();

    public static final ItemEntry<UpgradeItem> PROCESSING_MODULE = GregPacks.REGISTRATE
            .item("processing_module", p -> new UpgradeItem(UpgradeType.PROCESSING, "item.gregpacks.processing_module.tooltip"))
            .lang("Processing Module").register();

    public static void init() {}
}