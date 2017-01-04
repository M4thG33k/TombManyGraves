package com.m4thg33k.tombmanygraves.core.util;

import com.google.common.primitives.Bytes;
import net.minecraft.nbt.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NBTBlacklistItem {

    private List<String> names = new ArrayList<>();
    private List<NBTBase> data = new ArrayList<>();
    private boolean stillValid = true;

    public NBTBlacklistItem(String string)
    {
        Pattern pattern = Pattern.compile("([^,]+)");

        Matcher matcher = pattern.matcher(string);

        int type;
        String name;

        while (stillValid && matcher.find())
        {
            String match = matcher.group();


            try {
                type = Integer.parseInt(match);
                matcher.find();
                name = matcher.group();
            } catch (Exception e)
            {
                stillValid = false;
                LogHelper.warn("Invalid NBT path: " + string);
                return;
            }

            NBTBase base = null;

            switch (type)
            {
                case 1:
                    if (matcher.find())
                    {
                        int value = getInteger(matcher, string);
                        base = new NBTTagByte((byte)value);
                    }
                    break;
                case 2:
                    if (matcher.find())
                    {
                        int value = getInteger(matcher, string);
                        base = new NBTTagShort((short)value);
                    }
                    break;
                case 3:
                    if (matcher.find())
                    {
                        int value = getInteger(matcher, string);
                        base = new NBTTagInt(value);
                    }
                    break;
                case 4:
                    if (matcher.find())
                    {
                        long value = getLong(matcher, string);
                        base = new NBTTagLong(value);
                    }
                    break;
                case 5:
                    if (matcher.find())
                    {
                        float value = getFloat(matcher, string);
                        base = new NBTTagFloat(value);
                    }
                    break;
                case 6:
                    if (matcher.find())
                    {
                        double value = getDouble(matcher, string);
                        base = new NBTTagDouble(value);
                    }
                    break;
                case 7:
                    if (matcher.find())
                    {
                        byte[] value = getByteArray(matcher, string);
                        base = new NBTTagByteArray(value);
                    }
                    break;
                case 8:
                    base = new NBTTagString(name);
                    break;
                case 9:
                    base = new NBTTagList();
                    break;
                case 10:
                    base = new NBTTagCompound();
                    break;
                case 11:
                    if (matcher.find())
                    {
                        int[] value = getIntArray(matcher, string);
                        base = new NBTTagIntArray(value);
                    }
                    break;
                default:
                    base = null;
                    break;
            }

            if (stillValid)
            {
                names.add(name);
                data.add(base);
            }

        }

    }

    public void printData()
    {
        LogHelper.info(names.toString());
        LogHelper.info(data.toString());
    }

    private int getInteger(Matcher matcher, String string)
    {
        try {
            return Integer.parseInt(matcher.group());
        }
        catch (Exception e)
        {
            LogHelper.warn("Invalid NBT path: " + string);
            stillValid = false;
            return 0;
        }
    }

    private long getLong(Matcher matcher, String string)
    {
        try {
            return Long.parseLong(matcher.group());
        } catch (Exception e)
        {
            LogHelper.warn("Invalid NBT path: " + string);
            stillValid = false;
            return 0;
        }
    }

    private float getFloat(Matcher matcher, String string)
    {
        try {
            return Float.parseFloat(matcher.group());
        } catch (Exception e)
        {
            LogHelper.warn("Invalid NBT path: " + string);
            stillValid = false;
            return 0;
        }
    }

    private double getDouble(Matcher matcher, String string)
    {
        try {
            return Double.parseDouble(matcher.group());
        } catch (Exception e)
        {
            LogHelper.warn("Invalid NBT path: " + string);
            stillValid = false;
            return 0;
        }
    }

    private byte[] getByteArray(Matcher matcher, String string)
    {
        try {
            int length = getInteger(matcher, string);

            List<Byte> bytes = new ArrayList<>();

            for (int i=0; i<length; i++)
            {
                if (!matcher.find())
                {
                    stillValid = false;
                    return new byte[]{};
                }
                bytes.add((byte)getInteger(matcher, string));
            }

            return Bytes.toArray(bytes);
        } catch (Exception e)
        {
            LogHelper.warn("Invalid NBT path: " + string);
            stillValid = false;
            return new byte[]{};
        }
    }

    private String getString(Matcher matcher, String string)
    {
        try
        {
            return matcher.group();
        } catch (Exception e)
        {
            LogHelper.warn("Invalid NBT path: " + string);
            stillValid = false;
            return "ERROR";
        }
    }


    private int[] getIntArray(Matcher matcher, String string)
    {
        try
        {
            int length = getInteger(matcher, string);

            List<Integer>  integers = new ArrayList<>();

            for (int i=0; i < length; i++)
            {
                if (!matcher.find())
                {
                    stillValid = false;
                    return new int[]{};
                }

                integers.add(getInteger(matcher, string));
            }

            return integers.stream().mapToInt(i->i).toArray();
        } catch (Exception e)
        {
            LogHelper.warn("Invalid NBT path: " + string);
            stillValid = false;
            return new int[]{};
        }
    }

    public boolean isStillValid()
    {
        return stillValid;
    }

    public List<String> getNames()
    {
        return names;
    }

    public List<NBTBase> getData()
    {
        return data;
    }

}
