package com.telekom.m2m.cot.restsdk.users;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * A class that represents a reference to a user. A reference holds the URL of a
 * given user. Created by Ozan Arslan on 25.07.2017
 *
 */

public class UserReference extends ExtensibleObject {
    private CloudOfThingsRestClient cloudOfThingsRestClient;
    private Gson gson = GsonUtils.createGson();
    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.user+json; charset=UTF-8; ver=0.9";

    public UserReference(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    /**
     * Default construction to create a new user reference.
     */
    public UserReference() {
        super();
    }

    /**
     * Internal constructor to create UserReference from base class.
     *
     * @param extensibleObject
     *            object from base class.
     */
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