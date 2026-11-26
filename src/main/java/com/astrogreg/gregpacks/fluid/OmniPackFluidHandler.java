package com.astrogreg.gregpacks.fluid;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import com.astrogreg.gregpacks.inventory.OmniPackInventory;
import com.astrogreg.gregpacks.item.OmniPackTier;
import com.astrogreg.gregpacks.upgrade.UpgradeEffects;
import org.jetbrains.annotations.NotNull;

public class OmniPackFluidHandler extends OmniPackFluidStorage implements IFluidHandlerItem {

    private final ItemStack container;
    private final OmniPackTier tier;

    public OmniPackFluidHandler(ItemStack container, OmniPackTier tier) {
        this.container = container;
        this.tier = tier;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return container;
    }

    @Override
    protected @NotNull FluidStack readFluid() {
        return FluidNBTHelper.getFluid(container);
    }

    @Override
    protected void writeFluid(@NotNull FluidStack stack) {
        if (stack.isEmpty()) {
            FluidNBTHelper.clear(container);
        } else {
            FluidNBTHelper.setFluid(container, stack);
        }
    }

    @Override
    protected int capacity() {
        OmniPackInventory upgradeInv = OmniPackInventory.fromUpgradeItem(container, tier.defaultMaxUpgrades);
        return Math.max(1000, new UpgradeEffects(tier, upgradeInv).totalFluidStorage);
    }
}
