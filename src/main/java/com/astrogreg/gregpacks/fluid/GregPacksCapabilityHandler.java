package com.astrogreg.gregpacks.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.astrogreg.gregpacks.GregPacks;
import com.astrogreg.gregpacks.item.OmniPackItem;
import com.astrogreg.gregpacks.item.OmniPackTier;
import com.astrogreg.gregpacks.registry.OmniPackBlockItem;

/**
 * Registers the IFluidHandlerItem capability on every OmniPack ItemStack
 * (both the item and the block-item).
 */
@Mod.EventBusSubscriber(modid = GregPacks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GregPacksCapabilityHandler {

    private static final ResourceLocation FLUID_CAP_KEY = new ResourceLocation(GregPacks.MOD_ID, "omnipack_fluid");

    @SubscribeEvent
    public static void onAttachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        OmniPackTier tier = getTier(stack);
        if (tier == null) return;

        event.addCapability(FLUID_CAP_KEY,
                new OmniPackCapabilityProvider(stack, tier));
    }

    private static OmniPackTier getTier(ItemStack stack) {
        if (stack.getItem() instanceof OmniPackItem item) return item.getTier();
        if (stack.getItem() instanceof OmniPackBlockItem item) return item.getBlock().getTier();
        return null;
    }
}
