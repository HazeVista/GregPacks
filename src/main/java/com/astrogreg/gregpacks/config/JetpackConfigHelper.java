package com.astrogreg.gregpacks.config;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import com.astrogreg.gregpacks.item.UpgradeItem;
import com.astrogreg.gregpacks.item.UpgradeType;

public class JetpackConfigHelper {

    private static final String KEY_SPEED = "JetpackSpeed";
    private static final String KEY_THRUST = "JetpackThrust";
    private static final int MIN = 1, MAX = 5, DEFAULT = 3;

    public static ItemStack findModule(Container upgrades) {
        for (int i = 0; i < upgrades.getContainerSize(); i++) {
            ItemStack s = upgrades.getItem(i);
            if (!s.isEmpty() && s.getItem() instanceof UpgradeItem u &&
                    u.getUpgradeType() == UpgradeType.JETPACK_II) {
                return s;
            }
        }
        return ItemStack.EMPTY;
    }

    public static int getSpeed(ItemStack module) {
        return read(module, KEY_SPEED);
    }

    public static void setSpeed(ItemStack module, int val) {
        module.getOrCreateTag().putInt(KEY_SPEED, Math.max(MIN, Math.min(MAX, val)));
    }

    public static int getThrust(ItemStack module) {
        return read(module, KEY_THRUST);
    }

    public static void setThrust(ItemStack module, int val) {
        module.getOrCreateTag().putInt(KEY_THRUST, Math.max(MIN, Math.min(MAX, val)));
    }

    private static int read(ItemStack module, String key) {
        if (!module.hasTag() || !module.getTag().contains(key)) return DEFAULT;
        return Math.max(MIN, Math.min(MAX, module.getTag().getInt(key)));
    }

    // EU/t while ascending. Shared by the tick handler and the tooltip so they always agree.
    public static long ascendCost(int configCost, int speed, int thrust) {
        return (long) configCost * ((speed + thrust) / 2);
    }

    // EU/t while hovering (a quarter of ascending, at least 1). Descending is free.
    public static long hoverCost(int configCost, int speed, int thrust) {
        return Math.max(1, ascendCost(configCost, speed, thrust) / 4);
    }
}