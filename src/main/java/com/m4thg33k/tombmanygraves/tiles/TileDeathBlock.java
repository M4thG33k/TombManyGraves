package com.m4thg33k.tombmanygraves.tiles;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import com.m4thg33k.tombmanygraves.core.handlers.FriendHandler;
import com.m4thg33k.tombmanygraves.core.util.ChatHelper;
import com.m4thg33k.tombmanygraves.core.util.LogHelper;
import com.m4thg33k.tombmanygraves.lib.TombManyGravesConfigs;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class TileDeathBlock extends TileEntity {

    private String playerName = "";
    private InventoryPlayer savedPlayerInventory = new InventoryPlayer(null);
    private NBTTagCompound baublesNBT = new NBTTagCompound();
    private ItemStack groundMaterial = null;
    private boolean locked = false;

    private UUID playerID = null;

    private int angle = 0;

    private boolean renderGround = false;
    private ItemStack skull = null;

    public TileDeathBlock()
    {
        locked = TombManyGravesConfigs.DEFAULT_TO_LOCKED;

        LogHelper.info("Creating Tomb");
    }

    public void setPlayerName(String name)
    {
        playerName = name;
        this.setSkull();
    }

    public void setPlayerID(UUID id)
    {
        playerID = id;
    }

    public void grabPlayer(EntityPlayer player)
    {

        angle = (int)player.rotationYawHead;

        if (worldObj.isRemote)
        {
            return;
        }
//        angle = TombManyGraves.rand.nextInt(360);
        setPlayerName(player.getName());
        setPlayerID(player.getUniqueID());
        setThisInventory(player.inventory);

        if (TombManyGraves.isBaublesInstalled)
        {
            setBaubleInventory(player);
        }

        this.markDirty();
        worldObj.markAndNotifyBlock(pos, null, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 1);
    }

    public void setBaubleInventory(EntityPlayer player)
    {
        PlayerHandler.getPlayerBaubles(player).saveNBT(baublesNBT);
        PlayerHandler.clearPlayerBaubles(player);
    }

    public void setThisInventory(InventoryPlayer inventoryPlayer)
    {
        this.savedPlayerInventory.copyInventory(inventoryPlayer);
        inventoryPlayer.clear();
    }

    public boolean isSamePlayer(EntityPlayer player)
    {
        return TombManyGravesConfigs.ALLOW_GRAVE_ROBBING || player.getUniqueID().equals(playerID); //player.getName().equals(playerName);
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public UUID getPlayerID()
    {
        return playerID;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        playerName = compound.getString("PlayerName");
        this.setSkull();
        savedPlayerInventory.readFromNBT(compound.getTagList("Inventory",10));
        baublesNBT = compound.getCompoundTag("BaublesNBT");
        angle = compound.getInteger("AngleOfDeath");

        groundMaterial = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("Material"));
        this.setBlockMaterial();
        locked = compound.getBoolean("IsLocked");

        playerID = compound.getUniqueId("PlayerID");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setString("PlayerName",playerName);

        NBTTagList tagList = new NBTTagList();
        savedPlayerInventory.writeToNBT(tagList);
        compound.setTag("Inventory",tagList);

        compound.setTag("BaublesNBT",baublesNBT);
        compound.setInteger("AngleOfDeath",angle);


        NBTTagCompound material = new NBTTagCompound();
        if (groundMaterial != null)
        {
            groundMaterial.writeToNBT(material);
        }
        compound.setTag("Material",material);
        compound.setBoolean("IsLocked", locked);

        compound.setUniqueId("PlayerID", playerID);

        return compound;
    }

    public void onCollision(EntityPlayer player)
    {
        if (worldObj.isRemote || locked || !(hasAccess(player)))
        {
            return;
        }

        replacePlayerInventory(player);

        if (TombManyGraves.isBaublesInstalled)
        {
            replaceBaublesInventory(player);
        }

        worldObj.setBlockToAir(pos);
    }

    public void replaceSpecificInventory(EntityPlayer player, IInventory playerInventory,IInventory savedInventory)
    {
        for (int i=0; i < playerInventory.getSizeInventory(); i++)
        {
            if (savedInventory.getStackInSlot(i) != null && savedInventory.getStackInSlot(i).stackSize > 0)
            {
                if (playerInventory.getStackInSlot(i) == null)
                {
                    playerInventory.setInventorySlotContents(i, savedInventory.getStackInSlot(i));
                }
                else
                {
                    EntityItem entityItem = new EntityItem(worldObj, player.posX, player.posY, player.posZ, savedInventory.getStackInSlot(i));
                    worldObj.spawnEntityInWorld(entityItem);
                }
            }
        }
    }

    public void replacePlayerInventory(EntityPlayer player)
    {
        replaceSpecificInventory(player,player.inventory,savedPlayerInventory);
        savedPlayerInventory = new InventoryPlayer(null);
    }

    public void replaceBaublesInventory(EntityPlayer player)
    {
        IInventory currentBaubles = PlayerHandler.getPlayerBaubles(player);
        InventoryBaubles savedBaubles = new InventoryBaubles(player);
        savedBaubles.readNBT(baublesNBT);

        replaceSpecificInventory(player,currentBaubles,savedBaubles);

        baublesNBT = new NBTTagCompound();

    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        playerName = pkt.getNbtCompound().getString("PlayerName");
        this.setSkull();
        angle = pkt.getNbtCompound().getInteger("AngleOfDeath");
        groundMaterial = ItemStack.loadItemStackFromNBT(pkt.getNbtCompound().getCompoundTag("Material"));
        this.setBlockMaterial();
        locked = pkt.getNbtCompound().getBoolean("IsLocked");
        playerID = pkt.getNbtCompound().getUniqueId("PlayerID");
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = this.getUpdateTag();
//        NBTTagCompound compound = new NBTTagCompound();
//        compound.setString("PlayerName",playerName);
//        compound.setInteger("AngleOfDeath",angle);
//
//        NBTTagCompound material = new NBTTagCompound();
//        if (groundMaterial != null)
//        {
//            groundMaterial.writeToNBT(material);
//        }
//        compound.setTag("Material",material);
//        compound.setBoolean("IsLocked",locked);
//        compound.setUniqueId("PlayerID",playerID);
        return new SPacketUpdateTileEntity(pos,0,compound);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
//        NBTTagCompound compound = new NBTTagCompound();
//        compound.setString("PlayerName",playerName);
//        compound.setInteger("AngleOfDeath",angle);
//
//        NBTTagCompound material = new NBTTagCompound();
//        if (groundMaterial != null)
//        {
//            groundMaterial.writeToNBT(material);
//        }
//        compound.setTag("Material",material);
//        compound.setBoolean("IsLocked",locked);
//        compound.setUniqueId("PlayerID",playerID);
//        return compound;
        return this.writeToNBT(new NBTTagCompound());
    }

    public int getAngle()
    {
        return angle;
    }

    public void dropAllItems()
    {
        InventoryHelper.dropInventoryItems(worldObj, pos, savedPlayerInventory);
        if (TombManyGraves.isBaublesInstalled)
        {
            InventoryBaubles baubles = new InventoryBaubles(null);
            baubles.readNBT(baublesNBT);
            InventoryHelper.dropInventoryItems(worldObj, pos, baubles);
        }
    }

    public static boolean isInventoryEmpty(EntityPlayer player)
    {
        boolean toReturn = isSpecificInventoryEmpty(player.inventory);

        if (TombManyGraves.isBaublesInstalled)
        {
            toReturn = toReturn && isSpecificInventoryEmpty(PlayerHandler.getPlayerBaubles(player));
        }

        return toReturn;
    }

    public static boolean isSpecificInventoryEmpty(IInventory inventory)
    {
        for (int i=0; i < inventory.getSizeInventory(); i++)
        {
            if (inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).stackSize > 0)
            {
                return false;
            }
        }

        return true;
    }

    public void setGroundMaterial(ItemStack material)
    {
        groundMaterial = material.copy();
    }

    public ItemStack getGroundMaterial()
    {
        return groundMaterial;
    }

    public boolean isLocked()
    {
        return locked;
    }

    public void toggleLock(EntityPlayer player)
    {
        if (worldObj.isRemote)
        {
            return;
        }
        if (hasAccess(player)) {
            locked = !locked;
            if (TombManyGravesConfigs.ALLOW_LOCKING_MESSAGES)
            {
                ChatHelper.sayMessage(player.worldObj, player, "This grave is now " + (locked ? "locked!" : "unlocked!"));
            }

            markDirty();
            worldObj.markAndNotifyBlock(pos, null, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 2);
        }
        else
        {
            ChatHelper.sayMessage(player.worldObj, player, "You do not have permission to modify this grave.");
        }
    }

    public boolean isFriend(EntityPlayer player)
    {
        return FriendHandler.isFriendOf(playerID,player.getUniqueID());
    }

    public boolean hasAccess(EntityPlayer player)
    {
        return TombManyGravesConfigs.ALLOW_GRAVE_ROBBING || isSamePlayer(player) || isFriend(player);
    }

    private void setBlockMaterial()
    {
        if (groundMaterial == null)
        {
            renderGround = true;
            groundMaterial = new ItemStack(Blocks.DIRT,1);
        }
        else if (groundMaterial.getItem() == Item.getItemFromBlock(ModBlocks.blockDeath))
        {
            renderGround = false;
        }
        else
        {
            renderGround = true;
        }
    }

    public boolean getRenderGround()
    {
        return renderGround;
    }

    public ItemStack getSkull()
    {
        return skull;
    }

    private void setSkull()
    {
        skull = new ItemStack(Items.SKULL,1,3);
        skull.setTagCompound(new NBTTagCompound());
        skull.getTagCompound().setTag("SkullOwner",new NBTTagString(playerName));
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return true;
    }
}
