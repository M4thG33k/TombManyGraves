package com.m4thg33k.tombmanygraves.core.proxy;

import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import com.m4thg33k.tombmanygraves.core.events.TombManyGravesCommonEvents;
import com.m4thg33k.tombmanygraves.core.handlers.FriendHandler;
import com.m4thg33k.tombmanygraves.gui.TombManyGravesGuiHandler;
import com.m4thg33k.tombmanygraves.items.ModItems;
import com.m4thg33k.tombmanygraves.lib.TombManyGravesConfigs;
import com.m4thg33k.tombmanygraves.tiles.ModTiles;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {

    public void preinit(FMLPreInitializationEvent event)
    {
        TombManyGraves.file = event.getModConfigurationDirectory().getParentFile();
        FriendHandler.importFriendLists();

        TombManyGravesConfigs.preInit(event);
        ModItems.createItems();
        ModBlocks.preInit();
    }

    public void init(FMLInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(TombManyGraves.MODID, new TombManyGravesGuiHandler());
        ModTiles.init();
        MinecraftForge.EVENT_BUS.register(new TombManyGravesCommonEvents());
        MinecraftForge.EVENT_BUS.register(new FriendHandler());
    }

    public void postinit(FMLPostInitializationEvent event)
    {

    }
}
