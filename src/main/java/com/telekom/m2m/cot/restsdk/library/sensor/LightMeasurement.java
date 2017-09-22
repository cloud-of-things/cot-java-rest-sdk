package com.telekom.m2m.cot.restsdk.library.sensor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

public class LightMeasurement implements Fragment{

    private float lightValue;
    private String lightUnit;
    
    public LightMeasurement(float lightValue, String lightUnit){
        
        this.lightUnit=lightUnit;
        this.lightValue=lightValue;
    }
    
    
    public float getLightValue(){
        
        return lightValue;
    }

    public String getLightUnit(){
        
        return lightUnit;
    }

    
    
    @Override
    public String getId() {
        return "c8y_LightMeasurement";
    }
    
    
    
    @Override
    public JsonElement getJson() {
        JsonObject light = new JsonObject();
        light.addProperty("value", lightValue);
        light.addProperty("unit", lightUnit);



        JsonObject lightObject = new JsonObject();
        lightObject.add("e", light);

        return lightObject;
    }
    
}
