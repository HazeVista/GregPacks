package com.astrogreg.gregpacks.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import com.astrogreg.gregpacks.config.GregPacksConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.List;

public class UpgradeItem extends Item {

    private final UpgradeType upgradeType;
    private final String descriptionKey;

    public UpgradeItem(UpgradeType upgradeType, String descriptionKey) {
        super(new Item.Properties().stacksTo(1));
        this.upgradeType = upgradeType;
        this.descriptionKey = descriptionKey;
    }

    public UpgradeType getUpgradeType() {
        return upgradeType;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable(descriptionKey));
        Component effect = effectLine();
        if (effect != null) tooltipComponents.add(effect);
    }

    // reads the client config values.
    @Nullable
    private Component effectLine() {
        if (GregPacksConfig.INSTANCE == null) return null;
        GregPacksConfig.UpgradeConfigs cfg = GregPacksConfig.INSTANCE.ModuleValues;

        return switch (upgradeType) {
            case ITEM_CAPACITY_I -> slots(cfg.itemModule1Bonus);
            case ITEM_CAPACITY_II -> slots(cfg.itemModule2Bonus);
            case ITEM_CAPACITY_III -> slots(cfg.itemModule3Bonus);

            case FLUID_CAPACITY_I -> fluid(cfg.fluidModule1Bonus);
            case FLUID_CAPACITY_II -> fluid(cfg.fluidModule2Bonus);
            case FLUID_CAPACITY_III -> fluid(cfg.fluidModule3Bonus);

            case ENERGY_CAPACITY_I -> energy(cfg.energyModule1Bonus);
            case ENERGY_CAPACITY_II -> energy(cfg.energyModule2Bonus);
            case ENERGY_CAPACITY_III -> energy(cfg.energyModule3Bonus);

            case MAGNET_I -> radius(cfg.magnetModule1Radius);
            case MAGNET_II -> radius(cfg.magnetModule2Radius);

            case JETPACK_I -> cost(cfg.jetpackModule1EUCost);
            case JETPACK_II -> cost(cfg.jetpackModule2EUCost);

            default -> null;
        };
    }

    private static Component slots(int v) {
        return Component.translatable("item.gregpacks.module.tooltip.item_slots", String.valueOf(v));
    }

    private static Component fluid(double v) {
        return Component.translatable("item.gregpacks.module.tooltip.fluid_multiplier", fmt(v));
    }

    private static Component energy(double v) {
        return Component.translatable("item.gregpacks.module.tooltip.energy_multiplier", fmt(v));
    }

    private static Component radius(int v) {
        return Component.translatable("item.gregpacks.module.tooltip.magnet_radius", String.valueOf(v));
    }

    private static Component cost(int v) {
        return Component.translatable("item.gregpacks.module.tooltip.energy_cost", String.valueOf(v));
    }

    // this just strips off trailing zeros
    // (i.e. 2.00 -> "2.0", 1.25 -> "1.25")
    private static String fmt(double v) {
        String s = BigDecimal.valueOf(v).stripTrailingZeros().toPlainString();
        return s.contains(".") ? s : s + ".0";
    }
}