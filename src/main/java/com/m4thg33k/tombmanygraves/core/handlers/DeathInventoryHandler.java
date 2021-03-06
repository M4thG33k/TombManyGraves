package com.m4thg33k.tombmanygraves.core.handlers;

import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.lib.TombManyGravesConfigs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.io.filefilter.PrefixFileFilter;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DeathInventoryHandler {

    public static String FILE_PREFIX = "/TooManyGravesData/savedInventories";

    public static void checkFilePath()
    {
        File file = new File(TombManyGraves.file + FILE_PREFIX);
        if (!file.exists())
        {
            file.mkdirs();
        }
    }

    public static String createDeathInventory(EntityPlayer player, BlockPos gravePos)
    {
        checkFilePath();

        if (!TombManyGravesConfigs.ALLOW_INVENTORY_SAVES)
        {
            return null;
        }

        DeathInventory deathInventory = new DeathInventory(player, gravePos);
        return deathInventory.writeFile(player);
    }

    public static boolean restorePlayerInventory(EntityPlayer player, String timestamp) {
        DeathInventory deathInventory = new DeathInventory(player,null);
        return deathInventory.restoreAll(player, timestamp);
    }

    public static boolean dropPlayerInventory(EntityPlayer player, BlockPos position, String timestamp)
    {
        DeathInventory deathInventory = new DeathInventory(player,null);
        return deathInventory.dropAll(player, position, timestamp);
    }

    public static List<String> getFilenames(String playerName)
    {
        checkFilePath();

        File file = new File(TombManyGraves.file + FILE_PREFIX);
        String[] fileNames = file.list(new PrefixFileFilter(playerName));


        for (int i=0; i < fileNames.length; i++)
        {
            fileNames[i] = fileNames[i].substring(fileNames[i].indexOf("#")+1,fileNames[i].indexOf(".json"));
        }

        return Arrays.asList(fileNames);
    }

    public static boolean getDeathList(EntityPlayer player, String playerName, String timestamp)
    {
        DeathInventory deathInventory = new DeathInventory(player,null);
        return deathInventory.getDeathList(player, playerName, timestamp);
    }
}
