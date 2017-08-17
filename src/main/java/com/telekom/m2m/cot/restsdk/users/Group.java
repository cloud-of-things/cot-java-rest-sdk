package com.telekom.m2m.cot.restsdk.users;

import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;


/**
 * Class that defines the methods of group. Groups are categories of users.
 * Created by Ozan Arslan on 13.07.2017
 */
public class Group extends ExtensibleObject {

    public Group() {}

    public Group(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }


    /**
     * Get the unique identifier of the group. If the group was retrieved from
     * the platform, it has an ID. If just created, there is no ID. TODO: verify
     * whether/why this is a number and not a String as in other entities.
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
     * Return the group description
     * 
     * @return description
     */
    public String getDescription() {
        return (String) anyObject.get("description");
    }

    /**
     * Set the description of the group
     * 
     * @param description
     */
    public void setDescription(String description) {
        anyObject.put("description", description);
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

}
