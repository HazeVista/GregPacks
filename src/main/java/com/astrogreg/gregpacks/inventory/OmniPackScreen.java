package com.astrogreg.gregpacks.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;

import com.astrogreg.gregpacks.GregPacks;
import com.astrogreg.gregpacks.network.CPacketFluidInteract;
import com.astrogreg.gregpacks.network.CPacketJetpackConfig;
import com.astrogreg.gregpacks.network.GregPacksNetwork;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    // Layout
    private static final int PAD = 7;
    private static final int COLS = 9;
    private static final int SLOT_S = 18;
    private static final int LABEL_H = 14;

    private static final int BAR_W = 10;
    private static final int BAR_SEP = 4;
    private static final int BAR_GAP = 3;

    // Rail buttons
    private static final int BTN = 20;
    private static final int BTN_GAP = 2;
    private static final int ICON = 16;

    // Panels
    private static final int UP_COLS = 2;
    private static final int UP_PAD = 6;
    private static final int ARROW_GAP = 24;
    private static final int POPUP_GAP = 4;
    private static final int CRAFT_W = UP_PAD + 3 * SLOT_S + ARROW_GAP + SLOT_S + UP_PAD;
    private static final int CRAFT_H = UP_PAD + 3 * SLOT_S + UP_PAD;
    private static final int JETPACK_PANEL_W = 90;
    private static final int JETPACK_PANEL_H = 44;
    // widest panel, used to decide which side rail goes on. Raise if a wider panel is added.
    private static final int MAX_PANEL_W = CRAFT_W;

    private enum Panel {

        UPGRADES("upgrades", "container.gregpacks.tab.upgrades", "U", 0xFF3B6FD4),
        CRAFTING("crafting", "container.gregpacks.tab.crafting", "C", 0xFF8A5A2B),
        JETPACK("jetpack", "container.gregpacks.tab.jetpack", "J", 0xFFC0392B);

        final ResourceLocation icon;
        final String langKey;
        final String letter;
        final int color;

        Panel(String name, String langKey, String letter, int color) {
            this.icon = new ResourceLocation(GregPacks.MOD_ID, "textures/gui/icons/" + name + ".png");
            this.langKey = langKey;
            this.letter = letter;
            this.color = color;
        }
    }

    // State
    // Remembered between screens so reopened omnipacks keeps the open panel
    private static Panel lastSelected = null;

    private Panel selected = lastSelected; // what the player chooses
    private Panel laidOut = null; // panel whose layout is currently applied
    private int lastFlags = -1;
    private boolean railLeft;
    private int railX;
    private int panelX, panelY, panelW, panelH;
    private final Map<Panel, Boolean> iconPresent = new EnumMap<>(Panel.class);
    private final Map<Panel, Button> railButtons = new EnumMap<>(Panel.class);

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

        // Rail goes on the left when the rail + widest panel fit, otherwise on the right (past the bars)
        railLeft = leftPos - POPUP_GAP - BTN - POPUP_GAP - MAX_PANEL_W >= 2;
        railX = railLeft ? leftPos - POPUP_GAP - BTN :
                leftPos + imageWidth + BAR_GAP + 2 * BAR_W + BAR_SEP + POPUP_GAP;

        for (Panel p : Panel.values()) {
            iconPresent.put(p, Minecraft.getInstance().getResourceManager().getResource(p.icon).isPresent());
        }

        // Vanilla buttons: grey frame, hover highlight, click sound and tooltip come from the library
        railButtons.clear();
        for (Panel p : Panel.values()) {
            Button b = Button.builder(Component.empty(), btn -> onRailPressed(p))
                    .bounds(railX, topPos, BTN, BTN)
                    .tooltip(Tooltip.create(Component.translatable(p.langKey)))
                    .build();
            railButtons.put(p, addRenderableWidget(b));
        }

        laidOut = null;
        lastFlags = -1;
        refreshPanel();
    }

    private void onRailPressed(Panel p) {
        selected = (laidOut == p) ? null : p;
        lastSelected = selected;
        refreshPanel();
    }

    // Shows the buttons of installed modules packed from the top in enum order
    private void positionRailButtons() {
        int row = 0;
        for (Panel p : Panel.values()) {
            Button b = railButtons.get(p);
            if (b == null) continue;
            boolean show = isAvailable(p);
            b.visible = show;
            if (show) b.setPosition(railX, btnY(row++));
        }
    }

    // Panel state
    private boolean isAvailable(Panel p) {
        return switch (p) {
            case UPGRADES -> true;
            case CRAFTING -> menu.hasCrafting();
            case JETPACK -> menu.hasJetpack2();
        };
    }

    private Panel effectivePanel() {
        return selected != null && isAvailable(selected) ? selected : null;
    }

    private List<Panel> visiblePanels() {
        List<Panel> list = new ArrayList<>();
        for (Panel p : Panel.values()) {
            if (isAvailable(p)) list.add(p);
        }
        return list;
    }

    // Re-applies the layout whenever the open panel or the installed modules change (checked every frame)
    private void refreshPanel() {
        Panel eff = effectivePanel();
        int flags = (menu.hasCrafting() ? 1 : 0) | (menu.hasJetpack2() ? 2 : 0);
        if (eff == laidOut && flags == lastFlags) return;
        laidOut = eff;
        lastFlags = flags;
        positionRailButtons();
        layoutPanel(eff);
    }

    private int[] panelSize(Panel p) {
        return switch (p) {
            case UPGRADES -> {
                int rows = (int) Math.ceil(menu.getMaxUpgrades() / (double) UP_COLS);
                yield new int[] { UP_PAD * 2 + UP_COLS * SLOT_S, UP_PAD * 2 + rows * SLOT_S };
            }
            case CRAFTING -> new int[] { CRAFT_W, CRAFT_H };
            case JETPACK -> new int[] { JETPACK_PANEL_W, JETPACK_PANEL_H };
        };
    }

    private void layoutPanel(Panel p) {
        hideUpSlots();
        hideCraftSlots();
        if (p == null) return;

        int[] size = panelSize(p);
        panelW = size[0];
        panelH = size[1];
        panelY = topPos;
        panelX = railLeft ? railX - POPUP_GAP - panelW : railX + BTN + POPUP_GAP;

        switch (p) {
            case UPGRADES -> placeUpgradeSlots();
            case CRAFTING -> placeCraftSlots();
            case JETPACK -> {}
        }
    }

    private void placeUpgradeSlots() {
        int uStart = menu.getPackSlots(), uEnd = uStart + menu.getMaxUpgrades();
        for (int i = uStart; i < uEnd; i++) {
            int idx = i - uStart;
            menu.getHideableSlot(i).moveTo(
                    panelX - leftPos + UP_PAD + (idx % UP_COLS) * SLOT_S,
                    panelY - topPos + UP_PAD + (idx / UP_COLS) * SLOT_S);
            menu.getHideableSlot(i).show();
        }
    }

    private void placeCraftSlots() {
        int gridStart = menu.getCraftingGridStart();
        if (gridStart < 0) return;
        for (int i = 0; i < 9; i++) {
            int col = i % 3, row = i / 3;
            menu.getHideableSlot(gridStart + i).moveTo(
                    panelX - leftPos + UP_PAD + col * SLOT_S,
                    panelY - topPos + UP_PAD + row * SLOT_S);
            menu.getHideableSlot(gridStart + i).show();
        }

        HideableResultSlot result = menu.getCraftingResultSlotObj();
        if (result != null) {
            result.moveTo(
                    panelX - leftPos + UP_PAD + 3 * SLOT_S + ARROW_GAP,
                    panelY - topPos + UP_PAD + SLOT_S);
            result.show();
        }
    }

    private void hideUpSlots() {
        int uStart = menu.getPackSlots(), uEnd = uStart + menu.getMaxUpgrades();
        for (int i = uStart; i < uEnd; i++) menu.getHideableSlot(i).hide();
    }

    private void hideCraftSlots() {
        if (!menu.hasCrafting()) return;
        int gridStart = menu.getCraftingGridStart();
        if (gridStart < 0) return;
        for (int i = gridStart; i < gridStart + 9; i++) menu.getHideableSlot(i).hide();
        HideableResultSlot result = menu.getCraftingResultSlotObj();
        if (result != null) result.hide();
    }

    // Render
    @Override
    public void render(@NotNull GuiGraphics g, int mx, int my, float pt) {
        refreshPanel();
        renderBackground(g);
        super.render(g, mx, my, pt);
        renderPanel(g);
        renderRailIcons(g);
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

    // Rail
    private int btnY(int index) {
        return topPos + index * (BTN + BTN_GAP);
    }

    // Icon drawn over each vanilla button
    private void renderRailIcons(GuiGraphics g) {
        for (Panel p : Panel.values()) {
            Button b = railButtons.get(p);
            if (b == null || !b.visible) continue;

            boolean active = p == laidOut;
            if (active) g.fill(b.getX() + 2, b.getY() + 2, b.getX() + BTN - 2, b.getY() + BTN - 2, 0x50000000);

            int off = active ? 1 : 0;
            int ix = b.getX() + (BTN - ICON) / 2 + off;
            int iy = b.getY() + (BTN - ICON) / 2 + off;
            if (Boolean.TRUE.equals(iconPresent.get(p))) {
                g.blit(p.icon, ix, iy, 0, 0, ICON, ICON, ICON, ICON);
            } else {
                g.fill(ix, iy, ix + ICON, iy + ICON, p.color);
                g.drawString(font, p.letter, ix + (ICON - font.width(p.letter)) / 2,
                        iy + (ICON - 8) / 2, 0xFFFFFFFF, false);
            }
        }
    }

    // Panels
    private void renderPanel(GuiGraphics g) {
        if (laidOut == null) return;
        g.fill(panelX, panelY, panelX + panelW, panelY + panelH, C_BG);
        raised(g, panelX, panelY, panelW, panelH);

        switch (laidOut) {
            case UPGRADES -> renderUpgradesPanel(g);
            case CRAFTING -> renderCraftPanel(g);
            case JETPACK -> renderJetpackPanel(g);
        }
    }

    private void renderUpgradesPanel(GuiGraphics g) {
        int uStart = menu.getPackSlots(), uEnd = uStart + menu.getMaxUpgrades();
        for (int i = uStart; i < uEnd; i++) {
            HideableSlot s = menu.getHideableSlot(i);
            if (s.isVisible()) slotInset(g, leftPos + s.x - 1, topPos + s.y - 1);
        }
        renderTHEHover(g, uStart, uEnd);
    }

    private void renderCraftPanel(GuiGraphics g) {
        int gridStart = menu.getCraftingGridStart();
        if (gridStart >= 0) {
            for (int i = gridStart; i < gridStart + 9; i++) {
                HideableSlot s = menu.getHideableSlot(i);
                if (s.isVisible()) slotInset(g, leftPos + s.x - 1, topPos + s.y - 1);
            }
        }
        // The arrow
        int arrowX = panelX + UP_PAD + 3 * SLOT_S + 4;
        int arrowCY = panelY + UP_PAD + SLOT_S + SLOT_S / 2;
        drawArrowShape(g, arrowX + 1, arrowCY + 1, C_DARK);
        drawArrowShape(g, arrowX, arrowCY, C_SLOT);

        HideableResultSlot rs = menu.getCraftingResultSlotObj();
        if (rs != null && rs.isVisible()) {
            slotInset(g, leftPos + rs.x - 1, topPos + rs.y - 1);
        }

        if (gridStart >= 0) renderTHEHover(g, gridStart, gridStart + 10);
    }

    private void renderJetpackPanel(GuiGraphics g) {
        int px = panelX, py = panelY, pw = panelW;

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
            g.renderTooltip(font, List.of(
                            Component.literal("§bFluid: " + nm),
                            Component.literal(amt + " / " + cap + " mB"),
                            Component.literal("§7Left-click to fill, Right-click to drain")),
                    Optional.empty(), mx, my);
        }

        // Energy bar
        bar(g, bx2, by, bh);
        long eu = menu.getSyncedEU(), euMax = menu.getSyncedMaxEU();
        if (eu > 0 && euMax > 0) {
            int fh = (int) ((float) eu / euMax * (bh - 2));
            g.fill(bx2 + 1, by + bh - 1 - fh, bx2 + BAR_W - 1, by + bh - 1, C_EU);
        }
        if (mx >= bx2 && mx < bx2 + BAR_W && my >= by && my < by + bh) {
            g.renderTooltip(font, List.of(
                            Component.literal("§6Energy (EU)"),
                            Component.literal(eu + " / " + euMax + " EU")),
                    Optional.empty(), mx, my);
        }
    }

    // Input
    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        if (laidOut == Panel.JETPACK) {
            int px = panelX, py = panelY, pw = panelW;
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

        int railH = visiblePanels().size() * (BTN + BTN_GAP);
        if (mx >= railX && mx < railX + BTN && my >= guiTop && my < guiTop + railH) return false;

        if (laidOut != null) {
            if (mx >= panelX && mx < panelX + panelW && my >= panelY && my < panelY + panelH) return false;
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