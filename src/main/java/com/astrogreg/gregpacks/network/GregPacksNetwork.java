package com.astrogreg.gregpacks.network;

import com.astrogreg.gregpacks.GregPacks;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

// Handles the registration of network messages for the mod.
@SuppressWarnings("all")
public class GregPacksNetwork {

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(GregPacks.MOD_ID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals);

    public static void init() {
        CHANNEL.messageBuilder(CPacketOpenOmniPack.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(CPacketOpenOmniPack::encode)
                .decoder(CPacketOpenOmniPack::decode)
                .consumerMainThread(CPacketOpenOmniPack::handle)
                .add();
    }
}