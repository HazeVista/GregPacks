package com.astrogreg.gregpacks.inventory;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.astrogreg.gregpacks.GregPacks;

// Registers the menu types for the mod's containers.
public class GregPacksMenus {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES,
            GregPacks.MOD_ID);

    public static final RegistryObject<MenuType<OmniPackMenu>> OMNIPACK_MENU = MENUS.register("omnipack_menu",
            () -> IForgeMenuType.create(OmniPackMenu::new));

    public static void init() {}
}
