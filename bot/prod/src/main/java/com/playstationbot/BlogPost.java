package com.playstationbot;

import java.util.Date;
import java.util.HashSet;

public class BlogPost {
    private int postId;
    private String postLink = null;
    private String title = null;
    private Date publishedTime;
    private Date updatedTime;
    private HashSet<String> tags = new HashSet<String>();

    public BlogPost(String postId, String postLink) {
        this.postId = parsePostId(postId);
        this.postLink = postLink;
    }

    private static int parsePostId(String postId) {
        int result = -1;

        StringBuilder sb = new StringBuilder();
        
        for(int i = 0; i < postId.length(); i++) {
            if(Character.isDigit(postId.charAt(i))) {
                sb.append(postId.charAt(i));
            }
        }

        if(sb.length() > 0) {
            result = Integer.parseInt(sb.toString());
        }

        return result;
    }

    public int getPostId() {
        return this.postId;
    }

    public String getPostLink() {
        return this.postLink;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String value) {
        this.title = value;
    }

    public Date getPublishedTime() {
        return this.publishedTime;
    }

    public void setPublishedTime(Date value) {
        this.publishedTime = value;
    }

    public Date getUpdatedTime() {
        return this.updatedTime;
    }

    public void setUpdatedTime(Date value) {
        this.updatedTime = value;
    }   
    
    public void addTag(String value) {
        this.tags.add(value);
    }

    public HashSet<String> getTags() {
        return this.tags;
    }
}
