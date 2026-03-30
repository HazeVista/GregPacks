package com.astrogreg.gregpacks.upgrade;

import com.astrogreg.gregpacks.config.GregPacksConfig;
import com.astrogreg.gregpacks.inventory.OmniPackInventory;
import com.astrogreg.gregpacks.item.OmniPackTier;
import com.astrogreg.gregpacks.item.UpgradeItem;
import com.astrogreg.gregpacks.item.UpgradeType;

import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.Set;

public class UpgradeEffects {

    private final Set<UpgradeType> installed;

    public final int totalSlots;
    public final int totalFluidStorage;
    public final long totalEnergyStorage;
    public final int magnetRadius;
    public final boolean hasFeeding;
    public final boolean hasMaintenance;
    public final boolean hasJetpack1;
    public final boolean hasJetpack2;
    public final boolean hasCrafting;
    public final boolean hasProcessing;

    // Only manage to make Magnet and Feeding module work.
    public UpgradeEffects(OmniPackTier tier, OmniPackInventory upgradeInventory) {
        this.installed = collectInstalled(upgradeInventory);

        GregPacksConfig.UpgradeConfigs cfg = GregPacksConfig.INSTANCE.ModuleValues;

        // Item capacity — slots stack additively
        int bonusSlots = 0;
        if (installed.contains(UpgradeType.ITEM_CAPACITY_I))   bonusSlots += cfg.itemModule1Bonus;
        if (installed.contains(UpgradeType.ITEM_CAPACITY_II))  bonusSlots += cfg.itemModule2Bonus;
        if (installed.contains(UpgradeType.ITEM_CAPACITY_III)) bonusSlots += cfg.itemModule3Bonus;
        this.totalSlots = tier.defaultSlots + bonusSlots;

        // Fluid capacity — multipliers stack multiplicatively
        double fluidMultiplier = 1.0;
        if (installed.contains(UpgradeType.FLUID_CAPACITY_I))   fluidMultiplier *= cfg.fluidModule1Bonus;
        if (installed.contains(UpgradeType.FLUID_CAPACITY_II))  fluidMultiplier *= cfg.fluidModule2Bonus;
        if (installed.contains(UpgradeType.FLUID_CAPACITY_III)) fluidMultiplier *= cfg.fluidModule3Bonus;
        this.totalFluidStorage = (int) (tier.defaultFluidStorage * fluidMultiplier);

        // Energy capacity — multipliers stack multiplicatively
        double energyMultiplier = 1.0;
        if (installed.contains(UpgradeType.ENERGY_CAPACITY_I))   energyMultiplier *= cfg.energyModule1Bonus;
        if (installed.contains(UpgradeType.ENERGY_CAPACITY_II))  energyMultiplier *= cfg.energyModule2Bonus;
        if (installed.contains(UpgradeType.ENERGY_CAPACITY_III)) energyMultiplier *= cfg.energyModule3Bonus;
        this.totalEnergyStorage = (long) (tier.defaultEnergyStorage * energyMultiplier);

        // Magnet — higher tier overrides lower if both present
        if (installed.contains(UpgradeType.MAGNET_II))     this.magnetRadius = cfg.magnetModule2Radius;
        else if (installed.contains(UpgradeType.MAGNET_I)) this.magnetRadius = cfg.magnetModule1Radius;
        else                                                this.magnetRadius = 0;

        this.hasFeeding     = installed.contains(UpgradeType.FEEDING);
        this.hasMaintenance = installed.contains(UpgradeType.MAINTENANCE);
        this.hasJetpack1    = installed.contains(UpgradeType.JETPACK_I);
        this.hasJetpack2    = installed.contains(UpgradeType.JETPACK_II);
        this.hasCrafting    = installed.contains(UpgradeType.CRAFTING);
        this.hasProcessing  = installed.contains(UpgradeType.PROCESSING);
    }

    public boolean has(UpgradeType type) {
        return installed.contains(type);
    }

    private static Set<UpgradeType> collectInstalled(OmniPackInventory inv) {
        Set<UpgradeType> result = EnumSet.noneOf(UpgradeType.class);
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof UpgradeItem upgradeItem) {
                result.add(upgradeItem.getUpgradeType());
            }
        }
        return result;
    }
}