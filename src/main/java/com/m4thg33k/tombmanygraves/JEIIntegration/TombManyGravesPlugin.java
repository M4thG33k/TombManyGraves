package com.m4thg33k.tombmanygraves.JEIIntegration;

import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

@JEIPlugin
public class TombManyGravesPlugin extends BlankModPlugin{

    @Override
    public void register(@Nonnull IModRegistry registry) {
        IJeiHelpers jeiHelpers = registry.getJeiHelpers();

        jeiHelpers.getItemBlacklist().addItemToBlacklist(new ItemStack(ModBlocks.blockDeath));
    }


}
