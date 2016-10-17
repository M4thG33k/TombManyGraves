package com.m4thg33k.tombmanygraves.core.handlers;

import baubles.api.BaublesApi;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class BaubleHandler {

    public static final String BAUBLES_NBT = "Items";

    public static NBTTagCompound getNBTFromInventory(IInventory inventory)
    {
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagList list = new NBTTagList();

        for (int i=0; i < inventory.getSizeInventory(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack != null)
            {
                NBTTagCompound slot = new NBTTagCompound();
                stack.writeToNBT(slot);
                slot.setInteger("Slot", i);

                list.appendTag(slot);
            }
        }

        compound.setTag("Items", list);
        compound.setInteger("Size", inventory.getSizeInventory());

        return compound;
    }

    public static NBTTagCompound getBaubleNBT(EntityPlayer player)
    {
        IBaublesItemHandler iBaublesItemHandler = BaublesApi.getBaublesHandler(player);
        return getBaubleNBT(iBaublesItemHandler);
    }

    public static NBTTagCompound getBaubleNBT(IBaublesItemHandler baubles)
    {
        NBTTagCompound compound = new NBTTagCompound();

        NBTTagList tagList = new NBTTagList();

        for (int i=0; i<baubles.getSlots(); i++)
        {
            ItemStack stack = baubles.getStackInSlot(i);
            if (stack != null)
            {
                NBTTagCompound slot = new NBTTagCompound();
                stack.writeToNBT(slot);
                slot.setInteger("Slot", i);

                tagList.appendTag(slot);
            }
        }

        compound.setTag(BAUBLES_NBT, tagList);
        compound.setInteger("Size", baubles.getSlots());

        return compound;
    }

    public static IInventory getSavedBaubles(NBTTagCompound compound)
    {
        return getSavedBaubles(compound, new BaublesContainer());
    }

    public static IInventory getSavedBaubles(NBTTagCompound compound, IBaublesItemHandler baubles)
    {
        InventoryBasic basic = new InventoryBasic("SavedBaubles", false, baubles.getSlots());

        NBTTagList tagList = compound.getTagList(BAUBLES_NBT, 10);

        for (int i=0; i < tagList.tagCount(); i++)
        {
            NBTTagCompound slot = (NBTTagCompound)tagList.get(i);
            basic.setInventorySlotContents(slot.getInteger("Slot"), ItemStack.loadItemStackFromNBT(slot));
        }

        return basic;
    }

    public static IInventory getSavedBaubles(NBTTagCompound compound, EntityPlayer player)
    {
        return getSavedBaubles(compound, BaublesApi.getBaublesHandler(player));
    }

    public static IInventory getCurrentBaubles(IBaublesItemHandler baubles)
    {
        InventoryBasic basic = new InventoryBasic("CurrentBaubles", false, baubles.getSlots());

        for (int i=0; i < basic.getSizeInventory(); i++)
        {
            basic.setInventorySlotContents(i, baubles.getStackInSlot(i));
        }

        return basic;
    }

    public static IInventory getCurrentBaubles(EntityPlayer player)
    {
        return getCurrentBaubles(BaublesApi.getBaublesHandler(player));
    }

    public static void setPlayerBaubles(EntityPlayer player, NBTTagCompound compound)
    {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);

        IInventory stored = getSavedBaubles(compound, baubles);

        for (int i=0; i < baubles.getSlots(); i++)
        {
            baubles.setStackInSlot(i, stored.getStackInSlot(i));
        }
    }

    public static void setPlayerBaubles(EntityPlayer player, IInventory inventory)
    {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);

        for (int i=0; i < inventory.getSizeInventory(); i++)
        {
            baubles.setStackInSlot(i, inventory.getStackInSlot(i));
        }
    }

    public static void clearBaubles(EntityPlayer player)
    {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);

        for (int i=0; i < handler.getSlots(); i++)
        {
            handler.setStackInSlot(i, null);
        }
    }

    public static int getNumSlots()
    {
        return new BaublesContainer().getSlots();
    }
}
