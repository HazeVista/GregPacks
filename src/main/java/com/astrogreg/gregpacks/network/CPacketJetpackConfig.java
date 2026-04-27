package com.astrogreg.gregpacks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import com.astrogreg.gregpacks.inventory.OmniPackMenu;
import com.astrogreg.gregpacks.util.JetpackConfigHelper;

import java.util.function.Supplier;

public class CPacketJetpackConfig {

    private final boolean isSpeed;
    private final int delta;

    public CPacketJetpackConfig(boolean isSpeed, int delta) {
        this.isSpeed = isSpeed;
        this.delta = delta;
    }

    public static void encode(CPacketJetpackConfig msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.isSpeed);
        buf.writeByte(msg.delta);
    }

    public static CPacketJetpackConfig decode(FriendlyByteBuf buf) {
        return new CPacketJetpackConfig(buf.readBoolean(), buf.readByte());
    }

    public static void handle(CPacketJetpackConfig msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!(player.containerMenu instanceof OmniPackMenu menu)) return;
            ItemStack pack = menu.getActiveStack();
            if (pack.isEmpty()) return;

            if (msg.isSpeed) {
                int cur = JetpackConfigHelper.getSpeed(pack);
                JetpackConfigHelper.setSpeed(pack, cur + msg.delta);
            } else {
                int cur = JetpackConfigHelper.getThrust(pack);
                JetpackConfigHelper.setThrust(pack, cur + msg.delta);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}