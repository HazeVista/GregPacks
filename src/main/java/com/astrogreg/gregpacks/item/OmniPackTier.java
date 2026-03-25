package com.astrogreg.gregpacks.item;

public enum OmniPackTier {

    BASIC("basic", 30, 200_000, 6, 32_000),
    ADVANCED("advanced", 50, 1_000_000, 10, 128_000),
    ELITE("elite", 100, 200_000_000, 16, 512_000);

    private final String id;
    public final int defaultSlots;
    public final int defaultEnergyStorage;
    public final int defaultFluidStorage;
    public final int defaultMaxUpgrades;

    OmniPackTier(String id, int defaultSlots, int defaultEnergyStorage, int defaultMaxUpgrades,
                 int defaultFluidStorage) {
        this.id = id;
        this.defaultSlots = defaultSlots;
        this.defaultEnergyStorage = defaultEnergyStorage;
        this.defaultMaxUpgrades = defaultMaxUpgrades;
        this.defaultFluidStorage = defaultFluidStorage;
    }

    public String itemId() {
        return id + "_omnipack";
    }
}
