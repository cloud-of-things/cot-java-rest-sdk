package com.telekom.m2m.cot.restsdk.library.sensor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

public class MoistureMeasurement implements Fragment {

    private float moistureValue;
    private String moistureUnit;


    public MoistureMeasurement(float moistureValue, String moistureUnit) {
        
        this.moistureUnit=moistureUnit;
        this.moistureValue=moistureValue;
    }
    
    
    public float getMoistureValue() {
        
        return moistureValue;
    }

    public String getMoistureUnit() {
        
        return moistureUnit;
    }


    @Override
    public String getId() {
        return "c8y_MoistureMeasurement";
    }

    
    @Override
    public JsonElement getJson() {
        JsonObject moisture = new JsonObject();
        moisture.addProperty("value", moistureValue);
        moisture.addProperty("unit", moistureUnit);

        JsonObject moistureObject = new JsonObject();
        moistureObject.add("moisture", moisture);

        return moistureObject;
    }
    
}
