package com.telekom.m2m.cot.restsdk.alarm;

import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

import java.util.Date;

/**
 * Created by breucking on 22.09.16.
 */
public class Alarm extends ExtensibleObject {

    public static final String STATE_ACTIVE = "ACTIVE";
    public static final String STATE_ACKNOWLEDGED = "ACKNOWLEDGED";
    public static final String STATE_CLEARED = "CLEARED";

    public static final String SEVERITY_CRITICAL = "CRITICAL";
    public static final String SEVERITY_MAJOR = "MAJOR";
    public static final String SEVERITY_MINOR = "MINOR";
    public static final String SEVERITY_WARNING = "WARNING";


    public Alarm(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }

    public Alarm() {
        super();
    }

    public void setId(String id) {
        anyObject.put("id", id);
    }

    public String getId() {
        return (String) anyObject.get("id");
    }

    public String getType() {
        return (String) anyObject.get("type");
    }

    public String getText() {
        return (String) anyObject.get("text");
    }

    public Date getCreationTime() {
        return (Date) anyObject.get("creationTime");

    }

    public Date getTime() {
        return (Date) anyObject.get("time");
    }

    public ManagedObject getSource() {
        return (ManagedObject) anyObject.get("source");
    }

    public void setText(String text) {
        anyObject.put("text", text);
    }

    public void setType(String type) {
        anyObject.put("type", type);
    }

    public void setTime(Date time) {
        anyObject.put("time", time);
    }

    public void setSource(ManagedObject source) {
        anyObject.put("source", source);
    }

    public void setStatus(String status) {
        anyObject.put("status", status);
    }

    public void setSeverity(String severity) {
        anyObject.put("severity", severity);
    }
}
