package com.astrogreg.gregpacks.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import com.astrogreg.gregpacks.item.UpgradeItem;
import com.astrogreg.gregpacks.item.UpgradeType;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class MagnetConfigHelper {

    public static final int FILTER_SLOTS = 9;

    private static final String KEY_RANGE = "MagnetRange";
    private static final String KEY_FILTER_ON = "MagnetFilterOn";
    private static final String KEY_WHITELIST = "MagnetWhitelist";
    private static final String KEY_FILTER = "MagnetFilter";
    private static final int MIN_RANGE = 1;

    public static ItemStack findModule(Container upgrades) {
        for (int i = 0; i < upgrades.getContainerSize(); i++) {
            ItemStack s = upgrades.getItem(i);
            if (!s.isEmpty() && s.getItem() instanceof UpgradeItem u &&
                    u.getUpgradeType() == UpgradeType.MAGNET_II) {
                return s;
            }
        }
        return ItemStack.EMPTY;
    }

    // Range (defaults to the config maximum, always clamped to it)
    public static int getRange(ItemStack module, int max) {
        int top = Math.max(MIN_RANGE, max);
        CompoundTag tag = module.getTag();
        if (tag == null || !tag.contains(KEY_RANGE)) return top;
        return Math.max(MIN_RANGE, Math.min(top, tag.getInt(KEY_RANGE)));
    }

    public static void setRange(ItemStack module, int value, int max) {
        int top = Math.max(MIN_RANGE, max);
        module.getOrCreateTag().putInt(KEY_RANGE, Math.max(MIN_RANGE, Math.min(top, value)));
    }

    // Filter flags
    public static boolean isFilterOn(ItemStack module) {
        CompoundTag tag = module.getTag();
        return tag != null && tag.getBoolean(KEY_FILTER_ON);
    }

    public static void setFilterOn(ItemStack module, boolean on) {
        module.getOrCreateTag().putBoolean(KEY_FILTER_ON, on);
    }

    public static boolean isWhitelist(ItemStack module) {
        CompoundTag tag = module.getTag();
        return tag == null || !tag.contains(KEY_WHITELIST) || tag.getBoolean(KEY_WHITELIST);
    }

    public static void setWhitelist(ItemStack module, boolean whitelist) {
        module.getOrCreateTag().putBoolean(KEY_WHITELIST, whitelist);
    }

    // Filter items
    private static String[] readIds(ItemStack module) {
        String[] ids = new String[FILTER_SLOTS];
        for (int i = 0; i < FILTER_SLOTS; i++) ids[i] = "";
        CompoundTag tag = module.getTag();
        if (tag != null && tag.contains(KEY_FILTER, Tag.TAG_LIST)) {
            ListTag list = tag.getList(KEY_FILTER, Tag.TAG_STRING);
            for (int i = 0; i < Math.min(FILTER_SLOTS, list.size()); i++) ids[i] = list.getString(i);
        }
        return ids;
    }

    private static Item toItem(String id) {
        if (id.isEmpty()) return null;
        ResourceLocation rl = ResourceLocation.tryParse(id);
        if (rl == null) return null;
        Item item = ForgeRegistries.ITEMS.getValue(rl);
        return item == null || item == Items.AIR ? null : item;
    }

    public static ItemStack getFilterItem(ItemStack module, int slot) {
        if (slot < 0 || slot >= FILTER_SLOTS) return ItemStack.EMPTY;
        Item item = toItem(readIds(module)[slot]);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    /** Pass EMPTY to clear the slot. */
    public static void setFilterItem(ItemStack module, int slot, ItemStack stack) {
        if (slot < 0 || slot >= FILTER_SLOTS) return;
        String[] ids = readIds(module);
        ResourceLocation key = stack.isEmpty() ? null : ForgeRegistries.ITEMS.getKey(stack.getItem());
        ids[slot] = key == null ? "" : key.toString();

        ListTag list = new ListTag();
        for (String id : ids) list.add(StringTag.valueOf(id));
        module.getOrCreateTag().put(KEY_FILTER, list);
    }

    public static Predicate<ItemStack> filterOf(ItemStack module) {
        if (module.isEmpty() || !isFilterOn(module)) return s -> true;

        Set<Item> items = new HashSet<>();
        for (String id : readIds(module)) {
            Item item = toItem(id);
            if (item != null) items.add(item);
        }
        boolean whitelist = isWhitelist(module);
        return s -> items.contains(s.getItem()) == whitelist;
    }
}