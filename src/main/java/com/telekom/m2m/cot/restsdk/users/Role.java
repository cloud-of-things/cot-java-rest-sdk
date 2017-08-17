package com.telekom.m2m.cot.restsdk.users;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;


/**
 * Class that defines the methods of a role. Roles are categories of users and
 * groups. In other words, they are types of users and group of users. The users
 * and group of users can be assigned to new roles or they can be unassigned
 * from their roles. Created by Ozan Arslan on 13.07.2017
 */
public class Role extends ExtensibleObject {

    public Role() {}

    public Role(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }


    /**
     * Get the unique identifier of the role.
     *
     * @return String the unique identifier of the user or null if not
     *         available.
     */
    public String getId() {
        return (String) anyObject.get("id");
    }

    /**
     * Return the name of the role
     * 
     * @return name of the role.
     */
    public String getName() {
        return (String) anyObject.get("name");
    }

}
