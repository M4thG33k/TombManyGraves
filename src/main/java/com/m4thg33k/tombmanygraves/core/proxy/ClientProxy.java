package com.m4thg33k.tombmanygraves.core.proxy;

import com.m4thg33k.tombmanygraves.client.render.ModRenders;
import com.m4thg33k.tombmanygraves.client.render.registers.ItemBlockRegisters;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preinit(FMLPreInitializationEvent event) {
        super.preinit(event);
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
    }
}
