package com.telekom.m2m.cot.restsdk.library.devicemanagement;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SoftwareList implements Fragment {

    private static final Gson gson = GsonUtils.createGson();

    private List<Software> softwareList = new ArrayList<>();


    public SoftwareList(Software... softwareList) {
        Collections.addAll(this.softwareList, softwareList);
    }

    public List<Software> getSoftwareList() {
        // It's ok to give out our Software instances because they are immutable.
        return new ArrayList<>(softwareList);
    }

    public SoftwareList addSoftware(Software software) {
        softwareList.add(software);
        return this;
    }

    public SoftwareList removeSoftware(Software software) {
        softwareList.remove(software);
        return this;
    }


    @Override
    public String getId() {
        return "c8y_SoftwareList";
    }

    @Override
    public JsonElement getJson() {

        JsonArray array = new JsonArray();
        for (Software software : softwareList) {
            array.add(gson.toJsonTree(software));
        }

        return array;
    }


    public static class Software {
        public final String name;
        public final String version;
        public final String url;

        public Software(String name, String version, String url) {
            this.name = name;
            this.version = version;
            this.url = url;
        }
    }

}
