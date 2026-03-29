package com.astrogreg.gregpacks;

import com.astrogreg.gregpacks.inventory.OmniPackInventory;
import com.astrogreg.gregpacks.inventory.OmniPackMenu;
import com.astrogreg.gregpacks.item.OmniPackItem;
import com.astrogreg.gregpacks.item.OmniPackTier;
import com.astrogreg.gregpacks.registry.GregPacksBlocks;
import com.astrogreg.gregpacks.registry.OmniPackBlockItem;
import com.astrogreg.gregpacks.upgrade.UpgradeEffects;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * This class handles passive effects of the OmniPack and its upgrades, as well as saving inventory changes when the GUI is closed.
 * It listens for player ticks to apply effects like magnetism and auto-feeding, and for container close events to save inventory data back to the item.
 */
@Mod.EventBusSubscriber(modid = GregPacks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OmniPackTickHandler {

    private static final int TICK_INTERVAL = 10;

    // Central save on GUI close
    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event) {
        if (!(event.getContainer() instanceof OmniPackMenu menu)) return;

        Player player    = event.getEntity();
        int packSlot     = menu.getPackSlotIndex();
        if (packSlot < 0) return;

        ItemStack packStack = player.getInventory().getItem(packSlot);
        if (packStack.isEmpty()) return;

        menu.getPackInventory().saveToItem(packStack);
        menu.getUpgradeInventory().saveUpgradesToItem(packStack);
        player.getInventory().setChanged();
    }

    // Passive upgrade tick
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        Level level   = player.level();

        if (level.isClientSide) return;
        if (player.tickCount % TICK_INTERVAL != 0) return;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            OmniPackTier tier = getTier(stack);
            if (tier == null) continue;

            OmniPackInventory upgInv = OmniPackInventory.fromUpgradeItem(stack, tier.defaultMaxUpgrades);
            UpgradeEffects effects   = new UpgradeEffects(tier, upgInv);

            if (effects.magnetRadius > 0) applyMagnet(player, level, effects.magnetRadius);
            if (effects.hasFeeding)       applyFeeding(player, stack, tier, effects);

            break;
        }
    }

    // Helpers
    private static OmniPackTier getTier(ItemStack stack) {
        if (stack.getItem() instanceof OmniPackItem packItem) return packItem.getTier();
        if (stack.getItem() instanceof OmniPackBlockItem blockItem) return blockItem.getBlock().getTier();
        return null;
    }

    private static void applyMagnet(Player player, Level level, int radius) {
        AABB area = player.getBoundingBox().inflate(radius);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);
        for (ItemEntity itemEntity : items) {
            if (itemEntity.hasPickUpDelay()) continue;
            if (getTier(itemEntity.getItem()) != null) continue; // don't attract packs
            itemEntity.playerTouch(player);
        }
    }

    private static void applyFeeding(Player player, ItemStack packStack,
                                     OmniPackTier tier, UpgradeEffects effects) {
        if (player.getFoodData().getFoodLevel() >= 20) return;

        // Use totalSlots so food in upgrade-added slots is also checked
        OmniPackInventory packInv = OmniPackInventory.fromItem(packStack, effects.totalSlots);

        for (int i = 0; i < packInv.getContainerSize(); i++) {
            ItemStack food = packInv.getItem(i);
            if (food.isEmpty()) continue;
            FoodProperties props = food.getItem().getFoodProperties(food, player);
            if (props == null) continue;

            player.getFoodData().eat(food.getItem(), food);
            food.shrink(1);
            if (food.isEmpty()) packInv.setItem(i, ItemStack.EMPTY);

            packInv.saveToItem(packStack);
            player.getInventory().setChanged();
            break;
        }
    }
}