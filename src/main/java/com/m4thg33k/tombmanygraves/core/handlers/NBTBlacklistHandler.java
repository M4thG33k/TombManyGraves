package com.m4thg33k.tombmanygraves.core.handlers;

import com.m4thg33k.tombmanygraves.core.util.NBTBlacklistItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;

public class NBTBlacklistHandler {

    private static List<NBTBlacklistItem> blacklist = new ArrayList<>();

    public static void generateBlacklist(List<String> strings)
    {
        for (String string : strings)
        {
            NBTBlacklistItem item = new NBTBlacklistItem(string);
            if (item.isStillValid())
            {
                blacklist.add(item);
            }
        }

        for (NBTBlacklistItem item : blacklist)
        {
            item.printData();
        }
    }

    public static boolean itemInList(ItemStack stack)
    {
        if (stack == null || stack.stackSize == 0 || !stack.hasTagCompound())
        {
            return false;
        }

        NBTTagCompound compound = stack.getTagCompound();

        for (NBTBlacklistItem item : blacklist)
        {
            int index = 0;
            List<String> names = item.getNames();
            List<NBTBase> data = item.getData();

            NBTBase iterator = null;

            if (compound.hasKey(names.get(index)))
            {
                iterator = compound.getCompoundTag(names.get(index));
                if (iterator.getId() != data.get(index).getId())
                {
                    continue;
                }
            }

            index += 1;

            while (index < names.size())
            {
                if (iterator == null)
                {
                    break;
                }
                else if (iterator.getId() == 9 && ((NBTTagList)iterator).getTagType() == data.get(index).getId())
                {
                    NBTTagList theList = (NBTTagList)iterator;

                    for (int j=0; j < theList.tagCount(); j++)
                    {
                        NBTBase listItem = theList.get(j);
                    }
                }
            }
        }
        //abandoned for now (not really useful at the moment)
        return false;
    }

}
