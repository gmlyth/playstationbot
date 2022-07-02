package com.playstationbot;

import java.util.Date;

public class Tag {
    private Date lastSeen;
    private String tag;

    public Tag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return this.tag;
    }

    public Date getLastSeen() {
        return this.lastSeen;
    }

    public void setLastSeen(Date value) {
        this.lastSeen = value;
    }
}
