package com.telekom.m2m.cot.restsdk.devicecontrol;

import java.util.stream.StreamSupport;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.IterableObjectPagination;

/**
 * Represents a pageable BulkOperation collection.
 * <p>
 * Collection can be scrolled with next() and prev().
 * <p>
 *
 * @since 0.6.0
 * Created by Andreas Dyck on 04.09.17.
 */
public class BulkOperationCollection extends IterableObjectPagination<BulkOperation> {

    private static final String CONTENT_TYPE_BULK_OPERATION_COLLECTION = "application/vnd.com.nsn.cumulocity.bulkOperationCollection+json;charset=UTF-8;ver=0.9";
    private static final String BULK_OPERATION_COLLECTION_ELEMENT_NAME = "bulkOperations";

    /**
     * Creates an BulkOperationCollection.
     * Use {@link DeviceControlApi} to get BulkOperationCollection.
     *
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     * @param relativeApiUrl          relative url of the REST API without leading slash.
     * @param gson                    the necessary json De-/serializer.
     * @param filterBuilder           the build criteria or null if all items should be retrieved.
     * @param pageSize                max number of retrieved elements per page.
     */
    BulkOperationCollection(
        final CloudOfThingsRestClient cloudOfThingsRestClient,
        final String relativeApiUrl,
        final Gson gson,
        final Filter.FilterBuilder filterBuilder,
        final int pageSize
    ) {
        super(
            bulkOperationJson -> new BulkOperation(gson.fromJson(bulkOperationJson, ExtensibleObject.class)),
            cloudOfThingsRestClient,
            relativeApiUrl,
            gson,
            CONTENT_TYPE_BULK_OPERATION_COLLECTION,
            BULK_OPERATION_COLLECTION_ELEMENT_NAME,
            filterBuilder,
            pageSize
        );
    }

    /**
     * Retrieves the current page.
     * <p>
     * Retrieves the BulkOperations influenced by filters set in construction.
     *
     * @return array of found BulkOperations
     */
    public BulkOperation[] getBulkOperations() {
        final JsonArray jsonBulkOperations = getJsonArray();

        return (jsonBulkOperations == null) ? new BulkOperation[0] : StreamSupport.stream(jsonBulkOperations.spliterator(), false).
                map(objectMapper).
                toArray(BulkOperation[]::new);
    }
}
