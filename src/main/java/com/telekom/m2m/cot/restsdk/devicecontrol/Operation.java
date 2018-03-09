package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

import java.util.Date;

/**
 * An Operation is used to send operations to the device (or Thing in IoT terms).
 * <p>
 * Created by Patrick Steinert on 31.01.16.
 */
public class Operation extends ExtensibleObject {

    /**
     * Constructor to create new operations.
     */
    public Operation() {
        super();
    }

    /**
     * Creates an operation with an existing ID.
     *
     * @param id the unique identifier of the object.
     */
    public Operation(String id) {
        super();
        anyObject.put("id", id);
    }

    /**
     * Internal constructor to create an operation by base class.
     *
     * @param extensibleObject the base class object.
     */
    public Operation(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }

    /**
     * Setting the status of the operation.
     *
     * @param status use {@link OperationStatus} value.
     */
    public void setStatus(OperationStatus status) {
        anyObject.put("status", status);
    }

    /**
     * Getting the unique identifier of the operation.
     * If the operation was retrieved from the platform, it has an ID. If just
     * created, there is no ID.
     *
     * @return String with the unique identifier of the operation or null if not available.
     */
    public String getId() {
        return (String) anyObject.get("id");
    }

    /**
     * Getting the creation time of the operation, which is the timestamp when
     * operation is stored in the CoT platform.
     *
     * @return a Date object representing the timestamp.
     */
    public Date getCreationTime() {
        return (Date) anyObject.get("creationTime");
    }

    /**
     * Getting the status of the Operation.
     * Could throw an {@link IllegalArgumentException} if platform somehow
     * returns invalid stuff (maybe added new states)
     *
     * @return a value of {@link OperationStatus} with the status.
     */
    public OperationStatus getStatus() {
        if (!anyObject.containsKey("status")) {
            return null;
        }

        Object status = anyObject.get("status");

        if (status instanceof OperationStatus) {
            return (OperationStatus) status;
        }
        if (status instanceof String) {
            return OperationStatus.valueOf((String) status);
        }

        return null;
    }

    /**
     * Getting the deviceId of the target device.
     * The given device should execute the operation.
     *
     * @return a String with the unique identifier of the device.
     */
    public String getDeviceId() {
        return (String) anyObject.get("deviceId");
    }

    /**
     * Setting the deviceId of the target device.
     * The given device should execute the operation.
     *
     * @param deviceId the unique identifier of the target device.
     */
    public void setDeviceId(String deviceId) {
        anyObject.put("deviceId", deviceId);
    }

    /**
     * Setting the operation Id.
     * Just for internal purpose, unique identifier is set by platform, no
     * manipulation possible.
     *
     * @param id the unique identifier.
     */
    void setId(String id) {
        anyObject.put("id", id);
    }

    /**
     * Setting the deliveryType of the operation.
     *
     * @param deliveryType a String with delivery type, e.g. "SMS".
     */
    public void setDeliveryType(String deliveryType) {
        anyObject.put("deliveryType", deliveryType);
    }

    /**
     * Getting the deliveryType of the operation.
     *
     * @return a String with the delivery type, e.g. "SMS".
     */
    public String getDeliveryType() {
        return (String) anyObject.get("deliveryType");
    }

    /**
     * Setting failure reason in case of failing
     * @param failureReason a String with failure Reason, e.g. "Operation cancelled by user"
     */
    public void setFailureReason(String failureReason){
        if( failureReason==null){
            anyObject.remove("failureReason");
        }else{
            anyObject.put("failureReason",failureReason);
        }
    }


    /**
     * Getting FailureReason, when status is 'FAILED'
     * @return a String with the failure reason from the Operation
     */
    public String getFailureReason(){
        if(getStatus() == OperationStatus.FAILED) {
            return (String) anyObject.get("failureReason");
        }else{
            return null;
        }
    }
}

