package com.telekom.m2m.cot.restsdk.inventory;

/**
 * ManagedObjectReference is the container to place references in
 * ManagedObjects.
 * <p>
 * Could be used in this reference collections:
 * <ul>
 * <li>childAssets</li>
 * <li>childDevices</li>
 * <li>parentAssets</li>
 * <li>parentDevices</li>
 * </ul>
 * <p>
 * Created by breucking on 03.09.16.
 */
public class ManagedObjectReference {

    private ManagedObject managedObject;
    private String selfRef = null;

    /**
     * Constructor to create new ManagedObjectReferences.
     * <p>
     * Instanciate and create.
     *
     * @param managedObject
     */
    public ManagedObjectReference(ManagedObject managedObject) {
        this.managedObject = managedObject;
    }

    /**
     * Used via serialization.
     *
     * @param managedObject the referred ManagedObject
     * @param selfRef       a reference URL
     */
    public ManagedObjectReference(ManagedObject managedObject, String selfRef) {
        this.managedObject = managedObject;
        this.selfRef = selfRef;
    }

    /**
     * Returns the referred ManagedObject.
     *
     * @return the reffered ManagedObject
     */
    public ManagedObject getManagedObject() {
        return this.managedObject;
    }


    /**
     * Returns the URL to the object.
     *
     * @return a string containing the URL to the object.
     */
    public String getSelf() {
        return selfRef;
    }
}
