package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

import java.util.Date;

/**
 * BulkOperation represents a BulkOperation in the CoT.
 * Bulk operations are used to create operations for a complete group.
 * <p>
 * Created by Patrick Steinert on 02.01.17.
 */
public class BulkOperation extends ExtensibleObject {

    /**
     *  Status ACTIVE: when bulk operation is created.
     */
    public static final String STATUS_ACTIVE = "ACTIVE";

    /**
     *  Status IN_PROGRESS: when bulk operation is performing on all devices.
     */
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";

    /**
     * Status COMPLETED: when the operation was performed on all devices.
     */
    public static final String STATUS_COMPLETED = "COMPLETED";

    /**
     * Status DELETED: when already created operation was cancelled by deleting the bulk operation
     */
    public static final String STATUS_DELETED = "DELETED";

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
     * Set the unique identifier of the bulk operation.
     * Just used internally.
     *
     * @param id the new identifier created by storing the entity.
     */
    void setId(String id) {
        anyObject.put("id", id);
    }

    /**
     * Get the unique identifier for the bulk operation.
     *
     * @return String with the unique identifier of the bulk operation or null if not
     * available.
     */
    public String getId() {
        Object id = anyObject.get("id");
        if (id == null) {
            return null;
        }
        return id.toString();
    }

    /**
     * Set the groupId of the target group on which the bulk operation should be performed.
     * Group of devices is a managed object from type 'c8y_DeviceGroup' and flagged as 'c8y_IsDeviceGroup'
     *
     * @param groupId the unique identifier of the target group.
     */
    public void setGroupId(String groupId) {
        anyObject.put("groupId", groupId);
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
     * Set the bulk operation id to reschedule this operation on devices where it failed.
     *
     * @param failedBulkOperationId the unique identifier of the bulk operation.
     */
    public void setFailedBulkOperationId(String failedBulkOperationId) {
        anyObject.put("failedBulkOperationId", failedBulkOperationId);
    }

    /**
     * Get the bulk operation id from which failed operation should be rescheduled.
     *
     * @return String with the unique identifier of the failed bulk operation id
     * or null if not available.
     */
    public String getFailedBulkOperationId() {
        return (String) anyObject.get("failedBulkOperationId");
    }

    /**
     * Get the status of the bulk operation.
     * <p>
     * Valid values: ACTIVE, COMPLETED, DELETED
     *
     * @return a String with the status
     */
    public String getStatus() {
        return (String) anyObject.get("status");
    }

    /**
     * Set the status of the bulk operation.
     * <p>
     * Valid values: ACTIVE, COMPLETED, DELETED
     *
     * @param status String which represents the status of the bulk operation.
     */
    public void setStatus(String status) {
        anyObject.put("status", status);
    }

    /**
     * Set the ramp up time, specifying the delay between every operation.
     *
     * @param creationRamp number in seconds.
     */
    public void setCreationRamp(Number creationRamp) {
        anyObject.put("creationRamp", creationRamp);
    }

    /**
     * Get the ramp up time, specifying the delay between every operation.
     *
     * @return the number in seconds.
     */
    public Number getCreationRamp() {
        return (Number) anyObject.get("creationRamp");
    }

    /**
     * Get the time when operation should be started.
     *
     * @return Date object representing the timestamp when bulk operation should be performed.
     */
    public Date getStartDate() {
        return (Date) anyObject.get("startDate");
    }

    /**
     * Set the schedule time.
     *
     * @param startDate Date object with the time when bulk operation should be performed.
     */
    public void setStartDate(Date startDate) {
        anyObject.put("startDate", startDate);
    }

    /**
     * Get the operation which will be executed for every device in the target group.
     *
     * @return Operation object representing the operation which will be executed for every device in the target group.
     */
    public Operation getOperation() {
        Object operationPrototype = anyObject.get("operationPrototype");
        // since source value can be set as Operation Object via setter in regular way
        // and as ExtensibleObject via gson ExtensibleObjectSerializer.
        // At first we need to check the type to avoid an unnecessary wrap into Operation Object
        if(operationPrototype instanceof Operation) {
            return (Operation)operationPrototype;
        }
        return new Operation((ExtensibleObject) operationPrototype);
    }

    /**
     * Set the operation which should be executed for every device in the target group.
     *
     * @param operation Operation object representing the operation which need to be executed for every device in the target group.
     */
    public void setOperation(Operation operation) {
        anyObject.put("operationPrototype", operation);
    }

    /**
     * Get the progress of the bulk operation.
     *
     * @return Progress object containing the number of processed bulk operations.
     */
    public Progress getProgress() {
        Object progress = anyObject.get("progress");
        // since source value can be set as Progress Object via setter in regular way
        // and as ExtensibleObject via gson ExtensibleObjectSerializer.
        // At first we need to check the type to avoid an unnecessary wrap into Progress Object
        if(progress instanceof Progress) {
            return (Progress)progress;
        }
        return new Progress((ExtensibleObject) progress);
    }

    /**
     * Set the progress of the bulk operation.
     *
     * @param progress Progress object containing the number of processed bulk operations.
     */
    public void setProgress(Progress progress) {
        anyObject.put("progress", progress);
    }

}
