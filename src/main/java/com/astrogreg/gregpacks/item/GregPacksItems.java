package com.astrogreg.gregpacks.item;

import com.astrogreg.gregpacks.GregPacks;
import com.tterrag.registrate.util.entry.ItemEntry;

public class GregPacksItems {

    public static final ItemEntry<OmniPackItem> BASIC_OMNIPACK = GregPacks.REGISTRATE
            .item(OmniPackTier.BASIC.itemId(), p -> new OmniPackItem(OmniPackTier.BASIC))
            .lang("Basic OmniPack")
            .register();

    public static final ItemEntry<OmniPackItem> ADVANCED_OMNIPACK = GregPacks.REGISTRATE
            .item(OmniPackTier.ADVANCED.itemId(), p -> new OmniPackItem(OmniPackTier.ADVANCED))
            .lang("Advanced OmniPack")
            .register();

    public static final ItemEntry<OmniPackItem> ELITE_OMNIPACK = GregPacks.REGISTRATE
            .item(OmniPackTier.ELITE.itemId(), p -> new OmniPackItem(OmniPackTier.ELITE))
            .lang("Elite OmniPack")
            .register();

    public static void init() {}
}
