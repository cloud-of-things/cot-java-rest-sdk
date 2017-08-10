package com.telekom.m2m.cot.restsdk.users;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Currently only a place holder. A class that represents a reference to a user.
 * Created by Ozan Arslan on 25.07.2017
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
     * Default construction to create a new user.
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

    public User getUser() {
        ExtensibleObject obj = (ExtensibleObject) anyObject.get("user");
        User user = new User(obj);
        return user;
    }

    public String getSelf() {

        return (String) anyObject.get("self");

    }

    // TODO: below method is not complete
    public void setUser(User user) {
        anyObject.put("user", user);

    }

}