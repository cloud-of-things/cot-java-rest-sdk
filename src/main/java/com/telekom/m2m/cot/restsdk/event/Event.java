package com.telekom.m2m.cot.restsdk.event;

import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;

import java.util.Date;

/**
 * Created by breucking on 04.02.16.
 */
public class Event {
    private String id;
    private String type;
    private String text;
    private Date creationTime;
    private Date time;
    private ManagedObject source;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setSource(ManagedObject source) {
        this.source = source;
    }

    public ManagedObject getSource() {
        return source;
    }
}
