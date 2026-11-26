package com.astrogreg.gregpacks.inventory;

import net.minecraft.world.inventory.Slot;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

/**
 * reflectively writes Slot.x / Slot.y
 * unfortunately final in 1.20.1
 * uses SRG names for normal launch and falls back to default names in dev environment
 */
public class SlotAccessor {

    private static final Field FIELD_X = find("f_40220_", "x");
    private static final Field FIELD_Y = find("f_40221_", "y");

    private static Field find(String srgName, String plainName) {
        try {
            return ObfuscationReflectionHelper.findField(Slot.class, srgName);
        } catch (Exception ignored) {
        }
        try {
            Field f = Slot.class.getDeclaredField(plainName);
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException e) {
            StringBuilder sb = new StringBuilder();
            for (Field f : Slot.class.getDeclaredFields()) {
                sb.append(f.getType().getSimpleName()).append(' ').append(f.getName()).append(", ");
            }
            throw new RuntimeException("Could not find Slot." + plainName + " (" + srgName
                    + "). Declared fields: " + sb, e);
        }
    }

    public static void setX(Slot slot, int x) {
        try {
            FIELD_X.setInt(slot, x);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setY(Slot slot, int y) {
        try {
            FIELD_Y.setInt(slot, y);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}