package com.m4thg33k.tombmanygraves.core.proxy;

import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import com.m4thg33k.tombmanygraves.client.render.ModRenders;
import com.m4thg33k.tombmanygraves.client.render.registers.ItemBlockRegisters;
import com.m4thg33k.tombmanygraves.core.events.TombManyGravesClientEvents;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preinit(FMLPreInitializationEvent event) {
        super.preinit(event);
        OBJLoader.INSTANCE.addDomain(TombManyGraves.MODID);
        ItemBlockRegisters.registerItemRenders();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        ModRenders.init();
    }

    @Override
    public void postinit(FMLPostInitializationEvent event) {
        super.postinit(event);
        MinecraftForge.EVENT_BUS.register(new TombManyGravesClientEvents());
    }

}
