package com.astrogreg.gregpacks;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import com.astrogreg.gregpacks.block.OmniPackBlockEntity;
import com.astrogreg.gregpacks.config.GregPacksConfig;
import com.astrogreg.gregpacks.inventory.OmniPackInventory;
import com.astrogreg.gregpacks.inventory.OmniPackMenu;
import com.astrogreg.gregpacks.item.OmniPackItem;
import com.astrogreg.gregpacks.item.OmniPackTier;
import com.astrogreg.gregpacks.network.CPacketKeyState;
import com.astrogreg.gregpacks.network.GregPacksNetwork;
import com.astrogreg.gregpacks.network.SPacketJetpackThrust;
import com.astrogreg.gregpacks.registry.OmniPackBlockItem;
import com.astrogreg.gregpacks.upgrade.UpgradeEffects;

import java.util.List;

@Mod.EventBusSubscriber(modid = GregPacks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OmniPackTickHandler {

    private static final int TICK_INTERVAL = 10;
    private static final String TAG_EU = "StoredEU";

    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event) {
        if (!(event.getContainer() instanceof OmniPackMenu menu)) return;
        Player player = event.getEntity();

        OmniPackBlockEntity be = menu.getSourceBlockEntity();
        if (be != null) {
            be.setChanged();
            return;
        }

        ItemStack packStack = menu.getActiveStack();
        if (packStack == null || packStack.isEmpty()) return;

        menu.getPackInventory().saveToItem(packStack);
        menu.getUpgradeInventory().saveUpgradesToItem(packStack);
        player.getInventory().setChanged();

        int packSlot = menu.getPackSlotIndex();
        if (packSlot >= 0) {} else if (net.minecraftforge.fml.ModList.get().isLoaded("curios")) {
            top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player).ifPresent(inv -> {
                var slots = inv.getCurios();
                if (slots.containsKey("back")) {
                    slots.get("back").getStacks().setStackInSlot(0, packStack);
                }
            });
        }
    }

    // Player tick
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        Level level = player.level();
        if (level.isClientSide) return;

        ItemStack activeStack = findPackInCurios(player);
        boolean canJetpack = activeStack != null && !activeStack.isEmpty();

        if (activeStack == null || activeStack.isEmpty()) {
            activeStack = findPackInInventory(player);
            canJetpack = false;
        }

        if (activeStack == null || activeStack.isEmpty()) return;

        OmniPackTier tier = getTier(activeStack);
        if (tier == null) return;

        OmniPackInventory upgInv = OmniPackInventory.fromUpgradeItem(activeStack, tier.defaultMaxUpgrades);
        UpgradeEffects effects = new UpgradeEffects(tier, upgInv);

        // Magnet — works from anywhere
        if (effects.magnetRadius > 0) applyMagnet(player, level, effects.magnetRadius);

        // Feeding — throttled, works from anywhere
        if (player.tickCount % TICK_INTERVAL == 0) {
            if (effects.hasFeeding) applyFeeding(player, activeStack, effects);
        }

        // Jetpack — only when equipped in curios slot
        if (canJetpack) applyJetpack(player, activeStack, effects);
    }

    // Pack finders
    private static ItemStack findPackInInventory(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (getTier(stack) != null) return stack;
        }
        return null;
    }

    private static ItemStack findPackInCurios(Player player) {
        if (!net.minecraftforge.fml.ModList.get().isLoaded("curios")) return null;
        return top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player)
                .map(inv -> {
                    var slots = inv.getCurios();
                    if (!slots.containsKey("back")) return ItemStack.EMPTY;
                    var handler = slots.get("back").getStacks();
                    for (int i = 0; i < handler.getSlots(); i++) {
                        ItemStack s = handler.getStackInSlot(i);
                        if (getTier(s) != null) return s;
                    }
                    return ItemStack.EMPTY;
                })
                .orElse(ItemStack.EMPTY);
    }

    // Magnet
    private static void applyMagnet(Player player, Level level, int radius) {
        AABB area = player.getBoundingBox().inflate(radius);
        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);
        for (ItemEntity entity : items) {
            if (entity.hasPickUpDelay()) continue;
            if (getTier(entity.getItem()) != null) continue; // don't attract other packs
            entity.playerTouch(player);
        }
    }

    // Feeding
    private static void applyFeeding(Player player, ItemStack packStack, UpgradeEffects effects) {
        if (player.getFoodData().getFoodLevel() >= 20) return;

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

    // Jetpack
    // Tier I: gentle lift (+0.12/tick, cap 0.30), low EU cost.
    // Tier II: strong thrust (+0.20/tick, cap 0.50), higher EU cost.
    private static void applyJetpack(Player player, ItemStack packStack, UpgradeEffects effects) {
        if (!effects.hasJetpack1 && !effects.hasJetpack2) return;

        boolean jumping = CPacketKeyState.isJumping(player);
        boolean sneaking = CPacketKeyState.isSneaking(player);
        boolean inAir = !player.onGround();

        if (!inAir && !jumping) return;

        GregPacksConfig.UpgradeConfigs cfg = GregPacksConfig.INSTANCE.ModuleValues;
        long baseCost = effects.hasJetpack2 ? cfg.jetpackModule2EUCost : cfg.jetpackModule1EUCost;
        double thrustY = effects.hasJetpack2 ? 0.35 : 0.20;
        double capY = effects.hasJetpack2 ? 1.00 : 0.50;
        float speedH = effects.hasJetpack2 ? 0.35f : 0.14f;

        long storedEU = getStoredEU(packStack);

        Vec3 vel = player.getDeltaMovement();
        double newY;

        if (jumping && !sneaking) {
            // Subir
            if (storedEU < baseCost) return;
            double accel = thrustY * (vel.y < 0.3 ? 2.5 : 1.0);
            newY = Math.min(vel.y + accel, capY);
            setStoredEU(packStack, storedEU - baseCost, effects.totalEnergyStorage);
        } else if (sneaking && !jumping) {
            // Bajar — sin costo de EU
            newY = Math.max(vel.y - 0.08, -0.3);
        } else {
            // Hover — cancelar gravedad, costo mínimo
            long hoverCost = Math.max(1, baseCost / 4);
            if (storedEU < hoverCost) return;
            newY = 0; // cancelar gravedad completamente
            setStoredEU(packStack, storedEU - hoverCost, effects.totalEnergyStorage);
        }

        player.fallDistance = 0f;
        player.setDeltaMovement(vel.x, newY, vel.z);

        // Horizontal
        if (player.isSprinting()) speedH *= 1.3f;
        Vec3 movement = Vec3.ZERO;
        if (CPacketKeyState.isForward(player)) movement = movement.add(0, 0, speedH);
        if (CPacketKeyState.isBackward(player)) movement = movement.add(0, 0, -speedH * 0.8f);
        if (CPacketKeyState.isLeft(player)) movement = movement.add(speedH, 0, 0);
        if (CPacketKeyState.isRight(player)) movement = movement.add(-speedH, 0, 0);

        double dist = movement.length();
        if (dist >= 1.0E-7) player.moveRelative((float) dist, movement);

        if (player instanceof ServerPlayer sp) {
            GregPacksNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> sp),
                    new SPacketJetpackThrust(player.getDeltaMovement().x, newY, player.getDeltaMovement().z));
        }
    }

    // EU NBT helpers
    public static long getStoredEU(ItemStack stack) {
        if (!stack.hasTag()) return 0L;
        return stack.getTag().contains(TAG_EU) ? stack.getTag().getLong(TAG_EU) : 0L;
    }

    public static void setStoredEU(ItemStack stack, long eu, long max) {
        stack.getOrCreateTag().putLong(TAG_EU, Math.max(0, Math.min(eu, max)));
    }

    // Tier helper
    private static OmniPackTier getTier(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        if (stack.getItem() instanceof OmniPackItem item) return item.getTier();
        if (stack.getItem() instanceof OmniPackBlockItem item) return item.getBlock().getTier();
        return null;
    }
}
