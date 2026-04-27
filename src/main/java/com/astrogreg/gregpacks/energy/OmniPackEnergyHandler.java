package com.astrogreg.gregpacks.energy;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

import com.astrogreg.gregpacks.OmniPackTickHandler;
import com.astrogreg.gregpacks.inventory.OmniPackInventory;
import com.astrogreg.gregpacks.item.OmniPackTier;
import com.astrogreg.gregpacks.upgrade.UpgradeEffects;

/**
 * IEnergyStorage implementation for OmniPack ItemStacks.
 *
 * Capacity is read live from installed upgrade modules so it always reflects
 * the current state of the pack. EU is stored in the item NBT under StoredEU
 * via {@link OmniPackTickHandler}.
 */
public class OmniPackEnergyHandler implements IEnergyStorage {

    private final ItemStack container;
    private final OmniPackTier tier;

    public OmniPackEnergyHandler(ItemStack container, OmniPackTier tier) {
        this.container = container;
        this.tier = tier;
    }

    // Capacity
    @Override
    public int getMaxEnergyStored() {
        OmniPackInventory upgradeInv = OmniPackInventory.fromUpgradeItem(container, tier.defaultMaxUpgrades);
        UpgradeEffects effects = new UpgradeEffects(tier, upgradeInv);
        // Clamp to Integer.MAX_VALUE — IEnergyStorage uses int.
        return (int) Math.min(effects.totalEnergyStorage, Integer.MAX_VALUE);
    }

    @Override
    public int getEnergyStored() {
        long stored = OmniPackTickHandler.getStoredEU(container);
        return (int) Math.min(stored, Integer.MAX_VALUE);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive <= 0) return 0;

        long stored = OmniPackTickHandler.getStoredEU(container);
        long capacity = getMaxEnergyStored();
        long space = capacity - stored;
        if (space <= 0) return 0;

        long accepted = Math.min(space, maxReceive);

        if (!simulate) {
            OmniPackTickHandler.setStoredEU(container, stored + accepted, capacity);
        }
        return (int) accepted;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (maxExtract <= 0) return 0;

        long stored = OmniPackTickHandler.getStoredEU(container);
        if (stored <= 0) return 0;

        long drained = Math.min(stored, maxExtract);

        if (!simulate) {
            long capacity = getMaxEnergyStored();
            OmniPackTickHandler.setStoredEU(container, stored - drained, capacity);
        }
        return (int) drained;
    }

    // Flags
    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public boolean canExtract() {
        return true;
    }
}
