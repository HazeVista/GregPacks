package com.astrogreg.gregpacks.inventory;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;

import com.astrogreg.gregpacks.item.OmniPackTier;
import com.astrogreg.gregpacks.upgrade.UpgradeEffects;
import org.jetbrains.annotations.NotNull;

/**
 * helper for opening the OmniPack GUI from any context
 * buffer layout sent to client (must match OmniPackMenu client constructor):
 * [0] short   - real pack slot count (base + upgrade bonuses)
 * [1] byte    - upgrade slot count (= tier.defaultMaxUpgrades)
 * [2] byte    - tier ordinal
 * [3] boolean - crafting module installed
 * [4] int     - inventory slot index of the open pack
 */
public class OpenPackHelper {

    public static void open(ServerPlayer player, ItemStack stack, OmniPackTier tier, int slotIndex) {
        OmniPackInventory upgradeInv = OmniPackInventory.fromUpgradeItem(stack, tier.defaultMaxUpgrades);
        UpgradeEffects effects = new UpgradeEffects(tier, upgradeInv);

        // Load pack inventory with the real slot count (base + upgrade bonuses)
        OmniPackInventory inv = OmniPackInventory.fromItem(stack, effects.totalSlots);

        NetworkHooks.openScreen(player, new MenuProvider() {

            @Override
            public @NotNull Component getDisplayName() {
                return Component.translatable("container.gregpacks.omnipack");
            }

            @Override
            public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInv, @NotNull Player p) {
                OmniPackMenu menu = new OmniPackMenu(windowId, playerInv, inv, upgradeInv, tier);
                menu.setPackSlotIndex(slotIndex);
                menu.setSourceStack(stack);
                menu.setServerPlayer((ServerPlayer) p);
                return menu;
            }
        }, buf -> {
            buf.writeShort(effects.totalSlots);     // real pack slot count
            buf.writeByte(tier.defaultMaxUpgrades); // upgrade slot count
            buf.writeByte(tier.ordinal());          // tier ordinal
            buf.writeBoolean(effects.hasCrafting);  // crafting module installed
            buf.writeInt(slotIndex);                // pack's inventory slot (lets the client lock it too)
        });
    }

    public static int findSlot(Player player, ItemStack stack) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i) == stack) return i;
        }
        return -1;
    }
}