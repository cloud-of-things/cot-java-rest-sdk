package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

/**
 * Created by breucking on 19.12.16.
 */
public class NewDeviceRequest extends ExtensibleObject {

    public NewDeviceRequest(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }

    void setId(final String id) {
        anyObject.put("id", id);
    }

    public String getId() {
        return (String) anyObject.get("id");
    }

    public String getTenantId() {
        return (String) anyObject.get("tenantId");
    }

    public void setTenantId(final String tenantId) {
        anyObject.put("tenantId", tenantId);
    }

    /**
     * @deprecated use {@link #setStatus(NewDeviceRequestStatus)} instead
     * @param status the status value as String
     * @throws IllegalArgumentException if the parameter doesn't match one of the NewDeviceRequestStatus
     */
    @Deprecated
    public void setStatus(final String status) {
        NewDeviceRequestStatus.valueOf(status);
        anyObject.put("status", status);
    }

    public void setStatus(final NewDeviceRequestStatus status) {
        anyObject.put("status", status.toString());
    }

    public NewDeviceRequestStatus getStatus() {
        return NewDeviceRequestStatus.valueOf((String)anyObject.get("status"));
    }

}
