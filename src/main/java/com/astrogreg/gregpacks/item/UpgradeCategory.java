package com.astrogreg.gregpacks.item;

/*
 * determines the family an upgrade belongs to
 * only one upgrade per category can be installed at a time
 * single module categories like feeding module behave as exact-type blocks
 */
public enum UpgradeCategory {

    ITEM_CAPACITY,
    FLUID_CAPACITY,
    ENERGY_CAPACITY,
    MAGNET,
    JETPACK,
    FEEDING,
    MAINTENANCE,
    CRAFTING,
    PROCESSING(false);

    private final boolean exclusive;

    UpgradeCategory() {
        this(true);
    }

    UpgradeCategory(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public boolean isExclusive() {
        return exclusive;
    }
}