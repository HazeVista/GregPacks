package com.astrogreg.gregpacks.inventory;

import com.astrogreg.gregpacks.item.OmniPackTier;
import com.astrogreg.gregpacks.registry.GregPacksBlocks;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class OmniPackScreen extends AbstractContainerScreen<OmniPackMenu> {

    // colors
    private static final int COLOR_BG          = 0xFFC6C6C6; // inventory background
    private static final int COLOR_SHADOW_DARK  = 0xFF555555; // dark shadow (top-left border)
    private static final int COLOR_SHADOW_LIGHT = 0xFFFFFFFF; // light highlight (bottom-right)
    private static final int COLOR_SLOT_DARK    = 0xFF8B8B8B; // slot inset dark
    private static final int COLOR_SLOT_LIGHT   = 0xFFFFFFFF; // slot inset light
    private static final int COLOR_SLOT_BG      = 0xFF8B8B8B; // slot interior
    private static final int COLOR_DIVIDER      = 0xFF555555;
    private static final int COLOR_TITLE        = 0xFF404040;
    private static final int COLOR_TAB_ACTIVE   = 0xFFC6C6C6;
    private static final int COLOR_TAB_INACTIVE = 0xFF8B8B8B;
    private static final int COLOR_TAB_BORDER   = 0xFF555555;

    private static final int PADDING    = 7;
    private static final int COLS       = 9;
    private static final int SLOT_SIZE  = 18;
    private static final int TAB_WIDTH  = 24;
    private static final int TAB_HEIGHT = 24;
    private static final int TAB_OFFSET = -TAB_WIDTH;

    public enum Tab { INVENTORY, UPGRADES }
    private Tab activeTab = Tab.INVENTORY;

    public OmniPackScreen(OmniPackMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        int packRows     = (int) Math.ceil(menu.getPackSlots() / (double) COLS);
        this.imageWidth  = PADDING + COLS * SLOT_SIZE + PADDING;
        this.imageHeight = PADDING + 10
                + packRows * SLOT_SIZE + PADDING
                + 4
                + 3 * SLOT_SIZE + PADDING
                + 4
                + SLOT_SIZE + PADDING;
    }

    @Override
    protected void init() {
        super.init();
        applyTabLayout(Tab.INVENTORY);
    }

    // Rendering
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        renderTabTooltips(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;

        // Main background
        graphics.fill(x, y, x + imageWidth, y + imageHeight, COLOR_BG);

        // Outer border — dark top/left, light bottom/right (vanilla bevel)
        graphics.fill(x,                  y,                   x + imageWidth, y + 1,           COLOR_SHADOW_DARK);
        graphics.fill(x,                  y,                   x + 1,          y + imageHeight, COLOR_SHADOW_DARK);
        graphics.fill(x,                  y + imageHeight - 1, x + imageWidth, y + imageHeight, COLOR_SHADOW_LIGHT);
        graphics.fill(x + imageWidth - 1, y,                   x + imageWidth, y + imageHeight, COLOR_SHADOW_LIGHT);

        renderTabs(graphics, x, y);

        // Slot insets (vanilla sunken look)
        for (int i = 0; i < menu.slots.size(); i++) {
            HideableSlot slot = menu.getHideableSlot(i);
            if (!slot.isVisible()) continue;
            int sx = x + slot.x - 1;
            int sy = y + slot.y - 1;
            // Dark top/left
            graphics.fill(sx,           sy,           sx + SLOT_SIZE, sy + 1,          COLOR_SLOT_DARK);
            graphics.fill(sx,           sy,           sx + 1,         sy + SLOT_SIZE,  COLOR_SLOT_DARK);
            // Light bottom/right
            graphics.fill(sx,           sy + SLOT_SIZE - 1, sx + SLOT_SIZE, sy + SLOT_SIZE, COLOR_SLOT_LIGHT);
            graphics.fill(sx + SLOT_SIZE - 1, sy, sx + SLOT_SIZE, sy + SLOT_SIZE,           COLOR_SLOT_LIGHT);
            // Interior
            graphics.fill(sx + 1, sy + 1, sx + SLOT_SIZE - 1, sy + SLOT_SIZE - 1, COLOR_SLOT_BG);
        }

        // Divider between pack and player inventory
        if (activeTab == Tab.INVENTORY) {
            int packRows = (int) Math.ceil(menu.getPackSlots() / (double) COLS);
            int dividerY = y + PADDING + 10 + packRows * SLOT_SIZE + 4;
            graphics.fill(x + PADDING, dividerY, x + imageWidth - PADDING, dividerY + 1, COLOR_DIVIDER);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, PADDING, PADDING, COLOR_TITLE, false);
        if (activeTab == Tab.INVENTORY) {
            int packRows = (int) Math.ceil(menu.getPackSlots() / (double) COLS);
            int labelY   = PADDING + 10 + packRows * SLOT_SIZE + 6;
            graphics.drawString(font, Component.translatable("container.inventory"),
                    PADDING, labelY, COLOR_TITLE, false);
        } else {
            graphics.drawString(font, Component.translatable("container.gregpacks.upgrades"),
                    PADDING, PADDING + 12, COLOR_TITLE, false);
        }
    }

    // Tabs
    private void renderTabs(GuiGraphics graphics, int x, int y) {
        for (Tab tab : Tab.values()) {
            int tx     = x + TAB_OFFSET;
            int ty     = y + tab.ordinal() * (TAB_HEIGHT + 2);
            boolean active = activeTab == tab;

            // Tab background
            graphics.fill(tx, ty, tx + TAB_WIDTH, ty + TAB_HEIGHT,
                    active ? COLOR_TAB_ACTIVE : COLOR_TAB_INACTIVE);

            // Bevel
            graphics.fill(tx,                ty,                tx + TAB_WIDTH, ty + 1,          COLOR_SHADOW_DARK);
            graphics.fill(tx,                ty,                tx + 1,         ty + TAB_HEIGHT, COLOR_SHADOW_DARK);
            graphics.fill(tx,                ty + TAB_HEIGHT-1, tx + TAB_WIDTH, ty + TAB_HEIGHT, COLOR_SHADOW_LIGHT);
            if (!active)
                graphics.fill(tx + TAB_WIDTH - 1, ty, tx + TAB_WIDTH, ty + TAB_HEIGHT, COLOR_SHADOW_LIGHT);

            ItemStack icon = getTabIcon(tab);
            if (!icon.isEmpty()) graphics.renderItem(icon, tx + 4, ty + 4);
        }
    }

    private ItemStack getTabIcon(Tab tab) {
        return switch (tab) {
            case INVENTORY -> {
                OmniPackTier tier = menu.getTier();
                yield tier != null
                        ? new ItemStack(GregPacksBlocks.getItemForTier(tier))
                        : ItemStack.EMPTY;
            }
            case UPGRADES -> {
                for (int i = menu.getPackSlots(); i < menu.getPackSlots() + menu.getMaxUpgrades(); i++) {
                    ItemStack s = menu.slots.get(i).getItem();
                    if (!s.isEmpty()) yield s;
                }
                yield ItemStack.EMPTY;
            }
        };
    }

    private void renderTabTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        for (Tab tab : Tab.values()) {
            int tx = leftPos + TAB_OFFSET;
            int ty = topPos + tab.ordinal() * (TAB_HEIGHT + 2);
            if (mouseX >= tx && mouseX < tx + TAB_WIDTH && mouseY >= ty && mouseY < ty + TAB_HEIGHT) {
                Component label = tab == Tab.INVENTORY
                        ? Component.translatable("container.gregpacks.tab.inventory")
                        : Component.translatable("container.gregpacks.tab.upgrades");
                graphics.renderTooltip(font, label, mouseX, mouseY);
            }
        }
    }

    // Tab switching
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Tab tab : Tab.values()) {
            int tx = leftPos + TAB_OFFSET;
            int ty = topPos + tab.ordinal() * (TAB_HEIGHT + 2);
            if (mouseX >= tx && mouseX < tx + TAB_WIDTH && mouseY >= ty && mouseY < ty + TAB_HEIGHT) {
                if (activeTab != tab) {
                    activeTab = tab;
                    applyTabLayout(tab);
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void applyTabLayout(Tab tab) {
        int upgradeStart = menu.getPackSlots();
        int upgradeEnd   = menu.getPackSlots() + menu.getMaxUpgrades();
        int packRows     = (int) Math.ceil(menu.getPackSlots() / (double) COLS);

        // Player inventory Y depends on the tab:
        // - Inventory tab: below pack slots
        // - Upgrades tab: below upgrade grid (max 6 rows of 3 = 108px) + gap
        int upgradeRows  = (int) Math.ceil(menu.getMaxUpgrades() / 3.0);
        int playerInvY   = tab == Tab.INVENTORY
                ? OmniPackMenu.PACK_START_Y + packRows * SLOT_SIZE + 8
                : OmniPackMenu.PACK_START_Y + upgradeRows * SLOT_SIZE + 8;

        if (tab == Tab.INVENTORY) {
            for (int i = 0; i < upgradeStart; i++) {
                int col = i % COLS;
                int row = i / COLS;
                menu.getHideableSlot(i).moveTo(
                        PADDING + col * SLOT_SIZE,
                        OmniPackMenu.PACK_START_Y + row * SLOT_SIZE);
                menu.getHideableSlot(i).show();
            }
            for (int i = upgradeStart; i < upgradeEnd; i++)
                menu.getHideableSlot(i).hide();
        } else {
            for (int i = 0; i < upgradeStart; i++)
                menu.getHideableSlot(i).hide();
            for (int i = upgradeStart; i < upgradeEnd; i++) {
                int idx = i - upgradeStart;
                menu.getHideableSlot(i).moveTo(
                        PADDING + (idx % 3) * SLOT_SIZE,
                        OmniPackMenu.PACK_START_Y + (idx / 3) * SLOT_SIZE);
                menu.getHideableSlot(i).show();
            }
        }

        int playerStart = upgradeEnd;
        for (int i = 0; i < 27; i++) {
            menu.getHideableSlot(playerStart + i).moveTo(
                    PADDING + (i % 9) * SLOT_SIZE,
                    playerInvY + (i / 9) * SLOT_SIZE);
            menu.getHideableSlot(playerStart + i).show();
        }
        for (int i = 0; i < 9; i++) {
            menu.getHideableSlot(playerStart + 27 + i).moveTo(
                    PADDING + i * SLOT_SIZE,
                    playerInvY + 3 * SLOT_SIZE + 4);
            menu.getHideableSlot(playerStart + 27 + i).show();
        }
    }
}