package com.astrogreg.gregpacks.fluid;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import com.astrogreg.gregpacks.GregPacks;
import com.astrogreg.gregpacks.inventory.OmniPackInventory;
import com.astrogreg.gregpacks.item.OmniPackTier;
import com.astrogreg.gregpacks.upgrade.UpgradeEffects;
import org.jetbrains.annotations.NotNull;

public class OmniPackFluidHandler implements IFluidHandlerItem {

    private final ItemStack container;
    private final OmniPackTier tier;

    public OmniPackFluidHandler(ItemStack container, OmniPackTier tier) {
        this.container = container;
        this.tier = tier;
    }

    // IFluidHandlerItem
    @Override
    public @NotNull ItemStack getContainer() {
        return container;
    }

    // IFluidHandler
    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return FluidNBTHelper.getFluid(container);
    }

    @Override
    public int getTankCapacity(int tank) {
        OmniPackInventory upgradeInv = OmniPackInventory.fromUpgradeItem(container, tier.defaultMaxUpgrades);
        UpgradeEffects effects = new UpgradeEffects(tier, upgradeInv);
        return Math.max(1000, effects.totalFluidStorage);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        FluidStack current = FluidNBTHelper.getFluid(container);
        // Accept any fluid if tank is empty, or the same fluid if occupied.
        return current.isEmpty() || current.isFluidEqual(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        GregPacks.LOGGER.info("[FluidHandler] fill called resource={} amount={} action={}",
                net.minecraftforge.registries.ForgeRegistries.FLUIDS.getKey(resource.getFluid()),
                resource.getAmount(), action);
        if (resource.isEmpty()) return 0;

        FluidStack current = FluidNBTHelper.getFluid(container);
        int capacity = getTankCapacity(0);

        // Reject different fluid types.
        if (!current.isEmpty() && !current.isFluidEqual(resource)) return 0;

        int space = capacity - current.getAmount();
        int filled = Math.min(space, resource.getAmount());
        if (filled <= 0) return 0;

        if (action.execute()) {
            FluidStack result = resource.copy();
            result.setAmount(current.getAmount() + filled);
            FluidNBTHelper.setFluid(container, result);
        }
        return filled;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !resource.isFluidEqual(getFluidInTank(0))) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack current = FluidNBTHelper.getFluid(container);
        if (current.isEmpty() || maxDrain <= 0) return FluidStack.EMPTY;

        int drainedAmount = Math.min(current.getAmount(), maxDrain);
        FluidStack result = new FluidStack(current, drainedAmount);

        if (action.execute()) {
            int remaining = current.getAmount() - drainedAmount;
            if (remaining <= 0) {
                FluidNBTHelper.clear(container);
            } else {
                FluidStack updated = current.copy();
                updated.setAmount(remaining);
                FluidNBTHelper.setFluid(container, updated);
            }
        }
        return result;
    }


    private FluidStack drainInternal(int maxDrain, FluidAction action) {
        FluidStack current = FluidNBTHelper.getFluid(container);
        if (current.isEmpty()) return FluidStack.EMPTY;

        int drained = Math.min(current.getAmount(), maxDrain);
        FluidStack result = current.copy();
        result.setAmount(drained);

        if (action.execute()) {
            int remaining = current.getAmount() - drained;
            if (remaining <= 0) {
                FluidNBTHelper.clear(container);
            } else {
                FluidStack updated = current.copy();
                updated.setAmount(remaining);
                FluidNBTHelper.setFluid(container, updated);
            }
        }
        return result;
    }
}
