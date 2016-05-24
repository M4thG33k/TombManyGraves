package com.m4thg33k.tombmanygraves.client.render.tiles;

import com.m4thg33k.tombmanygraves.tiles.TileDeathBlock;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.Random;

public class TileDeathBlockRenderer extends TileEntitySpecialRenderer{

    RenderItem itemRenderer;

    public TileDeathBlockRenderer()
    {
        itemRenderer = Minecraft.getMinecraft().getRenderItem();
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {


        TileDeathBlock tileDeathBlock = (TileDeathBlock)te;

        int deathAngle = tileDeathBlock.getAngle();
        boolean isLocked = tileDeathBlock.isLocked();
        boolean renderGround = tileDeathBlock.getRenderGround();
        ItemStack groundType = tileDeathBlock.getGroundMaterial();

        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f,1.0f,1.0f,1.0f);

        GlStateManager.translate(x+0.5,y+0.5,z+0.5);

        ItemStack skull = tileDeathBlock.getSkull();

        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);


        if (renderGround) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(-deathAngle,0,1,0);
            if (isLocked)
            {
                GlStateManager.translate(0,-0.1,0);
                GlStateManager.rotate(-90,1,0,0);
            }
            else
            {
                GlStateManager.rotate(-45,1,0,0);
            }
            GlStateManager.scale(0.75,0.75,0.75);
            GlStateManager.pushAttrib();
            RenderHelper.enableStandardItemLighting();
            itemRenderer.renderItem(skull, ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, -0.25, 0);
            GlStateManager.scale(2, 1, 2);
            itemRenderer.renderItem(groundType, ItemCameraTransforms.TransformType.FIXED);

            GlStateManager.popMatrix();
        }
        else
        {
            Random rand = new Random(te.getPos().hashCode());
            float rotationY = (float) (360.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL) + rand.nextInt(360);
            float rotationX = (float) (360.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL) + rand.nextInt(360);
            float rotationZ = (float) (360.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL) + rand.nextInt(360);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(rotationY, 0, 1, 0);
            GlStateManager.rotate(rotationX, 1, 0, 0);
            GlStateManager.rotate(rotationZ, 0, 0, 1);
            if (isLocked)
            {
                GlStateManager.scale(0.25,0.25,0.25);
            }
            else
            {
                GlStateManager.scale(0.75, 0.75, 0.75);
            }
            GlStateManager.pushAttrib();
            RenderHelper.enableStandardItemLighting();
            itemRenderer.renderItem(skull, ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();

        }
        GlStateManager.popMatrix();

    }
}
