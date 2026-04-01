package com.astrogreg.gregpacks.registry;

import com.astrogreg.gregpacks.block.OmniPackBlock;
import com.astrogreg.gregpacks.block.OmniPackBlockEntity;
import com.astrogreg.gregpacks.inventory.OpenPackHelper;
import com.astrogreg.gregpacks.item.OmniPackTier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OmniPackBlockItem extends BlockItem {

    public OmniPackBlockItem(OmniPackBlock block, Properties properties) {
        super(block, properties);
    }

    @Override
    public OmniPackBlock getBlock() {
        return (OmniPackBlock) super.getBlock();
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        if (!context.getPlayer().isShiftKeyDown()) return InteractionResult.PASS;
        return super.useOn(context);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (!player.isShiftKeyDown()) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                ItemStack stack   = player.getItemInHand(hand);
                OmniPackTier tier = getBlock().getTier();
                int slotIndex     = OpenPackHelper.findSlot(player, stack);
                OpenPackHelper.open(serverPlayer, stack, tier, slotIndex);
            }
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level,
                                                 @Nullable Player player, ItemStack stack, BlockState state) {
        boolean result = super.updateCustomBlockEntityTag(pos, level, player, stack, state);
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof OmniPackBlockEntity be) {
            be.loadFromItemStack(stack);
        }
        return result;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        var tier = getBlock().getTier();
        tooltip.add(Component.translatable("item.gregpacks.omnipack.tooltip.slots",    tier.defaultSlots));
        tooltip.add(Component.translatable("item.gregpacks.omnipack.tooltip.fluid",    tier.defaultFluidStorage));
        tooltip.add(Component.translatable("item.gregpacks.omnipack.tooltip.energy",   tier.defaultEnergyStorage));
        tooltip.add(Component.translatable("item.gregpacks.omnipack.tooltip.upgrades", tier.defaultMaxUpgrades));
        tooltip.add(Component.translatable("item.gregpacks.omnipack.tooltip.place"));
    }
}