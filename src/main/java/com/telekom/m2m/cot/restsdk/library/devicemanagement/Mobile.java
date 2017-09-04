package com.telekom.m2m.cot.restsdk.library.devicemanagement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;


public class Mobile implements Fragment {

    private String imei;
    private String cellId;
    private String iccid;


    public Mobile(String imei, String cellId, String iccid) {
        this.imei = imei;
        this.cellId = cellId;
        this.iccid = iccid;
    }


    public String getImei() {
        return imei;
    }

    public String getCellId() {
        return cellId;
    }

    public String getIccid() {
        return iccid;
    }


    @Override
    public String getId() {
        return "c8y_Mobile";
    }

    @Override
    public JsonElement getJson() {
        JsonObject object = new JsonObject();
        object.addProperty("imei", imei);
        object.addProperty("cellId", cellId);
        object.addProperty("iccid", iccid);

        return object;
    }

}
