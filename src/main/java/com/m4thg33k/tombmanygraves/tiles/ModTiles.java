package com.m4thg33k.tombmanygraves.tiles;

import com.m4thg33k.tombmanygraves.lib.Names;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTiles {

    public static void init()
    {
        String prefix = "tile.tombmanygraves";
        GameRegistry.registerTileEntity(TileDeathBlock.class, prefix + Names.DEATH_BLOCK);
    }
}
