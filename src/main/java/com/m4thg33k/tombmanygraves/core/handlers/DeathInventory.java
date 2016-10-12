package com.m4thg33k.tombmanygraves.core.handlers;

import baubles.api.BaublesApi;
import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.core.util.ChatHelper;
import com.m4thg33k.tombmanygraves.items.ModItems;
import com.m4thg33k.tombmanygraves.lib.TombManyGravesConfigs;
import com.m4thg33k.tombmanygraves.tiles.TileDeathBlock;
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
import thut.wearables.inventory.PlayerWearables;
import thut.wearables.inventory.WearableHandler;

import java.io.BufferedReader;
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

    public DeathInventory(EntityPlayer player, BlockPos pos) {
        allNBT = new NBTTagCompound();

        NBTTagList tagList = new NBTTagList();



        InventoryPlayer inventoryToWrite = TileDeathBlock.getInventorySansSoulbound(player.inventory, false);
        if (TombManyGravesConfigs.ALLOW_MAIN_INVENTORY) {
            inventoryToWrite.writeToNBT(tagList);
        }
//      player.inventory.writeToNBT(tagList);
        allNBT.setTag("Main", tagList);

        NBTTagCompound baublesNBT = new NBTTagCompound();
        if (TombManyGraves.isBaublesInstalled && TombManyGravesConfigs.ALLOW_BAUBLES) {
            baublesNBT = TileDeathBlock.getBaublesNBTSansSoulbound(player, false);
//            baublesNBT = BaubleHandler.getBaubleNBT(BaublesApi.getBaublesHandler(player));
//            PlayerHandler.getPlayerBaubles(player).saveNBT(baublesNBT);
        }
        allNBT.setTag("Baubles", baublesNBT);

        NBTTagCompound cosmeticNBT = new NBTTagCompound();
        if (TombManyGraves.isCosmeticArmorInstalled && TombManyGravesConfigs.ALLOW_COSMETIC_ARMOR)
        {
            cosmeticNBT = TileDeathBlock.getCosmeticNBTSansSoulbound(player, false);
//            CosmeticArmorReworked.invMan.getCosArmorInventory(player.getUniqueID()).writeToNBT(cosmeticNBT);
        }
        allNBT.setTag("Cosmetic", cosmeticNBT);

        NBTTagCompound expandableBackpackNBT = new NBTTagCompound();
        if (TombManyGraves.isExpandableBackpacksInstalled && TombManyGravesConfigs.ALLOW_EXPANDABLE_BACKPACKS)
        {
            expandableBackpackNBT = TileDeathBlock.getExpandableBackpackNBTSansSoulbound(player, false);
        }
        allNBT.setTag("ExpandableBackpack",expandableBackpackNBT);

        NBTTagCompound eydamosBackpackNBT = new NBTTagCompound();
        if (TombManyGraves.isEydamosBackpacksInstalled && TombManyGravesConfigs.ALLOW_EYDAMOS_BACKPACKS)
        {
            eydamosBackpackNBT = TileDeathBlock.getEydamosBackpackNBTSansSoulbound(player, false);
        }
        allNBT.setTag("EydamosBackpack", eydamosBackpackNBT);

        NBTTagCompound thutNBT = new NBTTagCompound();
        if (TombManyGraves.isThutWearablesInstalled && TombManyGravesConfigs.ALLOW_THUT_WEARABLES)
        {
            thutNBT = TileDeathBlock.getThutNBTSansSoulbound(player, false);
        }
        allNBT.setTag("ThutWearables", thutNBT);

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
                IInventory inventoryBaubles = BaubleHandler.getSavedBaubles(allNBT.getCompoundTag("Baubles"), BaublesApi.getBaublesHandler(player));
//                InventoryBaubles inventoryBaubles = new InventoryBaubles(player);
//                inventoryBaubles.readNBT(allNBT.getCompoundTag("Baubles"));
                InventoryHelper.dropInventoryItems(player.worldObj, position, inventoryBaubles);
            }

            if (TombManyGraves.isCosmeticArmorInstalled)
            {
                InventoryCosArmor cosArmor = new InventoryCosArmor();
                cosArmor.readFromNBT(allNBT.getCompoundTag("Cosmetic"));
                InventoryHelper.dropInventoryItems(player.worldObj, position, cosArmor);
            }

            if (TombManyGraves.isExpandableBackpacksInstalled)
            {
                ItemStack stack = ItemStack.loadItemStackFromNBT(allNBT.getCompoundTag("ExpandableBackpack"));
                if (stack!=null && stack.stackSize > 0)
                {
                    player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, position.getX(), position.getY(), position.getZ(), stack));
                }
            }
            if (TombManyGraves.isEydamosBackpacksInstalled)
            {
                ItemStack stack = ItemStack.loadItemStackFromNBT(allNBT.getCompoundTag("EydamosBackpack"));
                if (stack!=null && stack.stackSize > 0)
                {
                    player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, position.getX(), position.getY(), position.getZ(), stack));
                }
            }
            if (TombManyGraves.isThutWearablesInstalled)
            {
                PlayerWearables playerWearables = new PlayerWearables();
                playerWearables.readFromNBT(allNBT.getCompoundTag("ThutWearables"));
                InventoryHelper.dropInventoryItems(player.worldObj, position, playerWearables);
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
                BaubleHandler.setPlayerBaubles(player,allNBT.getCompoundTag("Baubles") );
//                InventoryBaubles inventoryBaubles = new InventoryBaubles(player);
//                inventoryBaubles.readNBT(allNBT.getCompoundTag("Baubles"));
//                PlayerHandler.setPlayerBaubles(player,inventoryBaubles);
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
            if (TombManyGraves.isExpandableBackpacksInstalled)
            {
                ItemStack stack = ItemStack.loadItemStackFromNBT(allNBT.getCompoundTag("ExpandableBackpack"));
                if (stack!=null && stack.stackSize > 0)
                {
                    player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, stack));
                }
            }
            if (TombManyGraves.isEydamosBackpacksInstalled)
            {
                ItemStack stack = ItemStack.loadItemStackFromNBT(allNBT.getCompoundTag("EydamosBackpack"));
                if (stack!=null && stack.stackSize > 0)
                {
                    player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, stack));
                }
            }

            if (TombManyGraves.isThutWearablesInstalled)
            {
//                PlayerWearables playerWearables = new PlayerWearables();
//                playerWearables.readFromNBT(allNBT.getCompoundTag("ThutWearables"));

                ((IInventory)WearableHandler.getInstance().getPlayerData(player)).clear();
                WearableHandler.getInstance().getPlayerData(player).readFromNBT(allNBT.getCompoundTag("ThutWearables"));
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
