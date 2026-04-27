package com.astrogreg.gregpacks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import com.astrogreg.gregpacks.inventory.OpenPackHelper;
import com.astrogreg.gregpacks.item.OmniPackItem;
import com.astrogreg.gregpacks.item.OmniPackTier;
import com.astrogreg.gregpacks.registry.OmniPackBlockItem;

import java.util.function.Supplier;

/**
 * This packet is sent from the client to the server when the player wants to open an OmniPack GUI from their inventory.
 * The server will search the player's inventory for the first OmniPack item, and open the GUI for that item.
 * This allows the player to open the OmniPack GUI without having to place the block or use the item in hand.
 */
public class CPacketOpenOmniPack {

    public CPacketOpenOmniPack() {}

    public static void encode(CPacketOpenOmniPack msg, FriendlyByteBuf buf) {}

    public static CPacketOpenOmniPack decode(FriendlyByteBuf buf) {
        return new CPacketOpenOmniPack();
    }

    public static void handle(CPacketOpenOmniPack msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            if (net.minecraftforge.fml.ModList.get().isLoaded("curios")) {
                top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player).ifPresent(inv -> {
                    var slots = inv.getCurios();
                    if (!slots.containsKey("back")) return;
                    var handler = slots.get("back").getStacks();
                    for (int i = 0; i < handler.getSlots(); i++) {
                        ItemStack stack = handler.getStackInSlot(i);
                        OmniPackTier tier = getTier(stack);
                        if (tier == null) continue;
                        OpenPackHelper.open(player, stack, tier, -1);
                        return;
                    }
                });
                return;
            }

            // Fallback
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                OmniPackTier tier = getTier(stack);
                if (tier == null) continue;
                OpenPackHelper.open(player, stack, tier, i);
                return;
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private static OmniPackTier getTier(ItemStack stack) {
        if (stack.getItem() instanceof OmniPackItem packItem) return packItem.getTier();
        if (stack.getItem() instanceof OmniPackBlockItem blockItem) return blockItem.getBlock().getTier();
        return null;
    }
}
