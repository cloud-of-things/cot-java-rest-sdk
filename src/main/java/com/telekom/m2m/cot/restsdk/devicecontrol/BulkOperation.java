package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

/**
 * BulkOperation represents a BulkOperation in the CoT.
 * Bulk operations are used to create operations for a complete group.
 * <p>
 * Created by Patrick Steinert on 02.01.17.
 */
public class BulkOperation extends ExtensibleObject {

    /**
     * Constructor to create a bulk operation.
     */
    public BulkOperation() {
        super();
    }

    /**
     * Internal constructor to create a bulk operation object.
     *
     * @param extensibleObject existing base class object.
     */
    BulkOperation(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }

    /**
     * Get the unique identifier for the bulk operation.
     *
     * @return String with the unique identifier of the alarm or null if not
     * available.
     */
    public String getId() {
        return (String) anyObject.get("id");
    }

    /**
     * Get the groupId of the target group of the bulk operation.
     *
     * @return String with the unique identifier of the target group or null if not
     * available.
     */
    public String getGroupId() {
        return (String) anyObject.get("groupId");
    }

    /**
     * Get the status of the bulk operations.
     * <p>
     * Valid values: ACTIVE, COMPLETED, DELETED
     *
     * @return a String with the status
     */
    public String getStatus() {
        return (String) anyObject.get("status");
    }

    /**
     * Get the ramp up time, specifying the delay between every operation.
     *
     * @return the number in seconds.
     */
    public Number getCreationRamp() {
        return (Number) anyObject.get("creationRamp");
    }
}
