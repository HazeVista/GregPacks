package com.astrogreg.gregpacks.inventory;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

// we use a mixin (SlotMixin) to remove the modifier so we can write to them at runtime
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

    private static final TagKey<Item> CURIOS_BACK =
            TagKey.create(Registries.ITEM, new ResourceLocation("curios", "back"));

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (stack.is(CURIOS_BACK)) {
            return false;
        }
        return super.mayPlace(stack);
    }
}
