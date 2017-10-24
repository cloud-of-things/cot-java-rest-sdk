package com.telekom.m2m.cot.restsdk.devicecontrol;

import java.util.stream.StreamSupport;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.JsonArrayPagination;

/**
 * Represents a pageable Operation collection.
 * <p>
 * Collection can be scrolled with next() and prev().
 * <p>
 *
 * @since 0.1.0
 * Created by Patrick Steinert on 14.02.16.
 */
public class OperationCollection  extends JsonArrayPagination {

    private static final String CONTENT_TYPE_OPERATION_COLLECTION = "application/vnd.com.nsn.cumulocity.operationCollection+json;charset=UTF-8;ver=0.9";
    private static final String OPERATION_COLLECTION_ELEMENT_NAME = "operations";

    /**
     * Creates an OperationCollection.
     * Use {@link DeviceControlApi} to get OperationCollection.
     *
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     * @param relativeApiUrl          relative url of the REST API without leading slash.
     * @param gson                    the necessary json De-/serializer.
     * @param filterBuilder           the build criteria or null if all items should be retrieved.
     * @param pageSize                max number of retrieved elements per page.
     */
    OperationCollection(final CloudOfThingsRestClient cloudOfThingsRestClient,
                    final String relativeApiUrl,
                    final Gson gson,
                    final Filter.FilterBuilder filterBuilder,
                    final int pageSize) {
        super(cloudOfThingsRestClient, relativeApiUrl, gson, CONTENT_TYPE_OPERATION_COLLECTION, OPERATION_COLLECTION_ELEMENT_NAME, filterBuilder, pageSize);
    }

    /**
     * Retrieves the current page.
     * <p>
     * Retrieves the Operations influenced by filters set in construction.
     *
     * @return array of found Operations
     */
    public Operation[] getOperations() {
        final JsonArray jsonOperations = getJsonArray();

        return (jsonOperations == null) ? new Operation[0] : StreamSupport.stream(jsonOperations.spliterator(), false).
                map(operation -> new Operation(gson.fromJson(operation.getAsJsonObject(), ExtensibleObject.class))).
                toArray(Operation[]::new);
    }
}
