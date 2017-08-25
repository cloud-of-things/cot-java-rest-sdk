package com.telekom.m2m.cot.restsdk.users;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;


/**
 * A class that represents a reference to a user. A reference holds the URL of a
 * given user. Created by Ozan Arslan on 25.07.2017
 */
public class UserReference extends ExtensibleObject {

    public UserReference() {
        super();
    }

    public UserReference(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }


    /**
     * The method to retrieve the user that the reference points to.
     * 
     * @return the user which the reference refers.
     */
    public User getUser() {
        ExtensibleObject obj = (ExtensibleObject) anyObject.get("user");
        User user = new User(obj);
        return user;
    }

    /**
     * The method to retrieve the URL of the user that the reference holds.
     * 
     * @return the URL of the user.
     */
    public String getSelf() {
        return (String) anyObject.get("self");
    }

    // TODO: finalize the below method.
    public void setUser(User user) {
        anyObject.put("user", user);
    }

}
