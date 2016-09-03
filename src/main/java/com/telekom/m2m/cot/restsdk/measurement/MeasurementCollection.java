package com.telekom.m2m.cot.restsdk.measurement;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Created by breucking on 14.02.16.
 */
public class MeasurementCollection /*implements Iterable<Measurement>*/ {

    private CloudOfThingsRestClient cloudOfThingsRestClient;

    private Gson gson = GsonUtils.createGson();

    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.measurementCollection+json;charset=UTF-8;ver=0.9";

    public MeasurementCollection(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    public Measurement[] getMeasurements() {
        String response = cloudOfThingsRestClient.getResponse("", "/measurement/measurements", CONTENT_TYPE);
        JsonObject object = gson.fromJson(response, JsonObject.class);
        if (object.has("measurements")) {
            JsonArray jsonMeasurements = object.get("measurements").getAsJsonArray();
            Measurement[] arrayOfMeasurements = new Measurement[jsonMeasurements.size()];
            for (int i = 0; i < jsonMeasurements.size(); i++) {
                JsonElement jsonMeasurement = jsonMeasurements.get(i).getAsJsonObject();
                Measurement measurement = new Measurement(gson.fromJson(jsonMeasurement, ExtensibleObject.class));
                arrayOfMeasurements[i] = measurement;
            }
            return arrayOfMeasurements;
        } else
            return null;
    }


//    public Iterator<Measurement> iterator() {
//        return null;
//    }
//
//    public Iterable<Measurement> elements(int limit) {
//        return new MeasurementCollectionIterable()
//    }
}
