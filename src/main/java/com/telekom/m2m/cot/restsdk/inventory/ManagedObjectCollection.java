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
 * Created by Patrick Steinert on 30.01.16.
 *
 * @since 0.3.0
 */
public class ManagedObjectCollection {

    private Filter.FilterBuilder criteria = null;

    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    private int pageCursor = 1;

    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.managedObjectCollection+json;charset=UTF-8;ver=0.9";
    private boolean nextAvailable = false;
    private boolean previousAvailable = false;
    private int pageSize = 5;

    private Gson gson = GsonUtils.createGson();


    /**
     * Internal constructor to create a ManagedObjectCollection.
     * Use {@link InventoryApi} to get a ManagedObjectCollection.
     *
     * @param resultSize
     * @param cloudOfThingsRestClient
     */
    ManagedObjectCollection(int resultSize, CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.pageCursor = resultSize;
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }


    /**
     * Internal constructor to create a ManagedObjectCollection.
     * Use {@link InventoryApi} to get a ManagedObjectCollection.
     *
     * @param resultSize
     * @param cloudOfThingsRestClient
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     */
    ManagedObjectCollection(Filter.FilterBuilder filters, int resultSize, CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.criteria = filters;
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
        this.pageSize = resultSize;
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

    /**
     * Moves cursor to the next page.
     */
    public void next() {
        pageCursor += 1;
    }

    /**
     * Moves cursor to the previous page.
     */
    public void previous() {
        pageCursor -= 1;
    }

    /**
     * Checks if the next page has elements. <b>Use with caution, it does a seperate HTTP request, so it is considered as slow</b>
     *
     * @return true if next page has managedObjects, otherwise false.
     */
    public boolean hasNext() {
        JsonObject object = getJsonObject(pageCursor + 1);
        if (object.has("managedObjects")) {
            JsonArray jsonManagedObjects = object.get("managedObjects").getAsJsonArray();
            nextAvailable = jsonManagedObjects.size() > 0 ? true : false;
        }
        return nextAvailable;
    }

    /**
     * Checks if there is a previous page.
     *
     * @return true if next page has managedObjects, otherwise false.
     */
    public boolean hasPrevious() {
        return previousAvailable;
    }

    /**
     * Sets the page size for page queries.
     * The queries uses page size as a limit of elements to retrieve.
     * There is a maximum number of elements, currently 2,000 elements.
     * <i>Default is 5</i>
     *
     * @param pageSize the new page size as positive integer.
     */
    public void setPageSize(int pageSize) {
        if (pageSize > 0) {
            this.pageSize = pageSize;
        } else {
            this.pageSize = 0;
        }
    }
}
