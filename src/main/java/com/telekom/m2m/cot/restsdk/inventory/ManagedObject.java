package com.telekom.m2m.cot.restsdk.inventory;

import com.telekom.m2m.cot.restsdk.library.Fragment;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Represents a ManagedObject from the platform.
 * <p>
 * The ManagedObject can model any physical or cyber-physical object, even virtual object.
 * <p>
 * Created by Patrick Steinert on 30.01.16.
 */
public class ManagedObject extends ExtensibleObject {

    /**
     * Constructor to create a new empty managed object.
     */
    public ManagedObject() {
        super();
    }

    /**
     * Internal constructor to create managed objects from base class.
     *
     * @param extensibleObject object from base class.
     */
    public ManagedObject(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }

    /**
     * Get the unique identifier of the managed object.
     * If the ManagedObject was retrieved from the platform, it has an ID. If just
     * created, there is no ID.
     *
     * @return String the unique identifier of the event or null if not
     * available.
     */
    public String getId() {
        return (String) anyObject.get("id");
    }

    /**
     * Get the name of the managed object.
     *
     * @return a String with the name or null if not available.
     */
    public String getName() {
        return (String) anyObject.get("name");
    }

    /**
     * Setting the name of the managed object. The name is typically a human readable identifier of the device.
     *
     * @param name a String with the name.
     */
    public void setName(String name) {
        anyObject.put("name", name);
    }

    /**
     * Set the unique identifier of the managed object.
     * Just used internally.
     *
     * @param id the new identifier.
     */
    public void setId(String id) {
        anyObject.put("id", id);
    }

    /**
     * Setting the managed object type.
     *
     * @param type a String with the managed object type. Use cot_abc_xyz style.
     */
    public void setType(String type) {
        anyObject.put("type", type);
    }

    /**
     * Get the type of the managed object.
     * The type categorizes the managed object.
     *
     * @return a String with the type or null if not available.
     */
    public String getType() {
        return (String) anyObject.get("type");
    }

    /**
     * Get the time when the managed object was last updated.
     *
     * @return Date object representing the timestamp when the managed object was last updated.
     */
    public Date getLastUpdated() {
        return (Date) anyObject.get("lastUpdated");
    }

    /**
     * Get the child devices associated to the managed object.
     *
     * @return a ManagedObjectReferenceCollection with the child devices.
     */
    public ManagedObjectReferenceCollection getChildDevices() {
        if (anyObject.containsKey("childDevices")) {
            return (ManagedObjectReferenceCollection) anyObject.get("childDevices");
        } else {
            return new ManagedObjectReferenceCollection(new ArrayList<>(), null);
        }
    }

    /**
     * Get the child assets associated to the managed object.
     *
     * @return a ManagedObjectReferenceCollection with the child assets.
     */
    public ManagedObjectReferenceCollection getChildAssets() {
        if (anyObject.containsKey("childAssets")) {
            return (ManagedObjectReferenceCollection) anyObject.get("childAssets");
        } else {
            return new ManagedObjectReferenceCollection(new ArrayList<>(), null);
        }
    }

    /**
     * Get the parent devices associated to the managed object.
     *
     * @return a ManagedObjectReferenceCollection with the parent devices.
     */
    public ManagedObjectReferenceCollection getParentDevices() {
        if (anyObject.containsKey("deviceParents")) {
            return (ManagedObjectReferenceCollection) anyObject.get("deviceParents");
        } else {
            return new ManagedObjectReferenceCollection(new ArrayList<>(), null);
        }
    }

    /**
     * Get the parent assets associated to the managed object.
     *
     * @return a ManagedObjectReferenceCollection with the parent assets.
     */
    public ManagedObjectReferenceCollection getParentAssets() {
        if (anyObject.containsKey("assetParents")) {
            return (ManagedObjectReferenceCollection) anyObject.get("assetParents");
        } else {
            return new ManagedObjectReferenceCollection(new ArrayList<>(), null);
        }
    }

    /**
     * Add a library {@link Fragment} to this ManagedObject.
     *
     * @param fragment the {@link Fragment} object to add to this ManagedObject.
     */
    public void addFragment(Fragment fragment) {
        anyObject.put(fragment.getId(), fragment.getJson());
    }

}
