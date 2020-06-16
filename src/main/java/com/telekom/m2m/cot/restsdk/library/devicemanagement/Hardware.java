package com.telekom.m2m.cot.restsdk.library.devicemanagement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

public class Hardware implements Fragment {

    private final String model;
    private final String revision;
    private final String serialNumber;


    public Hardware(String model, String revision, String serialNumber) {
        this.model = model;
        this.revision = revision;
        this.serialNumber = serialNumber;
    }


    public String getModel() {
        return model;
    }

    public String getRevision() {
        return revision;
    }

    public String getSerialNumber() {
        return serialNumber;
    }


    @Override
    public String getId() {
        return "c8y_Hardware";
    }

    @Override
    public JsonElement getJson() {
        JsonObject object = new JsonObject();
        object.addProperty("model", model);
        object.addProperty("revision", revision);
        object.addProperty("serialNumber", serialNumber);

        return object;
    }
}
