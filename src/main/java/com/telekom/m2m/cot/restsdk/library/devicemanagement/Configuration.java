package com.telekom.m2m.cot.restsdk.library.devicemanagement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;


public class Configuration implements Fragment {

    private String config;


    public Configuration(String config) {
        this.config = config;
    }


    public String getConfig() {
        return config;
    }


    @Override
    public String getId() {
        return "c8y_Configuration";
    }

    @Override
    public JsonElement getJson() {
        JsonObject object = new JsonObject();
        object.addProperty(getId(), config);
        return object;
    }

}
