package com.m4thg33k.tombmanygraves.core.handlers;

import java.util.ArrayList;
import java.util.UUID;

public class Friends {

    private UUID myId;
    private ArrayList<UUID> myFriends;

    public Friends(UUID id)
    {
        myId = id;
        myFriends = new ArrayList<UUID>();
    }

    public ArrayList<UUID> getFriends()
    {
        return myFriends;
    }

    public UUID getOwner()
    {
        return myId;
    }

    public boolean addFriend(UUID friendID)
    {
        if (!myFriends.contains(friendID))
        {
            myFriends.add(friendID);
            return true;
        }
        return false;
    }

    public boolean removeFriend(UUID friendID)
    {
        if (myFriends.contains(friendID))
        {
            myFriends.remove(friendID);
            return true;
        }
        return false;
    }

    public void clearFriends()
    {
        myFriends = null;
        myFriends = new ArrayList<UUID>();
    }

    public boolean isFriend(UUID friendID)
    {
        return myFriends.contains(friendID);
    }

}
