package com.astrogreg.gregpacks.datagen;

import com.gregtechceu.gtceu.api.registry.registrate.SoundEntryBuilder;

import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.astrogreg.gregpacks.GregPacks;

public class GregPacksDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();

        if (event.includeClient()) {
            event.getGenerator().addProvider(
                    true,
                    new SoundEntryBuilder.SoundEntryProvider(packOutput, GregPacks.MOD_ID));
        }

        if (event.includeServer()) {
            event.getGenerator().addProvider(
                    true,
                    GregPacksLootTableProvider.create(packOutput));
            event.getGenerator().addProvider(true, new GregPacksCuriosProvider(packOutput));
        }
    }
}
