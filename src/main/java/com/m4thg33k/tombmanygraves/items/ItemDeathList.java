package com.m4thg33k.tombmanygraves.items;

import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.gui.TombManyGravesGuiHandler;
import com.m4thg33k.tombmanygraves.lib.Names;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class ItemDeathList extends Item {

    public ItemDeathList()
    {
        super();

        this.setUnlocalizedName(Names.DEATH_LIST);

        this.setMaxStackSize(1);
        this.setRegistryName(TombManyGraves.MODID, Names.DEATH_LIST);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        playerIn.openGui(TombManyGraves.INSTANCE, TombManyGravesGuiHandler.DEATH_ITEMS_GUI, worldIn, playerIn.getPosition().getX(), playerIn.getPosition().getY(), playerIn.getPosition().getZ());
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        boolean isShifted = Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
        boolean isControlled = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);

        if (isShifted) {
            tooltip.add(TextFormatting.GOLD + "Right-click to view a list of everything");
            tooltip.add(TextFormatting.GOLD + "you had on you when you died.");
            tooltip.add(TextFormatting.RED + "Drop from your inventory to destroy");
        }
        else {
            tooltip.add(TextFormatting.ITALIC + "<Shift for explanation>");
        }

        if (isControlled)
        {
            tooltip.add(TextFormatting.BLUE + "\"/tmg_deathlist [player] latest\"");
            tooltip.add(TextFormatting.BLUE + "will give you a list of everything");
            tooltip.add(TextFormatting.BLUE + "from before your last death");
        }
        else
        {
            tooltip.add(TextFormatting.ITALIC + "<Control for command>");
        }
    }
}
