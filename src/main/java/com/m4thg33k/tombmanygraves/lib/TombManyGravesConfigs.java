package com.m4thg33k.tombmanygraves.lib;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class TombManyGravesConfigs {

    public static Configuration config;

    public static boolean ENABLE_GRAVES;
    public static int GRAVE_RANGE;
    public static boolean VOID_SEARCH_1;
    public static boolean ALLOW_GRAVES_IN_WATER;
    public static boolean ALLOW_GRAVES_IN_FLOWING_WATER;
    public static boolean ALLOW_GRAVES_IN_LAVA;
    public static boolean ALLOW_GRAVES_IN_FLOWING_LAVA;
    public static boolean ALLOW_GRAVES_ON_PLANTS;

    public static boolean DEFAULT_TO_LOCKED;
    public static boolean ALLOW_GRAVE_ROBBING;

    public static boolean ALLOW_LOCKING_MESSAGES;
    public static boolean REQUIRE_SNEAKING;

    public static boolean ALLOW_INVENTORY_SAVES;

    public static boolean FORCE_DIRT_RENDER;

    public static boolean PRINT_DEATH_LOG;

    public static boolean DROP_ITEMS_ON_GROUND;
    public static boolean GIVE_PRIORITY_TO_GRAVE_ITEMS;
    public static boolean DISPLAY_GRAVE_NAME;

    public static int GRAVE_SKULL_RENDER_TYPE;

    public static boolean ASCEND_LIQUID;

    public static void preInit(FMLPreInitializationEvent event)
    {
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        ENABLE_GRAVES = config.get("Graves","enableGraves",true,"Set to false to disable graves spawning upon player deaths, saving their inventory. (Defaults to true)").getBoolean();
        GRAVE_RANGE = config.get("Graves","graveSearchRadius",9,"This is the radius that will be searched to find an air block to place the grave. (Radius = abs(max{x,y,z})). Note: if death happens with y<0, it will center its search around y=graveRadius (unless 'startVoidSearchAt1' is true). (Defaults to 9, max of 32)",0,32).getInt();
        if (GRAVE_RANGE<0)
        {
            GRAVE_RANGE = 0;
        }
        else if (GRAVE_RANGE > 32)
        {
            GRAVE_RANGE = 32;
        }
        VOID_SEARCH_1 = config.get("Graves", "startVoidSearchAt1",false,"If true, when a player dies with y < 0, the grave will start searching for a valid location at y = 1 instead. If false, it will start searching at y = GRAVE_RANGE. (Defaults to false.)").getBoolean();



        ALLOW_GRAVES_IN_WATER = config.get("Graves","allowGravesInWater",true,"If this is true, graves will be able to replace still water blocks. (Defaults to true)").getBoolean();
        ALLOW_GRAVES_IN_FLOWING_WATER = config.get("Graves","allowGravesInFLOWINGWater",true,"If this is true, graves will be able to replace flowing water blocks. (Defaults to true)").getBoolean();
        ALLOW_GRAVES_IN_LAVA = config.get("Graves","allowGravesInLava",true,"If this is true, graves will be able to replace still lava blocks. (Defaults to true)").getBoolean();
        ALLOW_GRAVES_IN_FLOWING_LAVA = config.get("Graves","allowGravesInFLOWINGLava",true,"If this is true, graves will be able to replace flowing lava blocks. (Defaults to true)").getBoolean();

        ALLOW_GRAVES_ON_PLANTS = config.get("Graves", "allowGravesOnPlants", true, "If this is true, graves will be able to replace any/all plants (anything that implements IPlantable). (Defaults to true)").getBoolean();

        DEFAULT_TO_LOCKED = config.get("Graves","defaultToLocked",false,"If this is true, spawned graves will default to being locked and will need to be unlocked (via shift-clicking) in order to get items back. (Defaults to true)").getBoolean();

        ALLOW_GRAVE_ROBBING = config.get("Graves","allowGraveRobbing",false,"If set to true, this will allow *any* player (not just the one who died) to lock/unlock graves and gather their items. (Defaults to false)").getBoolean();

        ALLOW_LOCKING_MESSAGES = config.get("Graves","allowLockingMessages",false,"If set to true, this will send chat messages to a player when the lock/unlock a grave. (Defaults to false)").getBoolean();
        REQUIRE_SNEAKING = config.get("Graves","requireSneaking",true,"If set to true, players will be required to sneak to their grave to get their items back; otherwise any contact at all will allow retrieval. (Defaults to true)").getBoolean();

        ALLOW_INVENTORY_SAVES = config.get("Inventory","allowInventorySaves",true,"If set to true, a file will be generated for each player death, allowing OPs to restore a player's inventory from the file. Note that these files are *per machine*, that means you could potentially restore inventories from other worlds in single-player (or servers if you don't delete the inventory data). (Defaults to true)").getBoolean();

        FORCE_DIRT_RENDER = config.get("Graves","forceDirtRender",false,"If true, all graves will render as either a floating head or with the dirt texture and will not adapt to the texture beneath it. (You can set this to true to fix specific client-side crashes.) (Defaults to false)").getBoolean();

        PRINT_DEATH_LOG = config.get("Logs","printDeathLog",true,"If true, the log will print the location of a player's death each time they die. (Defaults to true)").getBoolean();

        DROP_ITEMS_ON_GROUND = config.get("Graves","dropItemsOnGround",false,"If true, the graves will drop all items on the ground when 'broken' instead of attempting to place them in their original slots. (Defaults to false)").getBoolean();
        GIVE_PRIORITY_TO_GRAVE_ITEMS = config.get("Graves","givePriorityToGraveItems",true,"If true, grave items will be returned to their original slots even if they aren't empty. Any item that was in that slot will be dropped on the ground instead. You can right-click your grave to change to the alternate behavior in-game. (Defaults to true)").getBoolean();
        DISPLAY_GRAVE_NAME = config.get("Graves","displayGraveName",true,"If true, graves will display their owner's name above them when looking at the block. (Defaults to true)").getBoolean();

        GRAVE_SKULL_RENDER_TYPE = config.get("Graves","graveSkullRenderType",3,"Changing this value changes how the grave skull renders. 0 = Skeleton, 1 = Wither Skeleton, 2 = Zombie, 3 = Player, 4 = Creeper. Any value outside this range will be set to 3 instead. (Defaults to 3)").getInt();
        if (GRAVE_SKULL_RENDER_TYPE < 0 || GRAVE_SKULL_RENDER_TYPE > 4)
        {
            GRAVE_SKULL_RENDER_TYPE = 3;
        }

        ASCEND_LIQUID = config.get("Graves","ascendLiquid",false,"Setting this to true will have the grave try to place itself above bodies of liquid (water, lava, etc...). If a valid location is not found at the top, it will still attempt to place the grave near the actual location of death. (Defaults to false)").getBoolean();

        config.save();
    }
}
