package com.astrogreg.gregpacks.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class PackTags {
    public static final TagKey<Item> CURIOS_BACK =
            TagKey.create(Registries.ITEM, new ResourceLocation("curios", "back"));

    private PackTags() {}
}