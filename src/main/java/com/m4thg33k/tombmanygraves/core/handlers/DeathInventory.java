package com.m4thg33k.tombmanygraves.core.handlers;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.core.util.ChatHelper;
import com.m4thg33k.tombmanygraves.items.ModItems;
import com.m4thg33k.tombmanygraves.tiles.TileDeathBlock;
import com.sun.istack.internal.Nullable;
import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.inventory.InventoryCosArmor;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DeathInventory {

    private NBTTagCompound allNBT;

    public DeathInventory(EntityPlayer player, @Nullable BlockPos pos) {
        allNBT = new NBTTagCompound();

        NBTTagList tagList = new NBTTagList();



        InventoryPlayer inventoryToWrite = TileDeathBlock.getInventorySansSoulbound(player.inventory, false);
        inventoryToWrite.writeToNBT(tagList);
//      player.inventory.writeToNBT(tagList);
        allNBT.setTag("Main", tagList);

        NBTTagCompound baublesNBT = new NBTTagCompound();
        if (TombManyGraves.isBaublesInstalled) {
            PlayerHandler.getPlayerBaubles(player).saveNBT(baublesNBT);
        }
        allNBT.setTag("Baubles", baublesNBT);

        NBTTagCompound cosmeticNBT = new NBTTagCompound();
        if (TombManyGraves.isCosmeticArmorInstalled)
        {
            CosmeticArmorReworked.invMan.getCosArmorInventory(player.getUniqueID()).writeToNBT(cosmeticNBT);
        }
        allNBT.setTag("Cosmetic", cosmeticNBT);

        NBTTagCompound miscNBT = new NBTTagCompound();
        boolean flag = pos == null;
        miscNBT.setInteger("x",flag ? -1 : pos.getX());
        miscNBT.setInteger("y",flag ? -1 : pos.getY());
        miscNBT.setInteger("z",flag ? -1 : pos.getZ());
        allNBT.setTag("Misc",miscNBT);
    }

    public static boolean writePortion(String fileName,String toWrite)
    {
        boolean didWork = true;

        try (FileWriter file = new FileWriter(fileName))
        {
            file.write(toWrite);
            file.close();
        }
        catch (Exception e)
        {
//            e.printStackTrace();
            didWork = false;
        }

        return didWork;
    }

    public String writeFile(EntityPlayer player)
    {
        boolean didWork;

        String filename = "/" + player.getName();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String filePostfix =  timeStamp + ".json";

        allNBT.getCompoundTag("Misc").setString("Timestamp",timeStamp);


        String fullFileName = TombManyGraves.file + DeathInventoryHandler.FILE_PREFIX + filename + "#" + filePostfix;

        didWork = writePortion(TombManyGraves.file + DeathInventoryHandler.FILE_PREFIX + filename + "#" + filePostfix,allNBT.toString());

        if (didWork)
        {
            try {
                Path from = Paths.get(fullFileName);
                Path to = Paths.get(TombManyGraves.file + DeathInventoryHandler.FILE_PREFIX + filename + "#" + "latest.json");
                Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return timeStamp;
        }
//        didWork = writePortion(TombManyGraves.file + DeathInventoryHandler.FILE_PREFIX + filename + "_inventory_" + filePostfix,mainNBT.toString());
//        didWork = didWork && writePortion(TombManyGraves.file + DeathInventoryHandler.FILE_PREFIX + filename + "_baubles_" + filePostfix,baublesNBT.toString());

        return null;
    }

    public static void clearLatest(EntityPlayer player)
    {
        String filename = "/" + player.getName();
        String timeStamp = "latest";
        String filePostfix = timeStamp + ".json";

        String fullFileName = TombManyGraves.file + DeathInventoryHandler.FILE_PREFIX + filename + "#" + filePostfix;
        writePortion(fullFileName, "{}");
    }

    public boolean dropAll(EntityPlayer player, BlockPos position, String timestamp)
    {
        boolean didWork = true;

        String filename = TombManyGraves.file + DeathInventoryHandler.FILE_PREFIX + "/" + player.getName() + "#" + timestamp + ".json";

        BufferedReader reader;

        try
        {
            reader = new BufferedReader(new FileReader(filename));
            String fileData = reader.readLine();
            allNBT = JsonToNBT.getTagFromJson(fileData);
            InventoryPlayer inventoryPlayer = new InventoryPlayer(player);
            inventoryPlayer.readFromNBT(allNBT.getTagList("Main",10));
            InventoryHelper.dropInventoryItems(player.worldObj, position, inventoryPlayer);

            if (TombManyGraves.isBaublesInstalled)
            {
                InventoryBaubles inventoryBaubles = new InventoryBaubles(player);
                inventoryBaubles.readNBT(allNBT.getCompoundTag("Baubles"));
                InventoryHelper.dropInventoryItems(player.worldObj, position, inventoryBaubles);
            }

            if (TombManyGraves.isCosmeticArmorInstalled)
            {
                InventoryCosArmor cosArmor = new InventoryCosArmor();
                cosArmor.readFromNBT(allNBT.getCompoundTag("Cosmetic"));
                InventoryHelper.dropInventoryItems(player.worldObj, position, cosArmor);
            }
            reader.close();
        }
        catch (Exception e)
        {
//            e.printStackTrace();
            didWork = false;
        }

        return didWork;
    }

    public boolean restoreAll(EntityPlayer player, String timestamp)
    {
        boolean didWork = true;

        String filename = TombManyGraves.file + DeathInventoryHandler.FILE_PREFIX + "/" + player.getName() + "#" + timestamp + ".json";

        BufferedReader reader;

        try
        {
            reader = new BufferedReader(new FileReader(filename));
            String fileData = reader.readLine();
            allNBT = JsonToNBT.getTagFromJson(fileData);
            player.inventory.readFromNBT(allNBT.getTagList("Main",10));

            if (TombManyGraves.isBaublesInstalled)
            {
                InventoryBaubles inventoryBaubles = new InventoryBaubles(player);
                inventoryBaubles.readNBT(allNBT.getCompoundTag("Baubles"));
                PlayerHandler.setPlayerBaubles(player,inventoryBaubles);
            }

            if (TombManyGraves.isCosmeticArmorInstalled)
            {
                InventoryCosArmor cosArmor = new InventoryCosArmor();
                cosArmor.readFromNBT(allNBT.getCompoundTag("Cosmetic"));
                InventoryCosArmor playerCos = CosmeticArmorReworked.invMan.getCosArmorInventory(player.getUniqueID());
                for (int i=0; i < ((IInventory)playerCos).getSizeInventory(); i++)
                {
                    ((IInventory)playerCos).setInventorySlotContents(i, ((IInventory)cosArmor).getStackInSlot(i));
                }
            }


            reader.close();
        }
        catch (Exception e)
        {
//            e.printStackTrace();
            didWork = false;
        }

        return didWork;
    }

    public boolean getDeathList(EntityPlayer player, String playerName, String timestamp)
    {
        boolean didWork = true;

        String filename = TombManyGraves.file + DeathInventoryHandler.FILE_PREFIX + "/" + playerName + "#" + timestamp + ".json";

        BufferedReader reader;

        try
        {
            reader = new BufferedReader(new FileReader(filename));
            String fileData = reader.readLine();
            allNBT = JsonToNBT.getTagFromJson(fileData);
            if (allNBT.getKeySet().size() > 0) {
                ItemStack theList = new ItemStack(ModItems.itemDeathList, 1);
                theList.setTagCompound(allNBT);
                EntityItem entityItem = new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, theList);
                player.worldObj.spawnEntityInWorld(entityItem);
            }
            else
            {
                ChatHelper.sayMessage(player.worldObj, player, playerName + " had no items upon death!");
            }
            reader.close();
        }
        catch (Exception e)
        {
//            e.printStackTrace();
            didWork = false;
        }

        return didWork;
    }
}
