package com.astrogreg.gregpacks.datagen;

import com.astrogreg.gregpacks.datagen.lang.GregPacksLangHandler;
import com.tterrag.registrate.providers.ProviderType;

import static com.astrogreg.gregpacks.GregPacks.REGISTRATE;

public class GregPacksDatagen {

    public static void init() {
        REGISTRATE.addDataGenerator(ProviderType.LANG, GregPacksLangHandler::init);
    }
}
