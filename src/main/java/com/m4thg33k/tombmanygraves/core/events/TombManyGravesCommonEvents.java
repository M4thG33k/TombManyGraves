package com.m4thg33k.tombmanygraves.core.events;

import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import com.m4thg33k.tombmanygraves.core.handlers.DeathInventory;
import com.m4thg33k.tombmanygraves.core.handlers.DeathInventoryHandler;
import com.m4thg33k.tombmanygraves.core.util.ChatHelper;
import com.m4thg33k.tombmanygraves.core.util.LogHelper;
import com.m4thg33k.tombmanygraves.items.ModItems;
import com.m4thg33k.tombmanygraves.lib.TombManyGravesConfigs;
import com.m4thg33k.tombmanygraves.tiles.TileDeathBlock;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class TombManyGravesCommonEvents {

    private static final int MAX_RADIUS = TombManyGravesConfigs.GRAVE_RANGE;

    public TombManyGravesCommonEvents()
    {

    }


//    @SubscribeEvent(priority = EventPriority.NORMAL)
//    public void handleDeathDrops(PlayerDropsEvent event)
//    {
//        boolean flag1 = event.getEntityPlayer().worldObj.getGameRules().getBoolean("keepInventory");
//        boolean flag2 = event.getEntityPlayer().worldObj.isRemote;
//        boolean flag3 = TileDeathBlock.isInventoryEmpty(event.getEntityPlayer());
//        if (event.getEntityPlayer().worldObj.getGameRules().getBoolean("keepInventory") || event.getEntityPlayer().worldObj.isRemote || TileDeathBlock.isInventoryEmpty(event.getEntityPlayer()))
//        {
//            return;
//        }
//
//        EntityPlayer player = event.getEntityPlayer();
//
//        if (TileDeathBlock.isInventoryEmpty(event.getEntityPlayer()))
//        {
//            ChatHelper.sayMessage(player.worldObj, player, "Place of death (x,y,z) = (" + (int)player.posX + "," + (int)player.posY + "," + (int)player.posZ + ")");
//            ChatHelper.sayMessage(player.worldObj, player, "(But your inventory was empty)");
//            return;
//        }
//
//        //write the inventory backup file
//        DeathInventoryHandler.createDeathInventory(event.getEntityPlayer());
//
//        //create the grave
//        if (TombManyGravesConfigs.ENABLE_GRAVES)
//        {
//            IBlockState state = ModBlocks.blockDeath.getDefaultState();
//            BlockPos posToPlace = findValidLocation(player.worldObj, player.getPosition());
//            if (posToPlace.getY() != -1)
//            {
//                ChatHelper.sayMessage(player.worldObj, player, "Place of death (x,y,z) = (" + posToPlace.getX() + "," + posToPlace.getY() + "," + posToPlace.getZ() + ")");
//                player.worldObj.setBlockState(posToPlace, state);
//                TileEntity tileEntity = player.worldObj.getTileEntity(posToPlace);
//                if (tileEntity != null && tileEntity instanceof TileDeathBlock)
//                {
//                    ((TileDeathBlock)tileEntity).grabPlayer(player);
//
//                    IBlockState ground = getBlockBelow(player.worldObj, posToPlace);
//
//                    if (ground.getMaterial() == Material.AIR)
//                    {
//                        ground = ModBlocks.blockDeath.getDefaultState();
//                    }
//                    else if (ground.getMaterial() == Material.GRASS)
//                    {
//                        ground = Blocks.DIRT.getDefaultState();
//                    }
//                    ((TileDeathBlock) tileEntity).setCamoState(ground);
//                }
//                else
//                {
//                    LogHelper.info("Error! Death block tile not found!");
//                }
//            }
//            else
//            {
//                ChatHelper.sayMessage(player.worldObj,player,"Could not find suitable grave location.");
//            }
//        }
//
//    }


    @SubscribeEvent(priority = EventPriority.HIGH)
    public void savePlayerInventoryOnDeath(LivingDeathEvent event)
    {
        if (!event.getEntityLiving().worldObj.getGameRules().getBoolean("keepInventory") && event.getEntityLiving() instanceof EntityPlayer && !((EntityPlayer)event.getEntityLiving()).worldObj.isRemote)
        {
            if (TileDeathBlock.isInventoryEmpty((EntityPlayer)event.getEntityLiving())){
                DeathInventory.clearLatest((EntityPlayer)event.getEntityLiving());
            }
            else
            {
                DeathInventoryHandler.createDeathInventory((EntityPlayer)event.getEntityLiving());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onPlayerDeath(LivingDeathEvent event)
    {
        if (!event.getEntityLiving().worldObj.getGameRules().getBoolean("keepInventory") && TombManyGravesConfigs.ENABLE_GRAVES && event.getEntityLiving() instanceof EntityPlayer && !((EntityPlayer) event.getEntityLiving()).worldObj.isRemote)
        {
            EntityPlayer player = (EntityPlayer)event.getEntityLiving();

            if (!TileDeathBlock.isInventoryEmpty(player))
            {
                IBlockState state = ModBlocks.blockDeath.getDefaultState();
                BlockPos posToPlace = findValidLocation(player.worldObj,player.getPosition());
                if (posToPlace.getY() != -1)
                {
                    ChatHelper.sayMessage(player.worldObj, player, "Place of death (x,y,z) = (" + posToPlace.getX() + "," + posToPlace.getY() + "," + posToPlace.getZ() + ")");
                    player.worldObj.setBlockState(posToPlace, state);
                    TileEntity tileEntity = player.worldObj.getTileEntity(posToPlace);
                    if (tileEntity != null && tileEntity instanceof TileDeathBlock)
                    {
                        ((TileDeathBlock)tileEntity).grabPlayer(player);

                        IBlockState state1 = getBlockBelow(player.worldObj,posToPlace);

                        if (state1.getMaterial() == Material.AIR)
                        {
                            state1 = ModBlocks.blockDeath.getDefaultState();
                        }
                        else if (state1.getMaterial() == Material.GRASS)
                        {
                            state1 = Blocks.DIRT.getDefaultState();
                        }
                        ((TileDeathBlock) tileEntity).setCamoState(state1);
                    }
                    else
                    {
                        LogHelper.info("Error! Death block tile not found!");
                    }
                }
                else
                {
                    ChatHelper.sayMessage(player.worldObj,player,"Could not find suitable grave location.");
                }
            }
            else
            {
                ChatHelper.sayMessage(player.worldObj, player, "Place of death (x,y,z) = (" + (int)player.posX + "," + (int)player.posY + "," + (int)player.posZ + ")");
                ChatHelper.sayMessage(player.worldObj, player, "(But your inventory was empty)");
            }
        }
    }

    private BlockPos findValidLocation(World world, BlockPos pos)
    {
        BlockPos toReturn = new BlockPos(-1,-1,-1);
        BlockPos toCheck = pos.add(0,0,0);
        if (toCheck.getY()<=0)
        {
            toCheck = toCheck.add(0, MathHelper.abs_int(toCheck.getY())+1,0);
            LogHelper.info(toCheck.toString());
        }
        for (int r=0;r<=MAX_RADIUS;r++)
        {
            toReturn = checkLevel(world,toCheck,r);
            if (toReturn.getY()!=-1)
            {
                return toReturn;
            }
        }
        return toReturn;
    }

    private BlockPos checkLevel(World world, BlockPos pos, int radius)
    {
        if (radius==0 && isValidLocation(world,pos))
        {
            return pos;
        }
        for (int i=-radius;i<=radius;i++)
        {
            for (int j=-radius;j<=radius;j++)
            {
                for (int k=radius;k>=-radius;k--)
                {
                    if (MathHelper.abs_int(i)==radius || MathHelper.abs_int(j)==radius || MathHelper.abs_int(k)==radius)
                    {
                        if (isValidLocation(world,pos.add(i,j,k)))
                        {
                            return pos.add(i,j,k);
                        }
                    }
                }
            }
        }
        return new BlockPos(-1,-1,-1);
    }

    private boolean isValidLocation(World world,BlockPos pos)
    {
        Block theBlock = world.getBlockState(pos).getBlock();
        if (world.isAirBlock(pos))
        {
            return true;
        }
        if (TombManyGravesConfigs.ALLOW_GRAVES_IN_LAVA && theBlock == Blocks.LAVA)
        {
            return true;
        }
        if (TombManyGravesConfigs.ALLOW_GRAVES_IN_FLOWING_LAVA && theBlock == Blocks.FLOWING_LAVA)
        {
            return true;
        }
        if (TombManyGravesConfigs.ALLOW_GRAVES_IN_WATER && theBlock == Blocks.WATER)
        {
            return true;
        }
        if (TombManyGravesConfigs.ALLOW_GRAVES_IN_FLOWING_WATER && theBlock == Blocks.FLOWING_WATER)
        {
            return true;
        }
        if (TombManyGravesConfigs.ALLOW_GRAVES_ON_PLANTS && theBlock instanceof IPlantable)
        {
            return true;
        }
        return false;
    }

    private IBlockState getBlockBelow(World world, BlockPos pos)
    {
        return world.getBlockState(pos.add(0,-1,0));
    }

    @SubscribeEvent
    public void itemToss(ItemTossEvent event)
    {
        Item item = event.getEntityItem().getEntityItem().getItem();
        if (item == Item.getItemFromBlock(ModBlocks.blockDeath) || item == ModItems.itemDeathList)
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(net.minecraftforge.event.entity.player.PlayerEvent.Clone event)
    {
        if (!event.getEntityLiving().worldObj.getGameRules().getBoolean("keepInventory") && TombManyGravesConfigs.ALLOW_INVENTORY_SAVES && !(event.getEntityPlayer().worldObj.isRemote) && event.isWasDeath())
        {
            DeathInventoryHandler.getDeathList(event.getEntityPlayer(), event.getEntityPlayer().getName(), "latest");
        }
    }
}
