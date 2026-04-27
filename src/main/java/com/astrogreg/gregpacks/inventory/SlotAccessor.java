package com.astrogreg.gregpacks.inventory;

import net.minecraft.world.inventory.Slot;

import java.lang.reflect.Field;

/**
 * Reflectively writes to Slot.x and Slot.y which are final in 1.20.1.
 * This is the only way to reposition slots at runtime without a Mixin (that's why the lie).
 */
public class SlotAccessor {

    private static final Field FIELD_X;
    private static final Field FIELD_Y;

    static {
        try {
            FIELD_X = Slot.class.getDeclaredField("x");
            FIELD_Y = Slot.class.getDeclaredField("y");
            FIELD_X.setAccessible(true);
            FIELD_Y.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Could not find Slot.x or Slot.y fields", e);
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
