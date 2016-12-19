package com.telekom.m2m.cot.restsdk.event;

import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

import java.util.Date;

/**
 * Represents an object in the platform.
 * <p>
 * Created by Patrick Steinert on 04.02.16.
 */
public class Event extends ExtensibleObject {

    /**
     * Default construction to create a new event.
     */
    public Event() {
        super();
    }

    /**
     * Internal constructor to create events from base class.
     *
     * @param extensibleObject object from base class.
     */
    public Event(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }

    /**
     * Get the unique identifier of the event.
     * If the Event was retrieved from the platform, it has an ID. If just
     * created, there is no ID.
     *
     * @return String the unique identifier of the event or null if not
     * available.
     */
    public String getId() {
        return (String) anyObject.get("id");
    }

    /**
     * Set the unique identifier of the alarm.
     * Just used internally.
     *
     * @param id the new identifier.
     */
    void setId(String id) {
        anyObject.put("id", id);
    }

    /**
     * Get the type of the event.
     * The type categorizes the event.
     *
     * @return a String with the type or null if not available.
     */
    public String getType() {
        return (String) anyObject.get("type");
    }

    /**
     * Setting the event type.
     *
     * @param type a String with the event type. Use cot_abc_xyz style.
     */
    public void setType(String type) {
        anyObject.put("type", type);
    }

    /**
     * Get the event text.
     * Event text is the human readable description of the event.
     *
     * @return a String with the text.
     */
    public String getText() {
        return (String) anyObject.get("text");
    }

    /**
     * Setting the event text.
     * Can be any type of string, CoT just displays plain text.
     *
     * @param text a String with the text to display.
     */
    public void setText(String text) {
        anyObject.put("text", text);
    }

    /**
     * Get the creation time of the event in the CoT.
     * Just setted by platform on creation, not manipulatable.
     *
     * @return Date object representing the timestamp when event was created in
     * CoT.
     */
    public Date getCreationTime() {
        return (Date) anyObject.get("creationTime");
    }

    /**
     * Get the time when event happened.
     *
     * @return Date object representing the timestamp when event happened.
     */
    public Date getTime() {
        return (Date) anyObject.get("time");
    }

    /**
     * Set the time when event happened.
     *
     * @param time Date object with the time when event happened.
     */
    public void setTime(Date time) {
        anyObject.put("time", time);
    }

    /**
     * Set the {@link ManagedObject} where event happened.
     * Mandatory when creating an event. Don't reset if already created.
     *
     * @param source
     */
    public void setSource(ManagedObject source) {
        anyObject.put("source", source);
    }

    /**
     * Get the ManagedObject where the event originated from.
     *
     * @return the originating {@link ManagedObject}
     */
    public ManagedObject getSource() {
        return (ManagedObject) anyObject.get("source");
    }


}
