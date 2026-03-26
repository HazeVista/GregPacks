package com.astrogreg.gregpacks.recipe;

import com.astrogreg.gregpacks.item.GregPacksItems;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.L;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.EXTRUDER_RECIPES;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES;

@SuppressWarnings("all")
public class GregPacksModuleBaseRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {

        // Module Bases
        VanillaRecipeHelper.addShapedRecipe(provider, true, "module_base_lv",
                GregPacksItems.LV_MODULE_BASE.asStack(), "   ", "PPf", "PPh",
                "P", new MaterialEntry(TagPrefix.plate, GTMaterials.Steel));
        VanillaRecipeHelper.addShapedRecipe(provider, true, "module_base_mv",
                GregPacksItems.MV_MODULE_BASE.asStack(), "   ", "PPf", "PPh",
                "P", new MaterialEntry(TagPrefix.plate, GTMaterials.Aluminium));
        VanillaRecipeHelper.addShapedRecipe(provider, true, "module_base_hv",
                GregPacksItems.HV_MODULE_BASE.asStack(), "   ", "PPf", "PPh",
                "P", new MaterialEntry(TagPrefix.plate, GTMaterials.StainlessSteel));
        VanillaRecipeHelper.addShapedRecipe(provider, true, "module_base_ev",
                GregPacksItems.EV_MODULE_BASE.asStack(), "   ", "PPf", "PPh",
                "P", new MaterialEntry(TagPrefix.plate, GTMaterials.Titanium));
        VanillaRecipeHelper.addShapedRecipe(provider, true, "module_base_iv",
                GregPacksItems.IV_MODULE_BASE.asStack(), "   ", "PPf", "PPh",
                "P", new MaterialEntry(TagPrefix.plate, GTMaterials.TungstenSteel));
        VanillaRecipeHelper.addShapedRecipe(provider, true, "module_base_luv",
                GregPacksItems.LUV_MODULE_BASE.asStack(), "   ", "PPf", "PPh",
                "P", new MaterialEntry(TagPrefix.plate, GTMaterials.HSSE));
//        VanillaRecipeHelper.addShapedRecipe(provider, true, "module_base_zpm",
//                GregPacksItems.ZPM_MODULE_BASE.asStack(), "   ", "PPf", "PPh",
//                "P", new MaterialEntry(TagPrefix.plate, GTMaterials.NaquadahAlloy));
//        VanillaRecipeHelper.addShapedRecipe(provider, true, "module_base_uv",
//                GregPacksItems.UV_MODULE_BASE.asStack(), "   ", "PPf", "PPh",
//                "P", new MaterialEntry(TagPrefix.plate, GTMaterials.Tritanium));
        EXTRUDER_RECIPES.recipeBuilder("module_base_lv")
                .inputItems(TagPrefix.ingot, GTMaterials.Steel)
                .notConsumable(GregPacksItems.MODULE_EXTRUDER_MOLD)
                .outputItems(GregPacksItems.LV_MODULE_BASE)
                .duration((int) GTMaterials.Steel.getMass())
                .EUt(24)
                .save(provider);
        FLUID_SOLIDFICATION_RECIPES.recipeBuilder("module_base_lv")
                .inputFluids(GTMaterials.Steel.getFluid(L * 4))
                .notConsumable(GregPacksItems.MODULE_CASTING_MOLD)
                .outputItems(GregPacksItems.LV_MODULE_BASE)
                .duration((int) GTMaterials.Steel.getMass())
                .EUt(24)
                .save(provider);
        EXTRUDER_RECIPES.recipeBuilder("module_base_mv")
                .inputItems(TagPrefix.ingot, GTMaterials.Aluminium)
                .notConsumable(GregPacksItems.MODULE_EXTRUDER_MOLD)
                .outputItems(GregPacksItems.MV_MODULE_BASE)
                .duration((int) GTMaterials.Aluminium.getMass())
                .EUt(24)
                .save(provider);
        FLUID_SOLIDFICATION_RECIPES.recipeBuilder("module_base_mv")
                .inputFluids(GTMaterials.Aluminium.getFluid(L * 4))
                .notConsumable(GregPacksItems.MODULE_CASTING_MOLD)
                .outputItems(GregPacksItems.MV_MODULE_BASE)
                .duration((int) GTMaterials.Aluminium.getMass())
                .EUt(24)
                .save(provider);
        EXTRUDER_RECIPES.recipeBuilder("module_base_hv")
                .inputItems(TagPrefix.ingot, GTMaterials.StainlessSteel)
                .notConsumable(GregPacksItems.MODULE_EXTRUDER_MOLD)
                .outputItems(GregPacksItems.HV_MODULE_BASE)
                .duration((int) GTMaterials.StainlessSteel.getMass())
                .EUt(24)
                .save(provider);
        FLUID_SOLIDFICATION_RECIPES.recipeBuilder("module_base_hv")
                .inputFluids(GTMaterials.StainlessSteel.getFluid(L * 4))
                .notConsumable(GregPacksItems.MODULE_CASTING_MOLD)
                .outputItems(GregPacksItems.HV_MODULE_BASE)
                .duration((int) GTMaterials.StainlessSteel.getMass())
                .EUt(24)
                .save(provider);
        EXTRUDER_RECIPES.recipeBuilder("module_base_ev")
                .inputItems(TagPrefix.ingot, GTMaterials.Titanium)
                .notConsumable(GregPacksItems.MODULE_EXTRUDER_MOLD)
                .outputItems(GregPacksItems.EV_MODULE_BASE)
                .duration((int) GTMaterials.Titanium.getMass())
                .EUt(24)
                .save(provider);
        FLUID_SOLIDFICATION_RECIPES.recipeBuilder("module_base_ev")
                .inputFluids(GTMaterials.Titanium.getFluid(L * 4))
                .notConsumable(GregPacksItems.MODULE_CASTING_MOLD)
                .outputItems(GregPacksItems.EV_MODULE_BASE)
                .duration((int) GTMaterials.Titanium.getMass())
                .EUt(24)
                .save(provider);
        EXTRUDER_RECIPES.recipeBuilder("module_base_iv")
                .inputItems(TagPrefix.ingot, GTMaterials.TungstenSteel)
                .notConsumable(GregPacksItems.MODULE_EXTRUDER_MOLD)
                .outputItems(GregPacksItems.IV_MODULE_BASE)
                .duration((int) GTMaterials.TungstenSteel.getMass())
                .EUt(24)
                .save(provider);
        FLUID_SOLIDFICATION_RECIPES.recipeBuilder("module_base_iv")
                .inputFluids(GTMaterials.TungstenSteel.getFluid(L * 4))
                .notConsumable(GregPacksItems.MODULE_CASTING_MOLD)
                .outputItems(GregPacksItems.IV_MODULE_BASE)
                .duration((int) GTMaterials.TungstenSteel.getMass())
                .EUt(24)
                .save(provider);
        EXTRUDER_RECIPES.recipeBuilder("module_base_luv")
                .inputItems(TagPrefix.ingot, GTMaterials.HSSS)
                .notConsumable(GregPacksItems.MODULE_EXTRUDER_MOLD)
                .outputItems(GregPacksItems.LUV_MODULE_BASE)
                .duration((int) GTMaterials.HSSS.getMass())
                .EUt(24)
                .save(provider);
        FLUID_SOLIDFICATION_RECIPES.recipeBuilder("module_base_luv")
                .inputFluids(GTMaterials.HSSS.getFluid(L * 4))
                .notConsumable(GregPacksItems.MODULE_CASTING_MOLD)
                .outputItems(GregPacksItems.LUV_MODULE_BASE)
                .duration((int) GTMaterials.HSSS.getMass())
                .EUt(24)
                .save(provider);
//        EXTRUDER_RECIPES.recipeBuilder("module_base_zpm")
//                .inputItems(TagPrefix.ingot, GTMaterials.NaquadahAlloy)
//                .notConsumable(GregPacksItems.MODULE_EXTRUDER_MOLD)
//                .outputItems(GregPacksItems.ZPM_MODULE_BASE)
//                .duration((int) GTMaterials.NaquadahAlloy.getMass())
//                .EUt(24)
//                .save(provider);
//        FLUID_SOLIDFICATION_RECIPES.recipeBuilder("module_base_zpm")
//                .inputFluids(GTMaterials.NaquadahAlloy.getFluid(L * 4))
//                .notConsumable(GregPacksItems.MODULE_CASTING_MOLD)
//                .outputItems(GregPacksItems.ZPM_MODULE_BASE)
//                .duration((int) GTMaterials.NaquadahAlloy.getMass())
//                .EUt(24)
//                .save(provider);
//        EXTRUDER_RECIPES.recipeBuilder("module_base_uv")
//                .inputItems(TagPrefix.ingot, GTMaterials.Tritanium)
//                .notConsumable(GregPacksItems.MODULE_EXTRUDER_MOLD)
//                .outputItems(GregPacksItems.UV_MODULE_BASE)
//                .duration((int) GTMaterials.Tritanium.getMass())
//                .EUt(24)
//                .save(provider);
//        FLUID_SOLIDFICATION_RECIPES.recipeBuilder("module_base_uv")
//                .inputFluids(GTMaterials.Tritanium.getFluid(L * 4))
//                .notConsumable(GregPacksItems.MODULE_CASTING_MOLD)
//                .outputItems(GregPacksItems.UV_MODULE_BASE)
//                .duration((int) GTMaterials.Tritanium.getMass())
//                .EUt(24)
//                .save(provider);

    }

}
