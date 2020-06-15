package com.telekom.m2m.cot.restsdk.library.sensor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

public class TemperatureMeasurement implements Fragment {

    private final float temperatureValue;
    private final String temperatureUnit;
    
    
    public TemperatureMeasurement(float temperatureValue, String temperatureUnit) {
        this.temperatureValue = temperatureValue;
        this.temperatureUnit = temperatureUnit;
    }


    public float getTemperatureValue(){
        return temperatureValue;
    }

    public String getTemperatureUnit(){
        return temperatureUnit;
    }
    
    @Override
    public String getId() {
        return "c8y_TemperatureMeasurement";
    }

    
    @Override
    public JsonElement getJson() {
        JsonObject temperature = new JsonObject();
        temperature.addProperty("value", temperatureValue);
        temperature.addProperty("unit", temperatureUnit);

        JsonObject temperatureObject = new JsonObject();
        temperatureObject.add("T", temperature);
        return temperatureObject;
    }
    
}
