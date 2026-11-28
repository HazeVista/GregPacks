package com.astrogreg.gregpacks.inventory;

import com.astrogreg.gregpacks.util.OmniPackTickHandler;
import com.astrogreg.gregpacks.block.OmniPackBlockEntity;
import com.astrogreg.gregpacks.config.GregPacksConfig;
import com.astrogreg.gregpacks.fluid.FluidNBTHelper;
import com.astrogreg.gregpacks.item.OmniPackTier;
import com.astrogreg.gregpacks.item.UpgradeItem;
import com.astrogreg.gregpacks.item.UpgradeType;
import com.astrogreg.gregpacks.registry.GregPacksMenus;
import com.astrogreg.gregpacks.upgrade.UpgradeEffects;
import com.astrogreg.gregpacks.util.JetpackConfigHelper;
import com.astrogreg.gregpacks.util.MagnetConfigHelper;
import com.astrogreg.gregpacks.util.PackTags;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class OmniPackMenu extends AbstractContainerMenu {

    public static final int SLOT_SIZE = 18;
    public static final int PACK_START_X = 8;
    public static final int PACK_START_Y = 22;
    public static final int COLS = 9;
    private final int packSlots;
    private final int maxUpgrades;
    private int packSlotIndex = -1;
    private final OmniPackInventory packInventory;
    private final OmniPackInventory upgradeInventory;
    private final OmniPackTier tier;
    private final ContainerData fluidData = new SimpleContainerData(4);
    private final ContainerData energyData = new SimpleContainerData(4);
    private final ContainerData jetpackData = new SimpleContainerData(2);

    private OmniPackCraftingContainer craftingContainer = null;
    private final ResultContainer craftingResult = new ResultContainer();
    private int craftingGridStart = -1;
    private int craftingResultSlot = -1;
    private HideableResultSlot craftingResultSlotObj = null;
    private boolean craftingInstalled = false;

    private ServerPlayer serverPlayer = null;
    private OmniPackBlockEntity sourceBlockEntity = null;
    private Inventory playerInventory = null;

    private String syncedFluidKey = "";

    public void setSyncedFluidKey(String key) {
        this.syncedFluidKey = key;
    }

    // Server-side constructor
    public OmniPackMenu(int containerId, Inventory playerInventory,
                        OmniPackInventory packInventory, OmniPackInventory upgradeInventory,
                        OmniPackTier tier) {
        super(GregPacksMenus.OMNIPACK_MENU.get(), containerId);
        this.tier = tier;
        this.upgradeInventory = upgradeInventory;
        this.maxUpgrades = upgradeInventory.getContainerSize();
        this.packSlots = packInventory.getContainerSize();
        this.packInventory = packInventory;
        this.playerInventory = playerInventory;
        this.craftingInstalled = new UpgradeEffects(tier, upgradeInventory).hasCrafting;
        addPackSlots();
        addUpgradeSlots();
        addPlayerSlots(playerInventory);
        addCraftingSlots();
        addDataSlots(fluidData);
        addDataSlots(energyData);
        addDataSlots(jetpackData);
        syncFluidData();
    }

    // NOTE: the buffer layout must match what OpenPackHelper write
    // the pack's inventory slot index is the last value written
    public OmniPackMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory,
                new OmniPackInventory(buf.readShort()),
                new OmniPackInventory(buf.readByte()),
                OmniPackTier.values()[buf.readByte()],
                buf.readBoolean());
        this.packSlotIndex = buf.readInt();
    }

    private OmniPackMenu(int containerId, Inventory playerInventory,
                         OmniPackInventory packInventory, OmniPackInventory upgradeInventory,
                         OmniPackTier tier, boolean craftingInstalled) {
        super(GregPacksMenus.OMNIPACK_MENU.get(), containerId);
        this.tier = tier;
        this.upgradeInventory = upgradeInventory;
        this.maxUpgrades = upgradeInventory.getContainerSize();
        this.packSlots = packInventory.getContainerSize();
        this.packInventory = packInventory;
        this.playerInventory = playerInventory;
        this.craftingInstalled = craftingInstalled;
        addPackSlots();
        addUpgradeSlots();
        addPlayerSlots(playerInventory);
        addCraftingSlots();
        addDataSlots(fluidData);
        addDataSlots(energyData);
        addDataSlots(jetpackData);
    }

    private void addPackSlots() {
        for (int i = 0; i < packSlots; i++) {
            int col = i % COLS;
            int row = i / COLS;
            addSlot(new HideableSlot(packInventory, i,
                    PACK_START_X + col * SLOT_SIZE,
                    PACK_START_Y + row * SLOT_SIZE) {

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return !stack.is(PackTags.CURIOS_BACK) && super.mayPlace(stack);
                }
            });
        }
    }

    private void addUpgradeSlots() {
        final OmniPackInventory upgradeInvRef = upgradeInventory;
        upgradeInventory.addListener(c -> onUpgradesChanged());
        for (int i = 0; i < maxUpgrades; i++) {
            final int slotIdx = i;
            addSlot(new HideableSlot(upgradeInventory, i, 0, 0) {

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.getItem() instanceof UpgradeItem && !conflictsWithInstalled(stack, slotIdx);
                }

                @Override
                public boolean mayPickup(Player player) {
                    ItemStack current = upgradeInvRef.getItem(slotIdx);
                    if (current.isEmpty() || !(current.getItem() instanceof UpgradeItem upgradeItem))
                        return true;

                    int thisBonus = itemCapacityBonus(upgradeItem.getUpgradeType());
                    if (thisBonus == 0) return true;
                    int slotsWithout = tier.defaultSlots;
                    for (int j = 0; j < maxUpgrades; j++) {
                        if (j == slotIdx) continue;
                        ItemStack other = upgradeInvRef.getItem(j);
                        if (other.isEmpty() || !(other.getItem() instanceof UpgradeItem otherUpgrade))
                            continue;
                        slotsWithout += itemCapacityBonus(otherUpgrade.getUpgradeType());
                    }

                    for (int j = slotsWithout; j < packSlots; j++) {
                        if (!OmniPackMenu.this.packInventory.getItem(j).isEmpty())
                            return false;
                    }
                    return true;
                }
            });
        }
    }

    /**
     * True if the stack is an upgrade whose category is exclusive and another installed module
     * (ignoring ignoreSlot) already belongs to that category.
     */
    private boolean conflictsWithInstalled(ItemStack stack, int ignoreSlot) {
        if (!(stack.getItem() instanceof UpgradeItem incomingItem)) return false;
        var category = incomingItem.getUpgradeType().category();
        if (!category.isExclusive()) return false;
        for (int j = 0; j < maxUpgrades; j++) {
            if (j == ignoreSlot) continue;
            ItemStack other = upgradeInventory.getItem(j);
            if (!other.isEmpty() && other.getItem() instanceof UpgradeItem otherUpgrade &&
                    otherUpgrade.getUpgradeType().category() == category) {
                return true;
            }
        }
        return false;
    }

    // Action bar message (pass false instead of true to print in chat)
    private void notifyModuleLimit(Player player) {
        player.displayClientMessage(Component.translatable("message.gregpacks.module_limit"), true);
    }

    private void addCraftingSlots() {
        if (!craftingInstalled) return;

        craftingContainer = new OmniPackCraftingContainer(ItemStack.EMPTY);
        craftingGridStart = slots.size();

        for (int i = 0; i < 9; i++) {
            addSlot(new HideableSlot(craftingContainer, i, 0, 0) {

                @Override
                public void setChanged() {
                    super.setChanged();
                    updateCraftingResult();
                }
            });
        }

        craftingResultSlot = slots.size();
        HideableResultSlot resultSlotObj = new HideableResultSlot(
                playerInventory.player,
                craftingContainer,
                craftingResult,
                0, 0, 0) {

            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);
                craftingContainer.save();
                if (sourceBlockEntity != null) sourceBlockEntity.setChanged();
                updateCraftingResult();
            }
        };
        craftingResultSlotObj = resultSlotObj;
        addSlot(resultSlotObj);
    }

    private void updateCraftingResult() {
        if (craftingContainer == null || serverPlayer == null) return;
        var level = serverPlayer.level();
        var optional = level.getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, craftingContainer, level);
        if (optional.isPresent()) {
            CraftingRecipe recipe = optional.get();
            craftingResult.setItem(0, recipe.assemble(craftingContainer, level.registryAccess()));
        } else {
            craftingResult.setItem(0, ItemStack.EMPTY);
        }
        broadcastChanges();
    }

    private static int itemCapacityBonus(UpgradeType type) {
        GregPacksConfig.UpgradeConfigs cfg = GregPacksConfig.INSTANCE.ModuleValues;
        return switch (type) {
            case ITEM_CAPACITY_I -> cfg.itemModule1Bonus;
            case ITEM_CAPACITY_II -> cfg.itemModule2Bonus;
            case ITEM_CAPACITY_III -> cfg.itemModule3Bonus;
            default -> 0;
        };
    }

    private void onUpgradesChanged() {
        if (serverPlayer == null) return;

        UpgradeEffects effects = new UpgradeEffects(tier, upgradeInventory);
        int fluidCap = Math.max(1000, effects.totalFluidStorage);
        long euCap = effects.totalEnergyStorage;

        if (sourceBlockEntity != null) {
            FluidStack fluid = sourceBlockEntity.getFluid();
            if (!fluid.isEmpty() && fluid.getAmount() > fluidCap) {
                FluidStack clamped = fluid.copy();
                clamped.setAmount(fluidCap);
                sourceBlockEntity.setFluid(clamped);
            }
            if (sourceBlockEntity.getStoredEU() > euCap) {
                sourceBlockEntity.setStoredEUDirect(euCap);
            }
        } else {
            ItemStack packStack = getActiveStack();
            if (!packStack.isEmpty()) {
                FluidStack fluid = FluidNBTHelper.getFluid(packStack);
                if (!fluid.isEmpty() && fluid.getAmount() > fluidCap) {
                    FluidStack clamped = fluid.copy();
                    clamped.setAmount(fluidCap);
                    FluidNBTHelper.setFluid(packStack, clamped);
                }
                long storedEU = OmniPackTickHandler.getStoredEU(packStack);
                if (storedEU > euCap) {
                    OmniPackTickHandler.setStoredEU(packStack, storedEU, euCap);
                }
                upgradeInventory.saveUpgradesToItem(packStack);
                serverPlayer.getInventory().setChanged();

                if ((effects.totalSlots != packSlots || effects.hasCrafting != craftingInstalled)
                        && packSlotIndex >= 0) {
                    packInventory.saveToItem(packStack);
                    serverPlayer.getServer().execute(() ->
                            OpenPackHelper.open(serverPlayer, packStack, tier, packSlotIndex));
                    return;
                }
            }
        }

        broadcastChanges();
    }


    // Creates a player-inventory slot that is locked while it holds the pack that this menu was opened from.
    private HideableSlot playerSlot(Inventory inv, int invIndex, int x, int y) {
        return new HideableSlot(inv, invIndex, x, y) {

            private boolean isLockedPack() {
                return invIndex == OmniPackMenu.this.packSlotIndex;
            }

            @Override
            public boolean mayPickup(Player player) {
                return !isLockedPack() && super.mayPickup(player);
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return !isLockedPack() && super.mayPlace(stack);
            }
        };
    }

    private void addPlayerSlots(Inventory playerInventory) {
        int packRows = (int) Math.ceil(packSlots / (double) COLS);
        int playerInvY = PACK_START_Y + packRows * SLOT_SIZE + 8;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(playerSlot(playerInventory,
                        col + row * 9 + 9,
                        PACK_START_X + col * SLOT_SIZE,
                        playerInvY + row * SLOT_SIZE));
            }
        }
        int hotbarY = playerInvY + 3 * SLOT_SIZE + 4;
        for (int col = 0; col < 9; col++) {
            addSlot(playerSlot(playerInventory, col,
                    PACK_START_X + col * SLOT_SIZE, hotbarY));
        }
    }

    /**
     * Number keys / the offhand key swap the hovered slot with a hotbar slot. The slot-level checks
     * only look at the hovered slot, so block swaps that would move the open pack out of its hotbar
     * (or offhand) slot.
     */
    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (clickType == ClickType.SWAP && packSlotIndex >= 0 && button == packSlotIndex) {
            return;
        }

        // tell the player why a module was refused (server side only)
        if (!player.level().isClientSide && slotId >= packSlots && slotId < packSlots + maxUpgrades) {
            ItemStack incoming = ItemStack.EMPTY;
            if (clickType == ClickType.PICKUP) {
                incoming = getCarried();
            } else if (clickType == ClickType.SWAP && ((button >= 0 && button < 9) || button == 40)) {
                incoming = player.getInventory().getItem(button);
            }
            if (conflictsWithInstalled(incoming, slotId - packSlots)) notifyModuleLimit(player);
        }

        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack slotStack = slot.getItem();
        ItemStack result = slotStack.copy();

        final int packStart = 0, packEnd = packSlots;
        final int upgradeStart = packSlots, upgradeEnd = packSlots + maxUpgrades;
        final int playerStart = upgradeEnd, playerEnd = upgradeEnd + 36;
        final boolean hasCraft = craftingGridStart >= 0;
        final int gridStart = craftingGridStart, gridEnd = hasCraft ? craftingGridStart + 9 : -1;

        if (hasCraft && index == craftingResultSlot) {
            if (!moveItemStackTo(slotStack, packStart, packEnd, false) &&
                    !moveItemStackTo(slotStack, playerStart, playerEnd, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(slotStack, result);
        } else if (index >= packStart && index < packEnd) {
            if (!moveItemStackTo(slotStack, playerStart, playerEnd, true)) return ItemStack.EMPTY;
        } else if (index >= upgradeStart && index < upgradeEnd) {
            if (!moveItemStackTo(slotStack, playerStart, playerEnd, true)) return ItemStack.EMPTY;
        } else if (hasCraft && index >= gridStart && index < gridEnd) {
            if (!moveItemStackTo(slotStack, packStart, packEnd, false) &&
                    !moveItemStackTo(slotStack, playerStart, playerEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (slotStack.getItem() instanceof UpgradeItem) {
                if (!moveItemStackTo(slotStack, upgradeStart, upgradeEnd, false)) {
                    if (!player.level().isClientSide && conflictsWithInstalled(slotStack, -1))
                        notifyModuleLimit(player);
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(slotStack, packStart, packEnd, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (slotStack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        if (slotStack.getCount() == result.getCount()) return ItemStack.EMPTY;

        slot.onTake(player, slotStack);

        if (hasCraft && index == craftingResultSlot && !slotStack.isEmpty()) {
            player.drop(slotStack, false);
        }
        return result;
    }

    private String lastSentFluidKey = null;

    private void syncFluidData() {
        if (serverPlayer == null) return;

        FluidStack fluid;
        if (sourceBlockEntity != null) {
            fluid = sourceBlockEntity.getFluid();
        } else {
            ItemStack packStack = getActiveStack();
            if (packStack.isEmpty()) return;
            fluid = FluidNBTHelper.getFluid(packStack);
        }

        int amount = fluid.getAmount();
        int capacity = getTankCapacity();

        fluidData.set(0, (amount >> 16) & 0xFFFF);
        fluidData.set(1, amount & 0xFFFF);
        fluidData.set(2, (capacity >> 16) & 0xFFFF);
        fluidData.set(3, capacity & 0xFFFF);

        String key = fluid.isEmpty() ? "" :
                net.minecraftforge.registries.ForgeRegistries.FLUIDS
                        .getKey(fluid.getFluid()).toString();
        if (!key.equals(lastSentFluidKey)) {
            lastSentFluidKey = key;
            com.astrogreg.gregpacks.network.GregPacksNetwork.CHANNEL.send(
                    net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new com.astrogreg.gregpacks.network.SPacketFluidSync(key));
        }
    }

    @Nullable
    public IFluidHandler getFluidHandler() {
        if (sourceBlockEntity != null) {
            return sourceBlockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
        }
        ItemStack stack = getActiveStack();
        return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
    }

    private void syncEnergyData() {
        if (serverPlayer == null) return;

        long storedEU;
        long maxEU;

        if (sourceBlockEntity != null) {
            storedEU = sourceBlockEntity.getStoredEU();
            maxEU = new UpgradeEffects(tier, sourceBlockEntity.getUpgradeInventory()).totalEnergyStorage;
        } else {
            ItemStack packStack = getActiveStack();
            if (packStack.isEmpty()) return;
            storedEU = OmniPackTickHandler.getStoredEU(packStack);
            maxEU = new UpgradeEffects(tier, upgradeInventory).totalEnergyStorage;
        }

        int euScaled = (int) Math.min(storedEU / 100, Integer.MAX_VALUE);
        int maxScaled = (int) Math.min(maxEU / 100, Integer.MAX_VALUE);

        energyData.set(0, (euScaled >> 16) & 0xFFFF);
        energyData.set(1, euScaled & 0xFFFF);
        energyData.set(2, (maxScaled >> 16) & 0xFFFF);
        energyData.set(3, maxScaled & 0xFFFF);
    }

    public ItemStack getActiveStack() {
        if (sourceStack != null && !sourceStack.isEmpty()) return sourceStack;
        if (packSlotIndex >= 0) return serverPlayer.getInventory().getItem(packSlotIndex);
        return ItemStack.EMPTY;
    }

    public int getTankCapacity() {
        UpgradeEffects effects = new UpgradeEffects(tier, upgradeInventory);
        return effects.totalFluidStorage;
    }

    public int getJetpackSpeed() {
        return jetpackData.get(0);
    }

    public int getJetpackThrust() {
        return jetpackData.get(1);
    }

    public int getSyncedFluidAmount() {
        return (fluidData.get(0) << 16) | (fluidData.get(1) & 0xFFFF);
    }

    public int getSyncedFluidCapacity() {
        return (fluidData.get(2) << 16) | (fluidData.get(3) & 0xFFFF);
    }

    public long getSyncedEU() {
        return ((long) ((energyData.get(0) << 16) | (energyData.get(1) & 0xFFFF))) * 100L;
    }

    public long getSyncedMaxEU() {
        return ((long) ((energyData.get(2) << 16) | (energyData.get(3) & 0xFFFF))) * 100L;
    }

    public Fluid getSyncedFluid() {
        if (syncedFluidKey.isEmpty()) return null;
        return ForgeRegistries.FLUIDS.getValue(new ResourceLocation(syncedFluidKey));
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public ItemStack getMagnetModule() {
        return MagnetConfigHelper.findModule(upgradeInventory);
    }

    public boolean hasMagnet2() {
        return !getMagnetModule().isEmpty();
    }

    public boolean hasJetpack2() {
        for (int i = 0; i < maxUpgrades; i++) {
            ItemStack s = upgradeInventory.getItem(i);
            if (!s.isEmpty() && s.getItem() instanceof UpgradeItem u &&
                    u.getUpgradeType() == UpgradeType.JETPACK_II)
                return true;
        }
        return false;
    }

    @Override
    public void broadcastChanges() {
        syncFluidData();
        syncEnergyData();
        super.broadcastChanges();

        ItemStack pack = getActiveStack();
        jetpackData.set(0, pack.isEmpty() ? 3 : JetpackConfigHelper.getSpeed(pack));
        jetpackData.set(1, pack.isEmpty() ? 3 : JetpackConfigHelper.getThrust(pack));
    }

    public void setServerPlayer(ServerPlayer player) {
        this.serverPlayer = player;
    }

    private ItemStack sourceStack = null;

    public void setSourceStack(ItemStack stack) {
        this.sourceStack = stack;
        if (craftingContainer != null) craftingContainer.rebind(stack);
    }

    public void setSourceBlockEntity(OmniPackBlockEntity be) {
        this.sourceBlockEntity = be;
        if (craftingContainer != null && be != null) {
            craftingContainer.rebind(ItemStack.EMPTY);
        }
    }

    public OmniPackBlockEntity getSourceBlockEntity() {
        return sourceBlockEntity;
    }

    public OmniPackInventory getPackInventory() {
        return packInventory;
    }

    public OmniPackInventory getUpgradeInventory() {
        return upgradeInventory;
    }

    public OmniPackTier getTier() {
        return tier;
    }

    public int getPackSlots() {
        return packSlots;
    }

    public int getMaxUpgrades() {
        return maxUpgrades;
    }

    public int getPackSlotIndex() {
        return packSlotIndex;
    }

    public void setPackSlotIndex(int index) {
        this.packSlotIndex = index;
    }

    public HideableSlot getHideableSlot(int index) {
        Slot s = slots.get(index);
        if (!(s instanceof HideableSlot hs)) {
            throw new IllegalArgumentException(
                    "Slot " + index + " is not a HideableSlot it's a " + s.getClass().getSimpleName() +
                            ". Check that renderbg skips crafting slots (indices " + craftingGridStart + ".." +
                            (craftingGridStart >= 0 ? craftingGridStart + 9 : "?") + ").");
        }
        return hs;
    }

    public Container getContainer() {
        return this.packInventory;
    }

    public boolean hasCrafting() {
        return craftingGridStart >= 0;
    }

    public int getCraftingGridStart() {
        return craftingGridStart;
    }

    public HideableResultSlot getCraftingResultSlotObj() {
        return craftingResultSlotObj;
    }

    public OmniPackCraftingContainer getCraftingContainer() {
        return craftingContainer;
    }
}