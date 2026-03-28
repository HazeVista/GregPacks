package com.astrogreg.gregpacks.inventory;

import com.astrogreg.gregpacks.item.OmniPackTier;
import com.astrogreg.gregpacks.item.UpgradeItem;
import com.astrogreg.gregpacks.item.UpgradeType;
import com.astrogreg.gregpacks.registry.GregPacksMenus;
import com.astrogreg.gregpacks.config.GregPacksConfig;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class OmniPackMenu extends AbstractContainerMenu {

    private final OmniPackInventory packInventory;
    private final OmniPackInventory upgradeInventory;
    private final OmniPackTier tier;
    private final int packSlots;
    private final int maxUpgrades;
    private int packSlotIndex = -1;

    public static final int SLOT_SIZE    = 18;
    public static final int PACK_START_X = 8;
    public static final int PACK_START_Y = 22;
    public static final int COLS         = 9;

    // Server-side constructor — receives real inventories and tier
    public OmniPackMenu(int containerId, Inventory playerInventory,
                        OmniPackInventory packInventory, OmniPackInventory upgradeInventory,
                        OmniPackTier tier) {
        super(GregPacksMenus.OMNIPACK_MENU.get(), containerId);
        this.tier             = tier;
        this.upgradeInventory = upgradeInventory;
        this.maxUpgrades      = upgradeInventory.getContainerSize();

        // Trust the passed inventory size — caller is responsible for correct size
        this.packSlots     = packInventory.getContainerSize();
        this.packInventory = packInventory;

        addPackSlots();
        addUpgradeSlots();
        addPlayerSlots(playerInventory);
    }

    // Client-side constructor — reads tier ordinal from buf
    public OmniPackMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory,
                new OmniPackInventory(OmniPackTier.values()[buf.readByte()].defaultSlots),
                new OmniPackInventory(OmniPackTier.values()[buf.readByte()].defaultMaxUpgrades),
                OmniPackTier.values()[buf.readByte()]); // third byte = tier ordinal
    }

    // Slot registration helpers
    private void addPackSlots() {
        for (int i = 0; i < packSlots; i++) {
            int col = i % COLS;
            int row = i / COLS;
            addSlot(new HideableSlot(packInventory, i,
                    PACK_START_X + col * SLOT_SIZE,
                    PACK_START_Y + row * SLOT_SIZE));
        }
    }

    private void addUpgradeSlots() {
        final OmniPackInventory upgradeInvRef = upgradeInventory;
        for (int i = 0; i < maxUpgrades; i++) {
            final int slotIdx = i;
            addSlot(new HideableSlot(upgradeInventory, i, 0, 0) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    if (!(stack.getItem() instanceof UpgradeItem upgradeItem)) return false;
                    UpgradeType incoming = upgradeItem.getUpgradeType();
                    for (int j = 0; j < maxUpgrades; j++) {
                        if (j == slotIdx) continue;
                        ItemStack other = upgradeInvRef.getItem(j);
                        if (!other.isEmpty() && other.getItem() instanceof UpgradeItem otherUpgrade
                                && otherUpgrade.getUpgradeType() == incoming) {
                            return false;
                        }
                    }
                    return true;
                }

                @Override
                public boolean mayPickup(Player player) {
                    ItemStack current = upgradeInvRef.getItem(slotIdx);
                    if (current.isEmpty() || !(current.getItem() instanceof UpgradeItem upgradeItem))
                        return true;

                    GregPacksConfig.UpgradeConfigs cfg =
                            com.astrogreg.gregpacks.config.GregPacksConfig.INSTANCE.ModuleValues;
                    int bonusSlots = switch (upgradeItem.getUpgradeType()) {
                        case ITEM_CAPACITY_I   -> cfg.itemModule1Bonus;
                        case ITEM_CAPACITY_II  -> cfg.itemModule2Bonus;
                        case ITEM_CAPACITY_III -> cfg.itemModule3Bonus;
                        default -> 0;
                    };
                    if (bonusSlots == 0) return true;

                    int newSlots = packSlots - bonusSlots;
                    for (int j = newSlots; j < packSlots; j++) {
                        if (!OmniPackMenu.this.packInventory.getItem(j).isEmpty())
                            return false;
                    }
                    return true;
                }
            });
        }
    }

    private void addPlayerSlots(Inventory playerInventory) {
        int packRows   = (int) Math.ceil(packSlots / (double) COLS);
        int playerInvY = PACK_START_Y + packRows * SLOT_SIZE + 8;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new HideableSlot(playerInventory,
                        col + row * 9 + 9,
                        PACK_START_X + col * SLOT_SIZE,
                        playerInvY + row * SLOT_SIZE));
            }
        }
        int hotbarY = playerInvY + 3 * SLOT_SIZE + 4;
        for (int col = 0; col < 9; col++) {
            addSlot(new HideableSlot(playerInventory, col,
                    PACK_START_X + col * SLOT_SIZE,
                    hotbarY));
        }
    }

    // Shift-click
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();

            int upgradeStart = packSlots;
            int upgradeEnd   = packSlots + maxUpgrades;
            int playerEnd    = slots.size();

            if (index < upgradeStart) {
                if (!moveItemStackTo(slotStack, upgradeEnd, playerEnd, true))
                    return ItemStack.EMPTY;
            } else if (index < upgradeEnd) {
                if (!moveItemStackTo(slotStack, upgradeEnd, playerEnd, true))
                    return ItemStack.EMPTY;
            } else {
                if (slotStack.getItem() instanceof UpgradeItem) {
                    if (!moveItemStackTo(slotStack, upgradeStart, upgradeEnd, false))
                        return ItemStack.EMPTY;
                } else {
                    if (!moveItemStackTo(slotStack, 0, upgradeStart, false))
                        return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) { return true; }

    // Accessors
    public OmniPackInventory getPackInventory()    { return packInventory; }
    public OmniPackInventory getUpgradeInventory() { return upgradeInventory; }
    public OmniPackTier getTier()                  { return tier; }
    public int getPackSlots()                      { return packSlots; }
    public int getMaxUpgrades()                    { return maxUpgrades; }
    public int getPackSlotIndex()                  { return packSlotIndex; }
    public void setPackSlotIndex(int index)        { this.packSlotIndex = index; }

    public HideableSlot getHideableSlot(int index) {
        return (HideableSlot) slots.get(index);
    }
}