package com.astrogreg.gregpacks.util;

import net.minecraft.world.item.ItemStack;

public class JetpackConfigHelper {

    private static final String KEY_SPEED  = "JetpackSpeed";
    private static final String KEY_THRUST = "JetpackThrust";
    private static final int MIN = 1, MAX = 5;

    public static int getSpeed(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains(KEY_SPEED)) return 3;
        return stack.getTag().getInt(KEY_SPEED);
    }

    public static void setSpeed(ItemStack stack, int val) {
        stack.getOrCreateTag().putInt(KEY_SPEED, Math.max(MIN, Math.min(MAX, val)));
    }

    public static int getThrust(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains(KEY_THRUST)) return 3;
        return stack.getTag().getInt(KEY_THRUST);
    }

    public static void setThrust(ItemStack stack, int val) {
        stack.getOrCreateTag().putInt(KEY_THRUST, Math.max(MIN, Math.min(MAX, val)));
    }
}