package com.telekom.m2m.cot.restsdk.library.sensor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

public class AccelerationMeasurement  implements Fragment {

    private float accelerationValue;
    private String accelerationUnit;


    public AccelerationMeasurement(float accelerationValue, String accelerationUnit) {
        
        this.accelerationUnit=accelerationUnit;
        this.accelerationValue=accelerationValue;
    }
    
    
    public float getAccelerationValue() {
        
        return accelerationValue;
    }

    public String getAccelerationUnit() {
        
        return accelerationUnit;
    }

    
    @Override
    public String getId() {
        return "c8y_AccelerationMeasurement";
    }
    

    @Override
    public JsonElement getJson() {
        JsonObject acceleration = new JsonObject();
        acceleration.addProperty("value", accelerationValue);
        acceleration.addProperty("unit", accelerationUnit);

        JsonObject accelerationObject = new JsonObject();
        accelerationObject.add("acceleration", acceleration);

        return accelerationObject;
    }
    
}
