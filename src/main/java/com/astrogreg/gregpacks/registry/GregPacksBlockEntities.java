package com.astrogreg.gregpacks.registry;

import com.astrogreg.gregpacks.GregPacks;
import com.astrogreg.gregpacks.block.OmniPackBlockEntity;
import com.astrogreg.gregpacks.item.OmniPackTier;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GregPacksBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GregPacks.MOD_ID);

    // Use suppliers (() -> block.get()) instead of block.get() directly.
    // This avoids classloading GregPacksBlocks before it's ready — the supplier
    // is only evaluated when the registry event fires, at which point all blocks
    // are guaranteed to be registered.
    public static final RegistryObject<BlockEntityType<OmniPackBlockEntity>> BASIC_OMNIPACK_BE =
            BLOCK_ENTITIES.register("basic_omnipack", () ->
                    BlockEntityType.Builder.of(
                                    (pos, state) -> new OmniPackBlockEntity(pos, state, OmniPackTier.BASIC),
                                    GregPacksBlocks.BASIC_OMNIPACK_BLOCK.get())
                            .build(null));

    public static final RegistryObject<BlockEntityType<OmniPackBlockEntity>> ADVANCED_OMNIPACK_BE =
            BLOCK_ENTITIES.register("advanced_omnipack", () ->
                    BlockEntityType.Builder.of(
                                    (pos, state) -> new OmniPackBlockEntity(pos, state, OmniPackTier.ADVANCED),
                                    GregPacksBlocks.ADVANCED_OMNIPACK_BLOCK.get())
                            .build(null));

    public static final RegistryObject<BlockEntityType<OmniPackBlockEntity>> ELITE_OMNIPACK_BE =
            BLOCK_ENTITIES.register("elite_omnipack", () ->
                    BlockEntityType.Builder.of(
                                    (pos, state) -> new OmniPackBlockEntity(pos, state, OmniPackTier.ELITE),
                                    GregPacksBlocks.ELITE_OMNIPACK_BLOCK.get())
                            .build(null));

    public static BlockEntityType<OmniPackBlockEntity> getTypeForTier(OmniPackTier tier) {
        return switch (tier) {
            case BASIC    -> BASIC_OMNIPACK_BE.get();
            case ADVANCED -> ADVANCED_OMNIPACK_BE.get();
            case ELITE    -> ELITE_OMNIPACK_BE.get();
        };
    }

    // Force classload — call from GregPacks constructor AFTER GregPacksBlocks.init()
    public static void init() {}
}