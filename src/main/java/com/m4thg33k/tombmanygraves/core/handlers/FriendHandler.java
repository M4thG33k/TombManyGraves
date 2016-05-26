package com.m4thg33k.tombmanygraves.core.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.m4thg33k.tombmanygraves.TombManyGraves;
import com.m4thg33k.tombmanygraves.core.util.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FriendHandler {

    private static ArrayList<Friends> friendList;
    private static String FILE_PREFIX = "/TooManyGravesData/friendList";
    private static String FILENAME = "/friends.json";

    public static void importFriendLists()
    {
        BufferedReader reader = null;
        try{
            LogHelper.info(TombManyGraves.file + FILE_PREFIX + FILENAME);
            checkFilePath();
            reader = new BufferedReader(new FileReader(TombManyGraves.file + FILE_PREFIX + FILENAME));
            Gson gson = new GsonBuilder().create();
            friendList = new ArrayList<Friends>(Arrays.asList(gson.fromJson(reader, Friends[].class)));
            reader.close();
        }
        catch (Exception e)
        {
            LogHelper.error("");
            LogHelper.error("Got an error while importing Friends");
            renameFile();
            friendList = null;
            friendList = new ArrayList<Friends>();
        }

        writeFriends();
    }

    public static void checkFilePath()
    {
        File file = new File(TombManyGraves.file + FILE_PREFIX);
        if (!file.exists())
        {
            file.mkdirs();
        }
    }

    public static boolean hasAsFriend(UUID player1, UUID player2)
    {
        if (friendList != null)
        {
            for (Friends fList : friendList)
            {
                if (fList.getOwner().equals(player1))
                {
                    return fList.isFriend(player2);
                }
            }
            return false;
        }
        return false;
    }
    public static void writeFriends()
    {
        checkFilePath();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter file = new FileWriter(TombManyGraves.file + FILE_PREFIX + FILENAME))
        {
            file.write(gson.toJson(friendList));
            file.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void renameFile()
    {
        String filename_error = "/friends_ERROR";
        File oldName = new File(TombManyGraves.file + FILE_PREFIX + FILENAME);
        File newName = new File(TombManyGraves.file + FILE_PREFIX + filename_error);

        boolean success = oldName.renameTo(newName);
        if (!success)
        {
            LogHelper.error("Something is very wrong.");
        }
    }

    public static boolean hasFriendList(UUID playerID)
    {
        for (Friends friends : friendList)
        {
            if (friends.getOwner().equals(playerID))
            {
                return true;
            }
        }
        return false;
    }

    public static void createFriendListFor(UUID playerID)
    {
        if (!hasFriendList(playerID))
        {
            friendList.add(new Friends(playerID));
            writeFriends();
        }
    }

    public static Friends getFriendListFor(UUID playerID)
    {
        for (Friends friends : friendList)
        {
            if (friends.getOwner().equals(playerID))
            {
                return friends;
            }
        }
        return null;
    }

    public static List<String> getFriendStringListFor(MinecraftServer server, UUID playerID)
    {
        List<UUID> friendIDs = getFriendListFor(playerID).getFriends();

        List<String> friendNames = new ArrayList<String>();

        for (UUID id : friendIDs)
        {
            friendNames.add(server.getPlayerProfileCache().getProfileByUUID(id).getName());
        }

        return friendNames;
    }

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent event)
    {
        if (event.getEntity() instanceof EntityPlayer)
        {
            FriendHandler.createFriendListFor(event.getEntity().getUniqueID());
        }
    }

    public static boolean addToFriendList(UUID owner, UUID friendToAdd)
    {
        for (Friends friends : friendList)
        {
            if (friends.getOwner().equals(owner))
            {
                boolean toReturn = friends.addFriend(friendToAdd);
                writeFriends();
                return toReturn;
            }
        }

        return false;
    }

    public static void printFriendList(MinecraftServer server, EntityPlayer player)
    {
        UUID playerID = player.getUniqueID();

        ArrayList<UUID> friends = getFriendListFor(playerID).getFriends();

        if (friends.size() == 0)
        {
            player.addChatMessage(new TextComponentString("You have not added any friends yet."));
            return;
        }

        for (UUID id : friends)
        {
            player.addChatMessage(new TextComponentString(server.getPlayerProfileCache().getProfileByUUID(id).getName()));
        }
    }

    public static boolean removeFriend(UUID ownerID, UUID friendID)
    {
        for (Friends friends : friendList)
        {
            if (friends.getOwner().equals(ownerID))
            {
                boolean toReturn = friends.removeFriend(friendID);
                writeFriends();
                return toReturn;
            }
        }

        return false;
    }

    public static void clearFriends(UUID owner)
    {
        for (Friends friends : friendList)
        {
            if (friends.getOwner().equals(owner))
            {
                friends.clearFriends();
                writeFriends();
                return;
            }
        }
    }

    public static boolean isFriendOf(UUID owner, UUID friend)
    {
        for (Friends friends : friendList)
        {
            if (friends.getOwner().equals(owner))
            {
                return friends.isFriend(friend);
            }
        }
        return false;
    }
}
