package com.astrogreg.gregpacks.integration.emi;

import net.minecraft.world.inventory.Slot;

import com.astrogreg.gregpacks.inventory.OmniPackMenu;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;

import java.util.ArrayList;
import java.util.List;

public class CraftingRecipeHandler implements StandardRecipeHandler<OmniPackMenu> {

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory() == VanillaEmiRecipeCategories.CRAFTING;
    }

    @Override
    public List<Slot> getInputSources(OmniPackMenu menu) {
        int playerStart = menu.getPackSlots() + menu.getMaxUpgrades();
        List<Slot> slots = new ArrayList<>();
        for (int i = 0; i < 36; i++) slots.add(menu.slots.get(playerStart + i));
        for (int i = 0; i < menu.getPackSlots(); i++) slots.add(menu.slots.get(i));
        return slots;
    }

    @Override
    public List<Slot> getCraftingSlots(OmniPackMenu menu) {
        int gridStart = menu.getCraftingGridStart();
        List<Slot> slots = new ArrayList<>(9);
        if (gridStart < 0) return slots;
        for (int i = 0; i < 9; i++) slots.add(menu.slots.get(gridStart + i));
        return slots;
    }
}
