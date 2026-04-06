package com.astrogreg.gregpacks.inventory;

import com.astrogreg.gregpacks.item.OmniPackTier;
import com.astrogreg.gregpacks.item.UpgradeItem;
import com.astrogreg.gregpacks.item.UpgradeType;
import com.astrogreg.gregpacks.registry.GregPacksMenus;
import com.astrogreg.gregpacks.config.GregPacksConfig;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;

public class OmniPackMenu extends AbstractContainerMenu {

    private final OmniPackInventory packInventory;
    private final OmniPackInventory upgradeInventory;
    private final OmniPackTier tier;
    private final int packSlots;
    private final int maxUpgrades;
    private int packSlotIndex = -1;
    private ServerPlayer serverPlayer = null;

    public static final int SLOT_SIZE    = 18;
    public static final int PACK_START_X = 8;
    public static final int PACK_START_Y = 22;
    public static final int COLS         = 9;

    // Server-side constructor
    public OmniPackMenu(int containerId, Inventory playerInventory,
                        OmniPackInventory packInventory, OmniPackInventory upgradeInventory,
                        OmniPackTier tier) {
        super(GregPacksMenus.OMNIPACK_MENU.get(), containerId);
        this.tier             = tier;
        this.upgradeInventory = upgradeInventory;
        this.maxUpgrades      = upgradeInventory.getContainerSize();
        this.packSlots        = packInventory.getContainerSize();
        this.packInventory    = packInventory;

        addPackSlots();
        addUpgradeSlots();
        addPlayerSlots(playerInventory);
    }

    // Client-side constructor — reads real slot count from buf
    // Buffer layout (written by OpenPackHelper):
    public OmniPackMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory,
                new OmniPackInventory(buf.readShort()),              // real slot count
                new OmniPackInventory(buf.readByte()),               // upgrade slot count
                OmniPackTier.values()[buf.readByte()]);              // tier ordinal
    }

    public void setServerPlayer(ServerPlayer player) {
        this.serverPlayer = player;
    }

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
                            GregPacksConfig.INSTANCE.ModuleValues;

                    // Only item-capacity modules shrink the pack gui
                    int thisBonus = switch (upgradeItem.getUpgradeType()) {
                        case ITEM_CAPACITY_I   -> cfg.itemModule1Bonus;
                        case ITEM_CAPACITY_II  -> cfg.itemModule2Bonus;
                        case ITEM_CAPACITY_III -> cfg.itemModule3Bonus;
                        default -> 0;
                    };
                    if (thisBonus == 0) return true;
                    int slotsWithout = tier.defaultSlots;
                    for (int j = 0; j < maxUpgrades; j++) {
                        if (j == slotIdx) continue; // skip the module being removed
                        ItemStack other = upgradeInvRef.getItem(j);
                        if (other.isEmpty() || !(other.getItem() instanceof UpgradeItem otherUpgrade))
                            continue;
                        slotsWithout += switch (otherUpgrade.getUpgradeType()) {
                            case ITEM_CAPACITY_I   -> cfg.itemModule1Bonus;
                            case ITEM_CAPACITY_II  -> cfg.itemModule2Bonus;
                            case ITEM_CAPACITY_III -> cfg.itemModule3Bonus;
                            default -> 0;
                        };
                    }

                    // Slots [slotsWithout, packSlots) would disappear — they must be empty.
                    for (int j = slotsWithout; j < packSlots; j++) {
                        if (!OmniPackMenu.this.packInventory.getItem(j).isEmpty())
                            return false;
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);

        if (serverPlayer == null) return;
        if (container != upgradeInventory) return;         // only care about upgrade changes

        int newSlotCount = computeCurrentSlotCount();
        if (newSlotCount == packSlots) return;

        // Save current pack contents into the item before reopening,
        // so nothing is lost during the GUI swap.
        int slot = packSlotIndex;
        if (slot < 0) return;

        ItemStack packStack = serverPlayer.getInventory().getItem(slot);
        if (packStack.isEmpty()) return;

        packInventory.saveToItem(packStack);
        upgradeInventory.saveUpgradesToItem(packStack);
        serverPlayer.getInventory().setChanged();

        // Schedule on the next server tick to avoid reopening mid-slot-change.
        serverPlayer.getServer().execute(() ->
                OpenPackHelper.open(serverPlayer, packStack, tier, slot));
    }

    // Recalculates total item slots based on the current upgrade inventory contents.
    private int computeCurrentSlotCount() {
        GregPacksConfig.UpgradeConfigs cfg = GregPacksConfig.INSTANCE.ModuleValues;
        int total = tier.defaultSlots;
        for (int i = 0; i < maxUpgrades; i++) {
            ItemStack stack = upgradeInventory.getItem(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof UpgradeItem upgradeItem)) continue;
            total += switch (upgradeItem.getUpgradeType()) {
                case ITEM_CAPACITY_I   -> cfg.itemModule1Bonus;
                case ITEM_CAPACITY_II  -> cfg.itemModule2Bonus;
                case ITEM_CAPACITY_III -> cfg.itemModule3Bonus;
                default -> 0;
            };
        }
        return total;
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