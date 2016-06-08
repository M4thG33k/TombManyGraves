package com.m4thg33k.tombmanygraves.tiles;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.blocks.BlockDeath;
import com.m4thg33k.tombmanygraves.core.handlers.FriendHandler;
import com.m4thg33k.tombmanygraves.core.util.ChatHelper;
import com.m4thg33k.tombmanygraves.core.util.LogHelper;
import com.m4thg33k.tombmanygraves.lib.TombManyGravesConfigs;
import crazypants.enderio.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.UUID;

public class TileDeathBlock extends TileEntity {
    private static final String TAG_CAMO = "camo";
    private static final String TAG_CAMO_META = "camoMeta";

    private String playerName = "";
    private InventoryPlayer savedPlayerInventory = new InventoryPlayer(null);
    private NBTTagCompound baublesNBT = new NBTTagCompound();
    private boolean locked = false;

    private UUID playerID = null;

    private int angle = 0;

    private boolean renderGround = false;
    private ItemStack skull = null;

    private IBlockState camoState;

    public TileDeathBlock()
    {
        locked = TombManyGravesConfigs.DEFAULT_TO_LOCKED;
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
        baublesNBT = getBaublesNBTSansSoulbound(player, true);
//        InventoryBaubles temp = new InventoryBaubles(player);
//        InventoryBaubles playerBaubles = PlayerHandler.getPlayerBaubles(player);
//
//        IInventory tempInv = (IInventory)temp;
//        IInventory playerBaublesInv = (IInventory) playerBaubles;
//
//        for (int i=0; i < tempInv.getSizeInventory(); i++)
//        {
//            if (isValidForGrave(playerBaublesInv.getStackInSlot(i)))
//            {
//                tempInv.setInventorySlotContents(i, playerBaublesInv.getStackInSlot(i).copy());
//                playerBaublesInv.setInventorySlotContents(i, null);
//            }
//        }
//
//        temp.saveNBT(baublesNBT);


//        PlayerHandler.getPlayerBaubles(player).saveNBT(baublesNBT);
//        PlayerHandler.clearPlayerBaubles(player);
    }

    public void setThisInventory(InventoryPlayer inventoryPlayer)
    {
        this.savedPlayerInventory = getInventorySansSoulbound(inventoryPlayer, true);
//        this.savedPlayerInventory = new InventoryPlayer(inventoryPlayer.player);

//        for (int i = 0; i < this.savedPlayerInventory.getSizeInventory(); i++)
//        {
//            if (isValidForGrave(inventoryPlayer.getStackInSlot(i)))
//            {
//                this.savedPlayerInventory.setInventorySlotContents(i,inventoryPlayer.getStackInSlot(i).copy());
//                inventoryPlayer.setInventorySlotContents(i, null);
//            }
//        }

//        this.savedPlayerInventory.copyInventory(inventoryPlayer);
//        inventoryPlayer.clear();
    }

    public static boolean isValidForGrave(ItemStack stack)
    {
        boolean hasSize = stack != null && stack.stackSize > 0;
        if (!hasSize)
        {
            return false;
        }
        boolean notSoulbound = !hasSoulboundEnchantment(stack);
        return notSoulbound;
    }

    public boolean isSamePlayer(EntityPlayer player)
    {
        return TombManyGravesConfigs.ALLOW_GRAVE_ROBBING || player.getUniqueID().equals(playerID); //player.getName().equals(playerName);
    }

    public String getPlayerName()
    {
        return playerName;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        playerName = compound.getString("PlayerName");
        this.setSkull();
        savedPlayerInventory.readFromNBT(compound.getTagList("Inventory",10));
        baublesNBT = compound.getCompoundTag("BaublesNBT");
        angle = compound.getInteger("AngleOfDeath");

        locked = compound.getBoolean("IsLocked");

        playerID = compound.getUniqueId("PlayerID");

        Block b = Block.getBlockFromName(compound.getString(TAG_CAMO));
        if (b != null)
        {
            camoState = b.getStateFromMeta(compound.getInteger(TAG_CAMO_META));
        }

        setRenderGround();
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

        compound.setBoolean("IsLocked", locked);

        compound.setUniqueId("PlayerID", playerID);

        if (camoState != null)
        {
            compound.setString(TAG_CAMO, Block.REGISTRY.getNameForObject(camoState.getBlock()).toString());
            compound.setInteger(TAG_CAMO_META, camoState.getBlock().getMetaFromState(camoState));
        }

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

        locked = pkt.getNbtCompound().getBoolean("IsLocked");
        playerID = pkt.getNbtCompound().getUniqueId("PlayerID");

        Block b = Block.getBlockFromName(pkt.getNbtCompound().getString(TAG_CAMO));
        if (b != null)
        {
            camoState = b.getStateFromMeta(pkt.getNbtCompound().getInteger(TAG_CAMO_META));
        }
        setRenderGround();
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = this.getUpdateTag();
        return new SPacketUpdateTileEntity(pos,0,compound);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
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
            if (inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).stackSize > 0 && !hasSoulboundEnchantment(inventory.getStackInSlot(i)))
            {
                return false;
            }
        }

        return true;
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

    public void setCamoState(IBlockState state)
    {
        camoState = state;
        setRenderGround();
    }

    public IBlockState getCamoState()
    {
        return camoState;
    }

    public void setRenderGround()
    {
        renderGround = !(camoState == null || camoState.getBlock() instanceof BlockDeath);
    }

    public static boolean hasSoulboundEnchantment(ItemStack stack)
    {
        if (stack == null || stack.stackSize == 0 || !TombManyGraves.isEnderIOInstalled)
        {
            return false;
        }

        if (Config.enchantmentSoulBoundEnabled)
        {
            Map<Enchantment, Integer> enchantMap = EnchantmentHelper.getEnchantments(stack);
            for (Enchantment enchantment : enchantMap.keySet())
            {
//                LogHelper.info(enchantment.getName());
                if (enchantment.getName().equals("enchantment.soulBound"))
                {
                    return enchantMap.get(enchantment) > 0;
                }
            }
        }
        return false;
    }

    public static void copyInventoryWithoutSoulbound(IInventory original,IInventory newer, boolean clearOriginal)
    {
        for (int i=0; i<newer.getSizeInventory();i++)
        {
            if (isValidForGrave(original.getStackInSlot(i)))
            {
                newer.setInventorySlotContents(i, original.getStackInSlot(i).copy());
                if (clearOriginal)
                {
                    original.setInventorySlotContents(i, null);
                }
            }
        }
    }

    public static InventoryPlayer getInventorySansSoulbound(InventoryPlayer original,boolean clearOriginal)
    {
        InventoryPlayer toReturn = new InventoryPlayer(original.player);
        copyInventoryWithoutSoulbound(original, toReturn, clearOriginal);
//        for (int i=0; i<toReturn.getSizeInventory(); i++)
//        {
//            if (isValidForGrave(original.getStackInSlot(i)))
//            {
//                toReturn.setInventorySlotContents(i, original.getStackInSlot(i).copy());
//                if (clearOriginal)
//                {
//                    original.setInventorySlotContents(i, null);
//                }
//            }
//        }
        return toReturn;
    }

    public static NBTTagCompound getBaublesNBTSansSoulbound(EntityPlayer player, boolean clearOriginal)
    {
        InventoryBaubles toReturn = new InventoryBaubles(player);
        InventoryBaubles current = PlayerHandler.getPlayerBaubles(player);

        copyInventoryWithoutSoulbound(current, toReturn, clearOriginal);
//        IInventory retInv = (IInventory) toReturn;
//        IInventory currentInv = (IInventory) current;

//        for (int i=0; i<retInv.getSizeInventory(); i++)
//        {
//            if (isValidForGrave(currentInv.getStackInSlot(i)))
//            {
//                retInv.setInventorySlotContents(i, currentInv.getStackInSlot(i).copy());
//                if (clearOriginal)
//                {
//                    currentInv.setInventorySlotContents(i, null);
//                }
//            }
//        }
        NBTTagCompound compound = new NBTTagCompound();
        toReturn.saveNBT(compound);
        return compound;
    }
}
