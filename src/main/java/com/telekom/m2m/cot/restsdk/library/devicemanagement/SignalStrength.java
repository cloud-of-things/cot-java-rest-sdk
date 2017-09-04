package com.telekom.m2m.cot.restsdk.library.devicemanagement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;


public class SignalStrength implements Fragment {

    private float rssiValue;
    private String rssiUnit;

    private float berValue;
    private String berUnit;


    public SignalStrength(float rssiValue, String rssiUnit, float berValue, String berUnit) {
        this.rssiValue = rssiValue;
        this.rssiUnit = rssiUnit;
        this.berValue = berValue;
        this.berUnit = berUnit;
    }


    public float getRssiValue() {
        return rssiValue;
    }

    public String getRssiUnit() {
        return rssiUnit;
    }

    public float getBerValue() {
        return berValue;
    }

    public String getBerUnit() {
        return berUnit;
    }


    @Override
    public String getId() {
        return "c8y_SignalStrength";
    }

    @Override
    public JsonElement getJson() {
        JsonObject rssi = new JsonObject();
        rssi.addProperty("value", rssiValue);
        rssi.addProperty("unit", rssiUnit);

        JsonObject ber = new JsonObject();
        ber.addProperty("value", berValue);
        ber.addProperty("unit", berUnit);

        JsonObject strengthObject = new JsonObject();
        strengthObject.add("rssi", rssi);
        strengthObject.add("ber", ber);

        return strengthObject;
    }

}
