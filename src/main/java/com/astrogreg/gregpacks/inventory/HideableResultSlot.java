package com.astrogreg.gregpacks.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;

public class HideableResultSlot extends ResultSlot {

    private static final int OFFSCREEN = -10000;

    private int visibleX;
    private int visibleY;
    private boolean visible = true;

    public HideableResultSlot(Player player, CraftingContainer craftSlots, Container container,
                              int slot, int x, int y) {
        super(player, craftSlots, container, slot, x, y);
        this.visibleX = x;
        this.visibleY = y;
    }

    public void show() {
        visible = true;
        SlotAccessor.setX(this, visibleX);
        SlotAccessor.setY(this, visibleY);
    }

    public void hide() {
        visible = false;
        SlotAccessor.setX(this, OFFSCREEN);
        SlotAccessor.setY(this, OFFSCREEN);
    }

    public void moveTo(int x, int y) {
        this.visibleX = x;
        this.visibleY = y;
        if (visible) {
            SlotAccessor.setX(this, x);
            SlotAccessor.setY(this, y);
        }
    }

    public boolean isVisible() {
        return visible;
    }
}
