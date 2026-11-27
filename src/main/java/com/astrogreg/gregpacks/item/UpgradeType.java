package com.astrogreg.gregpacks.item;


//Only one upgrade of each type can be installed at a time
public enum UpgradeType {

    // Capacity upgrades
    ITEM_CAPACITY_I(UpgradeCategory.ITEM_CAPACITY),
    ITEM_CAPACITY_II(UpgradeCategory.ITEM_CAPACITY),
    ITEM_CAPACITY_III(UpgradeCategory.ITEM_CAPACITY),

    FLUID_CAPACITY_I(UpgradeCategory.FLUID_CAPACITY),
    FLUID_CAPACITY_II(UpgradeCategory.FLUID_CAPACITY),
    FLUID_CAPACITY_III(UpgradeCategory.FLUID_CAPACITY),

    ENERGY_CAPACITY_I(UpgradeCategory.ENERGY_CAPACITY),
    ENERGY_CAPACITY_II(UpgradeCategory.ENERGY_CAPACITY),
    ENERGY_CAPACITY_III(UpgradeCategory.ENERGY_CAPACITY),

    // Passive Utility
    MAGNET_I(UpgradeCategory.MAGNET),
    MAGNET_II(UpgradeCategory.MAGNET),
    FEEDING(UpgradeCategory.FEEDING),
    MAINTENANCE(UpgradeCategory.MAINTENANCE),

    // Active (implemented later)
    JETPACK_I(UpgradeCategory.JETPACK),
    JETPACK_II(UpgradeCategory.JETPACK),
    CRAFTING(UpgradeCategory.CRAFTING),
    PROCESSING(UpgradeCategory.PROCESSING);

    private final UpgradeCategory category;

    UpgradeType(UpgradeCategory category) {
        this.category = category;
    }

    public UpgradeCategory category() {
        return category;
    }
}