package com.m4thg33k.tombmanygraves.client.render;

import com.m4thg33k.tombmanygraves.client.render.tiles.TileDeathBlockRenderer;
import com.m4thg33k.tombmanygraves.tiles.TileDeathBlock;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ModRenders {

    public static void init()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileDeathBlock.class,new TileDeathBlockRenderer());
    }
}
