package com.m4thg33k.tombmanygraves.core.events;

import com.m4thg33k.tombmanygraves.client.fx.ParticleRenderDispatcher;
import com.m4thg33k.tombmanygraves.client.render.models.GoodGraveModel;
import com.m4thg33k.tombmanygraves.core.util.LogHelper;
import com.m4thg33k.tombmanygraves.core.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TombManyGravesClientEvents {

    private static final ResourceLocation MODEL_grave = Util.getResource("block/grave");
    private static final String LOCATION_grave = Util.resource("DEATH_BLOCK");
    private static final ModelResourceLocation locGrave = new ModelResourceLocation(LOCATION_grave, "normal");

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event)
    {
        try
        {
            IModel model = ModelLoaderRegistry.getModel(MODEL_grave);
            if (model instanceof IRetexturableModel)
            {
                IRetexturableModel graveModel = (IRetexturableModel) model;
                IBakedModel standard = event.getModelRegistry().getObject(locGrave);
                IBakedModel finalModel = new GoodGraveModel(standard, graveModel);
                event.getModelRegistry().putObject(locGrave, finalModel);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void stitchTextures(TextureStitchEvent.Pre pre)
    {
        LogHelper.info("Stitching OBJ textures");
        pre.getMap().registerSprite(new ResourceLocation("tombmanygraves","blocks/red"));
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event)
    {
        Profiler profiler = Minecraft.getMinecraft().mcProfiler;

        profiler.startSection("tmg_particles");
        ParticleRenderDispatcher.dispatch();
        profiler.endSection();
    }



}
