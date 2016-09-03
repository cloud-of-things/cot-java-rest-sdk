package com.telekom.m2m.cot.restsdk.inventory;

/**
 * Created by breucking on 03.09.16.
 */
public class ManagedObjectReference {


    private ManagedObject managedObject;

    public ManagedObjectReference(ManagedObject managedObject) {
        this.managedObject = managedObject;
    }

    public ManagedObjectReference() {

    }

    public ManagedObject getManagedObject() {
        return this.managedObject;
    }


}
