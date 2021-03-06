package com.playstationbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlogCache {
    private static HashMap<Integer, BlogPost> blogPostMap = new HashMap<Integer, BlogPost>();
    private static List<BlogPost> blogPostOrdered = new ArrayList<BlogPost>();

    static boolean needToPost(BlogPost post) {
        boolean result = true;

        if(blogPostMap.containsKey(post.getPostId()))
            result = false;

        return result;
    }

    //This method in itself is O(1).
    public static void addPost(BlogPost post) {
        if (blogPostMap.containsKey(post.getPostId()) == false) {
            insertBlogPost(post);

            DynamoDbUtility.upsertBlogPost(post);
        }
    }

    //This is gonna be O(1) in all cases.
    //Simply remove from both the ordered list, and the hashmap.
    private static void expireBlogPost() {
        BlogPost post = blogPostOrdered.remove(0);

        if(blogPostMap.containsKey(post.getPostId()))
            blogPostMap.remove(post.getPostId());

        blogPostOrdered.remove(0);
    }

    //This is gonna be O(n) in worst case but usually O(1).
    private static void insertBlogPost(BlogPost post) {
        // insert to map
        blogPostMap.put(post.getPostId(), post);

        boolean inserted = false;

        // look for where it goes in ordered list!
        for (int i = blogPostOrdered.size() - 1; i >= 0; i--) {
            if (post.getPublishedTime().compareTo(blogPostOrdered.get(i).getPublishedTime()) > 0) {
                blogPostOrdered.add(i + 1, post);
                inserted = true;
            }
        }

        if (!inserted) {
            blogPostOrdered.add(0, post);
        }

        // 4. while map.size() is greater than 50, expire the oldest object.
        while (blogPostMap.size() > 50) {
            expireBlogPost();
        }
    }

    //This is gonna be O(n)
    public static void initializeFromDynamoDb() {
        List<BlogPost> posts = DynamoDbUtility.getBlogPostItems();

        for(BlogPost post : posts) {
            insertBlogPost(post);
        }
    }
}
