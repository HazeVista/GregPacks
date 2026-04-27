package com.astrogreg.gregpacks.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * A slot that can be shown or hidden by tracking its own position.
 * Since Slot.x and Slot.y are final, we use a Mixin (SlotMixin) to
 * remove the final modifier so we can write to them at runtime. (this is a lie)
 */
public class HideableSlot extends Slot {

    private static final int OFFSCREEN = -10000;

    private int visibleX;
    private int visibleY;
    private boolean visible = true;

    public HideableSlot(Container container, int index, int x, int y) {
        super(container, index, x, y);
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

    @Override
    public boolean mayPlace(ItemStack stack) {
        return super.mayPlace(stack);
    }
}
