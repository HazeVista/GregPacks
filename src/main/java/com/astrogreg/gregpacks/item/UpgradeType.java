package com.astrogreg.gregpacks.item;

/**
 * Identifies each unique upgrade type.
 * Only one upgrade of each type can be installed at a time.
 */
public enum UpgradeType {

    // Capacity upgrades
    ITEM_CAPACITY_I,
    ITEM_CAPACITY_II,
    ITEM_CAPACITY_III,

    FLUID_CAPACITY_I,
    FLUID_CAPACITY_II,
    FLUID_CAPACITY_III,

    ENERGY_CAPACITY_I,
    ENERGY_CAPACITY_II,
    ENERGY_CAPACITY_III,

    // Passive Utility
    MAGNET_I,
    MAGNET_II,
    FEEDING,
    MAINTENANCE,

    // Active (implemented later)
    JETPACK_I,
    JETPACK_II,
    CRAFTING,
    PROCESSING;
}
