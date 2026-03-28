package com.astrogreg.gregpacks.datagen.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

@SuppressWarnings("all")
public class GregPacksLangHandler {

    public static void init(RegistrateLangProvider provider) {

        // OmniPack Base Values
        provider.add("item.gregpacks.omnipack.tooltip.slots", "Upgrade Slots: %s");
        provider.add("item.gregpacks.omnipack.tooltip.energy", "Energy Capacity: %s EU");
        provider.add("item.gregpacks.omnipack.tooltip.upgrades", "Max Upgrades: %s");
        provider.add("item.gregpacks.omnipack.tooltip.fluid", "Fluid: %s mB");

        // Molds
        provider.add("item.gregpacks.module_extruder_mold.tooltip", "Extruder shape for making Module Bases");
        provider.add("item.gregpacks.module_casting_mold.tooltip", "Mold for making Module Bases");

        // Upgrade Modules
        provider.add("item.gregpacks.item_module_1.tooltip",
                "An upgrade module that adds more item slots to your OmniPack");
        provider.add("item.gregpacks.item_module_2.tooltip",
                "An upgrade module that adds more item slots to your OmniPack");
        provider.add("item.gregpacks.item_module_3.tooltip",
                "An upgrade module that adds more item slots to your OmniPack");
        provider.add("item.gregpacks.fluid_module_1.tooltip",
                "An upgrade module that adds more fluid capacity to your OmniPack");
        provider.add("item.gregpacks.fluid_module_2.tooltip",
                "An upgrade module that adds more fluid capacity to your OmniPack");
        provider.add("item.gregpacks.fluid_module_3.tooltip",
                "An upgrade module that adds more fluid capacity to your OmniPack");
        provider.add("item.gregpacks.energy_module_1.tooltip",
                "An upgrade module that adds more EU capacity to your OmniPack");
        provider.add("item.gregpacks.energy_module_2.tooltip",
                "An upgrade module that adds more EU capacity to your OmniPack");
        provider.add("item.gregpacks.energy_module_3.tooltip",
                "An upgrade module that adds more EU capacity to your OmniPack");
        provider.add("item.gregpacks.crafting_module.tooltip",
                "An upgrade module that adds a crafting table interface to your OmniPack");
        provider.add("item.gregpacks.feeding_module.tooltip",
                "An upgrade module that allows your OmniPack to consume food for you automatically");
        provider.add("item.gregpacks.jetpack_module_1.tooltip",
                "An upgrade module that grants creative flight");
        provider.add("item.gregpacks.jetpack_module_2.tooltip",
                "An upgrade module that grants creative flight with speed configurability");
        provider.add("item.gregpacks.maintenance_module.tooltip",
                "An upgrade module that allows you to use your OmniPack to satisfy maintenance requirements");
        provider.add("item.gregpacks.magnet_module_1.tooltip",
                "An upgrade module that increases your pickup range");
        provider.add("item.gregpacks.magnet_module_2.tooltip",
                "An upgrade module that increases your pickup range with configurable range and filtering");
        provider.add("item.gregpacks.processing_module.tooltip",
                "An upgrade module that can be combined with singleblock machines for portable processing in your OmniPack");

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
