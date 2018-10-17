package com.telekom.m2m.cot.restsdk.measurement;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The API object to operate with Measurements in the platform.
 * <p>
 * Created by Patrick Steinert on 07.02.16.
 *
 * @since 0.1.0
 */
public class MeasurementApi {

    private static final String CONTENT_TYPE_MEASUREMENT = "application/vnd.com.nsn.cumulocity.measurement+json;charset=UTF-8;ver=0.9";
    private static final String ACCEPT_MEASUREMENT = "application/vnd.com.nsn.cumulocity.measurement+json;charset=UTF-8;ver=0.9";
    private static final String CONTENT_TYPE_MEASUREMENT_COLLECTION = "application/vnd.com.nsn.cumulocity.measurementCollection+json;charset=UTF-8;ver=0.9";
    private static final String ACCEPT_MEASUREMENT_COLLECTION = "application/vnd.com.nsn.cumulocity.measurementCollection+json;charset=UTF-8;ver=0.9";
    private static final List<FilterBy> acceptedFilters = Arrays.asList(FilterBy.BYSOURCE, FilterBy.BYDATEFROM, FilterBy.BYDATETO, FilterBy.BYFRAGMENTTYPE, FilterBy.BYTYPE);

    private static final String MEASUREMENTS_API = "measurement/measurements/";

    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    private Gson gson = GsonUtils.createGson();

    /**
     * Internal Constructor.
     *
     * @param cloudOfThingsRestClient the configured rest client.
     */
    public MeasurementApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }


    /**
     * Retrives a specific Measurement.
     *
     * @param id of the desired Measurement.
     * @return the Measurement (or null if not found).
     */
    public Measurement getMeasurement(String id) {
        String response = cloudOfThingsRestClient.getResponse(id, MEASUREMENTS_API, CONTENT_TYPE_MEASUREMENT);
        //assuming: when response is null, then http status code is 404
        if (response == null) {
            throw new CotSdkException(404, "Measurement not found (id='" + id + "')");
        }
        return new Measurement(gson.fromJson(response, ExtensibleObject.class));
    }

    /**
     * Stores a Measurement.
     *
     * @param measurement the measurement to create.
     * @return the created measurement with the ID.
     */
    public Measurement createMeasurement(Measurement measurement) {
        String json = gson.toJson(measurement);
        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, MEASUREMENTS_API, CONTENT_TYPE_MEASUREMENT, ACCEPT_MEASUREMENT);
        measurement.setId(id);
        return measurement;
    }

    /**
     * Stores a list of Measurements.
     *
     * @param measurements the measurements to create.
     * @return the created measurement with the ID.
     */
    public List<Measurement> createMeasurements(final List<Measurement> measurements) {

        if (measurements.isEmpty()) {
            return Collections.emptyList();
        }

        final String json = gson.toJson(createJsonObject(measurements));

        final String response = cloudOfThingsRestClient.doPostRequest(json, MEASUREMENTS_API, CONTENT_TYPE_MEASUREMENT_COLLECTION, ACCEPT_MEASUREMENT_COLLECTION);

        return gson.fromJson(response, MeasurementsHolder.class)
                .getMeasurements();
    }

    /**
     * Deletes a Measurement.
     *
     * @param measurement the Measurement to delete
     */
    public void delete(Measurement measurement) {
        cloudOfThingsRestClient.delete(measurement.getId(), MEASUREMENTS_API);
    }

    /**
     * Retrieves Measurements.
     *
     * @param resultSize size of the results (Max. 2000)
     * @return the found Measurements.
     */
    public MeasurementCollection getMeasurements(int resultSize) {
        return new MeasurementCollection(
                cloudOfThingsRestClient,
                MEASUREMENTS_API,
                gson,
                null,
                resultSize);
    }

    /**
     * Retrieves Measurements filtered by criteria.
     *
     * @param filters    filters of measurement attributes.
     * @param resultSize size of the results (Max. 2000)
     * @return the MeasurementsCollections to navigate through the results.
     * @since 0.2.0
     */
    public MeasurementCollection getMeasurements(Filter.FilterBuilder filters, int resultSize) {
        if(filters != null) {
            filters.validateSupportedFilters(acceptedFilters);
        }
        return new MeasurementCollection(
                cloudOfThingsRestClient,
                MEASUREMENTS_API,
                gson,
                filters,
                resultSize);
    }

    /**
     * Deletes a collection of Measurements by criteria.
     *
     * @param filters filters of measurement attributes.
     */
    public void deleteMeasurements(Filter.FilterBuilder filters) {
        if(filters != null) {
            filters.validateSupportedFilters(acceptedFilters);
        }
        cloudOfThingsRestClient.delete("", MEASUREMENTS_API + "?" + filters.buildFilter() + "&x=");
    }

    private JsonObject createJsonObject(final List<Measurement> measurements) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.add("measurements", listToJsonArray(measurements));
        return jsonObject;
    }

    private JsonArray listToJsonArray(final List<Measurement> measurements) {
        final JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(gson.toJson(measurements)).getAsJsonArray();
    }

}
