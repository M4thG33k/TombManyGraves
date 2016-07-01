package com.m4thg33k.tombmanygraves.core.commands;

import com.m4thg33k.tombmanygraves.core.handlers.DeathInventoryHandler;
import com.m4thg33k.tombmanygraves.lib.TombManyGravesConfigs;
import com.m4thg33k.tombmanygraves.tiles.TileDeathBlock;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CommandLoot implements ICommand {

    public final String COMMAND_NAME = "tmg_loot";

    private final List<String> aliases;

    public CommandLoot()
    {
        aliases = new ArrayList<>();
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return COMMAND_NAME + ": <player to give items to> [player who owns grave if different] <x> <y> <z>";
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

        if (args.length < 4 || args.length > 5)
        {
            sender.addChatMessage(new TextComponentString(getCommandUsage(sender)));
            return;
        }

        boolean worked = false;

        EntityPlayer toGive = server.getPlayerList().getPlayerByUsername(args[0]);
        if (toGive != null)
        {
            EntityPlayer owner;
            int x;
            int y;
            int z;

            try {
                if (args.length == 4) {
                    owner = toGive;
                    x = Integer.parseInt(args[1]);
                    y = Integer.parseInt(args[2]);
                    z = Integer.parseInt(args[3]);
                } else {
                    owner = server.getPlayerList().getPlayerByUsername(args[1]);
                    x = Integer.parseInt(args[2]);
                    y = Integer.parseInt(args[3]);
                    z = Integer.parseInt(args[4]);
                }
            } catch (Exception e)
            {
                sender.addChatMessage(new TextComponentString(getCommandUsage(sender)));
                return;
            }

            BlockPos location = new BlockPos(x,y,z);
            TileEntity tileEntity = sender.getEntityWorld().getTileEntity(location);

            if (tileEntity == null || !(tileEntity instanceof TileDeathBlock))
            {
                sender.addChatMessage(new TextComponentString("There is not a grave at that location."));
                return;
            }

            String timestamp = ((TileDeathBlock) tileEntity).getTimestamp();

            if (timestamp == null || timestamp.equals(""))
            {
                sender.addChatMessage(new TextComponentString("The grave was created before this feature was implemented. Sorry. ;_;"));
                return;
            }

            if (owner != null)
            {
                worked = DeathInventoryHandler.dropPlayerInventory(owner, toGive.getPosition(), timestamp);

            }

            if (worked)
            {
                ((TileDeathBlock) tileEntity).clearInventory();
                sender.getEntityWorld().setBlockToAir(location);
            }
        }
        if (!worked)
        {
            sender.addChatMessage(new TextComponentString("Failed to drop inventory."));
            sender.addChatMessage(new TextComponentString("Check spelling and block location."));
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return (args.length == 1 || args.length == 2) ? CommandBase.getListOfStringsMatchingLastWord(args, server.getAllUsernames()) : null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0 || index == 1;
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
