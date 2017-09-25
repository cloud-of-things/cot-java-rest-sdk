package com.telekom.m2m.cot.restsdk.library.devicemanagement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;


/**
 * The SignalStrength fragment is part of the sensor library as well as the device control library. It has more
 * attributes in the device control lib. Just use the appropriate constructor to create one or the other.
 */
public class SignalStrength implements Fragment {

    private float rssiValue;
    private String rssiUnit;

    private Float berValue;
    private String berUnit;

    // This flag differentiates between the simple version (sensor lib; false) and the
    // complete, verbose version (device management lib; true).
    private boolean isComplete = true;

    /**
     * This is the partial constructor for c8y_SignalStrength as described in the sensor library.
     */
    public SignalStrength(float rssiValue, String rssiUnit) {
        this(rssiValue, rssiUnit, null, null);
        isComplete = false;
    }


    /**
     * This is the full constructor for c8y_SignalStrength as described in the device management library.
     */
    public SignalStrength(float rssiValue, String rssiUnit, Float berValue, String berUnit) {
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

    public Float getBerValue() {
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

        JsonObject strengthObject = new JsonObject();
        strengthObject.add("rssi", rssi);

        if (isComplete) {
            JsonObject ber = new JsonObject();
            ber.addProperty("value", berValue);
            ber.addProperty("unit", berUnit);
            strengthObject.add("ber", ber);
        }

        return strengthObject;
    }

}
