package com.astrogreg.gregpacks.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// Sent server → client every tick that the jetpack is actively thrusting
public class SPacketJetpackThrust {

    private final double velX;
    private final double velY;
    private final double velZ;

    public SPacketJetpackThrust(double velX, double velY, double velZ) {
        this.velX = velX;
        this.velY = velY;
        this.velZ = velZ;
    }

    public static void encode(SPacketJetpackThrust msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.velX);
        buf.writeDouble(msg.velY);
        buf.writeDouble(msg.velZ);
    }

    public static SPacketJetpackThrust decode(FriendlyByteBuf buf) {
        return new SPacketJetpackThrust(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static void handle(SPacketJetpackThrust msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            player.setDeltaMovement(msg.velX, msg.velY, msg.velZ);
            player.fallDistance = 0f;
        });
        ctx.get().setPacketHandled(true);
    }
}
