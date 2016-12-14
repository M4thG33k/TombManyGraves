package com.m4thg33k.tombmanygraves.client.gui;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.core.handlers.BaubleHandler;
import de.eydamos.backpack.data.BackpackSave;
import de.eydamos.backpack.item.ItemBackpack;
import gr8pefish.ironbackpacks.container.backpack.InventoryBackpack;
import gr8pefish.ironbackpacks.util.helpers.IronBackpacksHelper;
import lain.mods.cos.inventory.InventoryCosArmor;
import lellson.expandablebackpack.inventory.iinventory.BackpackInventory;
import lellson.expandablebackpack.item.backpack.Backpack;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import thut.wearables.inventory.PlayerWearables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GuiDeathItems extends GuiScreen {

    private EntityPlayer player;

    private ItemStack deathList;

    private List<String> header;
    private List<String> mainItems;
    private List<String> baubleItems;
    private List<String> cosmeticItems;
    private List<String> expandableBackpackItems;
    private List<String> eydamosBackpackItems;
    private List<String> thutItems;
    private List<String> ironBackpackItems;

    private Scrollbar scrollbar;


    private int xSize;
    private int ySize;

    private static String MAIN = "Main Inventory";
    private static String BAUBLES = "Baubles";
    private static String COSMETIC = "Cosmetic Armor";
    private static String EXPANDABLE = "Expandable Backpack";
    private static String EYDAMOS = "Eydamos Backpack";
    private static String THUT = "Thut Wearables";
    private static String IRON = "Iron Backpack";
    private static String LINE = "-----------------------------";
    private static String EOF = "END OF FILE";

    private static List<String> END_OF_FILE;

    public GuiDeathItems(EntityPlayer player, ItemStack deathList)
    {
        super();
        this.player = player;
        this.deathList = deathList.copy();
        createHeader();
        createListOfItemsInMainInventory();
        createListOfItemsInBaublesInventory();
        createListOfItemsInCosmeticInventory();
        createListOfItemsInExpandableBackpack();
        createListOfItemsInEydamosBackpack();
        createListOfItemsInThut();
        createListOfItemsInIronBackpack();

        END_OF_FILE = new ArrayList<String>();
        END_OF_FILE.add(LINE);
        END_OF_FILE.add(EOF);
        END_OF_FILE.add(LINE);

        xSize = 200;
        ySize = 150;

        scrollbar = new Scrollbar(xSize - 12, 0, 12, ySize);
        scrollbar.setScrollDelta(1f);
    }



    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        bindTexture("DeathListBackground.png");
        drawTexture(getGuiLeft(),getGuiTop(),0,0,xSize,ySize);

        scrollbar.update(this,mouseX,mouseY);
        scrollbar.draw(this);

        int endHeight = drawHeader();
        endHeight = drawMainItems(endHeight);
        endHeight = drawBaubleItems(endHeight);
        endHeight = drawCosmeticItems(endHeight);
        endHeight = drawExpandableBackpackItems(endHeight);
        endHeight = drawEydamosBackpackItems(endHeight);
        endHeight = drawThutItems(endHeight);
        endHeight = drawIronBackpackItems(endHeight);

        drawEOF(endHeight);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private int drawHeader()
    {
        int height = 0;
        int gLeft =getGuiLeft();
        int gTop = getGuiTop();

        int counter = 0;

        for (int i=0; i < header.size(); i++)
        {
            height = 10 * i + (int)scrollbar.getCurrentScroll()*(-10)+10;
            counter += 1;
            if (height < 4 || height >= ySize - 12)
            {
                continue;
            }
            this.fontRendererObj.drawString(header.get(i), gLeft + 12, gTop + height, 0);
        }

        return counter * 10;
    }

    private int drawMainItems(int startHeight)
    {
        int height ;
        int gLeft = getGuiLeft();
        int gTop = getGuiTop();

        int counter = 0;

        for (int i=0; i<mainItems.size();i++)
        {
            height = startHeight + 10*i + (int)scrollbar.getCurrentScroll()*(-10) + 10;
            counter += 1;
            if (height < 4 || height >= ySize - 12)
            {
                continue;
            }
            this.fontRendererObj.drawString(mainItems.get(i), gLeft + 12, gTop + height, 0);
        }

        return startHeight + counter*10;
    }

    private int drawBaubleItems(int startHeight)
    {
        if (baubleItems.size() < 4)
        {
            return startHeight;
        }
        int height;
        int gLeft = getGuiLeft();
        int gTop = getGuiTop();

        int counter = 0;

        for (int i=0; i<baubleItems.size(); i++)
        {
            height = startHeight + 10*i + (int)scrollbar.getCurrentScroll()*(-10) + 10;
            counter += 1;
            if (height < 4 || height >= ySize - 12)
            {
                continue;
            }
            this.fontRendererObj.drawString(baubleItems.get(i), gLeft + 12, gTop + height, 0);
        }

        return startHeight + counter*10;
    }

    private int drawCosmeticItems(int startHeight)
    {
        if (cosmeticItems.size() < 4){
            return startHeight;
        }
        int height;
        int gLeft = getGuiLeft();
        int gTop = getGuiTop();

        int counter = 0;

        for (int i=0; i < cosmeticItems.size(); i++)
        {
            height = startHeight + 10*i + (int)scrollbar.getCurrentScroll()*(-10) + 10;
            counter += 1;
            if (height < 4 || height >= ySize - 12)
                {
                    continue;
                }
            this.fontRendererObj.drawString(cosmeticItems.get(i), gLeft + 12, gTop + height, 0);
        }

        return startHeight + counter*10;
    }

    private int drawExpandableBackpackItems(int startHeight)
    {
        if (expandableBackpackItems.size() < 4)
        {
            return startHeight;
        }

        int height;
        int gLeft = getGuiLeft();
        int gTop = getGuiTop();

        int counter = 0;
        for (int i=0; i<expandableBackpackItems.size();i++)
        {
            height = startHeight + 10*i + (int)scrollbar.getCurrentScroll()*(-10) + 10;
            counter += 1;
            if (height < 4 || height >= ySize - 12)
            {
                continue;
            }
            this.fontRendererObj.drawString(expandableBackpackItems.get(i), gLeft+12, gTop + height, 0);
        }

        return startHeight + counter *10;
    }

    private int drawEydamosBackpackItems(int startHeight)
    {
        if (eydamosBackpackItems.size() < 4)
        {
            return startHeight;
        }

        int height;
        int gLeft = getGuiLeft();
        int gTop = getGuiTop();

        int counter = 0;
        for (int i=0; i<eydamosBackpackItems.size(); i++)
        {
            height = startHeight + 10*i + (int)scrollbar.getCurrentScroll()*(-10) + 10;
            counter += 1;
            if (height < 4 || height >= ySize - 12)
            {
                continue;
            }
            this.fontRendererObj.drawString(eydamosBackpackItems.get(i), gLeft+12, gTop+height, 0);
        }

        return startHeight + counter*10;
    }

    private int drawThutItems(int startHeight)
    {
        if (thutItems.size() < 4)
        {
            return startHeight;
        }

        int height;
        int gLeft= getGuiLeft();
        int gTop = getGuiTop();

        int counter = 0;
        for (int i=0; i<thutItems.size(); i++)
        {
            height = startHeight + 10*i + (int)scrollbar.getCurrentScroll()*(-10) + 10;
            counter += 1;
            if (height < 4 || height >= ySize - 12)
            {
                continue;
            }
            this.fontRendererObj.drawString(thutItems.get(i), gLeft+12, gTop+height, 0);
        }

        return startHeight + counter*10;
    }

    private int drawIronBackpackItems(int startHeight)
    {
        if (ironBackpackItems.size() < 4)
        {
            return startHeight;
        }

        int height;
        int gLeft = getGuiLeft();
        int gTop = getGuiTop();

        int counter = 0;
        for (int i=0; i<ironBackpackItems.size(); i++)
        {
            height = startHeight + 10*i + (int)scrollbar.getCurrentScroll()*(-10) + 10;
            counter += 1;
            if (height < 4 || height >= ySize - 12)
            {
                continue;
            }
            this.fontRendererObj.drawString(ironBackpackItems.get(i), gLeft + 12, gTop+height, 0);
        }

        return startHeight + counter*10;
    }

    private void drawEOF(int startHeight)
    {
        int height;
        int gLeft = getGuiLeft();
        int gTop = getGuiTop();

        for (int i=0; i<END_OF_FILE.size(); i++)
        {
            height = startHeight + 10*i + (int)scrollbar.getCurrentScroll()*(-10) + 10;
            if (height < 4 || height >= ySize - 12)
            {
                continue;
            }
            this.fontRendererObj.drawString(END_OF_FILE.get(i), gLeft + 12, gTop + height, (i==1?0xFF0000:0));
        }
    }

    private void createHeader()
    {
        header = new ArrayList<>();

        NBTTagCompound tagCompound = deathList.getTagCompound();
        if (tagCompound.hasKey("Misc"))
        {
            NBTTagCompound misc = tagCompound.getCompoundTag("Misc");
            int x = misc.getInteger("x");
            int y = misc.getInteger("y");
            int z = misc.getInteger("z");
            if (y < 0)
            {
                header.add("No grave exists.");
            }
            else
            {
                header.add("Grave at: (x,y,z) = (" + x + "," + y + "," + z + ")");
            }
            header.add("Timestamp: " + misc.getString("Timestamp"));
        }
    }

    private void createListOfItemsInMainInventory()
    {
        NBTTagList tagList = deathList.getTagCompound().getTagList("Main",10);
        InventoryPlayer inventoryPlayer = new InventoryPlayer(player);
        inventoryPlayer.readFromNBT(tagList);

        mainItems = createListFromInventory(inventoryPlayer,MAIN);
    }

    private void createListOfItemsInBaublesInventory()
    {
        if (!TombManyGraves.isBaublesInstalled)
        {
            baubleItems = new ArrayList<String>();
            return;
        }
        NBTTagCompound tag = deathList.getTagCompound().getCompoundTag("Baubles");
        IBaublesItemHandler inventoryBaubles = BaublesApi.getBaublesHandler(player);

        IInventory baubles = BaubleHandler.getSavedBaubles(tag, inventoryBaubles);

//        InventoryBaubles inventoryBaubles = new InventoryBaubles(player);
//        inventoryBaubles.readNBT(tag);

        baubleItems = createListFromInventory(baubles,BAUBLES);
    }

    private  void createListOfItemsInCosmeticInventory()
    {
        if (!TombManyGraves.isCosmeticArmorInstalled)
        {
            cosmeticItems = new ArrayList<String>();
            return;
        }
        NBTTagCompound tag = deathList.getTagCompound().getCompoundTag("Cosmetic");
        InventoryCosArmor cosArmor = new InventoryCosArmor();
        cosArmor.readFromNBT(tag);

        cosmeticItems = createListFromInventory(cosArmor, COSMETIC);
    }

    private void createListOfItemsInThut()
    {
        if (!TombManyGraves.isThutWearablesInstalled)
        {
            thutItems = new ArrayList<String>();
            return;
        }

        NBTTagCompound tag = deathList.getTagCompound().getCompoundTag("ThutWearables");
        PlayerWearables playerWearables = new PlayerWearables();
        playerWearables.readFromNBT(tag);

        thutItems = createListFromInventory(playerWearables, THUT);
    }

    private void createListOfItemsInExpandableBackpack()
    {
        if (!TombManyGraves.isExpandableBackpacksInstalled)
        {
            expandableBackpackItems = new ArrayList<>();
            return;
        }
        NBTTagCompound tag = deathList.getTagCompound().getCompoundTag("ExpandableBackpack");
        ItemStack stack = ItemStack.loadItemStackFromNBT(tag);
        if (stack == null || stack.stackSize == 0)
        {
            expandableBackpackItems = new ArrayList<>();
            return;
        }

        IInventory inventory = new InventoryBasic("temp",false,1);
        inventory.setInventorySlotContents(0,stack);
        expandableBackpackItems = createListFromInventory(inventory, EXPANDABLE);
    }

    private void createListOfItemsInEydamosBackpack()
    {
        if (!TombManyGraves.isEydamosBackpacksInstalled)
        {
            eydamosBackpackItems = new ArrayList<>();
            return;
        }
        NBTTagCompound tag = deathList.getTagCompound().getCompoundTag("EydamosBackpack");
        ItemStack stack = ItemStack.loadItemStackFromNBT(tag);
        if (stack == null || stack.stackSize == 0)
        {
            eydamosBackpackItems = new ArrayList<>();
            return;
        }

//        BackpackSave instance = BackpackSave.loadBackpack(player.worldObj, stack, player, false);
//
//        eydamosBackpackItems = createListFromInventory(instance, EYDAMOS);
//        return;
        IInventory inventory = new InventoryBasic("temp",false,1);
        inventory.setInventorySlotContents(0, stack);

        eydamosBackpackItems = createListFromInventory(inventory, EYDAMOS);

    }

    private void createListOfItemsInIronBackpack()
    {
        if (!TombManyGraves.isIronBackpacksInstalled)
        {
            ironBackpackItems = new ArrayList<>();
            return;
        }
        NBTTagCompound tag = deathList.getTagCompound().getCompoundTag("IronBackpacks");
        ItemStack stack = ItemStack.loadItemStackFromNBT(tag);
        if (stack == null || stack.stackSize == 0)
        {
            ironBackpackItems = new ArrayList<>();
            return;
        }

        IInventory inventory = new InventoryBackpack(stack, true);

        ironBackpackItems = createListFromInventory(inventory, IRON);
    }

    private List<String> createBoringListFromInventory(IInventory inventory)
    {
        List<String> stringList = new ArrayList<>();

        for (int i=0;i<inventory.getSizeInventory();i++)
        {
            if (inventory.getStackInSlot(i)!=null && inventory.getStackInSlot(i).stackSize > 0)
            {
                String name = inventory.getStackInSlot(i).getDisplayName();
                stringList.add(name + (inventory.getStackInSlot(i).stackSize>1 ? " x" + inventory.getStackInSlot(i).stackSize : ""));
                Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(inventory.getStackInSlot(i));
                for (Enchantment key : enchants.keySet())
                {
                    stringList.add("  -> " + key.getTranslatedName(enchants.get(key)));
                }
            }
        }
        return stringList;
    }


    private List<String> createListFromInventory(IInventory inventory,String sectionName)
    {
        List<String> stringList = new ArrayList<String>();
        stringList.add(LINE);
        stringList.add(sectionName);
        stringList.add(LINE);

        int itemNumber = 1;

        for (int i=0; i<inventory.getSizeInventory();i++)
        {
            if (inventory.getStackInSlot(i)!=null && inventory.getStackInSlot(i).stackSize > 0)
            {
                ItemStack inSlot = inventory.getStackInSlot(i);
                String name = inSlot.getDisplayName();
                if (name.length()>28)
                {
                    name = name.substring(0,25) + "...";
                }
                stringList.add(itemNumber + ") " + name + (inSlot.stackSize>1 ? " x" + inSlot.stackSize : ""));
                Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(inSlot);
                for (Enchantment key : enchants.keySet())
                {
                    stringList.add("  -> " + key.getTranslatedName(enchants.get(key)));// key.getName() + ": " + enchants.get(key));
                }

                //handle the case in which the item is, in fact, an Eydamos Backpack (state the items in the pack)
                if (TombManyGraves.isEydamosBackpacksInstalled && inSlot.getItem() instanceof ItemBackpack)
                {
                    BackpackSave instance = BackpackSave.loadBackpack(player.worldObj, inSlot, player, false);
                    List<String> boringList = createBoringListFromInventory(instance);
                    if (boringList.size() > 0) {
                        stringList.add("  Backpack contents:");
                        for (int j = 0; j < boringList.size(); j++) {
                            stringList.add("  ->" + boringList.get(j));
                        }
                    }
                    else
                    {
                        stringList.add("  (Backpack empty)");
                    }
                }
                if (TombManyGraves.isExpandableBackpacksInstalled && inSlot.getItem() instanceof Backpack)
                {
                    List<ItemStack> itemStacks = BackpackInventory.getStacks(inSlot);
                    if (itemStacks.size() > 0)
                    {
                        stringList.add("  Backpack contents:");
                        IInventory basicInventory = new InventoryBasic("verytemp",false,itemStacks.size());
                        for (int j=0; j <itemStacks.size(); j++)
                        {
                            basicInventory.setInventorySlotContents(j,itemStacks.get(j));
                        }
                        List<String> boringList = createBoringListFromInventory(basicInventory);
                        for (String item : boringList)
                        {
                            stringList.add("  ->" + item);
                        }
                    }
                    else
                    {
                        stringList.add("  (Backpack empty");
                    }
                }
                if (TombManyGraves.isIronBackpacksInstalled && inSlot.getItem() instanceof gr8pefish.ironbackpacks.items.backpacks.ItemBackpack)
                {
                    IInventory backpackInventory = new InventoryBackpack(inSlot, true);
                    List<ItemStack> itemStacks = new ArrayList<>();
                    for (int j=0; j<backpackInventory.getSizeInventory();j++)
                    {
                        ItemStack stack = backpackInventory.getStackInSlot(j);
                        if (stack!=null)
                        {
                            itemStacks.add(stack);
                        }
                    }
                    if (itemStacks.size() > 0)
                    {
                        stringList.add(" Backpack contents:");
                        IInventory basicInventory = new InventoryBasic("verytemp", false, itemStacks.size());
                        for (int j=0; j< itemStacks.size(); j++)
                        {
                            basicInventory.setInventorySlotContents(j, itemStacks.get(j));
                        }
                        List<String> boringList = createBoringListFromInventory(basicInventory);
                        for (String item : boringList)
                        {
                            stringList.add("  ->" + item);
                        }
                    }
                    else
                    {
                        stringList.add("  (Backpack empty)");
                    }
                }
                itemNumber += 1;
            }
        }

        return stringList;
    }

    public void bindTexture(String filename)
    {
        bindTexture(TombManyGraves.MODID, filename);
    }

    public void bindTexture(String base, String filename)
    {
        mc.getTextureManager().bindTexture(new ResourceLocation(base, "textures/gui/" + filename));
    }

    public void drawTexture(int x, int y, int textureX, int textureY, int width, int height)
    {
        drawTexturedModalRect(x,y,textureX,textureY,width,height);
    }

    public int getGuiLeft()
    {
        return (this.width - this.xSize) / 2;
    }

    public int getGuiTop()
    {
        return (this.height - this.ySize) / 2;
    }

    public boolean inBounds(int x, int y, int w, int h, int ox, int oy)
    {
//        LogHelper.info(x + "," + y + "," + w + "," + h + "," + ox + "," + oy + " ; top: " + getGuiTop() + ", left: " + getGuiLeft());
        return ox - getGuiLeft() >= + x && ox - getGuiLeft() <= x + w && oy - getGuiTop() >= y && oy - getGuiTop()<= y + h;
    }

    public int getxSize()
    {
        return xSize;
    }

    public int getySize()
    {
        return ySize;
    }
}
