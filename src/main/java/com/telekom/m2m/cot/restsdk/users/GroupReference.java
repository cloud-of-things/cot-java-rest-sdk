package com.telekom.m2m.cot.restsdk.users;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * A class that represents a reference to a group. A reference is an object that
 * holds the URL of the original object that it refers to. Created by Ozan
 * Arslan on 25.07.2017
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
     * Default construction to create a new group reference.
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

    /**
     * The method to retrieve the group that the group reference holds.
     * 
     * @return Group hold by the reference.
     */
    public Group getGroup() {
        ExtensibleObject obj = (ExtensibleObject) anyObject.get("group");
        Group group = new Group(obj);
        return group;
    }

    /**
     * The method to retrieve the URL of a group.
     * 
     * @return the URL of the group.
     */
    public String getSelf() {

        return (String) anyObject.get("self");

    }

    // TODO: Finalise the following method.
    /**
     * The method to set the reference to a group.
     * 
     * @param group
     */
    public void setGroup(Group group) {
        anyObject.put("group", group);

    }

}