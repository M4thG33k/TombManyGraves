package com.m4thg33k.tombmanygraves.blocks.itemblocks;

import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.lib.Names;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public class DeathItemBlock extends ItemBlock {

    public DeathItemBlock(Block block)
    {
        super(block);
        this.setMaxDamage(0);

        this.setRegistryName(TombManyGraves.MODID, Names.DEATH_BLOCK);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return EnumActionResult.FAIL;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        tooltip.add(TextFormatting.RED + "You should not have this block. Unless you cheated it in.");
        tooltip.add(TextFormatting.RED + "Please report this to M4thG33k otherwise.");
        tooltip.add(TextFormatting.ITALIC + "Cannot be placed in the world!");
        tooltip.add(TextFormatting.GOLD + "" + TextFormatting.ITALIC + "Throw from your inventory to delete.");
    }
}
