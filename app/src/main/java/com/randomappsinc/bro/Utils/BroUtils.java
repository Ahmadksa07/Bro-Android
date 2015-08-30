package com.randomappsinc.bro.Utils;

import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import com.randomappsinc.bro.Models.Record;
import com.randomappsinc.bro.Persistence.PreferencesManager;
import com.randomappsinc.bro.Persistence.RecordDataSource;
import com.randomappsinc.bro.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by alexanderchiou on 8/25/15.
 */
public class BroUtils
{
    public static final String APP_LINK_MESSAGE = "\n\nJoin the brovolution: " +
            "https://play.google.com/store/apps/details?id=com.randomappsinc.bro";

    public static List<String> getMessageOptions(Context context)
    {
        List<String> messageOptions = new ArrayList<>();
        messageOptions.add("Bro");
        int numFriendsInvited = PreferencesManager.get(context).getInvitedPhoneNumbers().size();
        if (numFriendsInvited >= 1)
        {
            messageOptions.add("Brah");
        }
        if (numFriendsInvited >= 2)
        {
            messageOptions.add("Bruh");
        }
        if (numFriendsInvited >= 3)
        {
            messageOptions.add("Broski");
        }
        if (numFriendsInvited >= 4)
        {
            messageOptions.add("Broseph");
        }
        if (numFriendsInvited >= 5)
        {
            messageOptions.add("Brochacho");
        }
        return messageOptions;
    }

    public static String processBro(Context context, Record record, boolean sendInvite)
    {
        String message = PreferencesManager.get(context).getMessage();
        String textMessage = message;
        String statusMessage = record.getEventDeclaration();
        Set<String> invitedPhoneNumbers = PreferencesManager.get(context).getInvitedPhoneNumbers();
        if (sendInvite)
        {
            if (invitedPhoneNumbers.contains(record.getTargetPhoneNumber()))
            {
                statusMessage += "You have already shared Bro with this friend, so we didn't add a link to your text.";
            }
            else
            {
                textMessage += APP_LINK_MESSAGE;
                String unlockedMessage = getUnlockedMessage(invitedPhoneNumbers.size());
                if (!unlockedMessage.isEmpty())
                {
                    statusMessage += " Also, by asking your friend to join the brovolution, " +
                            "you have unlocked the word \"" + unlockedMessage + "\".";
                }
                PreferencesManager.get(context).addInvitedPhoneNumber(record.getTargetPhoneNumber());
            }
        }
        // Update the DB/Shared Preferences
        RecordDataSource.insertRecord(record);
        PreferencesManager.get(context).incrementHighestRecordId();

        // Send the text
        SmsManager.getDefault().sendTextMessage(record.getTargetPhoneNumber(), null, textMessage, null, null);

        // Update history
        Intent intent = new Intent();
        intent.setAction(context.getString(R.string.bro_event_key));
        intent.putExtra(context.getString(R.string.record_key), record);
        context.sendBroadcast(intent);
        return statusMessage;
    }

    public static String getUnlockedMessage(int numFriendsInvited)
    {
        if (numFriendsInvited == 0)
        {
           return "Brah";
        }
        if (numFriendsInvited == 1)
        {
           return "Bruh";
        }
        if (numFriendsInvited == 2)
        {
            return "Broski";
        }
        if (numFriendsInvited == 3)
        {
            return "Broseph";
        }
        if (numFriendsInvited == 4)
        {
            return "Brochacho";
        }
        return "";
    }

    public static int getCurrentMessageIndex(Context context)
    {
        String message = PreferencesManager.get(context).getMessage();
        switch (message)
        {
            case "Bro":
                return 0;
            case "Brah":
                return 1;
            case "Bruh":
                return 2;
            case "Broski":
                return 3;
            case "Broseph":
                return 4;
            case "Brochacho":
                return 5;
            default:
                return 0;
        }
    }
}