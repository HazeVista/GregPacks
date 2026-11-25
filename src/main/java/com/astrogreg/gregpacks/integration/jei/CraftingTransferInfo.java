package com.astrogreg.gregpacks.integration.jei;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.CraftingRecipe;

import com.astrogreg.gregpacks.inventory.GregPacksMenus;
import com.astrogreg.gregpacks.inventory.OmniPackMenu;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CraftingTransferInfo implements IRecipeTransferInfo<OmniPackMenu, CraftingRecipe> {

    @Override
    public Class<? extends OmniPackMenu> getContainerClass() {
        return OmniPackMenu.class;
    }

    @Override
    public Optional<MenuType<OmniPackMenu>> getMenuType() {
        return Optional.of(GregPacksMenus.OMNIPACK_MENU.get());
    }

    @Override
    public RecipeType<CraftingRecipe> getRecipeType() {
        return RecipeTypes.CRAFTING;
    }

    @Override
    public boolean canHandle(OmniPackMenu container, CraftingRecipe recipe) {
        return container.getCraftingGridStart() >= 0;
    }

    @Override
    public List<Slot> getRecipeSlots(OmniPackMenu container, CraftingRecipe recipe) {
        int gridStart = container.getCraftingGridStart();
        List<Slot> slots = new ArrayList<>(9);
        if (gridStart < 0) return slots;
        for (int i = 0; i < 9; i++) slots.add(container.slots.get(gridStart + i));
        return slots;
    }

    @Override
    public List<Slot> getInventorySlots(OmniPackMenu container, CraftingRecipe recipe) {
        int playerStart = container.getPackSlots() + container.getMaxUpgrades();
        List<Slot> slots = new ArrayList<>();
        for (int i = 0; i < 36; i++) slots.add(container.slots.get(playerStart + i));
        for (int i = 0; i < container.getPackSlots(); i++) slots.add(container.slots.get(i));
        return slots;
    }
}
