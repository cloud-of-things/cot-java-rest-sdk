package com.telekom.m2m.cot.restsdk.operation;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

import java.util.Date;

/**
 * Created by breucking on 31.01.16.
 */
public class Operation extends ExtensibleObject {

    private String id;
    private OperationStatus status;

    public Operation() {
        super();
    }

    public Operation(String id) {
        super();
        anyObject.put("id", id);
    }

    public Operation(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }


    public void setStatus(OperationStatus status) {
        this.status = status;
    }

    public String getId() {
        return (String) anyObject.get("id");
    }

    public Date getCreationTime() {
        return (Date) anyObject.get("creationTime");
    }

    public String getStatus() {
        return (String) anyObject.get("status");
    }

    public String getDeviceId() {
        return (String) anyObject.get("deviceId");
    }

    public void setDeviceId(String deviceId) {
        anyObject.put("deviceId", deviceId);
    }

    public void setId(String id) {
        anyObject.put("id", id);
    }
}
