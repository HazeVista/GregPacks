package com.astrogreg.gregpacks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;

import com.astrogreg.gregpacks.OmniPackTickHandler;
import com.astrogreg.gregpacks.fluid.FluidNBTHelper;
import com.astrogreg.gregpacks.inventory.OmniPackInventory;
import com.astrogreg.gregpacks.inventory.OmniPackMenu;
import com.astrogreg.gregpacks.item.OmniPackTier;
import com.astrogreg.gregpacks.registry.GregPacksBlockEntities;
import com.astrogreg.gregpacks.registry.GregPacksBlocks;
import com.astrogreg.gregpacks.upgrade.UpgradeEffects;
import org.jetbrains.annotations.NotNull;

// The BlockEntity for the OmniPackBlock. Stores the pack's inventory and upgrades, and handles saving/loading to NBT
// and ItemStacks.
// Also implements MenuProvider to open the GUI directly from the block (without needing to convert to an ItemStack
// first).
public class OmniPackBlockEntity extends BlockEntity implements MenuProvider {

    private final OmniPackTier tier;
    private OmniPackInventory packInventory;
    private OmniPackInventory upgradeInventory;
    private FluidStack fluid = FluidStack.EMPTY;

    public FluidStack getFluid() {
        return fluid;
    }

    private boolean pickedUp = false;

    public long getStoredEU() {
        return storedEU;
    }

    private long storedEU = 0L;

    public OmniPackBlockEntity(BlockPos pos, BlockState state, OmniPackTier tier) {
        super(GregPacksBlockEntities.getTypeForTier(tier), pos, state);
        this.tier = tier;
        this.packInventory = new OmniPackInventory(tier.defaultSlots);
        this.upgradeInventory = new OmniPackInventory(tier.defaultMaxUpgrades);
    }

    // NBT
    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Items", packInventory.serializeToTag());
        tag.put("Upgrades", upgradeInventory.serializeToTag());
        long eu = storedEU;
        if (eu > 0) tag.putLong("StoredEU", eu);
        if (!fluid.isEmpty()) tag.put("Fluid", fluid.writeToNBT(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        packInventory = new OmniPackInventory(tier.defaultSlots);
        upgradeInventory = new OmniPackInventory(tier.defaultMaxUpgrades);
        packInventory.deserializeFromTag(tag.getList("Items", 10));
        upgradeInventory.deserializeFromTag(tag.getList("Upgrades", 10));
        storedEU = tag.contains("StoredEU") ? tag.getLong("StoredEU") : 0L;
        fluid = tag.contains("Fluid") ? FluidStack.loadFluidStackFromNBT(tag.getCompound("Fluid")) : FluidStack.EMPTY;
    }

    // ItemStack <> BlockEntity transfer
    public void loadFromItemStack(ItemStack stack) {
        UpgradeEffects effects = new UpgradeEffects(tier,
                OmniPackInventory.fromUpgradeItem(stack, tier.defaultMaxUpgrades));

        packInventory = OmniPackInventory.fromItem(stack, effects.totalSlots);
        upgradeInventory = OmniPackInventory.fromUpgradeItem(stack, tier.defaultMaxUpgrades);
        storedEU = OmniPackTickHandler.getStoredEU(stack);
        fluid = FluidNBTHelper.getFluid(stack);
        setChanged();
    }

    public ItemStack toItemStack() {
        ItemStack stack = new ItemStack(GregPacksBlocks.getItemForTier(tier));
        packInventory.saveToItem(stack);
        upgradeInventory.saveUpgradesToItem(stack);
        if (storedEU > 0) {
            OmniPackTickHandler.setStoredEU(stack, storedEU,
                    new UpgradeEffects(tier, upgradeInventory).totalEnergyStorage);
        }
        if (!fluid.isEmpty()) {
            FluidNBTHelper.setFluid(stack, fluid);
        }
        return stack;
    }

    // MenuProvider
    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.gregpacks.omnipack");
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInv, @NotNull Player player) {
        UpgradeEffects effects = new UpgradeEffects(tier, upgradeInventory);
        if (effects.totalSlots != packInventory.getContainerSize()) {
            OmniPackInventory resized = new OmniPackInventory(effects.totalSlots);
            for (int i = 0; i < Math.min(effects.totalSlots, packInventory.getContainerSize()); i++) {
                resized.setItem(i, packInventory.getItem(i));
            }
            packInventory = resized;
        }
        OmniPackMenu menu = new OmniPackMenu(windowId, playerInv, packInventory, upgradeInventory, tier);
        menu.setSourceBlockEntity(this);
        if (player instanceof ServerPlayer sp) menu.setServerPlayer(sp);
        return menu;
    }

    // Helpers
    public void setPickedUp(boolean pickedUp) {
        this.pickedUp = pickedUp;
    }

    public boolean isPickedUp() {
        return pickedUp;
    }

    public OmniPackTier getTier() {
        return tier;
    }

    public OmniPackInventory getPackInventory() {
        return packInventory;
    }

    public OmniPackInventory getUpgradeInventory() {
        return upgradeInventory;
    }

    public void setStoredEU(long eu) {
        this.storedEU = Math.max(0, eu);
        setChanged();
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos()).inflate(1.0);
    }
}
