package com.playstationbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlogCache {
    private static HashMap<Integer, BlogPost> blogPostMap = new HashMap<Integer, BlogPost>();
    private static List<BlogPost> blogPostOrdered = new ArrayList<BlogPost>();

    //This method in itself is O(1).
    public static boolean needToPost(BlogPost post) {
        boolean result = false;

        //1. if we don't have a matching post in the map, insert it...
        //then look for where it should go into the ordered list and insert it.
        //Then insert to dynamo db, and return true.

        //2. if we DO have a matching post, see if the updated date is newer. if so, let's 
        //update the cache item, update dynamo db, and return true.

        //3. Otherwise, return false. We've seen this and we've posted it.

        //4. while map.size() is greater than 0, expire the oldest object.
        while(blogPostMap.size() > 100) {
            expireBlogPost();
        }

        return result;
    }

    //This is gonna be O(1) in all cases.
    //Simply remove from both the ordered list, and the hashmap.
    private static void expireBlogPost() {
        BlogPost post = blogPostOrdered.remove(0);

        if(blogPostMap.containsKey(post.getPostId()))
            blogPostMap.remove(post.getPostId());
    }

    //This is gonna be O(n) in worst case but usually O(1).
    private static void insertBlogPost(BlogPost post) {

    }

    //This is gonna be O(n) in average case.
    private static void updateBlogPost(BlogPost post) {

    }

    //This is gonna be O(n) in theory, except...we're stopping at 100 records so it's really O(1).
    private static void initializeFromDynamoDb() {

    }
}
