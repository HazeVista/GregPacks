package com.astrogreg.gregpacks.inventory;

import com.astrogreg.gregpacks.network.CPacketOpenOmniPack;
import com.astrogreg.gregpacks.network.GregPacksNetwork;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class OmniPackKeybind {

    public static final KeyMapping OPEN_PACK = new KeyMapping(
            "key.gregpacks.open_omnipack",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "key.categories.gregpacks");

    // Call this from RegisterKeyMappingsEvent on the mod event bus.
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(OPEN_PACK);
    }

    // Tick handler — runs on the client each tick to check if the key was pressed.
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        while (OPEN_PACK.consumeClick()) {
            GregPacksNetwork.CHANNEL.sendToServer(new CPacketOpenOmniPack());
        }
    }
}