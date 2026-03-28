package com.astrogreg.gregpacks.item;

import com.astrogreg.gregpacks.inventory.OpenPackHelper;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The OmniPackItem is the portable version of the OmniPackBlock. It can be used to open the OmniPack GUI
 * from the player's inventory, and can be placed as a block in the world. It shares the same tier and
 * capabilities as the block version.
 */
public class OmniPackItem extends Item {

    private final OmniPackTier tier;

    public OmniPackItem(OmniPackTier tier) {
        super(new Item.Properties().stacksTo(1));
        this.tier = tier;
    }

    public OmniPackTier getTier() {
        return tier;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            ItemStack stack = player.getItemInHand(hand);
            int slotIndex   = OpenPackHelper.findSlot(player, stack);
            OpenPackHelper.open(serverPlayer, stack, tier, slotIndex);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("item.gregpacks.omnipack.tooltip.slots",    tier.defaultSlots));
        tooltipComponents.add(Component.translatable("item.gregpacks.omnipack.tooltip.energy",   tier.defaultEnergyStorage));
        tooltipComponents.add(Component.translatable("item.gregpacks.omnipack.tooltip.upgrades", tier.defaultMaxUpgrades));
        tooltipComponents.add(Component.translatable("item.gregpacks.omnipack.tooltip.fluid",    tier.defaultFluidStorage));
    }
}