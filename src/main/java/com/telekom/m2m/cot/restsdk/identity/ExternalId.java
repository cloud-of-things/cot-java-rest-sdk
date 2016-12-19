package com.telekom.m2m.cot.restsdk.identity;

import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;

/**
 * Created by Patrick Steinert on 31.01.16.
 */
public class ExternalId {
    private ManagedObject managedObject;
    private String externalId;
    private String type;

    public void setManagedObject(ManagedObject managedObject) {
        this.managedObject = managedObject;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getExternalId() {
        return externalId;
    }

    public ManagedObject getManagedObject() {
        return managedObject;
    }
}
