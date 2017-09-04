package com.telekom.m2m.cot.restsdk.library.devicemanagement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;


public class RequiredAvailability implements Fragment {

    private float responseInterval;


    public RequiredAvailability(float responseInterval) {
        this.responseInterval = responseInterval;
    }


    public float getResponseInterval() {
        return responseInterval;
    }


    @Override
    public String getId() {
        return "c8y_RequiredAvailability";
    }

    @Override
    public JsonElement getJson() {
        JsonObject intervalObject = new JsonObject();
        intervalObject.addProperty("responseInterval", responseInterval);

        return intervalObject;
    }

}
