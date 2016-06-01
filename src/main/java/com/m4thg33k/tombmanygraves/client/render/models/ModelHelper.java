package com.m4thg33k.tombmanygraves.client.render.models;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.init.Blocks;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.model.TRSRTransformation;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ModelHelper {

    public static OBJModel loadModel(String suffix){
        OBJModel model;

        try {
            model = ((OBJModel) OBJLoader.INSTANCE.loadModel(new ResourceLocation("tombmanygraves:models/block/" + suffix + ".obj")));
            return model;
        } catch (Exception e)
        {
            throw new ReportedException(new CrashReport("Error making the model for " + suffix + "!", e));
        }
    }

    public static IModel retexture(OBJModel model, String toReplace, String suffix)
    {
        return (((OBJModel) model.retexture(ImmutableMap.of(toReplace, "tombmanygraves:blocks/" + suffix))).process(ImmutableMap.of("flip-v","true")));
    }

    public static IModel retexture(OBJModel model, ImmutableMap<String, String> map)
    {
        return (((OBJModel) model.retexture(map)).process(ImmutableMap.of("flip-v","true")));
    }

    public static IBakedModel bake(IModel model)
    {
        return model.bake(TRSRTransformation.identity(), Attributes.DEFAULT_BAKED_FORMAT, ModelLoader.defaultTextureGetter());
    }

    public static void renderModel(IBakedModel model, int color)
    {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
//        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
//        GlStateManager.disableLighting();

        List<BakedQuad> quads = model.getQuads(null,null,0);

        for (BakedQuad quad : quads)
        {
            LightUtil.renderQuadColor(buffer, quad, color);
        }

        tessellator.draw();
    }

    public static TextureAtlasSprite getTextureFromBlockstate(IBlockState state)
    {
        if (state == null)
        {
            state = Blocks.DIRT.getDefaultState();
        }
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
        return sprite;
//        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
    }
}
