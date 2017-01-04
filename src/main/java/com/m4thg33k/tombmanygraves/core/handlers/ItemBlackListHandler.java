package com.m4thg33k.tombmanygraves.core.handlers;

import com.m4thg33k.tombmanygraves.core.util.LogHelper;
import com.m4thg33k.tombmanygraves.lib.TombManyGravesConfigs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;

public class ItemBlackListHandler {

    private HashMap<String, Set<NBTTagCompound>> blacklistedData = new HashMap<>();

    public ItemBlackListHandler()
    {
        List<String> blacklistedStrings = TombManyGravesConfigs.BLACKLISTED_ITEMS_AS_STRINGS;

        for (String string : blacklistedStrings)
        {
            int colon = string.indexOf(":");
            if (colon == -1)
            {
                giveWarning(string);
                continue;
            }

            String modName = string.substring(0,colon);
            String remainder = string.substring(colon+1,string.length());
            if (remainder.length() == 0)
            {
                giveWarning(string);
                continue;
            }

            NBTTagCompound compound = new NBTTagCompound();

            //check if metadata is given
            int meta = remainder.indexOf("#");
            if (meta != -1)
            {
                String itemName = remainder.substring(0,meta);
                try {
                    int metaVal = Integer.parseInt(remainder.substring(meta + 1, remainder.length()));
                    compound.setString("Name", itemName);
                    compound.setInteger("Meta", metaVal);
                } catch (Exception e)
                {
                    giveWarning(string);
                    continue;
                }
            }
            else
            {
                compound.setString("Name", remainder);
            }

            if (!blacklistedData.containsKey(modName))
            {
                blacklistedData.put(modName, new HashSet<NBTTagCompound>());
            }

            blacklistedData.get(modName).add(compound);
        }
    }

    private void giveWarning(String string)
    {
        LogHelper.warn("The blacklisted item [" + string + "] is not properly formatted!");
    }

    public boolean isBlacklisted(ItemStack stack)
    {
        if (stack == null || stack.stackSize == 0)
        {
            return false;
        }

        String modName = stack.getItem().getRegistryName().getResourceDomain();
        if (!blacklistedData.containsKey(modName))
        {
            return false;
        }

        for (NBTTagCompound compound : blacklistedData.get(modName))
        {
            if (compound.getString("Name").equals(stack.getItem().getRegistryName().getResourcePath()))
            {
                if (!compound.hasKey("Meta"))
                {
                    return true;
                }

                if (compound.getInteger("Meta") == stack.getItemDamage())
                {
                    return true;
                }
            }
        }

        return false;
    }
}
