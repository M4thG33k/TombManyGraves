package com.m4thg33k.tombmanygraves.core.commands;

import com.m4thg33k.tombmanygraves.core.handlers.FriendHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandRemoveFriend implements ICommand {

    public final String COMMAND_NAME = "tmg_removefriend";

    private final List<String> aliases;

    public CommandRemoveFriend()
    {
        aliases = new ArrayList<String>();
        aliases.add("tmg_deletefriend");
        aliases.add("tmg_unfriend");
        aliases.add("tmg_defriend");
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return COMMAND_NAME + " [player]";
    }

    @Override
    public List<String> getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer)
        {
            if (!((EntityPlayer) sender).worldObj.isRemote)
            {
                if (args.length == 0)
                {
                    sender.addChatMessage(new TextComponentString("Invalid argument: /" + COMMAND_NAME + " [player]"));
                    return;
                }

                UUID playerID = ((EntityPlayer)sender).getUniqueID();
                UUID friendID = server.getPlayerProfileCache().getGameProfileForUsername(args[0]).getId();

                if (FriendHandler.removeFriend(playerID,friendID))
                {
                    sender.addChatMessage(new TextComponentString("Removed " + args[0] + " from your friends."));
                }
                else
                {
                    sender.addChatMessage(new TextComponentString("Unable to remove " + args[0] + " from your friends."));
                    sender.addChatMessage(new TextComponentString("Maybe they never existed?"));
                }
            }
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        if (sender instanceof EntityPlayer)
        {
            return args.length == 1 ? CommandBase.getListOfStringsMatchingLastWord(args,FriendHandler.getFriendStringListFor(server, ((EntityPlayer)sender).getUniqueID())) : null;
        }
        return args.length == 1 ? CommandBase.getListOfStringsMatchingLastWord(args, server.getAllUsernames()) : null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index==0;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
