package com.telekom.m2m.cot.restsdk.measurement;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;
import com.telekom.m2m.cot.restsdk.util.IFilter;

/**
 * The API object to operate with Measrements in the platform.
 *
 * Created by breucking on 07.02.16.
 * @since 0.1.0
 */
public class MeasurementApi {

    private static final String CONTENT_TYPE = " application/vnd.com.nsn.cumulocity.measurement+json;charset=UTF-8;ver=0.9";

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
     * @param id of the desired Measurement.
     * @return the Measurement (or null if not found).
     */
    public Measurement getMeasurement(String id) {
        String response = cloudOfThingsRestClient.getResponse(id, "measurement/measurements/", CONTENT_TYPE);
        if (response == null) {
            throw new CotSdkException("Measurement not found (id='" + id + "')");
        }
        Measurement measurement = new Measurement(gson.fromJson(response, ExtensibleObject.class));
        return measurement;
    }

    /**
     * Stores a Measurement.
     * @param measurement the measurement to create.
     * @return the created measurement with the ID.
     */
    public Measurement createMeasurement(Measurement measurement) {
        String json = gson.toJson(measurement);
        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "measurement/measurements/", CONTENT_TYPE);
        measurement.setId(id);
        return measurement;
    }

    /**
     * Deletes a Measurement.
     * @param measurement the Measurement to delete
     */
    public void delete(Measurement measurement) {
        cloudOfThingsRestClient.delete(measurement.getId(), "measurement/measurements/");
    }

    /**
     * Retrieves Measurements.
     *
     * @return the found Measurements.
     */
    public MeasurementCollection getMeasurements() {
        return new MeasurementCollection(cloudOfThingsRestClient);
    }

    /**
     * Retrieves Measurements filtered by criteria.
     *
     * @param criteria filter of Measurements.
     * @return the MeasurementsCollections to naviagte through the results.
     * @since 0.2.0
     */
    public MeasurementCollection getMeasurements(IFilter criteria) {
        return new MeasurementCollection(criteria, cloudOfThingsRestClient);
    }
}
