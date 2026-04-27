package com.astrogreg.gregpacks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import com.astrogreg.gregpacks.inventory.OmniPackMenu;

import java.util.function.Supplier;

// Needs more work, made to sync the fluid color.
public class SPacketFluidSync {

    private final String fluidKey;

    public SPacketFluidSync(String fluidKey) {
        this.fluidKey = fluidKey;
    }

    public static void encode(SPacketFluidSync msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.fluidKey);
    }

    public static SPacketFluidSync decode(FriendlyByteBuf buf) {
        return new SPacketFluidSync(buf.readUtf());
    }

    public static void handle(SPacketFluidSync msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.player != null && mc.player.containerMenu instanceof OmniPackMenu menu) {
                menu.setSyncedFluidKey(msg.fluidKey);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
