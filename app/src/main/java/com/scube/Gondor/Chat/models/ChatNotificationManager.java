package com.scube.Gondor.Chat.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by srikanthsridhara on 6/13/15.
 */
public class ChatNotificationManager {

    // This class is used to manage the chat notifications in the top bar
    private static ChatNotificationManager chatNotificationManager;
    private Map<String, Integer> notificationIdMap = new HashMap<String, Integer>();
    private Map<String, Integer> notificationCountMap = new HashMap<String, Integer>();
    private Map<String, List<String>> notificationChatsMap = new HashMap<String, List<String>>();
    private int notificationIdCounter = 0;
    private int notificationId = 1;

    // If it is initialized for the first time, call a constructor
    public static synchronized ChatNotificationManager getChatNotificationManager() {
        if (chatNotificationManager == null) {
            chatNotificationManager = new ChatNotificationManager();
        }
        return chatNotificationManager;
    }

    // We need a unique notification Id for
    public int getIncrementingNotificationId() {
        int idToReturn = notificationId;
        notificationId++;
        return idToReturn;
    }

    public int getNotificationId(String s, String data) {

        if (notificationIdMap.size() == 0) {
            notificationIdMap.put(s, notificationIdCounter);
            notificationCountMap.put(s, 1);

            List<String> chatList = new ArrayList<String>();
            chatList.add(data);
            notificationChatsMap.put(s, chatList);

            int i = notificationIdCounter;
            ++notificationIdCounter;
            return i;
        } else {
            Integer count = notificationCountMap.get(s);
            int i = notificationIdMap.get(s);
            count++;
            notificationCountMap.put(s, count);

            List<String> chatList = notificationChatsMap.get(s);
            chatList.add(data);
            notificationChatsMap.put(s, chatList);
            return i;
        }

    }

    public int getNotificationCount(String s) {
        return (notificationCountMap.get(s));
    }

    public List<String> getNotificationData(String s) {
        return (notificationChatsMap.get(s));
    }

    public void removeStringFromMaps(String s) {
        notificationIdMap.remove(s);
        notificationCountMap.remove(s);
        notificationChatsMap.remove(s);
    }
}
