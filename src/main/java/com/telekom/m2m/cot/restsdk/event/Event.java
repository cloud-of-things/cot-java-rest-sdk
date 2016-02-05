package com.telekom.m2m.cot.restsdk.event;

import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

import java.util.Date;

/**
 * Created by breucking on 04.02.16.
 */
public class Event extends ExtensibleObject {

    public Event() {
        super();
    }

    public Event(ExtensibleObject extensibleObject) {
        super (extensibleObject);
    }

    public String getId() {
        return (String) anyObject.get("id");
    }

    public void setId(String id) {
        anyObject.put("id", id);
    }

    public String getType() {
        return (String) anyObject.get("type");
    }

    public void setType(String type) {
        anyObject.put("type", type);
    }

    public String getText() {
        return (String) anyObject.get("text");
    }

    public void setText(String text) {
        anyObject.put("text", text);
    }

    public Date getCreationTime() {
        return (Date) anyObject.get("creationTime");
    }

    public void setCreationTime(Date creationTime) {
        anyObject.put("creationTime", creationTime);
    }

    public Date getTime() {
        return (Date) anyObject.get("time");
    }

    public void setTime(Date time) {
        anyObject.put("time", time);
    }

    public void setSource(ManagedObject source) {
        anyObject.put("source", source);
    }

    public ManagedObject getSource() {
        return (ManagedObject) anyObject.get("source");
    }


}
