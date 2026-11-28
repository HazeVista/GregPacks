package com.astrogreg.gregpacks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import com.astrogreg.gregpacks.config.GregPacksConfig;
import com.astrogreg.gregpacks.inventory.OmniPackInventory;
import com.astrogreg.gregpacks.inventory.OmniPackMenu;
import com.astrogreg.gregpacks.config.MagnetConfigHelper;

import java.util.function.Supplier;

public class CPacketMagnetConfig {

    public static final int RANGE = 0; // value = delta
    public static final int TOGGLE_FILTER = 1;
    public static final int TOGGLE_MODE = 2;
    public static final int SET_SLOT = 3; // value = slot, item = what the player is carrying (read server side)
    public static final int CLEAR_SLOT = 4; // value = slot

    private final int action;
    private final int value;

    public CPacketMagnetConfig(int action, int value) {
        this.action = action;
        this.value = value;
    }

    public static void encode(CPacketMagnetConfig msg, FriendlyByteBuf buf) {
        buf.writeByte(msg.action);
        buf.writeInt(msg.value);
    }

    public static CPacketMagnetConfig decode(FriendlyByteBuf buf) {
        return new CPacketMagnetConfig(buf.readByte(), buf.readInt());
    }

    public static void handle(CPacketMagnetConfig msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (!(player.containerMenu instanceof OmniPackMenu menu)) return;

            OmniPackInventory upgrades = menu.getUpgradeInventory();
            ItemStack module = MagnetConfigHelper.findModule(upgrades);
            if (module.isEmpty()) return;

            int maxRange = GregPacksConfig.INSTANCE.ModuleValues.magnetModule2Radius;

            switch (msg.action) {
                case RANGE -> {
                    int delta = Math.max(-64, Math.min(64, msg.value));
                    int cur = MagnetConfigHelper.getRange(module, maxRange);
                    MagnetConfigHelper.setRange(module, cur + delta, maxRange);
                }
                case TOGGLE_FILTER -> MagnetConfigHelper.setFilterOn(module, !MagnetConfigHelper.isFilterOn(module));
                case TOGGLE_MODE -> MagnetConfigHelper.setWhitelist(module, !MagnetConfigHelper.isWhitelist(module));
                case SET_SLOT -> {
                    ItemStack carried = menu.getCarried();
                    if (carried.isEmpty()) return;
                    MagnetConfigHelper.setFilterItem(module, msg.value, carried);
                }
                case CLEAR_SLOT -> MagnetConfigHelper.setFilterItem(module, msg.value, ItemStack.EMPTY);
                default -> {
                    return;
                }
            }

            // saves the upgrades onto the pack stack and syncs the changed module to the client
            upgrades.setChanged();
            if (menu.getSourceBlockEntity() != null) menu.getSourceBlockEntity().setChanged();
        });
        ctx.get().setPacketHandled(true);
    }
}