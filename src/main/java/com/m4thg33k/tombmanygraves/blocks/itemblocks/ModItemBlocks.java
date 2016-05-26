package com.m4thg33k.tombmanygraves.blocks.itemblocks;

import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItemBlocks {

    public static DeathItemBlock deathItemBlock = new DeathItemBlock(ModBlocks.blockDeath);

    public static void createItemblocks()
    {
        GameRegistry.register(deathItemBlock);
    }
}
