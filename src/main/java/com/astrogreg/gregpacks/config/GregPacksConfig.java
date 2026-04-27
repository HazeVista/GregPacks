package com.astrogreg.gregpacks.config;

import com.astrogreg.gregpacks.GregPacks;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.format.ConfigFormats;

@SuppressWarnings("all")
@Config(id = GregPacks.MOD_ID)
public class GregPacksConfig {

    public static GregPacksConfig INSTANCE;

    public static ConfigHolder<GregPacksConfig> CONFIG_HOLDER;

    public static void init() {
        CONFIG_HOLDER = Configuration.registerConfig(GregPacksConfig.class, ConfigFormats.yaml());
        INSTANCE = CONFIG_HOLDER.getConfigInstance();
    }

    @Configurable
    @Configurable.Comment({ "Config options for OmniPacks base values" })
    public PackValueConfigs OmniPackBaseValues = new PackValueConfigs();
    @Configurable
    @Configurable.Comment({ "Config options for Upgrade Modules" })
    public UpgradeConfigs ModuleValues = new UpgradeConfigs();

    public static class PackValueConfigs{

        @Configurable
        @Configurable.Comment({ "Basic OmniPack Base Values" })
        public BasicPack basicPack = new BasicPack();
        @Configurable
        @Configurable.Comment({ "Advanced OmniPack Base Values" })
        public AdvancedPack advancedPack = new AdvancedPack();
        @Configurable
        @Configurable.Comment({ "Elite OmniPack Base Values" })
        public ElitePack elitePack = new ElitePack();

        public static class BasicPack{

            @Configurable
            @Configurable.Comment({"Base number of slots for the Basic OmniPack", "Default: 27"})
            public int basicPackItemSlots = 27;
            @Configurable
            @Configurable.Comment({ "Base fluid capacity for the Basic OmniPack in millibuckets", "Default: 32000" })
            public int basicPackFluidStorage = 32_000;
            @Configurable
            @Configurable.Comment({ "Base EU capacity for the Basic OmniPack", "Default: 200000" })
            public int basicPackEUStorage = 200_000;
            @Configurable
            @Configurable.Comment({ "Number of upgrade slots on the Basic OmniPack", "Default: 6" })
            public int basicPackUpgradeSlots = 6;

        }

        public static class AdvancedPack{

            @Configurable
            @Configurable.Comment({"Base number of slots for the Advanced OmniPack", "Default: 45"})
            public int advancedPackItemSlots = 45;
            @Configurable
            @Configurable.Comment({ "Base fluid capacity for the Advanced OmniPack in millibuckets", "Default: 128000" })
            public int advancedPackFluidStorage = 128_000;
            @Configurable
            @Configurable.Comment({ "Base EU capacity for the Advanced OmniPack", "Default: 1000000" })
            public int advancedPackEUStorage = 1_000_000;
            @Configurable
            @Configurable.Comment({ "Number of upgrade slots on the Advanced OmniPack", "Default: 10" })
            public int advancedPackUpgradeSlots = 10;

        }

        public static class ElitePack{

            @Configurable
            @Configurable.Comment({"Base number of slots for the Elite OmniPack", "Default: 90"})
            public int elitePackItemSlots = 90;
            @Configurable
            @Configurable.Comment({ "Base fluid capacity for the Elite OmniPack in millibuckets", "Default: 512000" })
            public int elitePackFluidStorage = 512_000;
            @Configurable
            @Configurable.Comment({ "Base EU capacity for the Elite OmniPack", "Default: 200000000" })
            public int elitePackEUStorage = 200_000_000;
            @Configurable
            @Configurable.Comment({ "Number of upgrade slots on the Elite OmniPack", "Default: 16" })
            public int elitePackUpgradeSlots = 16;

        }

    }

    public static class UpgradeConfigs {

        @Configurable
        @Configurable.Comment({ "Number of slots added by the Item Capacity Module I", "Default: 9" })
        public int itemModule1Bonus = 9;
        @Configurable
        @Configurable.Comment({ "Number of slots added by the Item Capacity Module II", "Default: 18" })
        public int itemModule2Bonus = 18;
        @Configurable
        @Configurable.Comment({ "Number of slots added by the Item Capacity Module III", "Default: 27" })
        public int itemModule3Bonus = 27;
        @Configurable
        @Configurable.Comment({ "Tank multiplier for the Fluid Capacity Module I", "Default: 2.00" })
        public double fluidModule1Bonus = 2.00;
        @Configurable
        @Configurable.Comment({ "Tank multiplier for the Fluid Capacity Module II", "Default: 4.00" })
        public double fluidModule2Bonus = 4.00;
        @Configurable
        @Configurable.Comment({ "Tank multiplier for the Fluid Capacity Module III", "Default: 8.00" })
        public double fluidModule3Bonus = 8.00;
        @Configurable
        @Configurable.Comment({ "EU capacity multiplier for the Energy Capacity Module I", "Default: 1.25" })
        public double energyModule1Bonus = 1.25;
        @Configurable
        @Configurable.Comment({ "EU capacity multiplier for the Energy Capacity Module II", "Default: 1.50" })
        public double energyModule2Bonus = 1.50;
        @Configurable
        @Configurable.Comment({ "EU capacity multiplier for the Energy Capacity Module III", "Default: 2.00" })
        public double energyModule3Bonus = 2.00;
        @Configurable
        @Configurable.Comment({ "EU per tick cost of the Jetpack Module I", "Default: 30" })
        public int jetpackModule1EUCost = 30;
        @Configurable
        @Configurable.Comment({ "EU per tick cost of the Jetpack Module II", "Default: 90" })
        public int jetpackModule2EUCost = 90;
        @Configurable
        @Configurable.Comment({ "Pickup radius in blocks of the Magnet Module I", "Default: 5" })
        public int magnetModule1Radius = 5;
        @Configurable
        @Configurable.Comment({ "Pickup radius in blocks of the Magnet Module II", "Default: 10" })
        public int magnetModule2Radius = 10;

    }

}
