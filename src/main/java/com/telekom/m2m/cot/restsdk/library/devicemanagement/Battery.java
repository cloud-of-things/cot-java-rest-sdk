package com.telekom.m2m.cot.restsdk.library.devicemanagement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;


public class Battery implements Fragment {

    private float value;
    private String unit;


    public Battery(float value, String unit) {
        this.value = value;
        this.unit = unit;
    }


    public float getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }


    @Override
    public String getId() {
        return "c8y_Battery";
    }

    @Override
    public JsonElement getJson() {
        JsonObject level = new JsonObject();
        level.addProperty("value", value);
        level.addProperty("unit", unit);

        JsonObject levelObject = new JsonObject();
        levelObject.add("level", level);

        JsonObject object = new JsonObject();
        object.add(getId(), levelObject);
        return object;
    }

}
