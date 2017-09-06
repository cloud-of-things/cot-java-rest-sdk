package com.telekom.m2m.cot.restsdk.users;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;


/**
 * Class that defines the methods of group. Groups are categories of users.
 * Created by Ozan Arslan on 13.07.2017
 */
public class Group extends ExtensibleObject {
    
    public Group() {
        super();
    }

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
     * @param id
     *            the new identifier.
     */
    void setId(String id) {
        anyObject.put("id", id);
    }

    /**
     * The method to return the group name.
     * 
     * @return name
     */
    public String getName() {
        return (String) anyObject.get("name");
    }

    /**
     * The method to set group name.
     * 
     * @param name
     */
    public void setName(String name) {
        anyObject.put("name", name);
    }

    /**
     * Return the users of the group
     * 
     * @return
     */
    public UserReferenceCollection getUsers() {
        return (UserReferenceCollection) anyObject.get("users");
    }

    /**
     * 
     * Set the users of the group
     * 
     * @param users
     */
    public void setUsers(UserReferenceCollection users) {
        anyObject.put("users", users);
    }

    /**
     * 
     * Return the roles of the group
     * 
     * @return roles
     */
    public RoleReferenceCollection getRoles() {
        return (RoleReferenceCollection) anyObject.get("roles");
    }

    /**
     * Set the roles for the group
     * 
     * @param roles
     */
    public void setRoles(RoleReferenceCollection roles) {
        anyObject.put("roles", roles);
    }
    
    
    
    /**
     * The method to retrieve the assigned device permissions of a group. The map
     * contains a series of keys of device ids, and the values are a list of
     * permissions of different type.
     * 
     * @return (Map<String, List<String>>) device permissions of a group
     */
    public Map<String, List<String>> getDevicePermissions() {
        ExtensibleObject obj = (ExtensibleObject) anyObject.get("devicePermissions");
        Map<String, List<String>> devicePermission = new LinkedHashMap<String, List<String>>();

        for (String key : obj.getAttributes().keySet()) {
            List<String> list = new ArrayList<String>();
            JsonArray jar = (JsonArray) obj.getAttributes().get(key);
            for (JsonElement el : jar) {
                list.add(el.getAsString());
            }
            devicePermission.put(key, list);
        }
        return devicePermission;
    }

    /**
     * The method to set device permissions to a group. It allows to set more
     * than one type of device permissions for more than one device at once by
     * employing a map of device ids and a list of permissions.
     * 
     * @param devicePermissions
     */
    public void setDevicePermissions(Map<String, List<String>> devicePermissions) {

        anyObject.put("devicePermissions", devicePermissions);

    }
    
    

}
