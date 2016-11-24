package com.telekom.m2m.cot.restsdk.alarm;

import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

import java.util.Date;

/**
 * Alarm represents an alarm in the CoT.
 * Alarms have status to indicate if alarm is active, acknowledged or cleared.
 * Alarms have a severity.
 * <p>
 * Created by breucking on 22.09.16.
 */
public class Alarm extends ExtensibleObject {

    /**
     * State if alarm is active.
     */
    public static final String STATE_ACTIVE = "ACTIVE";

    /**
     * State if alarm is acknowledged (does not escalate).
     */
    public static final String STATE_ACKNOWLEDGED = "ACKNOWLEDGED";

    /**
     * State if alarm is cleared.
     */
    public static final String STATE_CLEARED = "CLEARED";

    /**
     * Critical (highest severity).
     */
    public static final String SEVERITY_CRITICAL = "CRITICAL";

    /**
     * Major (2nd highest severity).
     */
    public static final String SEVERITY_MAJOR = "MAJOR";

    /**
     * Minor(2nd lowest severity).
     */
    public static final String SEVERITY_MINOR = "MINOR";

    /**
     * Warning (lowest severity).
     */
    public static final String SEVERITY_WARNING = "WARNING";

    /**
     * Internal constructor to create an alarm object.
     *
     * @param extensibleObject existing base class object.
     */
    public Alarm(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }

    /**
     * Constructor to create an alarm.
     */
    public Alarm() {
        super();
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
     * Get the unique identifier of the alarm.
     * If the Alarm was retrieved from the platform, it has an ID. If just
     * created, there is no ID.
     *
     * @return String the unique identifier of the alarm or null if not
     * available.
     */
    public String getId() {
        return (String) anyObject.get("id");
    }

    /**
     * Get the type of the alarm.
     * The type categorizes the alarm.
     *
     * @return a String with the type or null if not available.
     */
    public String getType() {
        return (String) anyObject.get("type");
    }

    /**
     * Get the alarm text.
     * Alarm text is the human readable description of the cause of the alarm.
     *
     * @return a String with the text.
     */
    public String getText() {
        return (String) anyObject.get("text");
    }

    /**
     * Get the creation time of the alarm in the CoT.
     *
     * @return Date object representing the timestamp when alarm was created in
     * CoT.
     */
    public Date getCreationTime() {
        return (Date) anyObject.get("creationTime");
    }

    /**
     * Get the time when alarm happened.
     *
     * @return Date object representing the timestamp when alarm happened.
     */
    public Date getTime() {
        return (Date) anyObject.get("time");
    }

    /**
     * Get the ManagedObject where the alarm originated from.
     *
     * @return the originating {@link ManagedObject}
     */
    public ManagedObject getSource() {
        return (ManagedObject) anyObject.get("source");
    }

    /**
     * Setting the alarm text.
     * Can be any type of string, CoT just displays plain text.
     *
     * @param text a String with the text to display.
     */
    public void setText(String text) {
        anyObject.put("text", text);
    }

    /**
     * Setting the alarm type.
     *
     * @param type a String with the alarm type. Use cot_abc_xyz style.
     */
    public void setType(String type) {
        anyObject.put("type", type);
    }

    /**
     * Set the time when alarm happened.
     *
     * @param time Date object with the time when alarm happened.
     */
    public void setTime(Date time) {
        anyObject.put("time", time);
    }

    /**
     * Set the {@link ManagedObject} where alarm happened.
     * Mandatory when creating an alarm. Don't reset if already created.
     *
     * @param source
     */
    public void setSource(ManagedObject source) {
        anyObject.put("source", source);
    }

    /**
     * Setting the current status of the alarm.
     * It is possible to update the status.
     *
     * @param status Use {@link Alarm} members.
     */
    public void setStatus(String status) {
        anyObject.put("status", status);
    }

    /**
     * Setting the current severity of the alarm.
     * It is possible to update the severity.
     *
     * @param severity Use {@link Alarm} members.
     */
    public void setSeverity(String severity) {
        anyObject.put("severity", severity);
    }

    /**
     * Getting the status of the Alarm.
     *
     * @return a String with the status. Use class members STATE_* to compare.
     */
    public String getStatus() {
        return (String) anyObject.get("status");
    }

    /**
     * Getting the severity of the Alarm.
     *
     * @return a String with the severity. Use class members SEVERITY_* to compare.
     */
    public String getSeverity() {
        return (String) anyObject.get("severity");
    }
}
