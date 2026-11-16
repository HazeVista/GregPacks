package com.astrogreg.gregpacks.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OmniPackCraftingContainer implements CraftingContainer {

    public static final String NBT_KEY = "CraftingGrid";

    private final ItemStack[] items = new ItemStack[9];
    private ItemStack packStack;
    private boolean dirty = false;

    public OmniPackCraftingContainer(ItemStack packStack) {
        this.packStack = packStack;
        for (int i = 0; i < 9; i++) items[i] = ItemStack.EMPTY;
        load();
    }

    public void rebind(ItemStack newPackStack) {
        this.packStack = newPackStack;
        for (int i = 0; i < 9; i++) items[i] = ItemStack.EMPTY;
        dirty = false;
        load();
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }

    @Override
    public @NotNull List<ItemStack> getItems() {
        List<ItemStack> list = new ArrayList<>(9);
        for (ItemStack s : items) list.add(s);
        return list;
    }

    @Override
    public void fillStackedContents(@NotNull StackedContents helper) {
        for (ItemStack s : items) helper.accountSimpleStack(s);
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack s : items) if (!s.isEmpty()) return false;
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return (slot >= 0 && slot < 9) ? items[slot] : ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        if (slot < 0 || slot >= 9 || items[slot].isEmpty()) return ItemStack.EMPTY;
        ItemStack stack = items[slot];
        if (stack.getCount() <= amount) {
            items[slot] = ItemStack.EMPTY;
            dirty = true;
            return stack;
        }
        ItemStack split = stack.split(amount);
        dirty = true;
        return split;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        if (slot < 0 || slot >= 9 || items[slot].isEmpty()) return ItemStack.EMPTY;
        ItemStack result = items[slot];
        items[slot] = ItemStack.EMPTY;
        dirty = true;
        return result;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        if (slot < 0 || slot >= 9) return;
        items[slot] = stack;
        dirty = true;
    }

    @Override
    public void setChanged() {
        // may have to add a dirty boolean.
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < 9; i++) items[i] = ItemStack.EMPTY;
        dirty = true;
    }

    public void load() {
        if (packStack == null || packStack.isEmpty() || !packStack.hasTag()) return;
        CompoundTag tag = packStack.getTag();
        if (!tag.contains(NBT_KEY)) return;
        ListTag list = tag.getList(NBT_KEY, 10);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            int slot = entry.getByte("Slot") & 0xFF;
            if (slot < 9) items[slot] = ItemStack.of(entry);
        }
        dirty = false;
    }

    public void save() {
        if (!dirty || packStack == null || packStack.isEmpty()) return;
        ListTag list = new ListTag();
        for (int i = 0; i < 9; i++) {
            if (!items[i].isEmpty()) {
                CompoundTag entry = items[i].save(new CompoundTag());
                entry.putByte("Slot", (byte) i);
                list.add(entry);
            }
        }
        packStack.getOrCreateTag().put(NBT_KEY, list);
        dirty = false;
    }

    public void forceSave() {
        dirty = true;
        save();
    }

    public boolean isDirty() {
        return dirty;
    }
}
