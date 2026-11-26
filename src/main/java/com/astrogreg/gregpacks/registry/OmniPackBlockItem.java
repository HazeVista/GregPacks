package com.astrogreg.gregpacks.registry;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.loading.FMLEnvironment;

import com.astrogreg.gregpacks.OmniPackTickHandler;
import com.astrogreg.gregpacks.block.OmniPackBlock;
import com.astrogreg.gregpacks.block.OmniPackBlockEntity;
import com.astrogreg.gregpacks.fluid.FluidNBTHelper;
import com.astrogreg.gregpacks.inventory.OmniPackInventory;
import com.astrogreg.gregpacks.inventory.OpenPackHelper;
import com.astrogreg.gregpacks.item.OmniPackTier;
import com.astrogreg.gregpacks.upgrade.UpgradeEffects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class OmniPackBlockItem extends BlockItem implements ICurioItem {

    public OmniPackBlockItem(OmniPackBlock block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return slotContext.identifier().equals("back");
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
            @NotNull Level level, @NotNull Player player,
            @NotNull InteractionHand hand) {
        if (!player.isShiftKeyDown()) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                ItemStack stack = player.getItemInHand(hand);
                OmniPackTier tier = getBlock().getTier();
                int slotIndex = OpenPackHelper.findSlot(player, stack);
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

        // read the upgrades saved on the stack so the values reflect installed value
        OmniPackInventory upgradeInv = OmniPackInventory.fromUpgradeItem(stack, tier.defaultMaxUpgrades);
        UpgradeEffects effects = new UpgradeEffects(tier, upgradeInv);

        int installed = 0;
        for (int i = 0; i < upgradeInv.getContainerSize(); i++) {
            if (!upgradeInv.getItem(i).isEmpty()) installed++;
        }

        tooltip.add(Component.translatable("item.gregpacks.omnipack.tooltip.slots", effects.totalSlots));
        tooltip.add(Component.translatable("item.gregpacks.omnipack.tooltip.upgrades", tier.defaultMaxUpgrades));

        // Fluid: [name] amount / capacity mB, with "(empty)" after the mB only when empty
        FluidStack fluid = FluidNBTHelper.getFluid(stack);
        String fluidAmounts = fmt(fluid.getAmount()) + " / " + fmt(effects.totalFluidStorage) + " mB";
        MutableComponent fluidLine = Component.literal("§bFluid: ");
        if (!fluid.isEmpty()) {
            fluidLine.append(fluid.getDisplayName()).append(Component.literal(" "));
        }
        fluidLine.append(Component.literal("§f" + fluidAmounts));
        tooltip.add(fluidLine);

        // Energy: stored / real maximum
        long storedEU = OmniPackTickHandler.getStoredEU(stack);
        tooltip.add(Component.literal(
                "§aEnergy: §f" + fmt(storedEU) + " / " + fmt(effects.totalEnergyStorage) + " EU"));

        // Upgrade list (and the blank line above it) only while Shift is held
        if (installed > 0 && shiftHeld()) {
            tooltip.add(Component.empty());
            tooltip.add(Component.literal(
                    "§bInstalled upgrades (" + installed + "/" + tier.defaultMaxUpgrades + ")"));
            for (int i = 0; i < upgradeInv.getContainerSize(); i++) {
                ItemStack upgrade = upgradeInv.getItem(i);
                if (!upgrade.isEmpty()) {
                    tooltip.add(Component.literal("  - ").withStyle(ChatFormatting.WHITE)
                            .append(upgrade.getHoverName().copy().withStyle(ChatFormatting.GRAY)));
                }
            }
        }

        tooltip.add(Component.translatable("item.gregpacks.omnipack.tooltip.place.0"));
        tooltip.add(Component.translatable("item.gregpacks.omnipack.tooltip.place.1"));
        tooltip.add(Component.translatable("item.gregpacks.omnipack.tooltip.place.2"));
        tooltip.add(Component.translatable("item.gregpacks.omnipack.tooltip.place.3"));
        tooltip.add(Component.translatable("item.gregpacks.omnipack.tooltip.place.4"));

        tooltip.add(Component.literal("Shift to show upgrades").withStyle(ChatFormatting.GRAY));
    }

    // client side only, guarded so the server never touches client Screen class
    private static boolean shiftHeld() {
        return FMLEnvironment.dist == Dist.CLIENT && net.minecraft.client.gui.screens.Screen.hasShiftDown();
    }

    private static String fmt(long value) {
        return String.format("%,d", value);
    }
}