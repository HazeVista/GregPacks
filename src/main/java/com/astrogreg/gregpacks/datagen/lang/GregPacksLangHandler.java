package com.astrogreg.gregpacks.datagen.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class GregPacksLangHandler {

    public static void init(RegistrateLangProvider provider) {

        provider.add("item.gregpacks.omnipack.tooltip.slots", "Upgrade Slots: %s");
        provider.add("item.gregpacks.omnipack.tooltip.energy", "Energy Capacity: %s EU");
        provider.add("item.gregpacks.omnipack.tooltip.max_upgrades", "Max Upgrades: %s");
        provider.add("item.gregpacks.omnipack.tooltip.fluid", "Fluid: %s mB");

    }

    protected static void multilineLang(RegistrateLangProvider provider, String key, String multiline) {
        var lines = multiline.split("\n");
        multiLang(provider, key, lines);
    }

    protected static void multiLang(RegistrateLangProvider provider, String key, String... values) {
        for (var i = 0; i < values.length; i++) {
            provider.add(getSubKey(key, i), values[i]);
        }
    }

    protected static String getSubKey(String key, int index) {
        return key + "." + index;
    }

}
