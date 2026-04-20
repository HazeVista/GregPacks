package com.astrogreg.gregpacks.integration.jade;

import com.astrogreg.gregpacks.block.OmniPackBlock;
import com.astrogreg.gregpacks.block.OmniPackBlockEntity;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class GregPackJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(new JadeGregPackProvider(), OmniPackBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new JadeGregPackProvider(), OmniPackBlock.class);
    }
}