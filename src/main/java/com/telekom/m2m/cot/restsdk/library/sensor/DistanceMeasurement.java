package com.telekom.m2m.cot.restsdk.library.sensor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

public class DistanceMeasurement implements Fragment {

    private float distanceValue;
    private String distanceUnit;


    public DistanceMeasurement(float distanceValue, String distanceUnit) {
        
        this.distanceUnit=distanceUnit;
        this.distanceValue=distanceValue;
    }
    
    
    public float getDistanceValue() {
        
        return distanceValue;
    }

    public String getDistanceUnit() {
        
        return distanceUnit;
    }

    
    
    @Override
    public String getId() {
        return "c8y_DistanceMeasurement";
    }
    
    
    
    @Override
    public JsonElement getJson() {
        JsonObject distance = new JsonObject();
        distance.addProperty("value", distanceValue);
        distance.addProperty("unit", distanceUnit);

        JsonObject distanceObject = new JsonObject();
        distanceObject.add("distance", distance);

        return distanceObject;
    }
    
}
