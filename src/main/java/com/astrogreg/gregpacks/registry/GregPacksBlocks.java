package com.astrogreg.gregpacks.registry;

import com.astrogreg.gregpacks.GregPacks;
import com.astrogreg.gregpacks.block.OmniPackBlock;
import com.astrogreg.gregpacks.item.OmniPackTier;

import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.item.Item;

@SuppressWarnings("all")
public class GregPacksBlocks {

    public static final BlockEntry<OmniPackBlock> BASIC_OMNIPACK_BLOCK =
            GregPacks.REGISTRATE.block("basic_omnipack",
                            props -> new OmniPackBlock(OmniPackTier.BASIC, props))
                    .properties(p -> p.strength(2f).sound(SoundType.METAL).noOcclusion())
                    .blockstate((ctx, prov) -> {})
                    .item(OmniPackBlockItem::new)
                    .model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                            new ResourceLocation("gregpacks", "block/basic_omnipack")))
                    .build()
                    .lang("§bBasic OmniPack")
                    .register();

    public static final BlockEntry<OmniPackBlock> ADVANCED_OMNIPACK_BLOCK =
            GregPacks.REGISTRATE.block("advanced_omnipack",
                            props -> new OmniPackBlock(OmniPackTier.ADVANCED, props))
                    .properties(p -> p.strength(2f).sound(SoundType.METAL).noOcclusion())
                    .blockstate((ctx, prov) -> {})
                    .item(OmniPackBlockItem::new)
                    .model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                            new ResourceLocation("gregpacks", "block/advanced_omnipack")))
                    .build()
                    .lang("§eAdvanced OmniPack")
                    .register();

    public static final BlockEntry<OmniPackBlock> ELITE_OMNIPACK_BLOCK =
            GregPacks.REGISTRATE.block("elite_omnipack",
                            props -> new OmniPackBlock(OmniPackTier.ELITE, props))
                    .properties(p -> p.strength(2f).sound(SoundType.METAL).noOcclusion())
                    .blockstate((ctx, prov) -> {})
                    .item(OmniPackBlockItem::new)
                    .model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                            new ResourceLocation("gregpacks", "block/elite_omnipack")))
                    .build()
                    .lang("§cElite OmniPack")
                    .register();

    public static Item getItemForTier(OmniPackTier tier) {
        return switch (tier) {
            case BASIC    -> BASIC_OMNIPACK_BLOCK.asItem();
            case ADVANCED -> ADVANCED_OMNIPACK_BLOCK.asItem();
            case ELITE    -> ELITE_OMNIPACK_BLOCK.asItem();
        };
    }

    public static void init() {}
}