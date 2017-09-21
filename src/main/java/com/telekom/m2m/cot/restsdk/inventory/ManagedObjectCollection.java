package com.telekom.m2m.cot.restsdk.inventory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.JsonArrayPagination;

/**
 * Represents a pageable ManagedObject collection.
 * <p>
 * Collection can be scrolled with next() and prev().
 * <p>
 * Created by Patrick Steinert on 30.01.16.
 *
 * @since 0.3.0
 */
public class ManagedObjectCollection extends JsonArrayPagination {

    private static final String COLLECTION_CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.managedObjectCollection+json;charset=UTF-8;ver=0.9";
    private static final String COLLECTION_ELEMENT_NAME = "managedObjects";

    /**
     * Internal constructor to create a ManagedObjectCollection.
     * Use {@link InventoryApi} to get ManagedObjectCollection.
     *
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     * @param relativeApiUrl          relative url of the REST API without leading slash.
     * @param gson                    the necessary json De-/serializer.
     * @param filterBuilder           the build criteria or null if all items should be retrieved.
     * @param pageSize                max number of retrieved elements per page.
     */
    ManagedObjectCollection(final CloudOfThingsRestClient cloudOfThingsRestClient,
                         final String relativeApiUrl,
                         final Gson gson,
                         final Filter.FilterBuilder filterBuilder,
                         final int pageSize) {
        super(cloudOfThingsRestClient, relativeApiUrl, gson, COLLECTION_CONTENT_TYPE, COLLECTION_ELEMENT_NAME, filterBuilder, pageSize);
    }

    /**
     * Retrieves the current page.
     * <p>
     * Retrieves the ManagedObjects influenced by filters set in construction.
     *
     * @return array of found ManagedObjects
     */
    public ManagedObject[] getManagedObjects() {
        final JsonArray jsonManagedObjects = getJsonArray();
        ManagedObject[] binaries = gson.fromJson(jsonManagedObjects, ManagedObject[].class);
return binaries;/*
        if (jsonManagedObjects != null) {
            final ManagedObject[] arrayOfManagedObjects = new ManagedObject[jsonManagedObjects.size()];
            for (int i = 0; i < jsonManagedObjects.size(); i++) {
                JsonElement jsonManagedObject = jsonManagedObjects.get(i).getAsJsonObject();
                final ManagedObject managedObject = new ManagedObject(gson.fromJson(jsonManagedObject, ExtensibleObject.class));
                arrayOfManagedObjects[i] = managedObject;
            }
            return arrayOfManagedObjects;
        } else {
            return null;
        }*/
    }
}
