package com.telekom.m2m.cot.restsdk.measurement;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.JsonArrayPagination;

/**
 * Represents a pageable Measurement collection.
 * <p>
 * Collection can be scrolled with next() and prev().
 * <p>
 *
 * @since 0.1.0
 * Created by Patrick Steinert on 14.02.16.
 */
public class MeasurementCollection extends JsonArrayPagination {

    private static final String COLLECTION_CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.measurementCollection+json;charset=UTF-8;ver=0.9";
    private static final String COLLECTION_ELEMENT_NAME = "measurements";

    /**
     * Internal contstructor to create an MeasurementCollection.
     * Use {@link MeasurementApi} to get MeasurementCollection.
     *
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     * @param relativeApiUrl          relative url of the REST API without leading slash.
     * @param gson                    the necessary json De-/serializer.
     * @param filterBuilder           the build criteria or null if all items should be retrieved.
     * @param pageSize                max number of retrieved elements per page.
     */
    MeasurementCollection(final CloudOfThingsRestClient cloudOfThingsRestClient,
                         final String relativeApiUrl,
                         final Gson gson,
                         final Filter.FilterBuilder filterBuilder,
                         final int pageSize) {
        super(cloudOfThingsRestClient, relativeApiUrl, gson, COLLECTION_CONTENT_TYPE, COLLECTION_ELEMENT_NAME, filterBuilder, pageSize);
    }

    /**
     * Retrieves the current page.
     * <p>
     * Retrieves the Measurements influenced by filters set in construction.
     *
     * @return array of found Measurements
     */
    public Measurement[] getMeasurements() {
        final JsonArray jsonMeasurements = getJsonArray();

        if (jsonMeasurements != null) {
            final Measurement[] arrayOfMeasurements = new Measurement[jsonMeasurements.size()];
            for (int i = 0; i < jsonMeasurements.size(); i++) {
                JsonElement jsonMeasurement = jsonMeasurements.get(i).getAsJsonObject();
                final Measurement measurement = new Measurement(gson.fromJson(jsonMeasurement, ExtensibleObject.class));
                arrayOfMeasurements[i] = measurement;
            }
            return arrayOfMeasurements;
        } else {
            return null;
        }
    }
}
