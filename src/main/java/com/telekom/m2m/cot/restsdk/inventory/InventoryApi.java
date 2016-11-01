package com.telekom.m2m.cot.restsdk.inventory;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;
import okhttp3.OkHttpClient;

/**
 * Created by breucking on 30.01.16.
 */
public class InventoryApi {
    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    protected Gson gson = GsonUtils.createGson();
    protected OkHttpClient client = new OkHttpClient();

    private static final String CONTENT_TYPE_MANAGEDOBJECT = "application/vnd.com.nsn.cumulocity.managedObject+json;charset=UTF-8;ver=0.9";
    private static final String CONTENT_TYPE_MANAGEDOBJECTREF = "application/vnd.com.nsn.cumulocity.managedObjectReference+json;charset=UTF-8;ver=0.9";

    public InventoryApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    public ManagedObject create(ManagedObject managedObject) {
        String json = gson.toJson(managedObject);

        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "inventory/managedObjects", CONTENT_TYPE_MANAGEDOBJECT);
        managedObject.setId(id);

        return managedObject;
    }

    public ManagedObject get(String s) {
        String response = cloudOfThingsRestClient.getResponse(s, "inventory/managedObjects", CONTENT_TYPE_MANAGEDOBJECT);
        ExtensibleObject extensibleObject = gson.fromJson(response, ManagedObject.class);
        if (extensibleObject != null) {
            ManagedObject mo = new ManagedObject(extensibleObject);
            return mo;
        } else {
            return null;
        }
    }

    public void delete(String id) {
        cloudOfThingsRestClient.delete(id, "inventory/managedObjects");
    }

    public void update(ManagedObject managedObject) {
        String json = gson.toJson(managedObject);
        cloudOfThingsRestClient.doPutRequest(json, managedObject.getId(), "inventory/managedObjects", CONTENT_TYPE_MANAGEDOBJECT);
    }

    public void addChildToManagedObject(ManagedObject managedObject, ManagedObjectReference managedObjectReference) {
        String json = gson.toJson(managedObjectReference);
        String selfRef = managedObject.getChildDevices().getSelf();
        int idx = selfRef.lastIndexOf("inventory");

        cloudOfThingsRestClient.doPostRequest(json, selfRef.substring(idx), CONTENT_TYPE_MANAGEDOBJECTREF);
    }


    public void removeChildFromManagedObject(ManagedObjectReference managedObjectReference) {
        cloudOfThingsRestClient.delete(managedObjectReference.getSelf());
    }
}
