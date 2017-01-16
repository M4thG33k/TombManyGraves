package com.m4thg33k.tombmanygraves.core.proxy;

import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import com.m4thg33k.tombmanygraves.client.fx.PathFX;
import com.m4thg33k.tombmanygraves.client.render.ModRenders;
import com.m4thg33k.tombmanygraves.client.render.registers.ItemBlockRegisters;
import com.m4thg33k.tombmanygraves.core.events.TombManyGravesClientEvents;
import com.m4thg33k.tombmanygraves.core.events.TombManyGravesRenderEventHandler;
import com.m4thg33k.tombmanygraves.core.util.LogHelper;
import com.m4thg33k.tombmanygraves.lib.TombManyGravesConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;

public class ClientProxy extends CommonProxy {

    private Color NEAR;
    private Color FAR;

    @Override
    public void preinit(FMLPreInitializationEvent event) {
        super.preinit(event);
        OBJLoader.INSTANCE.addDomain(TombManyGraves.MODID);
        ItemBlockRegisters.registerItemRenders();

        NEAR = TombManyGravesConfigs.NEAR_PARTICLE;
        FAR = TombManyGravesConfigs.FAR_PARTICLE;
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
        MinecraftForge.EVENT_BUS.register(new TombManyGravesRenderEventHandler());
    }

    @Override
    public void particleStream(Vector3f start, Vector3f end) {
        Vector3f diff = Vector3f.sub(start, end, null);
        float length = diff.length();
        float scale = diff.length() < 5 ? 10 : diff.length();
        Vector3f motion = new Vector3f(diff.x/scale, diff.y/scale, diff.z/scale);

        float[] color = getParticleColor(length);
        TombManyGraves.proxy.pathFX(start.x, start.y, start.z, color[0]/255.0f, color[1]/255.0f, color[2]/255.0f, 0.4f, motion.x, motion.y, motion.z, 1f);

//        float r = (length < 10 ? 1 : (length > 100 ? 0 : (-length/90f + 10/9f)));  //color.getRed() / 255f;
//        float g = (length < 10 ? 1 : (length > 100 ? 0 : (-length/90f + 10/9f)));//color.getGreen() / 255f;
//        float b = (length < 10 ? 1 : (length > 100 ? 0 : (-length/90f + 10/9f)));//color.getBlue() / 255f;

//        TombManyGraves.proxy.pathFX(start.x, start.y, start.z, r, g, b, 0.4f, motion.x, motion.y, motion.z, 1f);

    }

    private float[] getParticleColor(float length)
    {
        float[] ret = new float[3];
        if (length < 10)
        {
            ret[0] = NEAR.getRed();
            ret[1] = NEAR.getGreen();
            ret[2] = NEAR.getBlue();
        }
        else if (length > 100)
        {
            ret[0] = FAR.getRed();
            ret[1] = FAR.getGreen();
            ret[2] = FAR.getBlue();
        }
        else
        {
            ret[0] = intermediateValue(length, FAR.getRed(), NEAR.getRed());
            ret[1] = intermediateValue(length, FAR.getGreen(), NEAR.getGreen());
            ret[2] = intermediateValue(length, FAR.getBlue(), NEAR.getBlue());
        }

        return ret;
    }

    private int intermediateValue(float length, int far, int near)
    {
        return (int)((far-near)*length/90.0 + (10*near - far)/9.0);
    }

    @Override
    public void pathFX(double x, double y, double z, float r, float g, float b, float size, float motionx, float motiony, float motionz, float maxAge) {
        if (!TombManyGravesConfigs.ALLOW_PARTICLE_PATH)
        {
            return;
        }

//        LogHelper.info("Spawning particle at: " + x + "," + y + "," + z);
        PathFX path = new PathFX(Minecraft.getMinecraft().theWorld, x, y, z, size, r, g, b, true, false, maxAge);
        path.setSpeed(motionx, motiony, motionz);
        Minecraft.getMinecraft().effectRenderer.addEffect(path);
    }
}
