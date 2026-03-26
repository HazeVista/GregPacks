package com.astrogreg.gregpacks.item;

import com.astrogreg.gregpacks.GregPacks;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;

@SuppressWarnings("all")
public class GregPacksItems {

    // OmniPacks
//    public static final ItemEntry<OmniPackItem> BASIC_OMNIPACK = GregPacks.REGISTRATE
//            .item(OmniPackTier.BASIC.itemId(), p -> new OmniPackItem(OmniPackTier.BASIC))
//            .lang("Basic OmniPack")
//            .register();
//
//    public static final ItemEntry<OmniPackItem> ADVANCED_OMNIPACK = GregPacks.REGISTRATE
//            .item(OmniPackTier.ADVANCED.itemId(), p -> new OmniPackItem(OmniPackTier.ADVANCED))
//            .lang("Advanced OmniPack")
//            .register();
//
//    public static final ItemEntry<OmniPackItem> ELITE_OMNIPACK = GregPacks.REGISTRATE
//            .item(OmniPackTier.ELITE.itemId(), p -> new OmniPackItem(OmniPackTier.ELITE))
//            .lang("Elite OmniPack")
//            .register();

    // Module Base Molds
    public static final ItemEntry<Item> MODULE_EXTRUDER_MOLD = GregPacks.REGISTRATE
            .item("module_extruder_mold", Item::new)
            .lang("Module Base Extruder Mold")
            .register();
    public static final ItemEntry<Item> MODULE_CASTING_MOLD = GregPacks.REGISTRATE
            .item("module_casting_mold", Item::new)
            .lang("Module Base Casting Mold")
            .register();

    // Module Bases
    public static final ItemEntry<Item> LV_MODULE_BASE = GregPacks.REGISTRATE
            .item("lv_module_base", Item::new)
            .lang("LV Module Base")
            .register();
    public static final ItemEntry<Item> MV_MODULE_BASE = GregPacks.REGISTRATE
            .item("mv_module_base", Item::new)
            .lang("MV Module Base")
            .register();
    public static final ItemEntry<Item> HV_MODULE_BASE = GregPacks.REGISTRATE
            .item("hv_module_base", Item::new)
            .lang("HV Module Base")
            .register();
    public static final ItemEntry<Item> EV_MODULE_BASE = GregPacks.REGISTRATE
            .item("ev_module_base", Item::new)
            .lang("EV Module Base")
            .register();
    public static final ItemEntry<Item> IV_MODULE_BASE = GregPacks.REGISTRATE
            .item("iv_module_base", Item::new)
            .lang("IV Module Base")
            .register();
    public static final ItemEntry<Item> LUV_MODULE_BASE = GregPacks.REGISTRATE
            .item("luv_module_base", Item::new)
            .lang("LuV Module Base")
            .register();
    public static final ItemEntry<Item> ZPM_MODULE_BASE = GregPacks.REGISTRATE
            .item("zpm_module_base", Item::new)
            .lang("ZPM Module Base")
            .register();
    public static final ItemEntry<Item> UV_MODULE_BASE = GregPacks.REGISTRATE
            .item("uv_module_base", Item::new)
            .lang("UV Module Base")
            .register();

    // Upgrade Modules

    public static void init() {}
}
