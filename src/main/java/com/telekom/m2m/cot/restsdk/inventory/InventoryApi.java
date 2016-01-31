package com.telekom.m2m.cot.restsdk.inventory;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;
import okhttp3.OkHttpClient;

import java.io.IOException;

/**
 * Created by breucking on 30.01.16.
 */
public class InventoryApi {
    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    protected Gson gson = GsonUtils.createGson();
    protected OkHttpClient client = new OkHttpClient();

    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.managedObject+json;charset=UTF-8;ver=0.9";

    public InventoryApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    public ManagedObject create(ManagedObject managedObject) throws IOException {
        String json = gson.toJson(managedObject);

        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "inventory/managedObjects", CONTENT_TYPE);
        managedObject.setId(id);

        return managedObject;
    }

    public ManagedObject get(String s) throws IOException {

        String response = cloudOfThingsRestClient.getResponse(s, "inventory/managedObjects", CONTENT_TYPE);

        ManagedObject mo = gson.fromJson(response, ManagedObject.class);
        return mo;
    }

}
