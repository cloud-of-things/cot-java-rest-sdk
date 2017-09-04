package com.telekom.m2m.cot.restsdk.library.devicemanagement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;


public class IsDevice implements Fragment {

    @Override
    public String getId() {
        return "c8y_IsDevice";
    }

    @Override
    public JsonElement getJson() {
        return new JsonObject();
    }

}
