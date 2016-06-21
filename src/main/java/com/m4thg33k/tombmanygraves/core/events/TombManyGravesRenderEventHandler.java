package com.m4thg33k.tombmanygraves.core.events;

import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import com.m4thg33k.tombmanygraves.core.util.LogHelper;
import com.m4thg33k.tombmanygraves.lib.TombManyGravesConfigs;
import com.m4thg33k.tombmanygraves.tiles.TileDeathBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TombManyGravesRenderEventHandler {

    private static TombManyGravesRenderEventHandler INSTANCE;
    public Minecraft mc;

    public TombManyGravesRenderEventHandler()
    {
        this.mc = Minecraft.getMinecraft();
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onBlockHilight(DrawBlockHighlightEvent event)
    {
        if (!TombManyGravesConfigs.DISPLAY_GRAVE_NAME)
        {
            return;
        }
        RayTraceResult trace = event.getTarget();
        if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            World world = mc.theWorld;
            IBlockState state = world.getBlockState(trace.getBlockPos());

            if (state.getBlock() == ModBlocks.blockDeath)
            {
                TileEntity tileEntity = world.getTileEntity(trace.getBlockPos());
                if (tileEntity != null && tileEntity instanceof TileDeathBlock)
                {
                    String name = ((TileDeathBlock) tileEntity).getPlayerName();
                    boolean giveGravePriority = ((TileDeathBlock) tileEntity).areGraveItemsForced();
                    this.renderPlayerName(trace.getBlockPos(), event.getPartialTicks(), name, giveGravePriority);
                }
            }
        }
    }

    private void renderPlayerName(BlockPos pos, float partialTicks, String name, boolean giveGravePriority)
    {
        if (!name.equals("") && name.length() > 0)
        {
            GlStateManager.alphaFunc(516, 0.1f);
            renderPlayerName(name, this.mc.thePlayer, pos, partialTicks, giveGravePriority);
        }
    }

    private void renderPlayerName(String name, EntityPlayer player, BlockPos pos, float partialTicks, boolean giveGravePriority)
    {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        float angleH = player.rotationYawHead;
        float angleV = 0f;


        this.renderLabel(name, x - dx, y -dy, z - dz, angleH, angleV, giveGravePriority);
        if (giveGravePriority)
        {
            this.renderLabel("swap",x-dx, y - dy - 0.25, z - dz, angleH, angleV, giveGravePriority);
        }
    }

    protected void renderLabel(String name, double x, double y, double z, float angleH, float angleV, boolean giveGravePriority)
    {
        FontRenderer fontRenderer = this.mc.fontRendererObj;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x+0.5, y+1.5, z+0.5);
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-angleH, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-angleV, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-0.025f, -0.025f, 0.025f);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture2D();

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexBuffer = tessellator.getBuffer();
        int strLenHalved = fontRenderer.getStringWidth(name) / 2;

        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexBuffer.pos(-strLenHalved - 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        vertexBuffer.pos(-strLenHalved - 1,  8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        vertexBuffer.pos( strLenHalved + 1,  8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        vertexBuffer.pos( strLenHalved + 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        tessellator.draw();

        GlStateManager.enableTexture2D();

        fontRenderer.drawString(name, -strLenHalved, 0, giveGravePriority ? 0xFFFFFFFF : 0x20FFFFFF);
        GlStateManager.enableDepth();

        GlStateManager.depthMask(true);
        fontRenderer.drawString(name, -strLenHalved, 0, giveGravePriority ? 0x00FFFFFF : 0xFF870000);

        GlStateManager.disableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        GlStateManager.popMatrix();
    }
}
