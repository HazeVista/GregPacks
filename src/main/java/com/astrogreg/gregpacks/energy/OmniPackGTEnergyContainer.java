package com.astrogreg.gregpacks.energy;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;

import net.minecraft.core.Direction;

import com.astrogreg.gregpacks.block.OmniPackBlockEntity;
import com.astrogreg.gregpacks.item.OmniPackTier;
import com.astrogreg.gregpacks.upgrade.UpgradeEffects;

public class OmniPackGTEnergyContainer implements IEnergyContainer {

    private static final long MAX_VOLTAGE = Long.MAX_VALUE / 2;
    private static final long INPUT_AMPS = 2L;

    private final OmniPackBlockEntity be;
    private final OmniPackTier tier;

    public OmniPackGTEnergyContainer(OmniPackBlockEntity be, OmniPackTier tier) {
        this.be = be;
        this.tier = tier;
    }

    @Override
    public long acceptEnergyFromNetwork(Direction side, long voltage, long amperage) {
        // voltage = EU per packet, amperage = number of packets this tick
        long toAdd = voltage * amperage;
        long stored = be.getStoredEU();
        long cap = getEnergyCapacity();
        long space = cap - stored;
        if (space <= 0 || toAdd <= 0) return 0L;

        long actualAdd = Math.min(toAdd, space);
        long ampsUsed = (long) Math.ceil((double) actualAdd / voltage);
        ampsUsed = Math.min(ampsUsed, amperage);

        be.addStoredEU(ampsUsed * voltage);
        return ampsUsed;
    }

    @Override
    public boolean inputsEnergy(Direction side) {
        return true;
    }

    @Override
    public boolean outputsEnergy(Direction side) {
        return false;
    }

    @Override
    public long changeEnergy(long differenceAmount) {
        long stored = be.getStoredEU();
        long cap = getEnergyCapacity();
        long newVal = Math.max(0, Math.min(cap, stored + differenceAmount));
        long actual = newVal - stored;
        be.setStoredEUDirect(newVal);
        return actual;
    }

    @Override
    public long getEnergyStored() {
        return be.getStoredEU();
    }

    @Override
    public long getEnergyCapacity() {
        return new UpgradeEffects(tier, be.getUpgradeInventory()).totalEnergyStorage;
    }

    @Override
    public long getInputVoltage() {
        return MAX_VOLTAGE;
    }

    @Override
    public long getInputAmperage() {
        return INPUT_AMPS;
    }

    @Override
    public long getOutputVoltage() {
        return 0L;
    }

    @Override
    public long getOutputAmperage() {
        return 0L;
    }
}
