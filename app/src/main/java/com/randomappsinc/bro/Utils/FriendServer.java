package com.randomappsinc.bro.Utils;

import android.content.Context;

import com.randomappsinc.bro.Models.Friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderchiou on 8/20/15.
 */
public class FriendServer
{
    private static FriendServer instance;
    private List<Friend> friends;
    private Map<String, String> numberToName;

    public static FriendServer getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new FriendServer(context);
        }
        return instance;
    }

    private FriendServer(Context context)
    {
        friends = ContactUtils.getPhoneFriends(context.getContentResolver());
        numberToName = new HashMap<>();
        for (Friend friend : friends)
        {
            numberToName.put(friend.getPhoneNumber(), friend.getName());
        }
    }

    public List<Friend> getMatches(String prefix)
    {
        int indexOfMatch = binarySearch(prefix);
        if (prefix.isEmpty())
        {
            return new ArrayList<>(friends);
        }
        else if (indexOfMatch == -1)
        {
            return new ArrayList<>();
        }
        else
        {
            List<Friend> matchingFriends = new ArrayList<>();
            matchingFriends.add(friends.get(indexOfMatch));

            String cleanPrefix = prefix.toLowerCase();
            for (int i = indexOfMatch - 1; i >= 0; i--)
            {
                String friendSubstring = getSubstring(friends.get(i).getName(), prefix);
                if (friendSubstring.equals(cleanPrefix)) {matchingFriends.add(0, friends.get(i));}
                else {break;}
            }
            for (int i = indexOfMatch + 1; i < friends.size(); i++)
            {
                String friendSubstring = getSubstring(friends.get(i).getName(), prefix).toLowerCase();
                if (friendSubstring.equals(cleanPrefix)) {matchingFriends.add(friends.get(i));}
                else {break;}
            }
            return matchingFriends;
        }
    }

    public String getNameWithNumber(String phoneNumber)
    {
        return numberToName.get(phoneNumber);
    }

    // Returns index of first word with given prefix (-1 if it's not found)
    private int binarySearch(String prefix)
    {
        String cleanPrefix = prefix.toLowerCase();

        int lo = 0;
        int hi = friends.size() - 1;
        while (lo <= hi)
        {
            int mid = lo + (hi - lo) / 2;
            int compareToValue = cleanPrefix.compareTo(getSubstring(friends.get(mid).getName(), cleanPrefix));
            if (compareToValue < 0)
            {
                hi = mid - 1;
            }
            else if (compareToValue > 0)
            {
                lo = mid + 1;
            }
            else
            {
                return mid;
            }
        }
        return -1;
    }

    private String getSubstring(String main, String prefix)
    {
        if (prefix.length() > main.length())
        {
            return main.toLowerCase();
        }
        return main.substring(0, prefix.length()).toLowerCase();
    }
}