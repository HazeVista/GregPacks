package com.astrogreg.gregpacks.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import org.jetbrains.annotations.NotNull;

public abstract class OmniPackFluidStorage implements IFluidHandler {

    protected abstract @NotNull FluidStack readFluid();

    protected abstract void writeFluid(@NotNull FluidStack stack);

    protected abstract int capacity();

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return readFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return capacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        FluidStack current = readFluid();
        return current.isEmpty() || current.isFluidEqual(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0;

        FluidStack current = readFluid();
        if (!current.isEmpty() && !current.isFluidEqual(resource)) return 0;

        int space = capacity() - current.getAmount();
        int filled = Math.min(space, resource.getAmount());
        if (filled <= 0) return 0;

        if (action.execute()) {
            FluidStack result = resource.copy();
            result.setAmount(current.getAmount() + filled);
            writeFluid(result);
        }
        return filled;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !resource.isFluidEqual(readFluid())) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack current = readFluid();
        if (current.isEmpty() || maxDrain <= 0) return FluidStack.EMPTY;

        int drained = Math.min(current.getAmount(), maxDrain);
        FluidStack result = new FluidStack(current, drained);

        if (action.execute()) {
            int remaining = current.getAmount() - drained;
            if (remaining <= 0) {
                writeFluid(FluidStack.EMPTY);
            } else {
                FluidStack updated = current.copy();
                updated.setAmount(remaining);
                writeFluid(updated);
            }
        }
        return result;
    }
}
