package com.telekom.m2m.cot.restsdk.library.sensor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

public class MoistureSensor implements Fragment{

    

    @Override
    public String getId() {
        return "c8y_MoistureSensor";
    }

    @Override
    public JsonElement getJson() {
        return new JsonObject();


    }
}