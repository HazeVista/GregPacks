package com.astrogreg.gregpacks.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;

import com.astrogreg.gregpacks.network.CPacketFluidInteract;
import com.astrogreg.gregpacks.network.CPacketJetpackConfig;
import com.astrogreg.gregpacks.network.GregPacksNetwork;
import org.jetbrains.annotations.NotNull;

public class OmniPackScreen extends AbstractContainerScreen<OmniPackMenu> {

    private static final int C_BG = 0xFFC6C6C6;
    private static final int C_DARK = 0xFF555555;
    private static final int C_LIGHT = 0xFFFFFFFF;
    private static final int C_SLOT = 0xFF8B8B8B;
    private static final int C_SLOT_DARK = 0xFF373737;
    private static final int C_TEXT = 0xFF404040;
    private static final int C_BAR = 0xFF373737;
    private static final int C_FLUID = 0xFF0099FF;
    private static final int C_EU = 0xFFEEE600;
    private static final int C_TAB_IDLE = 0xFFAFAFAF;

    // Layout
    private static final int PAD = 7;
    private static final int COLS = 9;
    private static final int SLOT_S = 18; // gulp
    private static final int LABEL_H = 14;

    private static final int BAR_W = 10;
    private static final int BAR_SEP = 4;
    private static final int BAR_GAP = 3;

    // Tab
    private static final int TAB_W = 70;
    private static final int TAB_H = 14;
    private static final int TAB_OFF = 0;
    private static final int TAB2_OFF = TAB_W + 4;

    // Popup
    private static final int UP_COLS = 2;
    private static final int UP_PAD = 6;
    private static final int ARROW_GAP = 24;
    private static final int JETPACK_PANEL_H = 44;
    private static final int POPUP_GAP = 4;

    // State
    private boolean upOpen = false;
    private boolean craftOpen = false;
    private int popX, popY, popW, popH;
    private int craftPopX, craftPopY;

    private int pressedButton = -1;
    private long pressedUntil = 0;

    private final int packGridBottom;
    private final int invTop;
    private final int hotbarTop;

    public OmniPackScreen(OmniPackMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        int packRows = (int) Math.ceil(menu.getPackSlots() / (double) COLS);
        packGridBottom = PAD + 10 + packRows * SLOT_S;
        invTop = packGridBottom + LABEL_H;
        hotbarTop = invTop + 3 * SLOT_S + 4;
        imageWidth = PAD + COLS * SLOT_S + PAD;
        imageHeight = hotbarTop + SLOT_S + PAD;
    }

    @Override
    protected void init() {
        super.init();
        applyMainLayout();
        if (upOpen) openPopup();
        else hideUpSlots();
        if (craftOpen) openCraftPopup();
        else hideCraftSlots();
    }

    // Render
    @Override
    public void render(@NotNull GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        super.render(g, mx, my, pt);
        renderTab(g, mx, my);
        if (menu.hasCrafting()) renderCraftTab(g, mx, my);
        if (upOpen) renderPopup(g);
        if (craftOpen) renderCraftPopup(g);
        renderBars(g, mx, my);
        renderTooltip(g, mx, my);
    }

    @Override
    protected void renderBg(GuiGraphics g, float pt, int mx, int my) {
        int x = leftPos, y = topPos;
        g.fill(x, y, x + imageWidth, y + imageHeight, C_BG);
        raised(g, x, y, imageWidth, imageHeight);

        int uStart = menu.getPackSlots(), uEnd = uStart + menu.getMaxUpgrades();
        int craftStart = menu.getCraftingGridStart();
        int craftEnd = craftStart >= 0 ? craftStart + 10 : -1;

        for (int i = 0; i < menu.slots.size(); i++) {
            if (i >= uStart && i < uEnd) continue;
            if (craftStart >= 0 && i >= craftStart && i < craftEnd) continue;
            HideableSlot s = menu.getHideableSlot(i);
            if (s.isVisible()) slotInset(g, x + s.x - 1, y + s.y - 1);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mx, int my) {
        int titleY = (PAD + 10 - font.lineHeight) / 2;
        g.drawString(font, title, PAD, titleY, C_TEXT, false);
        int invLabelY = packGridBottom + (LABEL_H - font.lineHeight) / 2;
        g.drawString(font, Component.translatable("container.inventory"), PAD, invLabelY, C_TEXT, false);
    }

    private void renderTab(GuiGraphics g, int mx, int my) {
        renderTabButton(g, leftPos + TAB_OFF, "Upgrades " + (upOpen ? "▼" : "▶"), upOpen);
    }

    private void renderTabButton(GuiGraphics g, int tx, String label, boolean open) {
        int ty = topPos - TAB_H;
        int bg = open ? C_BG : C_TAB_IDLE;

        g.fill(tx, ty, tx + TAB_W, ty + TAB_H + (open ? 1 : 0), bg);
        g.fill(tx, ty, tx + TAB_W, ty + 1, C_LIGHT);
        g.fill(tx, ty, tx + 1, ty + TAB_H, C_LIGHT);
        g.fill(tx + TAB_W - 1, ty, tx + TAB_W, ty + TAB_H, C_DARK);
        if (!open) g.fill(tx + 1, ty + TAB_H - 1, tx + TAB_W - 1, ty + TAB_H, C_DARK);

        g.drawString(font, label,
                tx + (TAB_W - font.width(label)) / 2,
                ty + (TAB_H - 7) / 2,
                C_TEXT, false);
    }

    private void renderPopup(GuiGraphics g) {
        g.fill(popX, popY, popX + popW, popY + popH, C_BG);
        raised(g, popX, popY, popW, popH);

        int uStart = menu.getPackSlots(), uEnd = uStart + menu.getMaxUpgrades();
        for (int i = uStart; i < uEnd; i++) {
            HideableSlot s = menu.getHideableSlot(i);
            if (s.isVisible()) slotInset(g, leftPos + s.x - 1, topPos + s.y - 1);
        }

        if (menu.hasJetpack2()) {
            renderJetpackPanel(g);
        }

        renderTHEHover(g, uStart, uEnd);
    }

    private void renderJetpackPanel(GuiGraphics g) {
        int px = popX, py = popY + popH + POPUP_GAP;
        int pw = popW, ph = JETPACK_PANEL_H;

        g.fill(px, py, px + pw, py + ph, C_BG);
        raised(g, px, py, pw, ph);

        g.drawString(font, "Speed: " + menu.getJetpackSpeed(), px + UP_PAD, py + 6, C_TEXT, false);
        renderSmallButton(g, px + pw - UP_PAD - 22, py + 4, "-", 0);
        renderSmallButton(g, px + pw - UP_PAD - 10, py + 4, "+", 1);

        g.drawString(font, "Thrust: " + menu.getJetpackThrust(), px + UP_PAD, py + 24, C_TEXT, false);
        renderSmallButton(g, px + pw - UP_PAD - 22, py + 22, "-", 2);
        renderSmallButton(g, px + pw - UP_PAD - 10, py + 22, "+", 3);
    }

    private void renderSmallButton(GuiGraphics g, int x, int y, String label, int id) {
        boolean pressed = pressedButton == id && System.currentTimeMillis() < pressedUntil;
        int bg = pressed ? C_DARK : C_BG;
        int textColor = C_TEXT;
        g.fill(x, y, x + 10, y + 10, bg);
        if (!pressed) raised(g, x, y, 10, 10);
        else sunken(g, x, y, 10, 10);
        g.drawString(font, label, x + (label.equals("+") ? 2 : 3), y + (pressed ? 2 : 1), textColor, false);
    }

    // Bars/Crafting tab
    private void renderCraftTab(GuiGraphics g, int mx, int my) {
        renderTabButton(g, leftPos + TAB2_OFF, "Crafting " + (craftOpen ? "▼" : "▶"), craftOpen);
    }

    private void renderCraftPopup(GuiGraphics g) {
        int pw = UP_PAD + 3 * SLOT_S + ARROW_GAP + SLOT_S + UP_PAD;
        int ph = UP_PAD + 3 * SLOT_S + UP_PAD;
        g.fill(craftPopX, craftPopY, craftPopX + pw, craftPopY + ph, C_BG);
        raised(g, craftPopX, craftPopY, pw, ph);

        int gridStart = menu.getCraftingGridStart();
        if (gridStart >= 0) {
            for (int i = gridStart; i < gridStart + 9; i++) {
                HideableSlot s = menu.getHideableSlot(i);
                if (s.isVisible()) slotInset(g, leftPos + s.x - 1, topPos + s.y - 1);
            }
        }
        // The arrow
        int arrowX = craftPopX + UP_PAD + 3 * SLOT_S + 4;
        int arrowCY = craftPopY + UP_PAD + SLOT_S + SLOT_S / 2;
        drawArrowShape(g, arrowX + 1, arrowCY + 1, C_DARK);
        drawArrowShape(g, arrowX, arrowCY, C_SLOT);

        HideableResultSlot rs = menu.getCraftingResultSlotObj();
        if (rs != null && rs.isVisible()) {
            slotInset(g, leftPos + rs.x - 1, topPos + rs.y - 1);
        }

        if (gridStart >= 0) renderTHEHover(g, gridStart, gridStart + 10);
    }

    private void renderTHEHover(GuiGraphics g, int startIndex, int endIndex) {
        if (hoveredSlot == null || !hoveredSlot.isActive()) return;
        if (hoveredSlot.index >= startIndex && hoveredSlot.index < endIndex) {
            renderSlotHighlight(g, leftPos + hoveredSlot.x, topPos + hoveredSlot.y, 0);
        }
    }

    private void renderBars(GuiGraphics g, int mx, int my) {
        int bx1 = leftPos + imageWidth + BAR_GAP;
        int bx2 = bx1 + BAR_W + BAR_SEP;
        int by = topPos;
        int bh = imageHeight;

        // Fluid bar
        bar(g, bx1, by, bh);
        int amt = menu.getSyncedFluidAmount(), cap = menu.getSyncedFluidCapacity();
        if (amt > 0 && cap > 0) {
            int fh = (int) ((float) amt / cap * (bh - 2));
            Fluid fl = menu.getSyncedFluid();
            renderFluidFill(g, bx1 + 1, by + bh - 1 - fh, fh, fl);
        }
        if (mx >= bx1 && mx < bx1 + BAR_W && my >= by && my < by + bh) {
            Fluid fl = menu.getSyncedFluid();
            String nm = fl != null ? fl.getFluidType().getDescription().getString() : "Empty";
            g.renderTooltip(font, java.util.List.of(
                    Component.literal("§bFluid: " + nm),
                    Component.literal(amt + " / " + cap + " mB"),
                    Component.literal("§7Left-click to fill, Right-click to drain")),
                    java.util.Optional.empty(), mx, my);
        }

        // Energy bar
        bar(g, bx2, by, bh);
        long eu = menu.getSyncedEU(), euMax = menu.getSyncedMaxEU();
        if (eu > 0 && euMax > 0) {
            int fh = (int) ((float) eu / euMax * (bh - 2));
            g.fill(bx2 + 1, by + bh - 1 - fh, bx2 + BAR_W - 1, by + bh - 1, C_EU);
        }
        if (mx >= bx2 && mx < bx2 + BAR_W && my >= by && my < by + bh) {
            g.renderTooltip(font, java.util.List.of(
                    Component.literal("§6Energy (EU)"),
                    Component.literal(eu + " / " + euMax + " EU")),
                    java.util.Optional.empty(), mx, my);
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        if (btn == 0) {
            int ty = topPos - TAB_H;

            int tx = leftPos + TAB_OFF;
            if (mx >= tx && mx < tx + TAB_W && my >= ty && my < ty + TAB_H) {
                upOpen = !upOpen;
                if (upOpen) {
                    openPopup();
                } else {
                    hideUpSlots();
                    if (craftOpen) openCraftPopup();
                }
                return true;
            }

            if (menu.hasCrafting()) {
                int tx2 = leftPos + TAB2_OFF;
                if (mx >= tx2 && mx < tx2 + TAB_W && my >= ty && my < ty + TAB_H) {
                    craftOpen = !craftOpen;
                    if (craftOpen) openCraftPopup();
                    else hideCraftSlots();
                    return true;
                }
            }
        }

        if (upOpen && menu.hasJetpack2()) {
            int px = popX, py = popY + popH + 4;
            int pw = popW;
            if (inButton(mx, my, px + pw - UP_PAD - 22, py + 4)) {
                GregPacksNetwork.CHANNEL.sendToServer(new CPacketJetpackConfig(true, -1));
                pressedButton = 0;
                pressedUntil = System.currentTimeMillis() + 100;
                return true;
            } else if (inButton(mx, my, px + pw - UP_PAD - 10, py + 4)) {
                GregPacksNetwork.CHANNEL.sendToServer(new CPacketJetpackConfig(true, +1));
                pressedButton = 1;
                pressedUntil = System.currentTimeMillis() + 100;
                return true;
            } else if (inButton(mx, my, px + pw - UP_PAD - 22, py + 22)) {
                GregPacksNetwork.CHANNEL.sendToServer(new CPacketJetpackConfig(false, -1));
                pressedButton = 2;
                pressedUntil = System.currentTimeMillis() + 100;
                return true;
            } else if (inButton(mx, my, px + pw - UP_PAD - 10, py + 22)) {
                GregPacksNetwork.CHANNEL.sendToServer(new CPacketJetpackConfig(false, +1));
                pressedButton = 3;
                pressedUntil = System.currentTimeMillis() + 100;
                return true;
            }
        }

        int bx1 = leftPos + imageWidth + BAR_GAP;
        if (mx >= bx1 && mx < bx1 + BAR_W && my >= topPos && my < topPos + imageHeight) {
            if (btn == 0 || btn == 1) {
                GregPacksNetwork.CHANNEL.sendToServer(new CPacketFluidInteract(btn == 0));
                return true;
            }
        }

        return super.mouseClicked(mx, my, btn);
    }

    private boolean inButton(double mx, double my, int x, int y) {
        return mx >= x && mx < x + 10 && my >= y && my < y + 10;
    }

    @Override
    protected boolean hasClickedOutside(double mx, double my, int guiLeft, int guiTop, int mouseButton) {
        int bx1 = guiLeft + imageWidth + BAR_GAP;
        int bx2 = bx1 + BAR_W + BAR_SEP + BAR_W;
        if (mx >= bx1 && mx <= bx2 && my >= guiTop && my < guiTop + imageHeight) return false;

        if (upOpen) {
            if (mx >= popX && mx < popX + popW && my >= popY && my < upgradesStackBottom()) return false;
        }

        if (craftOpen) {
            int cpw = UP_PAD + 3 * SLOT_S + ARROW_GAP + SLOT_S + UP_PAD;
            int cph = UP_PAD + 3 * SLOT_S + UP_PAD;
            if (mx >= craftPopX && mx < craftPopX + cpw && my >= craftPopY && my < craftPopY + cph) return false;
        }

        return super.hasClickedOutside(mx, my, guiLeft, guiTop, mouseButton);
    }

    // Slot layout
    private void applyMainLayout() {
        int uStart = menu.getPackSlots(), uEnd = uStart + menu.getMaxUpgrades();

        for (int i = 0; i < uStart; i++)
            show(i, PAD + (i % COLS) * SLOT_S, PAD + 10 + (i / COLS) * SLOT_S);

        for (int i = 0; i < 27; i++)
            show(uEnd + i, PAD + (i % 9) * SLOT_S, invTop + (i / 9) * SLOT_S);

        for (int i = 0; i < 9; i++)
            show(uEnd + 27 + i, PAD + i * SLOT_S, hotbarTop);
    }

    private void openPopup() {
        int uStart = menu.getPackSlots(), uEnd = uStart + menu.getMaxUpgrades();
        int rows = (int) Math.ceil(menu.getMaxUpgrades() / (double) UP_COLS);
        int minW = menu.hasJetpack2() ? 90 : 0;
        popW = Math.max(minW, UP_PAD + UP_COLS * SLOT_S + UP_PAD);
        popH = UP_PAD + rows * SLOT_S + UP_PAD;
        popY = topPos;

        int leftCandidate = leftPos - popW - POPUP_GAP;
        popX = leftCandidate >= 2 ? leftCandidate : leftPos + imageWidth + POPUP_GAP;

        for (int i = uStart; i < uEnd; i++) {
            int idx = i - uStart;
            menu.getHideableSlot(i).moveTo(
                    popX - leftPos + UP_PAD + (idx % UP_COLS) * SLOT_S,
                    popY - topPos + UP_PAD + (idx / UP_COLS) * SLOT_S);
            menu.getHideableSlot(i).show();
        }

        if (craftOpen) openCraftPopup();
    }

    private int upgradesStackBottom() {
        return popY + popH + (menu.hasJetpack2() ? POPUP_GAP + JETPACK_PANEL_H : 0);
    }

    private void hideUpSlots() {
        int uStart = menu.getPackSlots(), uEnd = uStart + menu.getMaxUpgrades();
        for (int i = uStart; i < uEnd; i++) menu.getHideableSlot(i).hide();
    }

    private void openCraftPopup() {
        if (!menu.hasCrafting()) return;

        int cpw = UP_PAD + 3 * SLOT_S + ARROW_GAP + SLOT_S + UP_PAD;
        int cph = UP_PAD + 3 * SLOT_S + UP_PAD;
        int leftCandidate = leftPos - cpw - POPUP_GAP;
        boolean craftLeft = leftCandidate >= 2;
        craftPopX = craftLeft ? leftCandidate : leftPos + imageWidth + POPUP_GAP;

        craftPopY = topPos;
        if (upOpen && (popX < leftPos) == craftLeft) {
            craftPopY = upgradesStackBottom() + POPUP_GAP;
            int maxY = height - cph - 2;
            if (craftPopY > maxY) craftPopY = Math.max(topPos, maxY);
        }

        int gridStart = menu.getCraftingGridStart();
        for (int i = 0; i < 9; i++) {
            int col = i % 3, row = i / 3;
            menu.getHideableSlot(gridStart + i).moveTo(
                    craftPopX - leftPos + UP_PAD + col * SLOT_S,
                    craftPopY - topPos + UP_PAD + row * SLOT_S);
            menu.getHideableSlot(gridStart + i).show();
        }

        HideableResultSlot result = menu.getCraftingResultSlotObj();
        if (result != null) {
            result.moveTo(
                    craftPopX - leftPos + UP_PAD + 3 * SLOT_S + ARROW_GAP,
                    craftPopY - topPos + UP_PAD + SLOT_S);
            result.show();
        }
    }

    private void hideCraftSlots() {
        if (!menu.hasCrafting()) return;
        int gridStart = menu.getCraftingGridStart();
        if (gridStart < 0) return;
        for (int i = gridStart; i < gridStart + 9; i++) menu.getHideableSlot(i).hide();
        HideableResultSlot result = menu.getCraftingResultSlotObj();
        if (result != null) result.hide();
    }

    // Helpers
    private void show(int i, int x, int y) {
        menu.getHideableSlot(i).moveTo(x, y);
        menu.getHideableSlot(i).show();
    }

    private void raised(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + 1, C_LIGHT);
        g.fill(x, y, x + 1, y + h, C_LIGHT);
        g.fill(x, y + h - 1, x + w, y + h, C_DARK);
        g.fill(x + w - 1, y, x + w, y + h, C_DARK);
    }

    private void sunken(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + 1, C_SLOT_DARK);
        g.fill(x, y, x + 1, y + h, C_SLOT_DARK);
        g.fill(x, y + h - 1, x + w, y + h, C_LIGHT);
        g.fill(x + w - 1, y, x + w, y + h, C_LIGHT);
    }

    private void slotInset(GuiGraphics g, int x, int y) {
        g.fill(x, y, x + SLOT_S, y + SLOT_S, C_SLOT);
        g.fill(x, y, x + SLOT_S, y + 1, C_SLOT_DARK);
        g.fill(x, y, x + 1, y + SLOT_S, C_SLOT_DARK);
        g.fill(x, y + SLOT_S - 1, x + SLOT_S, y + SLOT_S, C_LIGHT);
        g.fill(x + SLOT_S - 1, y, x + SLOT_S, y + SLOT_S, C_LIGHT);
        g.fill(x + SLOT_S - 1, y, x + SLOT_S, y + 1, C_SLOT);
        g.fill(x, y + SLOT_S - 1, x + 1, y + SLOT_S, C_SLOT);
    }

    private void drawArrowShape(GuiGraphics g, int x, int cy, int color) {
        g.fill(x, cy - 2, x + 9, cy + 2, color);
        for (int i = 0; i < 6; i++) { // head
            g.fill(x + 8 + i, cy - 5 + i, x + 9 + i, cy + 5 - i, color);
        }
    }

    private void bar(GuiGraphics g, int x, int y, int h) {
        g.fill(x, y, x + OmniPackScreen.BAR_W, y + h, C_BAR);
        sunken(g, x, y, OmniPackScreen.BAR_W, h);
    }

    private void renderFluidFill(GuiGraphics g, int x, int top, int fillH, Fluid fl) {
        if (fillH <= 0) return;
        if (fl == null) {
            g.fill(x, top, x + 8, top + fillH, C_FLUID);
            return;
        }

        IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(fl.getFluidType());
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(ext.getStillTexture());
        int tint = ext.getTintColor();
        float r = ((tint >> 16) & 0xFF) / 255f;
        float gn = ((tint >> 8) & 0xFF) / 255f;
        float b = (tint & 0xFF) / 255f;

        int bottom = top + fillH;
        g.enableScissor(x, top, x + 8, bottom);
        g.setColor(r, gn, b, 1f);
        for (int yy = bottom - 16; yy > top - 16; yy -= 16) {
            g.blit(x, yy, 0, 8, 16, sprite);
        }
        g.setColor(1f, 1f, 1f, 1f);
        g.disableScissor();
    }
}
