package com.telekom.m2m.cot.restsdk.library.sensor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

public class HumidityMeasurement implements Fragment {

    private float humidityValue;
    private String humidityUnit;


    public HumidityMeasurement(float humidityValue, String humidityUnit) {
        
        this.humidityUnit=humidityUnit;
        this.humidityValue=humidityValue;
    }
    
    
    public float getHumidityValue() {
        
        return humidityValue;
    }

    public String getHumidityUnit() {
        
        return humidityUnit;
    }

    
    @Override
    public String getId() {
        return "c8y_HumidityMeasurement";
    }

    
    @Override
    public JsonElement getJson() {
        JsonObject humidity = new JsonObject();
        humidity.addProperty("value", humidityValue);
        humidity.addProperty("unit", humidityUnit);

        JsonObject humidityObject = new JsonObject();
        humidityObject.add("h", humidity);

        return humidityObject;
    }
    
}
