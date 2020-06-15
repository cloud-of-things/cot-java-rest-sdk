package com.telekom.m2m.cot.restsdk.users;

import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class that defines the methods of group. Groups are categories of users.
 * Created by Ozan Arslan on 13.07.2017
 */
public class Group extends ExtensibleObject {

    /**
     * Constructor to create a new empty group.
     */
    public Group() {
        super();
    }

    /**
     * Internal constructor to create a group from base class.
     *
     * @param extensibleObject object from base class.
     */
    public Group(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }


    /**
     * Get the unique identifier of the group. If the group was retrieved from
     * the platform, it has an ID. If just created, there is no ID. The returned groupId 
     * here is a number instead of a string unlike other ids such as users' or roles'.
     *
     * @return Long the unique identifier of the group or null if not available.
     */
    public Long getId() {
        Object id = anyObject.get("id");
        if (id == null) {
            return null;
        }

        if (id instanceof Number) {
            return ((Number) id).longValue();
        }
        if (id instanceof String) {
            return Long.parseLong((String) id);
        }
        try {
            return (Long) id;
        } catch (ClassCastException ex) {
            throw new CotSdkException("Group has invalid id in json.", ex);
        }
    }

    /**
     * Set the unique identifier of the group. Just used internally.
     *
     * @param id the new identifier.
     */
    void setId(String id) {
        anyObject.put("id", id);
    }

    /**
     * The method to return the group name.
     * 
     * @return name a String with the name of the group.
     */
    public String getName() {
        return (String) anyObject.get("name");
    }

    /**
     * The method to set group name.
     * 
     * @param name is a String with the name of the group.
     */
    public void setName(String name) {
        anyObject.put("name", name);
    }

    /**
     * Return the users of the group.
     * 
     * @return a collection with the users assigned to this group.
     */
    public UserReferenceCollection getUsers() {
        return (UserReferenceCollection) anyObject.get("users");
    }

    /**
     * Set the users of the group.
     * 
     * @param users a collection with the users to assign to this group.
     */
    public void setUsers(UserReferenceCollection users) {
        anyObject.put("users", users);
    }

    /**
     * Return the roles of the group.
     * 
     * @return roles a collection of type {@link RoleReferenceCollection} containing the roles assigned to this group. 
     */
    public RoleReferenceCollection getRoles() {
        return (RoleReferenceCollection) anyObject.get("roles");
    }

    /**
     * Set the roles for the group
     * 
     * @param roles a collection of type {@link RoleReferenceCollection} containing the roles to assign to this group.
     */
    public void setRoles(RoleReferenceCollection roles) {
        anyObject.put("roles", roles);
    }
    
    
    
    /**
     * The method to retrieve the assigned device permissions of a group. The map
     * contains a series of keys of device ids, and the values are a list of
     * permissions of different type.
     * 
     * @return a map of device permissions of a group
     */
    public Map<String, List<DevicePermission>> getDevicePermissions() {
        Map<String, List<DevicePermission>> devicePermissions = (Map<String, List<DevicePermission>>)anyObject.get("devicePermissions");
        if(devicePermissions != null) {
            return new HashMap<>(devicePermissions);
        } else {
            return new HashMap<>();
        }
    }

    /**
     * The method to set device permissions to a group. It allows to set more
     * than one type of device permissions for more than one device at once by
     * employing a map of device ids and a list of permissions.
     * 
     * @param devicePermissions a map with device id as a key and a list of permission strings as a value.
     */
    public void setDevicePermissions(Map<String, List<DevicePermission>> devicePermissions) {
        if(devicePermissions != null) {
            anyObject.put("devicePermissions", new HashMap<>(devicePermissions));
        } else {
            anyObject.put("devicePermissions", null);
        }
    }

}
