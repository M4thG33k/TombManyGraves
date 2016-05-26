package com.m4thg33k.tombmanygraves.core.commands;

import com.m4thg33k.tombmanygraves.core.handlers.FriendHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class CommandFriendList implements ICommand{

    public final String COMMAND_NAME = "tmg_friendlist";

    private final List<String> aliases;

    public CommandFriendList()
    {
        aliases = new ArrayList<String>();
        aliases.add("tmg_seefriends");
        aliases.add("tmg_viewfriends");
        aliases.add("tmg_listfriends");
        aliases.add("tmg_list");
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return COMMAND_NAME;
    }

    @Override
    public List<String> getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer)
        {
            FriendHandler.printFriendList(server,(EntityPlayer)sender);
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
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
