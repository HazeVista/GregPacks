package com.astrogreg.gregpacks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import com.astrogreg.gregpacks.inventory.OmniPackMenu;
import com.astrogreg.gregpacks.config.JetpackConfigHelper;

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

            // only the Jetpack II module inside the pack this player has open can be edited
            ItemStack module = JetpackConfigHelper.findModule(menu.getUpgradeInventory());
            if (module.isEmpty()) return;

            int delta = Integer.signum(msg.delta);
            if (msg.isSpeed) {
                JetpackConfigHelper.setSpeed(module, JetpackConfigHelper.getSpeed(module) + delta);
            } else {
                JetpackConfigHelper.setThrust(module, JetpackConfigHelper.getThrust(module) + delta);
            }

            // saves the upgrades to the pack and syncs changed module stack to the client
            menu.getUpgradeInventory().setChanged();
        });
        ctx.get().setPacketHandled(true);
    }
}