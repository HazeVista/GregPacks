package com.astrogreg.gregpacks.item;

import com.astrogreg.gregpacks.GregPacks;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("all")
public class GregPacksItems {

    // OmniPacks
//    public static final ItemEntry<OmniPackItem> BASIC_OMNIPACK = GregPacks.REGISTRATE
//            .item(OmniPackTier.BASIC.itemId(), p -> new OmniPackItem(OmniPackTier.BASIC))
//            .lang("Basic OmniPack")
//            .register();
//    public static final ItemEntry<OmniPackItem> ADVANCED_OMNIPACK = GregPacks.REGISTRATE
//            .item(OmniPackTier.ADVANCED.itemId(), p -> new OmniPackItem(OmniPackTier.ADVANCED))
//            .lang("Advanced OmniPack")
//            .register();
//    public static final ItemEntry<OmniPackItem> ELITE_OMNIPACK = GregPacks.REGISTRATE
//            .item(OmniPackTier.ELITE.itemId(), p -> new OmniPackItem(OmniPackTier.ELITE))
//            .lang("Elite OmniPack")
//            .register();

    // Module Base Molds
    public static final ItemEntry<GPItem> MODULE_EXTRUDER_MOLD = GregPacks.REGISTRATE
            .item("module_extruder_mold", GPItem::new)
            .lang("Module Base Extruder Mold")
            .register();
    public static final ItemEntry<GPItem> MODULE_CASTING_MOLD = GregPacks.REGISTRATE
            .item("module_casting_mold", GPItem::new)
            .lang("Module Base Casting Mold")
            .register();

    // Module Bases
    public static final ItemEntry<GPItem> LV_MODULE_BASE = GregPacks.REGISTRATE
            .item("lv_module_base", GPItem::new)
            .lang("LV Module Base")
            .register();
    public static final ItemEntry<GPItem> MV_MODULE_BASE = GregPacks.REGISTRATE
            .item("mv_module_base", GPItem::new)
            .lang("MV Module Base")
            .register();
    public static final ItemEntry<GPItem> HV_MODULE_BASE = GregPacks.REGISTRATE
            .item("hv_module_base", GPItem::new)
            .lang("HV Module Base")
            .register();
    public static final ItemEntry<GPItem> EV_MODULE_BASE = GregPacks.REGISTRATE
            .item("ev_module_base", GPItem::new)
            .lang("EV Module Base")
            .register();
    public static final ItemEntry<GPItem> IV_MODULE_BASE = GregPacks.REGISTRATE
            .item("iv_module_base", GPItem::new)
            .lang("IV Module Base")
            .register();
    public static final ItemEntry<GPItem> LUV_MODULE_BASE = GregPacks.REGISTRATE
            .item("luv_module_base", GPItem::new)
            .lang("LuV Module Base")
            .register();
//    public static final ItemEntry<GPItem> ZPM_MODULE_BASE = GregPacks.REGISTRATE
//            .item("zpm_module_base", GPItem::new)
//            .lang("ZPM Module Base")
//            .register();
//    public static final ItemEntry<GPItem> UV_MODULE_BASE = GregPacks.REGISTRATE
//            .item("uv_module_base", GPItem::new)
//            .lang("UV Module Base")
//            .register();

    // Upgrade Modules
    public static final ItemEntry<GPItem> ITEM_MODULE_1 = GregPacks.REGISTRATE
            .item("item_module_1", GPItem::new)
            .lang("Item Capacity Module I")
            .register();
    public static final ItemEntry<GPItem> ITEM_MODULE_2 = GregPacks.REGISTRATE
            .item("item_module_2", GPItem::new)
            .lang("Item Capacity Module II")
            .register();
    public static final ItemEntry<GPItem> ITEM_MODULE_3 = GregPacks.REGISTRATE
            .item("item_module_3", GPItem::new)
            .lang("Item Capacity Module III")
            .register();
    public static final ItemEntry<GPItem> FLUID_MODULE_1 = GregPacks.REGISTRATE
            .item("fluid_module_1", GPItem::new)
            .lang("Fluid Capacity Module I")
            .register();
    public static final ItemEntry<GPItem> FLUID_MODULE_2 = GregPacks.REGISTRATE
            .item("fluid_module_2", GPItem::new)
            .lang("Fluid Capacity Module II")
            .register();
    public static final ItemEntry<GPItem> FLUID_MODULE_3 = GregPacks.REGISTRATE
            .item("fluid_module_3", GPItem::new)
            .lang("Fluid Capacity Module III")
            .register();
    public static final ItemEntry<GPItem> ENERGY_MODULE_1 = GregPacks.REGISTRATE
            .item("energy_module_1", GPItem::new)
            .lang("Energy Capacity Module I")
            .register();
    public static final ItemEntry<GPItem> ENERGY_MODULE_2 = GregPacks.REGISTRATE
            .item("energy_module_2", GPItem::new)
            .lang("Energy Capacity Module II")
            .register();
    public static final ItemEntry<GPItem> ENERGY_MODULE_3 = GregPacks.REGISTRATE
            .item("energy_module_3", GPItem::new)
            .lang("Energy Capacity Module III")
            .register();
    public static final ItemEntry<GPItem> CRAFTING_MODULE = GregPacks.REGISTRATE
            .item("crafting_module", GPItem::new)
            .lang("Crafting Module")
            .register();
    public static final ItemEntry<GPItem> FEEDING_MODULE = GregPacks.REGISTRATE
            .item("feeding_module", GPItem::new)
            .lang("Feeding Module")
            .register();
    public static final ItemEntry<GPItem> JETPACK_MODULE_1 = GregPacks.REGISTRATE
            .item("jetpack_module_1", GPItem::new)
            .lang("Jetpack Module I")
            .register();
    public static final ItemEntry<GPItem> JETPACK_MODULE_2 = GregPacks.REGISTRATE
            .item("jetpack_module_2", GPItem::new)
            .lang("Jetpack Module II")
            .register();
    public static final ItemEntry<GPItem> MAINTENANCE_MODULE = GregPacks.REGISTRATE
            .item("maintenance_module", GPItem::new)
            .lang("Maintenance Module")
            .register();
    public static final ItemEntry<GPItem> MAGNET_MODULE_1 = GregPacks.REGISTRATE
            .item("magnet_module_1", GPItem::new)
            .lang("Magnet Module I")
            .register();
    public static final ItemEntry<GPItem> MAGNET_MODULE_2 = GregPacks.REGISTRATE
            .item("magnet_module_2", GPItem::new)
            .lang("Magnet Module II")
            .register();
    public static final ItemEntry<GPItem> PROCESSING_MODULE = GregPacks.REGISTRATE
            .item("processing_module", GPItem::new)
            .lang("Processing Module I")
            .register();

    public static class GPItem extends Item {

        public GPItem(Properties properties) {
            super(properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            String key = stack.getItem().getDescriptionId() + ".tooltip";
            if (I18n.exists(key)) {
                tooltip.add(Component.translatable(key));
            }
        }
    }

    public static void init() {}
}
