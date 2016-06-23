package com.m4thg33k.tombmanygraves.tiles;

import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.blocks.BlockDeath;
import com.m4thg33k.tombmanygraves.core.handlers.FriendHandler;
import com.m4thg33k.tombmanygraves.core.util.ChatHelper;
import com.m4thg33k.tombmanygraves.lib.TombManyGravesConfigs;
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
    private static final boolean DROP_ITEMS = TombManyGravesConfigs.DROP_ITEMS_ON_GROUND;
    private static final String TAG_CAMO = "camo";
    private static final String TAG_CAMO_META = "camoMeta";

    private String playerName = "";
    private InventoryPlayer savedPlayerInventory = new InventoryPlayer(null);
    private NBTTagCompound baublesNBT = new NBTTagCompound();
    private NBTTagCompound cosmeticNBT = new NBTTagCompound();
    private boolean locked = false;

    private UUID playerID = null;

    private int angle = 0;

    private boolean renderGround = false;
    private ItemStack skull = null;

    private IBlockState camoState;

    private boolean GIVE_PRIORITY_TO_GRAVE = TombManyGravesConfigs.GIVE_PRIORITY_TO_GRAVE_ITEMS;

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

//        if (TombManyGraves.isBaublesInstalled)
//        {
//            setBaubleInventory(player);
//        }
//        if (TombManyGraves.isCosmeticArmorInstalled)
//        {
//            setCosmeticInventory(player);
//        }

        this.markDirty();
        worldObj.markAndNotifyBlock(pos, null, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 1);
    }

//    public void setBaubleInventory(EntityPlayer player)
//    {
//        baublesNBT = getBaublesNBTSansSoulbound(player, true);
//    }

    public void setThisInventory(InventoryPlayer inventoryPlayer)
    {
        this.savedPlayerInventory = getInventorySansSoulbound(inventoryPlayer, true);
    }

//    public void setCosmeticInventory(EntityPlayer player)
//    {
//        this.cosmeticNBT = getCosmeticNBTSansSoulbound(player, true);
//    }

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

    public void onRightClick(EntityPlayer player)
    {
        if (this.hasAccess(player))
        {
            this.toggleGravePriority();
            if (GIVE_PRIORITY_TO_GRAVE)
            {
                ChatHelper.sayMessage(player.worldObj,player,"Grave items will be forced into their original slots.");
            }
            else
            {
                ChatHelper.sayMessage(player.worldObj,player,"Your current inventory will not be altered.");
            }
        }
        else
        {
            ChatHelper.sayMessage(player.worldObj, player, "You don't have permission to interact with this grave.");
        }
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

        GIVE_PRIORITY_TO_GRAVE = compound.getBoolean("GravePriority");

        Block b = Block.getBlockFromName(compound.getString(TAG_CAMO));
        if (b != null)
        {
            camoState = b.getStateFromMeta(compound.getInteger(TAG_CAMO_META));
        }

        cosmeticNBT = compound.getCompoundTag("CosmeticNBT");

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

        compound.setBoolean("GravePriority",GIVE_PRIORITY_TO_GRAVE);

        if (camoState != null)
        {
            compound.setString(TAG_CAMO, Block.REGISTRY.getNameForObject(camoState.getBlock()).toString());
            compound.setInteger(TAG_CAMO_META, camoState.getBlock().getMetaFromState(camoState));
        }

        compound.setTag("CosmeticNBT",cosmeticNBT);

        return compound;
    }

    private void toggleGravePriority()
    {
        GIVE_PRIORITY_TO_GRAVE = !GIVE_PRIORITY_TO_GRAVE;
        markDirty();
        worldObj.markAndNotifyBlock(pos, null, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 2);
    }

    public void onCollision(EntityPlayer player)
    {
        if (worldObj.isRemote || locked || !(hasAccess(player)))
        {
            return;
        }

        if (DROP_ITEMS)
        {
            dropAllItems();
        }
        else {
            if (GIVE_PRIORITY_TO_GRAVE)
            {
                swapPlayerInventory(player);
            }
            else
            {
                replacePlayerInventory(player);
            }

//            if (TombManyGraves.isBaublesInstalled) {
//                if (GIVE_PRIORITY_TO_GRAVE)
//                {
//                    swapPlayerBaubles(player);
//                }
//                else
//                {
//                    replaceBaublesInventory(player);
//                }
//            }

//            if (TombManyGraves.isCosmeticArmorInstalled)
//            {
//                if (GIVE_PRIORITY_TO_GRAVE)
//                {
//                    swapPlayerCosmetic(player);
//                }
//                else
//                {
//                    replaceCosmeticInventory(player);
//                }
//            }
        }
        worldObj.setBlockToAir(pos);
    }

    public void swapPlayerInventory(EntityPlayer player)
    {
        InventoryPlayer current = new InventoryPlayer(player);
        current.copyInventory(player.inventory);
        player.inventory.clear();
        replaceSpecificInventory(player,player.inventory,savedPlayerInventory);
        replaceSpecificInventory(player, player.inventory,current);
        savedPlayerInventory = new InventoryPlayer(player);
    }

//    public void swapPlayerBaubles(EntityPlayer player){
//        InventoryBaubles playerBaubles = PlayerHandler.getPlayerBaubles(player);
//        NBTTagCompound playerB = new NBTTagCompound();
//        playerBaubles.saveNBT(playerB);
//        InventoryBaubles currentBaubles = new InventoryBaubles(player);
//        currentBaubles.readNBT(playerB);
//        ((IInventory)playerBaubles).clear();
//        replaceBaublesInventory(player);
//        baublesNBT = playerB;
//        replaceBaublesInventory(player);
//        baublesNBT = new NBTTagCompound();
//    }

//    public void swapPlayerCosmetic(EntityPlayer player)
//    {
//        InventoryCosArmor playerCos = CosmeticArmorReworked.invMan.getCosArmorInventory(player.getUniqueID());
//        NBTTagCompound playerC = new NBTTagCompound();
//        playerCos.writeToNBT(playerC);
//        InventoryCosArmor currentCos = new InventoryCosArmor();
//        currentCos.readFromNBT(playerC);
//        ((IInventory)playerCos).clear();
//        replaceCosmeticInventory(player);
//        cosmeticNBT = playerC;
//        replaceCosmeticInventory(player);
//        cosmeticNBT = new NBTTagCompound();
//    }

    public void replaceSpecificInventory(EntityPlayer player, IInventory playerInventory, IInventory savedInventory)
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

//    public void replaceBaublesInventory(EntityPlayer player)
//    {
//        IInventory currentBaubles = PlayerHandler.getPlayerBaubles(player);
//        InventoryBaubles savedBaubles = new InventoryBaubles(player);
//        savedBaubles.readNBT(baublesNBT);
//
//        replaceSpecificInventory(player,currentBaubles,savedBaubles);
//
//        baublesNBT = new NBTTagCompound();
//
//    }

//    public void replaceCosmeticInventory(EntityPlayer player)
//    {
//        InventoryCosArmor currentCos = CosmeticArmorReworked.invMan.getCosArmorInventory(player.getUniqueID());
//        InventoryCosArmor savedCos = new InventoryCosArmor();
//        savedCos.readFromNBT(cosmeticNBT);
//
//        replaceSpecificInventory(player,currentCos,savedCos);
//
//        cosmeticNBT = new NBTTagCompound();
//    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        playerName = pkt.getNbtCompound().getString("PlayerName");
        this.setSkull();
        angle = pkt.getNbtCompound().getInteger("AngleOfDeath");

        locked = pkt.getNbtCompound().getBoolean("IsLocked");
        playerID = pkt.getNbtCompound().getUniqueId("PlayerID");

        GIVE_PRIORITY_TO_GRAVE = pkt.getNbtCompound().getBoolean("GravePriority");

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

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

    public int getAngle()
    {
        return angle;
    }

    public void dropAllItems()
    {
        InventoryHelper.dropInventoryItems(worldObj, pos, savedPlayerInventory);
//        if (TombManyGraves.isBaublesInstalled)
//        {
//            InventoryBaubles baubles = new InventoryBaubles(null);
//            baubles.readNBT(baublesNBT);
//            InventoryHelper.dropInventoryItems(worldObj, pos, baubles);
//        }
//        if (TombManyGraves.isCosmeticArmorInstalled)
//        {
//            InventoryCosArmor cosArmor = new InventoryCosArmor();
//            cosArmor.readFromNBT(cosmeticNBT);
//            InventoryHelper.dropInventoryItems(worldObj, pos, cosArmor);
//        }
    }

    public static boolean isInventoryEmpty(EntityPlayer player)
    {
        boolean toReturn = isSpecificInventoryEmpty(player.inventory);

//        if (TombManyGraves.isBaublesInstalled)
//        {
//            toReturn = toReturn && isSpecificInventoryEmpty(PlayerHandler.getPlayerBaubles(player));
//        }
//
//        if (TombManyGraves.isCosmeticArmorInstalled)
//        {
//            toReturn = toReturn && isSpecificInventoryEmpty(CosmeticArmorReworked.invMan.getCosArmorInventory(player.getUniqueID()));
//        }

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

        if (true)//(Config.enchantmentSoulBoundEnabled)
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
        return toReturn;
    }

//    public static NBTTagCompound getBaublesNBTSansSoulbound(EntityPlayer player, boolean clearOriginal)
//    {
//        InventoryBaubles toReturn = new InventoryBaubles(player);
//        InventoryBaubles current = PlayerHandler.getPlayerBaubles(player);
//
//        copyInventoryWithoutSoulbound(current, toReturn, clearOriginal);
//        NBTTagCompound compound = new NBTTagCompound();
//        toReturn.saveNBT(compound);
//        return compound;
//    }

//    public static NBTTagCompound getCosmeticNBTSansSoulbound(EntityPlayer player, boolean clearOriginal)
//    {
//        InventoryCosArmor toReturn = new InventoryCosArmor();
//        InventoryCosArmor current = CosmeticArmorReworked.invMan.getCosArmorInventory(player.getUniqueID());
//
//        copyInventoryWithoutSoulbound(current, toReturn, clearOriginal);
//        NBTTagCompound compound = new NBTTagCompound();
//        toReturn.writeToNBT(compound);
//        return compound;
//    }

    public boolean areGraveItemsForced()
    {
        return GIVE_PRIORITY_TO_GRAVE;
    }
}
