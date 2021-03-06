package com.playstationbot;

import java.util.HashMap;

//Settings are grouped by Guild.
public class SettingCache {
    private static HashMap<String, HashMap<String, String>> settingMap = initializeFromDynamoDb();;

    //This is gonna be O(n). We only keep 50 records but we iterate through them all to make sure we have the 50 most recent.
    public static HashMap<String, HashMap<String, String>> initializeFromDynamoDb() {
        return DynamoDbUtility.getSettingItems();
    }

    //O(1)
    public static String getSetting(String guildId, String settingKey) {
        String result = null;

        if(settingMap.containsKey(guildId) && settingMap.get(guildId).containsKey(settingKey))
            result = settingMap.get(guildId).get(settingKey);

        return result;
    }

    //O(1)
    public static void insertSetting(String guildId, String settingKey, String settingValue, boolean addToDynamoDb) {
        if(settingMap.containsKey(guildId) == false) {
            settingMap.put(guildId, new HashMap<String, String>());
        }
        settingMap.get(guildId).put(settingKey, settingValue);

        if (addToDynamoDb) {
            DynamoDbUtility.upsertSetting(guildId, settingKey, settingValue);
        }
    }
}
