package com.telekom.m2m.cot.restsdk.users;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Currently only a place holder. A class that represents a reference to a
 * group. Created by Ozan Arslan on 25.07.2017
 *
 */

public class GroupReference extends ExtensibleObject {
    private CloudOfThingsRestClient cloudOfThingsRestClient;
    private Gson gson = GsonUtils.createGson();
    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.group+json; charset=UTF-8; ver=0.9";

    public GroupReference(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    /**
     * Default construction to create a new group.
     */
    public GroupReference() {
        super();
    }

    /**
     * Internal constructor to create GroupReference from base class.
     *
     * @param extensibleObject
     *            object from base class.
     */
    public GroupReference(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }

    public Group getGroup() {
        ExtensibleObject obj = (ExtensibleObject) anyObject.get("group");
        Group group = new Group(obj);
        return group;
    }

    public String getSelf() {

        return (String) anyObject.get("self");

    }

    // TODO: below method is not complete
    public void setGroup(Group group) {
        anyObject.put("group", group);

    }

}