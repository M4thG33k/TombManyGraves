package com.m4thg33k.tombmanygraves.client.render.player;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;

public class PlayerRender extends RenderPlayer {

    public PlayerRender(RenderManager renderManager, boolean useSmallArms)
    {
        super(renderManager, useSmallArms);

        this.removeLayer(new LayerCustomHead(this.getMainModel().bipedHead));
    }
}
