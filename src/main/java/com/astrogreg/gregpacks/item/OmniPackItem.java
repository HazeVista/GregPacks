package com.astrogreg.gregpacks.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import com.astrogreg.gregpacks.util.OmniPackTickHandler;
import com.astrogreg.gregpacks.inventory.OmniPackInventory;
import com.astrogreg.gregpacks.inventory.OpenPackHelper;
import com.astrogreg.gregpacks.upgrade.UpgradeEffects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class OmniPackItem extends Item implements ICurioItem, Equipable {

    private final OmniPackTier tier;
    private ItemStack sourceStack = null;

    public OmniPackItem(OmniPackTier tier) {
        super(new Item.Properties().stacksTo(1));
        this.tier = tier;
    }

    public OmniPackTier getTier() {
        return tier;
    }

    public ItemStack getSourceStack() {
        return sourceStack;
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return slotContext.identifier().equals("back");
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.CHEST;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level, @NotNull Player player,
            @NotNull InteractionHand hand) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            ItemStack stack = player.getItemInHand(hand);
            int slotIndex = OpenPackHelper.findSlot(player, stack);
            OpenPackHelper.open(serverPlayer, stack, tier, slotIndex);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {

        // read the upgrades saved on the stack and show the real (upgrade-adjusted) values
        OmniPackInventory upgradeInv = OmniPackInventory.fromUpgradeItem(stack, tier.defaultMaxUpgrades);
        UpgradeEffects effects = new UpgradeEffects(tier, upgradeInv);

        int installed = 0;
        for (int i = 0; i < upgradeInv.getContainerSize(); i++) {
            if (!upgradeInv.getItem(i).isEmpty()) installed++;
        }

        tooltipComponents.add(Component.translatable("item.gregpacks.omnipack.tooltip.slots", effects.totalSlots));
        tooltipComponents
                .add(Component.translatable("item.gregpacks.omnipack.tooltip.fluid", effects.totalFluidStorage));
        tooltipComponents
                .add(Component.translatable("item.gregpacks.omnipack.tooltip.energy", effects.totalEnergyStorage));
        tooltipComponents
                .add(Component.translatable("item.gregpacks.omnipack.tooltip.upgrades", tier.defaultMaxUpgrades));

        long storedEU = OmniPackTickHandler.getStoredEU(stack);
        if (storedEU > 0) {
            tooltipComponents.add(Component.literal("§aEU: " + storedEU + " / " + effects.totalEnergyStorage));
        }

        if (installed > 0) {
            tooltipComponents.add(Component.literal(
                    "§bInstalled upgrades (" + installed + "/" + tier.defaultMaxUpgrades + "):"));
            for (int i = 0; i < upgradeInv.getContainerSize(); i++) {
                ItemStack upgrade = upgradeInv.getItem(i);
                if (!upgrade.isEmpty()) {
                    tooltipComponents.add(Component.literal("§7  - ").append(upgrade.getHoverName()));
                }
            }
        }

        tooltipComponents.add(Component.literal("§7Shift-click to equip as chestplate"));
    }
}