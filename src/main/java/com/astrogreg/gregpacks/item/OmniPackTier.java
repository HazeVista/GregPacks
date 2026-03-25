package com.astrogreg.gregpacks.item;

import com.astrogreg.gregpacks.config.GregPacksConfig;

public enum OmniPackTier {

    BASIC("basic",
            GregPacksConfig.INSTANCE.OmniPackBaseValues.basicPack.basicPackItemSlots,
            GregPacksConfig.INSTANCE.OmniPackBaseValues.basicPack.basicPackEUStorage,
            GregPacksConfig.INSTANCE.OmniPackBaseValues.basicPack.basicPackUpgradeSlots,
            GregPacksConfig.INSTANCE.OmniPackBaseValues.basicPack.basicPackFluidStorage),
    ADVANCED("advanced",
            GregPacksConfig.INSTANCE.OmniPackBaseValues.advancedPack.advancedPackItemSlots,
            GregPacksConfig.INSTANCE.OmniPackBaseValues.advancedPack.advancedPackEUStorage,
            GregPacksConfig.INSTANCE.OmniPackBaseValues.advancedPack.advancedPackUpgradeSlots,
            GregPacksConfig.INSTANCE.OmniPackBaseValues.advancedPack.advancedPackFluidStorage),
    ELITE("elite",
            GregPacksConfig.INSTANCE.OmniPackBaseValues.elitePack.elitePackItemSlots,
            GregPacksConfig.INSTANCE.OmniPackBaseValues.elitePack.elitePackEUStorage,
            GregPacksConfig.INSTANCE.OmniPackBaseValues.elitePack.elitePackUpgradeSlots,
            GregPacksConfig.INSTANCE.OmniPackBaseValues.elitePack.elitePackFluidStorage);

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
