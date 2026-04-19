package com.astrogreg.gregpacks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.NetworkEvent;

import com.astrogreg.gregpacks.inventory.OmniPackMenu;
import com.astrogreg.gregpacks.item.OmniPackItem;
import com.astrogreg.gregpacks.item.OmniPackTier;
import com.astrogreg.gregpacks.registry.OmniPackBlockItem;

import java.util.function.Supplier;

// Hello, pls fix uwu.
public class CPacketFluidInteract {

    private final int heldSlot;

    public CPacketFluidInteract(int heldSlot) {
        this.heldSlot = heldSlot;
    }

    public static void encode(CPacketFluidInteract msg, FriendlyByteBuf buf) {
        buf.writeByte(msg.heldSlot);
    }

    public static CPacketFluidInteract decode(FriendlyByteBuf buf) {
        return new CPacketFluidInteract(buf.readByte());
    }

    // Handle
    public static void handle(CPacketFluidInteract msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!(player.containerMenu instanceof OmniPackMenu menu)) return;

            ItemStack packStack = menu.getActiveStack();
            if (packStack == null || packStack.isEmpty()) return;
            if (!(packStack.getItem() instanceof OmniPackItem) && !(packStack.getItem() instanceof OmniPackBlockItem))
                return;

            ItemStack held = player.getInventory().getItem(msg.heldSlot);
            if (held.isEmpty()) return;
            if (!held.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) return;

            OmniPackTier tier = getTier(packStack);
            if (tier == null) return;

            com.astrogreg.gregpacks.fluid.OmniPackFluidHandler packFluidHandler = new com.astrogreg.gregpacks.fluid.OmniPackFluidHandler(
                    packStack.copy(), tier);

            player.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(playerInventory -> {
                net.minecraftforge.fluids.FluidActionResult result = net.minecraftforge.fluids.FluidUtil
                        .tryEmptyContainerAndStow(
                                held, packFluidHandler, playerInventory,
                                net.minecraftforge.fluids.FluidType.BUCKET_VOLUME, player, true);

                if (!result.isSuccess()) {
                    result = net.minecraftforge.fluids.FluidUtil.tryFillContainerAndStow(
                            held, packFluidHandler, playerInventory,
                            net.minecraftforge.fluids.FluidType.BUCKET_VOLUME, player, true);
                }

                if (result.isSuccess()) {
                    ItemStack modifiedPack = packFluidHandler.getContainer();
                    com.astrogreg.gregpacks.fluid.FluidNBTHelper.setFluid(
                            packStack,
                            com.astrogreg.gregpacks.fluid.FluidNBTHelper.getFluid(modifiedPack));

                    player.getInventory().setItem(msg.heldSlot, result.getResult());
                    if (net.minecraftforge.fml.ModList.get().isLoaded("curios")) {
                        top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player).ifPresent(inv -> {
                            var slots = inv.getCurios();
                            if (slots.containsKey("back")) {
                                slots.get("back").getStacks().setStackInSlot(0, packStack);
                            }
                        });
                    }

                    int packSlot = menu.getPackSlotIndex();
                    if (packSlot >= 0) {
                        player.getInventory().setItem(packSlot, packStack);
                    }

                    player.getInventory().setChanged();
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }

    private static OmniPackTier getTier(ItemStack stack) {
        if (stack.getItem() instanceof OmniPackItem item) return item.getTier();
        if (stack.getItem() instanceof OmniPackBlockItem item) return item.getBlock().getTier();
        return null;
    }
}
