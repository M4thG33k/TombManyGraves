package com.m4thg33k.tombmanygraves.core.commands;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class ModCommands {

    public static void initCommands(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandFriend());
        event.registerServerCommand(new CommandFriendList());
        event.registerServerCommand(new CommandRemoveFriend());
        event.registerServerCommand(new CommandClearFriendList());
        event.registerServerCommand(new CommandRestoreInventory());
        event.registerServerCommand(new CommandDropInventory());
        event.registerServerCommand(new CommandGetDeathList());
    }
}
