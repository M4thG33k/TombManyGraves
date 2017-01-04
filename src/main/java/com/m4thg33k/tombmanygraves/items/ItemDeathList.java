package com.m4thg33k.tombmanygraves.items;

import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.core.util.LogHelper;
import com.m4thg33k.tombmanygraves.gui.TombManyGravesGuiHandler;
import com.m4thg33k.tombmanygraves.lib.Names;
import com.m4thg33k.tombmanygraves.lib.TombManyGravesConfigs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.Vector3f;

import javax.vecmath.Vector3d;
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

    //code for this and used methods borrowed lovingly from Yrsegal's Natural Pledge
    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isRemote || !(entityIn instanceof EntityLivingBase) || (((EntityLivingBase) entityIn).getHeldItemMainhand() != stack && ((EntityLivingBase) entityIn).getHeldItemOffhand() != stack))// || !entityIn.isSneaking())
        {
            return;
        }

        if (TombManyGravesConfigs.REQUIRE_SNEAK_FOR_PATH && !entityIn.isSneaking())
        {
            return;
        }

        Vector3f startVector = new Vector3f((float)entityIn.posX, (float)(entityIn.posY - entityIn.getYOffset() /*+ entityIn.height*0.05*/), (float)entityIn.posZ);
        Vector3f dirVector = getDirectionalVector(stack, entityIn, startVector);
        if (dirVector == null || dirVector.length() == 0)
        {
            return;
        }

        Vector3f normed = dirVector.normalise(null);
        float length = Math.min(100, dirVector.length());
        Vector3f endVector = Vector3f.add(new Vector3f(normed.x*length, normed.y*length, normed.z*length), startVector, null);

        TombManyGraves.proxy.particleStream(startVector, endVector);
    }

    private Vector3f getDirectionalVector(ItemStack stack, Entity entity, Vector3f entityPos)
    {
        Vector3f end = getEndVector(stack);

        if (end == null)
        {
            return null;
        }

        return Vector3f.sub(entityPos, end, null);

    }

    private Vector3f getEndVector(ItemStack stack)
    {
        if (stack == null || stack.stackSize == 0 || !stack.hasTagCompound())
        {
            return null;
        }

        NBTTagCompound compound = stack.getTagCompound();

        if (compound.hasKey("Misc"))
        {
            compound = compound.getCompoundTag("Misc");
            float  x = compound.getInteger("x")+0.5f;
            float y = (compound.getInteger("y"))+0.5f;
            float z = (compound.getInteger("z"))+0.5f;

            if (y < 0)
            {
                return null;
            }

            return new Vector3f(x,y,z);
        }

        return null;
    }
}
