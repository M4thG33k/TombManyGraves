package com.m4thg33k.tombmanygraves.client.render.registers;

import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import com.m4thg33k.tombmanygraves.items.ModItems;
import com.m4thg33k.tombmanygraves.lib.Names;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ItemBlockRegisters {

    public static void registerItemRenders()
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.blockDeath),0,new ModelResourceLocation(TombManyGraves.MODID+":"+ Names.DEATH_BLOCK,"inventory"));

        ModelLoader.setCustomModelResourceLocation(ModItems.itemDeathList, 0, new ModelResourceLocation(TombManyGraves.MODID + ":" + Names.DEATH_LIST, "inventory"));
    }
}
