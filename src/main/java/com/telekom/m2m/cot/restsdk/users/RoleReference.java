package com.telekom.m2m.cot.restsdk.users;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Currently only a place holder. A class that represents a reference to a role.
 * Created by Ozan Arslan on 25.07.2017
 *
 */

public class RoleReference extends ExtensibleObject {
    private CloudOfThingsRestClient cloudOfThingsRestClient;
    private Gson gson = GsonUtils.createGson();
    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.role+json; charset=UTF-8; ver=0.9";

    public RoleReference(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    /**
     * Default construction to create a new role.
     */
    public RoleReference() {
        super();
    }

    /**
     * Internal constructor to create RoleReference from base class.
     *
     * @param extensibleObject
     *            object from base class.
     */
    public RoleReference(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }

    public Role getRole() {
        ExtensibleObject obj = (ExtensibleObject) anyObject.get("role");
        Role role = new Role(obj);
        return role;
    }

    public String getSelf() {

        return (String) anyObject.get("self");

    }

    // TODO: below method is not complete
    public void setRole(Role role) {
        anyObject.put("role", role);

    }

}