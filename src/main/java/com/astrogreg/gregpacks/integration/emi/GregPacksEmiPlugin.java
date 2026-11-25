package com.astrogreg.gregpacks.integration.emi;

import com.astrogreg.gregpacks.inventory.GregPacksMenus;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

@EmiEntrypoint
public class GregPacksEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addRecipeHandler(GregPacksMenus.OMNIPACK_MENU.get(), new CraftingRecipeHandler());
    }
}