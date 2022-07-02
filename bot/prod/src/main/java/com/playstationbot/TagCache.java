package com.playstationbot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TagCache {
    private static HashMap<String, Tag> tagMap = initializeFromDynamoDb();

    public static Tag getTag(String tag) {
        Tag result = null;

        if(tagMap.containsKey(tag)) {
            result = tagMap.get(tag);
        }

        return result;
    }

    private static HashMap<String, Tag> initializeFromDynamoDb() {
        return DynamoDbUtility.getTagItems();
    }

    public static void insertTag(String tagName, Date lastSeen, boolean addToDynamoDb) {
        boolean insertedOrUpdated = false;

        Tag tag = tagMap.get(tagName);

        if(tag == null) {
            tag = new Tag(tagName);
            tag.setLastSeen(lastSeen);
            tagMap.put(tagName, tag);
            insertedOrUpdated = true;
        }
        else {
            if(lastSeen.compareTo(tag.getLastSeen()) > 0) {
                tag.setLastSeen(lastSeen);
                tagMap.put(tagName, tag);
                insertedOrUpdated = true;
            }
        }

        if(addToDynamoDb && insertedOrUpdated) {
            DynamoDbUtility.upsertTag(tag);
        }
    }

    public static String getTagList() {
        StringBuilder result = new StringBuilder();

        //this can be more efficient...
        List<String> tags = new ArrayList<String>();
        
        tags.addAll(tagMap.keySet());

        Collections.sort(tags);

        for(String tag : tags) {
            result.append(tag + "\n");
        }

        return result.toString();
    }
}
