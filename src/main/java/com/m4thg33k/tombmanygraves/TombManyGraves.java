package com.m4thg33k.tombmanygraves;

import com.m4thg33k.tombmanygraves.blocks.ModBlocks;
import com.m4thg33k.tombmanygraves.core.commands.*;
import com.m4thg33k.tombmanygraves.core.proxy.CommonProxy;
import com.m4thg33k.tombmanygraves.core.util.LogHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;

import java.io.File;
import java.util.Random;

@Mod(modid = TombManyGraves.MODID, name = TombManyGraves.MODNAME, version = TombManyGraves.VERSION)
public class TombManyGraves {

    public static final String MODID = "tombmanygraves";
    public static final String VERSION = "@VERSION@";
    public static final String MODNAME = "TombManyGraves";

    public static boolean isBaublesInstalled = false;
    public static boolean isEnderIOInstalled = false;
    public static boolean isCosmeticArmorInstalled = false;
    public static boolean isExpandableBackpacksInstalled = false;
    public static boolean isEydamosBackpacksInstalled = false;
    public static boolean isThutWearablesInstalled = false;

    public static Random rand = new Random(System.currentTimeMillis());

    public static File file;

    @Mod.Instance
    public static TombManyGraves INSTANCE = new TombManyGraves();

    @SidedProxy(clientSide = "com.m4thg33k.tombmanygraves.core.proxy.ClientProxy", serverSide = "com.m4thg33k.tombmanygraves.core.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preinit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postinit(event);
        isBaublesInstalled = Loader.isModLoaded("Baubles");
        isEnderIOInstalled = Loader.isModLoaded("EnderIO");
        isCosmeticArmorInstalled = Loader.isModLoaded("cosmeticarmorreworked");
        isExpandableBackpacksInstalled = Loader.isModLoaded("expandablebackpack");
        isEydamosBackpacksInstalled = Loader.isModLoaded("backpack");
        isThutWearablesInstalled = Loader.isModLoaded("thut_wearables");
        LogHelper.info("Baubles is" + (isBaublesInstalled ? "" : " NOT") + " installed.");
        LogHelper.info("EnderIO is" + (isEnderIOInstalled ? "" : " NOT") + " installed.");
        LogHelper.info("Cosmetic Armor is" + (isCosmeticArmorInstalled ? "" : "NOT") + " installed.");
        LogHelper.info("Expandable Backpacks is " + (isExpandableBackpacksInstalled ? "" : "NOT " ) + "installed.");
        LogHelper.info("Eydamos's Backpacks is " + (isExpandableBackpacksInstalled ? "" : "NOT " ) + "installed.");
        LogHelper.info("Thut Wearables is " + (isThutWearablesInstalled ? "" : "NOT ") + "installed.");
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {
        ModCommands.initCommands(event);
    }
}
