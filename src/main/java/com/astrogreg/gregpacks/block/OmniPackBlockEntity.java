package com.astrogreg.gregpacks.block;

import com.astrogreg.gregpacks.inventory.OmniPackInventory;
import com.astrogreg.gregpacks.inventory.OmniPackMenu;
import com.astrogreg.gregpacks.item.OmniPackTier;
import com.astrogreg.gregpacks.registry.GregPacksBlockEntities;
import com.astrogreg.gregpacks.registry.GregPacksBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

// The BlockEntity for the OmniPackBlock. Stores the pack's inventory and upgrades, and handles saving/loading to NBT and ItemStacks.
// Also implements MenuProvider to open the GUI directly from the block (without needing to convert to an ItemStack first).
public class OmniPackBlockEntity extends BlockEntity implements MenuProvider {

    private final OmniPackTier tier;
    private OmniPackInventory packInventory;
    private OmniPackInventory upgradeInventory;
    private boolean pickedUp = false;

    public OmniPackBlockEntity(BlockPos pos, BlockState state, OmniPackTier tier) {
        super(GregPacksBlockEntities.getTypeForTier(tier), pos, state);
        this.tier             = tier;
        this.packInventory    = new OmniPackInventory(tier.defaultSlots);
        this.upgradeInventory = new OmniPackInventory(tier.defaultMaxUpgrades);
    }

    // NBT
    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Items",    packInventory.serializeToTag());
        tag.put("Upgrades", upgradeInventory.serializeToTag());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        packInventory    = new OmniPackInventory(tier.defaultSlots);
        upgradeInventory = new OmniPackInventory(tier.defaultMaxUpgrades);
        packInventory.deserializeFromTag(tag.getList("Items",    10));
        upgradeInventory.deserializeFromTag(tag.getList("Upgrades", 10));
    }

    // Transfer: ItemStack ↔ BlockEntity
    public void loadFromItemStack(ItemStack stack) {
        packInventory    = OmniPackInventory.fromItem(stack, tier.defaultSlots);
        upgradeInventory = OmniPackInventory.fromUpgradeItem(stack, tier.defaultMaxUpgrades);
        setChanged();
    }

    public ItemStack toItemStack() {
        ItemStack stack = new ItemStack(GregPacksBlocks.getItemForTier(tier));
        packInventory.saveToItem(stack);
        upgradeInventory.saveUpgradesToItem(stack);
        return stack;
    }

    // MenuProvider — opens GUI directly from the BlockEntity
    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.gregpacks.omnipack");
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInv,
                                            @NotNull Player player) {
        // Use the BE's own inventories — NOT new empty ones
        return new OmniPackMenu(windowId, playerInv, packInventory, upgradeInventory, tier);
    }


    // Helpers
    public void setPickedUp(boolean pickedUp) { this.pickedUp = pickedUp; }
    public boolean isPickedUp()               { return pickedUp; }

    public OmniPackTier getTier()                     { return tier; }
    public OmniPackInventory getPackInventory()        { return packInventory; }
    public OmniPackInventory getUpgradeInventory()     { return upgradeInventory; }
}