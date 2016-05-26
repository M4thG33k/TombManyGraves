package com.m4thg33k.tombmanygraves.core.commands;

import com.m4thg33k.tombmanygraves.core.handlers.FriendHandler;
import com.mojang.authlib.GameProfile;
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

public class CommandClearFriendList implements ICommand {

    public final String COMMAND_NAME = "tmg_clearFriends";

    private final List<String> aliases;

    public CommandClearFriendList()
    {
        aliases = new ArrayList<String>();
        aliases.add("tmg_clear");
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
            FriendHandler.clearFriends(((EntityPlayer)sender).getUniqueID());
            sender.addChatMessage(new TextComponentString("Your friend list has been cleared."));
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
