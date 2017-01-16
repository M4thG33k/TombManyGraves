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
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class TombManyGravesCommonEvents {

    private static final int MAX_RADIUS = TombManyGravesConfigs.GRAVE_RANGE;
    private static final boolean VOID_SEARCH_1 = TombManyGravesConfigs.VOID_SEARCH_1;

    public TombManyGravesCommonEvents()
    {

    }


//    @SubscribeEvent(priority = EventPriority.HIGH)
//    public void savePlayerInventoryOnDeath(LivingDeathEvent event)
//    {
////        if (!event.getEntityLiving().worldObj.getGameRules().getBoolean("keepInventory") && event.getEntityLiving() instanceof EntityPlayer && !((EntityPlayer)event.getEntityLiving()).worldObj.isRemote)
////        {
////            if (TileDeathBlock.isInventoryEmpty((EntityPlayer)event.getEntityLiving())){
////                DeathInventory.clearLatest((EntityPlayer)event.getEntityLiving());
////            }
////            else
////            {
////                DeathInventoryHandler.createDeathInventory((EntityPlayer)event.getEntityLiving());
////            }
////        }
//    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerDeath(LivingDeathEvent event)
    {
//        LogHelper.info("Debug line");
        if (TombManyGravesConfigs.PRINT_DEATH_LOG && event.getEntityLiving() instanceof EntityPlayer && !event.getEntityLiving().getEntityWorld().isRemote)
        {
            EntityPlayer player = (EntityPlayer)event.getEntityLiving();
            BlockPos pos = player.getPosition();
            LogHelper.info(player.getName() + " died in dimension " +  player.dimension + " at (x,y,z) = (" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + ")." + (TombManyGravesConfigs.ENABLE_GRAVES ? " Their grave may be near!" : ""));
        }
        if (!event.getEntityLiving().worldObj.getGameRules().getBoolean("keepInventory") && event.getEntityLiving() instanceof EntityPlayer && !((EntityPlayer) event.getEntityLiving()).worldObj.isRemote)
        {
            if (TombManyGravesConfigs.ENABLE_GRAVES) {
                EntityPlayer player = (EntityPlayer) event.getEntityLiving();

                if (!TileDeathBlock.isInventoryEmpty(player)) {
                    IBlockState state = ModBlocks.blockDeath.getDefaultState();
                    BlockPos posToPlace = new BlockPos(0,-1,0);
                    if (TombManyGravesConfigs.ASCEND_LIQUID)
                    {
                        posToPlace = findValidLocation(player.worldObj,ascendFromFluid(player.worldObj, player.getPosition()));
                    }
                    if (posToPlace.getY() == -1)
                    {
                        posToPlace = findValidLocation(player.worldObj, player.getPosition());
                    }
                    String timestamp = DeathInventoryHandler.createDeathInventory(player, posToPlace);
                    if (posToPlace.getY() != -1) {
                        ChatHelper.sayMessage(player.worldObj, player, "Place of death (x,y,z) = (" + posToPlace.getX() + "," + posToPlace.getY() + "," + posToPlace.getZ() + ")");
                        player.worldObj.setBlockState(posToPlace, state);
                        TileEntity tileEntity = player.worldObj.getTileEntity(posToPlace);
                        if (tileEntity != null && tileEntity instanceof TileDeathBlock) {
                            ((TileDeathBlock) tileEntity).grabPlayer(player);

                            IBlockState state1 = getBlockBelow(player.worldObj, posToPlace);

                            if (state1.getMaterial() == Material.AIR) {
                                state1 = ModBlocks.blockDeath.getDefaultState();
                            } else if (state1.getMaterial() == Material.GRASS) {
                                state1 = Blocks.DIRT.getDefaultState();
                            }
                            ((TileDeathBlock) tileEntity).setCamoState(state1);
                            ((TileDeathBlock) tileEntity).setTimeStamp(timestamp);
                        } else {
                            LogHelper.info("Error! Death block tile not found!");
                        }
                    } else {
                        ChatHelper.sayMessage(player.worldObj, player, "Could not find suitable grave location.");
                    }
                } else {
                    ChatHelper.sayMessage(player.worldObj, player, "Place of death (x,y,z) = (" + (int) player.posX + "," + (int) player.posY + "," + (int) player.posZ + ")");
                    ChatHelper.sayMessage(player.worldObj, player, "(But your inventory was empty)");
                    DeathInventory.clearLatest((EntityPlayer)event.getEntityLiving());
                }
            }
            else
            {
                DeathInventoryHandler.createDeathInventory((EntityPlayer)event.getEntityLiving(),((EntityPlayer) event.getEntityLiving()).getPosition());
            }
        }
    }

    private BlockPos findValidLocation(World world, BlockPos pos)
    {
        BlockPos toReturn = new BlockPos(-1,-1,-1);
        BlockPos toCheck = pos.add(0,0,0);
        if (toCheck.getY()<=0)
        {
            toCheck = toCheck.add(0, MathHelper.abs_int(toCheck.getY())+(VOID_SEARCH_1 ? 1 : MAX_RADIUS),0);
//            LogHelper.info(toCheck.toString());
        }
        for (int r=0;r<=MAX_RADIUS;r++)
        {
            toReturn = checkLevel(world,toCheck,r,false);
            if (toReturn.getY()!=-1)
            {
                return toReturn;
            }
        }
        return toReturn;
    }

    private BlockPos ascendFromFluid(World world, BlockPos pos)
    {
        BlockPos toCheck = pos.add(0,0,0);
        int height = 0;
        while (pos.getY()+height < world.getActualHeight()-TombManyGravesConfigs.GRAVE_RANGE && !isValidLocation(world,toCheck,true))
        {
            int temp = pos.getY()+height;
            int temp2 = world.getActualHeight();
            toCheck = checkLevel(world,pos.add(0,height,0),1,true);
            height += 1;
        }
        return toCheck;
    }

    private BlockPos checkLevel(World world, BlockPos pos, int radius, boolean ignoreFluidConfigs)
    {
        if (radius==0 && isValidLocation(world,pos,ignoreFluidConfigs))
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
                        if (isValidLocation(world,pos.add(i,j,k),ignoreFluidConfigs))
                        {
                            return pos.add(i,j,k);
                        }
                    }
                }
            }
        }
        return new BlockPos(-1,-1,-1);
    }

    private boolean isValidLocation(World world,BlockPos pos,boolean ignoreFluidConfigs)
    {
        if (pos.getY() < 0 || pos.getY() >= world.getActualHeight())
        {
            return false;
        }
        Block theBlock = world.getBlockState(pos).getBlock();
        if (world.isAirBlock(pos))
        {
            return true;
        }
        if (!ignoreFluidConfigs) {
            if (TombManyGravesConfigs.ALLOW_GRAVES_IN_LAVA && theBlock == Blocks.LAVA) {
                return true;
            }
            if (TombManyGravesConfigs.ALLOW_GRAVES_IN_FLOWING_LAVA && theBlock == Blocks.FLOWING_LAVA) {
                return true;
            }
            if (TombManyGravesConfigs.ALLOW_GRAVES_IN_WATER && theBlock == Blocks.WATER) {
                return true;
            }
            if (TombManyGravesConfigs.ALLOW_GRAVES_IN_FLOWING_WATER && theBlock == Blocks.FLOWING_WATER) {
                return true;
            }
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
        if (event.isCanceled())
        {
            return;
        }
        EntityItem entityItem = event.getEntityItem();
        Item item = entityItem.getEntityItem().getItem();
        if (item == Item.getItemFromBlock(ModBlocks.blockDeath) || item == ModItems.itemDeathList)
        {
            entityItem.setDead();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void itemDrop(PlayerDropsEvent event)
    {
        List<EntityItem> items = event.getDrops();
        for (EntityItem item : items)
        {
            if (item.getEntityItem().getItem() == ModItems.itemDeathList || item.getEntityItem().getItem() == Item.getItemFromBlock(ModBlocks.blockDeath))
            {
                item.setDead();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerClone(PlayerEvent.Clone event)
    {
        if (TombManyGravesConfigs.ALLOW_INVENTORY_LISTS && !event.isCanceled() && !event.getEntityLiving().worldObj.getGameRules().getBoolean("keepInventory") && TombManyGravesConfigs.ALLOW_INVENTORY_SAVES && !(event.getEntityLiving().worldObj.isRemote) && event.isWasDeath())
        {
            DeathInventoryHandler.getDeathList(event.getEntityPlayer(), event.getEntityPlayer().getName(), "latest");
        }
    }

//    @SubscribeEvent
//    public void onPlayerRespawn(net.minecraftforge.event.entity.player.PlayerEvent.Clone event)
//    {
//        if (!event.getEntityLiving().worldObj.getGameRules().getBoolean("keepInventory") && TombManyGravesConfigs.ALLOW_INVENTORY_SAVES && !(event.getEntityPlayer().worldObj.isRemote) && event.isWasDeath())
//        {
//            DeathInventoryHandler.getDeathList(event.getEntityPlayer(), event.getEntityPlayer().getName(), "latest");
//        }
//    }
}
