package com.playstationbot;
public class BlogPost {
    private String postId = null;
    private String postLink = null;

    public BlogPost(String postId, String postLink) {
        this.postId = postId;
        this.postLink = postLink;
    }

    public String getPostId() {
        return this.postId;
    }

    public String getPostLink() {
        return this.postLink;
    }
}
