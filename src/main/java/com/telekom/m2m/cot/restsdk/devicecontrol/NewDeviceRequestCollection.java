package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.JsonArrayPagination;

/**
 * Represents a pageable NewDeviceRequest collection.
 *
 * @since 0.3.0
 * Created by Patrick Steinert on 19.12.16.
 */
public class NewDeviceRequestCollection extends JsonArrayPagination {

    private static final String CONTENT_TYPE_NEW_DEVICE_REQUEST_COLLECTION = "application/vnd.com.nsn.cumulocity.newDeviceRequestCollection+json;charset=UTF-8;ver=0.9";
    private static final String NEW_DEVICE_REQUEST_COLLECTION_ELEMENT_NAME = "newDeviceRequests";

    /**
     * Creates an NewDeviceRequestCollection.
     * Use {@link DeviceCredentialsApi} to get NewDeviceRequestCollection.
     *
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     * @param relativeApiUrl          relative url of the REST API without leading slash.
     * @param gson                    the necessary json De-/serializer.
     * @param filterBuilder           the build criteria or null if all items should be retrieved.
     * @param pageSize                max number of retrieved elements per page.
     */
    NewDeviceRequestCollection(final CloudOfThingsRestClient cloudOfThingsRestClient,
                    final String relativeApiUrl,
                    final Gson gson,
                    final Filter.FilterBuilder filterBuilder,
                    final int pageSize) {
        super(cloudOfThingsRestClient, relativeApiUrl, gson, CONTENT_TYPE_NEW_DEVICE_REQUEST_COLLECTION, NEW_DEVICE_REQUEST_COLLECTION_ELEMENT_NAME, filterBuilder, pageSize);
    }

    /**
     * Retrieves the current page.
     * <p>
     * Retrieves the NewDeviceRequests influenced by filters set in construction.
     *
     * @return array of found NewDeviceRequests
     */
    public NewDeviceRequest[] getNewDeviceRequests() {
        final JsonArray jsonNewDeviceRequests = getJsonArray();

        if (jsonNewDeviceRequests != null) {
            final NewDeviceRequest[] arrayOfNewDeviceRequests = new NewDeviceRequest[jsonNewDeviceRequests.size()];
            for (int i = 0; i < jsonNewDeviceRequests.size(); i++) {
                JsonElement jsonNewDeviceRequest = jsonNewDeviceRequests.get(i).getAsJsonObject();
                final NewDeviceRequest newDeviceRequest = new NewDeviceRequest(gson.fromJson(jsonNewDeviceRequest, ExtensibleObject.class));
                arrayOfNewDeviceRequests[i] = newDeviceRequest;
            }
            return arrayOfNewDeviceRequests;
        } else {
            return new NewDeviceRequest[0];
        }
    }
}
