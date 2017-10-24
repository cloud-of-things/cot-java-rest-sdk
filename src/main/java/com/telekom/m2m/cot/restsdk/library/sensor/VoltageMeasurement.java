package com.telekom.m2m.cot.restsdk.library.sensor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

public class VoltageMeasurement implements Fragment {

    private float voltageValue;
    private String voltageUnit;
    
    public VoltageMeasurement(float voltageValue, String voltageUnit) {
        
        this.voltageUnit=voltageUnit;
        this.voltageValue=voltageValue;
    }
    
    
    public float getVoltageValue() {
        
        return voltageValue;
    }

    public String getVoltageUnit() {
        
        return voltageUnit;
    }


    @Override
    public String getId() {
        return "c8y_VoltageMeasurement";
    }

    
    @Override
    public JsonElement getJson() {
        JsonObject voltage = new JsonObject();
        voltage.addProperty("value", voltageValue);
        voltage.addProperty("unit", voltageUnit);

        JsonObject voltageObject = new JsonObject();
        voltageObject.add("voltage", voltage);

        return voltageObject;
    }
    
}
