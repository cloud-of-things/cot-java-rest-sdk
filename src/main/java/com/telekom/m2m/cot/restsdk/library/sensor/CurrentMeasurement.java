package com.telekom.m2m.cot.restsdk.library.sensor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

public class CurrentMeasurement implements Fragment{

    private float currentValue;
    private String currentUnit;
    
    public CurrentMeasurement(float currentValue, String currentUnit){
        
        this.currentUnit=currentUnit;
        this.currentValue=currentValue;
    }
    
    
    public float getcurrentValue(){
        
        return currentValue;
    }

    public String getcurrentUnit(){
        
        return currentUnit;
    }

    
    
    @Override
    public String getId() {
        return "c8y_CurrentMeasurement";
    }
    
    
    
    @Override
    public JsonElement getJson() {
        JsonObject current = new JsonObject();
        current.addProperty("value", currentValue);
        current.addProperty("unit", currentUnit);



        JsonObject currentObject = new JsonObject();
        currentObject.add("current", current);

        return currentObject;
    }
    
}
