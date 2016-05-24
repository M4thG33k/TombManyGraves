package com.m4thg33k.tombmanygraves.blocks;

import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.lib.Names;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBlocks {

    public static BlockDeath blockDeath = new BlockDeath();

    public static void preInit()
    {
        GameRegistry.register(blockDeath);
        GameRegistry.register(new ItemBlock(blockDeath).setRegistryName(TombManyGraves.MODID, Names.DEATH_BLOCK));
    }
}
