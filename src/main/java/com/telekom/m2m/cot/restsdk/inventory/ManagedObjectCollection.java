package com.telekom.m2m.cot.restsdk.inventory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Represents a pageable ManagedObject collection.
 * <p>
 * Collection can be scrolled with next() and prev().
 * <p>
 * Created by breucking on 30.01.16.
 *
 * @since 0.3.0
 */
public class ManagedObjectCollection {

    private Filter.FilterBuilder criteria = null;

    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    private int pageCursor;

    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.managedObjectCollection+json;charset=UTF-8;ver=0.9";
    private boolean nextAvailable = false;
    private boolean previousAvailable = false;
    private int pageSize = 5;

    private Gson gson = GsonUtils.createGson();


    /**
     * Internal constructor to create a ManagedObjectCollection.
     * Use {@link InventoryApi} to get a ManagedObjectCollection.
     *
     * @param pageSize
     * @param cloudOfThingsRestClient
     */
    ManagedObjectCollection(int pageSize, CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.pageCursor = pageSize;
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    /**
     * Retrieves the current page.
     *
     * @return array of found ManagedObjects.
     */
    public ManagedObject[] getManagedObjects() {
        JsonObject object = getJsonObject(pageCursor);

        previousAvailable = object.has("prev");

        if (object.has("managedObjects")) {
            JsonArray jsonManagedObjects = object.get("managedObjects").getAsJsonArray();
            ManagedObject[] arrayOfManagedObjects = new ManagedObject[jsonManagedObjects.size()];
            for (int i = 0; i < jsonManagedObjects.size(); i++) {
                JsonElement jsonMeasurement = jsonManagedObjects.get(i).getAsJsonObject();
                ManagedObject managedObject = new ManagedObject(gson.fromJson(jsonMeasurement, ExtensibleObject.class));
                arrayOfManagedObjects[i] = managedObject;
            }
            return arrayOfManagedObjects;
        } else
            return null;
    }

    private JsonObject getJsonObject(int page) {
        String response;
        String url = "/inventory/managedObjects?" +
                "currentPage=" + page +
                "&pageSize=" + pageSize;
        if (criteria != null) {
            url += "&" + criteria.buildFilter();
        }
        response = cloudOfThingsRestClient.getResponse(url, CONTENT_TYPE);

        return gson.fromJson(response, JsonObject.class);
    }
}
