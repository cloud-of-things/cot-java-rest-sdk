package com.telekom.m2m.cot.restsdk.library.sensor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

/**
 * The Mobile fragment is part of the sensor library as well as the device control library. It has more attributes
 * in the sensor lib. Just use the appropriate constructor to create one or the other.
 */
public class Mobile implements Fragment {

    public final String imsi;
    public final String imei;
    public final String currentOperator;
    public final String currentBand;
    public final String connType;
    public final String rssi;
    public final String ecn0;
    public final String rcsp;
    public final String mnc;
    public final String lac;
    public final String cellId;
    public final String msisdn;
    public final String iccid;

    // This flag differentiates between the simple version (device management lib; false) and the
    // complete, verbose version (sensor lib; true).
    private boolean isComplete = true;


    /**
     * This is the partial constructor for c8y_Mobile as described in the device management library.
     */
    public Mobile(String imei, String cellId, String iccid) {
        this(null, imei, null, null, null, null, null, null, null, null, cellId,  null, iccid);
        isComplete = false;
    }


    /**
     * This is the full constructor for c8y_Mobile as described in the sensor library.
     */
    public Mobile(String imsi,
                  String imei,
                  String currentOperator,
                  String currentBand,
                  String connType,
                  String rssi,
                  String ecn0,
                  String rcsp,
                  String mnc,
                  String lac,
                  String cellId,
                  String msisdn,
                  String iccid) {

        this.imsi = imsi;
        this.imei = imei;
        this.currentOperator = currentOperator;
        this.currentBand = currentBand;
        this.connType = connType;
        this.rssi = rssi;
        this.ecn0 = ecn0;
        this.rcsp = rcsp;
        this.mnc = mnc;
        this.lac = lac;
        this.cellId = cellId;
        this.msisdn = msisdn;
        this.iccid = iccid;
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

        if (isComplete) {
            object.addProperty("imsi", imsi);
            object.addProperty("currentOperator", currentOperator);
            object.addProperty("currentBand", currentBand);
            object.addProperty("connType", connType);
            object.addProperty("rssi", rssi);
            object.addProperty("ecn0", ecn0);
            object.addProperty("rcsp", rcsp);
            object.addProperty("mnc", mnc);
            object.addProperty("lac", lac);
            object.addProperty("msisdn", msisdn);
        }
        return object;
    }

}
