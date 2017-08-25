package com.telekom.m2m.cot.restsdk.users;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;


/**
 * A class that represents a reference to a role. A reference hold the URL of
 * the original object (in this case a role). Created by Ozan Arslan on
 * 25.07.2017
 *
 */
public class RoleReference extends ExtensibleObject {

    public RoleReference() {
        super();
    }

    public RoleReference(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }

    /**
     * The method to return the role that the reference holds.
     * 
     * @return
     */
    public Role getRole() {
        ExtensibleObject obj = (ExtensibleObject) anyObject.get("role");
        Role role = new Role(obj);
        return role;
    }

    /**
     * The method to return the URL of the role that the reference holds.
     * 
     * @return the URL of the role
     */
    public String getSelf() {

        return (String) anyObject.get("self");

    }

    // TODO: Finalize the below method.
    public void setRole(Role role) {
        anyObject.put("role", role);

    }

}