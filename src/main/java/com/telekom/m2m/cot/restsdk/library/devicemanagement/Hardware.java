package com.telekom.m2m.cot.restsdk.library.devicemanagement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

public class Hardware implements Fragment {

    private String model;
    private String revision;
    private String serialNumber;


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
        JsonObject internal = new JsonObject();
        internal.addProperty("model", model);
        internal.addProperty("revision", revision);
        internal.addProperty("serialNumber", serialNumber);

        JsonObject object = new JsonObject();
        object.add(getId(), internal);
        return object;
    }
}
