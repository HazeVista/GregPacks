package com.astrogreg.gregpacks.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.annotation.Nullable;

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
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("item.gregpacks.omnipack.tooltip.slots",
                tier.defaultSlots));
        tooltipComponents.add(Component.translatable("item.gregpacks.omnipack.tooltip.fluid",
                tier.defaultFluidStorage));
        tooltipComponents.add(Component.translatable("item.gregpacks.omnipack.tooltip.energy",
                tier.defaultEnergyStorage));
        tooltipComponents.add(Component.translatable("item.gregpacks.omnipack.tooltip.upgrades",
                tier.defaultMaxUpgrades));
    }
}
