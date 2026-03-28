package com.astrogreg.gregpacks;

import com.astrogreg.gregpacks.config.GregPacksConfig;
import com.astrogreg.gregpacks.datagen.GregPacksDataGenerators;
import com.astrogreg.gregpacks.datagen.GregPacksDatagen;
import com.astrogreg.gregpacks.inventory.GregPacksMenus;
import com.astrogreg.gregpacks.inventory.OmniPackKeybind;
import com.astrogreg.gregpacks.inventory.OmniPackScreen;
import com.astrogreg.gregpacks.item.GregPacksItems;
import com.astrogreg.gregpacks.network.GregPacksNetwork;
import com.astrogreg.gregpacks.registry.GregPacksBlockEntities;
import com.astrogreg.gregpacks.registry.GregPacksBlocks;
import com.astrogreg.gregpacks.registry.GregPacksUpgrades;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialRegistryEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.PostMaterialEvent;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.api.sound.SoundEntry;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(GregPacks.MOD_ID)
@SuppressWarnings("removal")
public class GregPacks {

    public static final String MOD_ID = "gregpacks";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final GTRegistrate REGISTRATE = GTRegistrate.create(MOD_ID);

    public GregPacks() {
        GregPacksConfig.init();
        GregPacksDatagen.init();
        GregPacksUpgrades.init();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        modEventBus.addListener(this::addMaterialRegistries);
        modEventBus.addListener(this::addMaterials);
        modEventBus.addListener(this::modifyMaterials);

        modEventBus.addGenericListener(GTRecipeType.class, this::registerRecipeTypes);
        modEventBus.addGenericListener(MachineDefinition.class, this::registerMachines);
        modEventBus.addGenericListener(SoundEntry.class, this::registerSounds);

        // BlockEntities still use DeferredRegister
        GregPacksMenus.MENUS.register(modEventBus);
        GregPacksBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        modEventBus.register(GregPacksDataGenerators.class);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(OmniPackKeybind::register);
            MinecraftForge.EVENT_BUS.register(OmniPackKeybind.class);
        }

        MinecraftForge.EVENT_BUS.register(this);

        // Classload — GTRegistrate handles blocks/items automatically
        GregPacksItems.init();
        GregPacksBlocks.init();
        GregPacksBlockEntities.init();

        REGISTRATE.registerRegistrate();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(GregPacksNetwork::init);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() ->
                MenuScreens.register(GregPacksMenus.OMNIPACK_MENU.get(), OmniPackScreen::new));
    }

    public static void init() {
        GregPacksConfig.init();
        GregPacksDatagen.init();
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private void addMaterialRegistries(MaterialRegistryEvent event) {
        GTCEuAPI.materialManager.createRegistry(MOD_ID);
    }

    private void addMaterials(MaterialEvent event) {}
    private void modifyMaterials(PostMaterialEvent event) {}
    private void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {}
    private void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {}
    public void registerSounds(GTCEuAPI.RegisterEvent<ResourceLocation, SoundEntry> event) {}
}