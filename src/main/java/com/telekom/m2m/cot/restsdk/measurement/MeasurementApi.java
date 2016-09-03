package com.telekom.m2m.cot.restsdk.measurement;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Created by breucking on 07.02.16.
 */
public class MeasurementApi {

    private static final String CONTENT_TYPE = " application/vnd.com.nsn.cumulocity.measurement+json;charset=UTF-8;ver=0.9";

    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    private Gson gson = GsonUtils.createGson();

    public MeasurementApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    public Measurement getMeasurement(String id) {
        String response = cloudOfThingsRestClient.getResponse(id, "measurement/measurements/", CONTENT_TYPE);
        if (response == null) {
            throw new CotSdkException("Measurement not found (id='" + id + "')");
        }
        Measurement measurement = new Measurement(gson.fromJson(response, ExtensibleObject.class));
        return measurement;
    }

    public Measurement createMeasurement(Measurement measurement) {
        String json = gson.toJson(measurement);
        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "measurement/measurements/", CONTENT_TYPE);
        measurement.setId(id);
        return measurement;
    }

    public void delete(Measurement measurement) {
        cloudOfThingsRestClient.delete(measurement.getId(), "measurement/measurements/");
    }

    public MeasurementCollection getMeasurements() {
        return new MeasurementCollection(cloudOfThingsRestClient);
    }
}
