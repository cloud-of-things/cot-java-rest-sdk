package com.telekom.m2m.cot.restsdk.library.devicemanagement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;


public class Firmware implements Fragment {

    private final String name;
    private final String version;
    private final String url;


    public Firmware(String name, String version, String url) {
        this.name = name;
        this.version = version;
        this.url = url;
    }


    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getUrl() {
        return url;
    }


    @Override
    public String getId() {
        return "c8y_Firmware";
    }

    @Override
    public JsonElement getJson() {
        JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.addProperty("version", version);
        object.addProperty("url", url);

        return object;
    }

}
