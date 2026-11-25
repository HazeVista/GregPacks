package com.astrogreg.gregpacks.integration.jei;

import com.astrogreg.gregpacks.GregPacks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class GregPacksJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return GregPacks.id("jei_plugin");
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new CraftingTransferInfo());
    }
}