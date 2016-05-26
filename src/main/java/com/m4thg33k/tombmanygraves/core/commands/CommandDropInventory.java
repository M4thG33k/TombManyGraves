package com.m4thg33k.tombmanygraves.core.commands;

import com.m4thg33k.tombmanygraves.core.handlers.DeathInventoryHandler;
import com.m4thg33k.tombmanygraves.lib.TombManyGravesConfigs;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandDropInventory implements ICommand {

    public final String COMMAND_NAME = "tmg_drop";

    private final List<String> aliases;

    public CommandDropInventory()
    {
        aliases = new ArrayList<String>();
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return COMMAND_NAME + " [player] <text|timestamp>";
    }

    @Override
    public List<String> getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender.getEntityWorld().isRemote)
        {
            return;
        }

        if (!TombManyGravesConfigs.ALLOW_INVENTORY_SAVES)
        {
            sender.addChatMessage(new TextComponentString("This command has been disabled"));
            return;
        }

        if (args.length < 2)
        {
            sender.addChatMessage(new TextComponentString(getCommandUsage(sender)));
        }

        EntityPlayer player = server.getPlayerList().getPlayerByUsername(args[0]);
        if (player!=null)
        {
            boolean worked = DeathInventoryHandler.dropPlayerInventory(player,args[1]);
            if (!worked)
            {
                sender.addChatMessage(new TextComponentString("Failed to drop inventory."));
                sender.addChatMessage(new TextComponentString("Check spelling and timestamp."));
            }
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? CommandBase.getListOfStringsMatchingLastWord(args, server.getAllUsernames()) : (args.length == 2 ? CommandBase.getListOfStringsMatchingLastWord(args,DeathInventoryHandler.getFilenames(args[0])) : null);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index==0;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

    public int getRequiredPermissionLevel()
    {
        return 4;
    }
}
