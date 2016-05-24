package com.m4thg33k.tombmanygraves.lib;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class TombManyGravesConfigs {

    public static Configuration config;

    public static boolean ENABLE_GRAVES;
    public static int GRAVE_RANGE;
    public static boolean ALLOW_GRAVES_IN_WATER;
    public static boolean ALLOW_GRAVES_IN_FLOWING_WATER;
    public static boolean ALLOW_GRAVES_IN_LAVA;
    public static boolean ALLOW_GRAVES_IN_FLOWING_LAVA;

    public static boolean DEFAULT_TO_LOCKED;
    public static boolean ALLOW_GRAVE_ROBBING;

    public static boolean ALLOW_LOCKING_MESSAGES;
    public static boolean REQUIRE_SNEAKING;

    public static boolean ALLOW_INVENTORY_SAVES;

    public static void preInit(FMLPreInitializationEvent event)
    {
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        ENABLE_GRAVES = config.get("Graves","enableGraves",true,"Set to false to disable graves spawning upon player deaths, saving their inventory. (Defaults to true)").getBoolean();
        GRAVE_RANGE = config.get("Graves","graveSearchRadius",9,"This is the radius that will be searched to find an air block to place the grave. (Radius = abs(max{x,y,z})). Note: if death happens with y<0, it will center its search around y=1 instead. (Defaults to 9)",0,25).getInt();
        if (GRAVE_RANGE<0)
        {
            GRAVE_RANGE = 0;
        }
        else if (GRAVE_RANGE > 32)
        {
            GRAVE_RANGE = 32;
        }


        ALLOW_GRAVES_IN_WATER = config.get("Graves","allowGravesInWater",true,"If this is true, graves will be able to replace still water blocks. (Defaults to true)").getBoolean();
        ALLOW_GRAVES_IN_FLOWING_WATER = config.get("Graves","allowGravesInFLOWINGWater",true,"If this is true, graves will be able to replace flowing water blocks. (Defaults to true)").getBoolean();
        ALLOW_GRAVES_IN_LAVA = config.get("Graves","allowGravesInLava",true,"If this is true, graves will be able to replace still lava blocks. (Defaults to true)").getBoolean();
        ALLOW_GRAVES_IN_FLOWING_LAVA = config.get("Graves","allowGravesInFLOWINGLava",true,"If this is true, graves will be able to replace flowing lava blocks. (Defaults to true)").getBoolean();

        DEFAULT_TO_LOCKED = config.get("Graves","defaultToLocked",false,"If this is true, spawned graves will default to being locked and will need to be unlocked (via shift-clicking) in order to get items back. (Defaults to true)").getBoolean();

        ALLOW_GRAVE_ROBBING = config.get("Graves","allowGraveRobbing",false,"If set to true, this will allow *any* player (not just the one who died) to lock/unlock graves and gather their items. (Defaults to false)").getBoolean();

        ALLOW_LOCKING_MESSAGES = config.get("Graves","allowLockingMessages",false,"If set to true, this will send chat messages to a player when the lock/unlock a grave. (Defaults to false)").getBoolean();
        REQUIRE_SNEAKING = config.get("Graves","requireSneaking",true,"If set to true, players will be required to sneak to their grave to get their items back; otherwise any contact at all will allow retrieval. (Defaults to true)").getBoolean();

        ALLOW_INVENTORY_SAVES = config.get("Inventory","allowInventorySaves",true,"If set to true, a file will be generated for each player death, allowing OPs to restore a player's inventory from the file. (Defaults to true)").getBoolean();

        config.save();
    }
}
