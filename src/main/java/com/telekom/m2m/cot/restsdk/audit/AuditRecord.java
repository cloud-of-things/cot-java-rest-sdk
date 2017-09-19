package com.telekom.m2m.cot.restsdk.audit;

import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

import java.util.Date;

/**
 * Represents an audit record in the platform.
 * <p>
 * Created by Andreas Dyck on 24.07.17.
 */
public class AuditRecord extends ExtensibleObject {
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
     * Just an information.
     */
    public static final String SEVERITY_INFORMATION = "INFORMATION";

    /**
     * Constructor to create a new empty audit record.
     */
    public AuditRecord() {
        super();
    }

    /**
     * Internal constructor to create audit records from base class.
     *
     * @param extensibleObject object from base class.
     */
    public AuditRecord(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }

    /**
     * Get the unique identifier of the audit record.
     * If the AuditRecord was retrieved from the platform, it has an ID. If just
     * created, there is no ID.
     *
     * @return String the unique identifier of the audit record or null if not
     * available.
     */
    public String getId() {
        return (String) anyObject.get("id");
    }

    /**
     * Set the unique identifier of the audit record.
     * Just used internally.
     *
     * @param id the new identifier.
     */
    void setId(String id) {
        anyObject.put("id", id);
    }

    /**
     * Get the type of the audit record.
     * The type categorizes the audit record.
     *
     * @return a String with the type or null if not available.
     */
    public String getType() {
        return (String) anyObject.get("type");
    }

    /**
     * Setting the audit record type.
     *
     * @param type a String with the audit record type. Use cot_abc_xyz style.
     */
    public void setType(String type) {
        anyObject.put("type", type);
    }

    /**
     * Get the audit record text.
     * AuditRecord text is the human readable description of the audit record.
     *
     * @return a String with the text.
     */
    public String getText() {
        return (String) anyObject.get("text");
    }

    /**
     * Setting the audit record text.
     * Can be any type of string, CoT just displays plain text.
     *
     * @param text a String with the text to display.
     */
    public void setText(String text) {
        anyObject.put("text", text);
    }

    /**
     * Get the creation time of the audit record in the CoT.
     * Just set by platform on creation, not manipulatable.
     *
     * @return Date object representing the timestamp when audit record was created in
     * CoT.
     */
    public Date getCreationTime() {
        return (Date) anyObject.get("creationTime");
    }

    /**
     * Get the time when audit entry was recorded.
     *
     * @return Date object representing the timestamp when audit entry was recorded.
     */
    public Date getTime() {
        return (Date) anyObject.get("time");
    }

    /**
     * Set the time when audit entry was recorded.
     *
     * @param time Date object with the time when audit entry was recorded.
     */
    public void setTime(Date time) {
        anyObject.put("time", time);
    }

    /**
     * Set the {@link ManagedObject} where audit entry was recorded.
     *
     * @param source the source ManagedObject where the audit entry was recorded.
     */
    public void setSource(ManagedObject source) {
        anyObject.put("source", source);
    }

    /**
     * Get the ManagedObject where the audit record originated from.
     *
     * It concerns not only the id of a ManagedObject,
     * but in case of "Operation" type - operation id
     * and in case of Alarm type - alarm Id.
     *
     * @return the originating {@link ManagedObject}
     */
    public ManagedObject getSource() {
        Object source = anyObject.get("source");
        // since source value can be set as ManagedObject via setter in regular way
        // and as ExtensibleObject via gson ExtensibleObjectSerializer.
        // At first we need to check the type to avoid an unnecessary wrap into ManagedObject
        if(source instanceof ManagedObject) {
            return (ManagedObject)source;
        }
        return new ManagedObject((ExtensibleObject) source);
    }

    /**
     * Set the audit record activity.
     *
     * @param activity a String with the activity of the audit record.
     */
    public void setActivity(String activity) {
        anyObject.put("activity", activity);
    }

    /**
     * Get the activity of the audit record.
     *
     * @return a String with the activity of the audit record.
     */
    public String getActivity() {
        return (String) anyObject.get("activity");
    }

    /**
     * Set the application which is creating this audit record.
     *
     * @param application a String with the application which is creating this audit record.
     */
    public void setApplication(String application) {
        anyObject.put("application", application);
    }

    /**
     * Get the application which created this audit record.
     *
     * @return a String with the application which created this audit record.
     */
    public String getApplication() {
        return (String) anyObject.get("application");
    }

    /**
     * Set the user which is creating this audit record.
     *
     * @param user a String with the user which is creating this audit record.
     */
    public void setUser(String user) {
        anyObject.put("user", user);
    }

    /**
     * Get the user which created this audit record.
     *
     * @return a String with the user which created this audit record.
     */
    public String getUser() {
        return (String) anyObject.get("user");
    }

    /**
     * Set the severity of the activity.
     *
     * @param severity a String with the severity of the activity.
     */
    public void setSeverity(String severity) {
        anyObject.put("severity", severity);
    }

    /**
     * Get the severity of the activity.
     *
     * @return a String with the severity of the activity.
     */
    public String getSeverity() {
        return (String) anyObject.get("severity");
    }
}
