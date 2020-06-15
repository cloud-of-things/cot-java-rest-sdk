package com.telekom.m2m.cot.restsdk.library.sensor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

public class Position implements Fragment {

    private final float altitude;
    private final float longitude;
    private final float latitude;
    private final String trackingProtocol;
    private final String reportReason;


    public Position(float altitude, float longitude, float latitude, String trackingProtocol, String reportReason) {
        this.altitude = altitude;
        this.longitude = longitude;
        this.latitude = latitude;
        this.trackingProtocol = trackingProtocol;
        this.reportReason = reportReason;
    }


    public float getAltitude() {
        return altitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public String getTrackingProtocol() {
        return trackingProtocol;
    }

    public String getReportReason() {
        return reportReason;
    }

    @Override
    public String getId() {
        return "c8y_Position";
    }

    @Override
    public JsonElement getJson() {
        JsonObject object = new JsonObject();
        object.addProperty("alt", altitude);
        object.addProperty("lng", longitude);
        object.addProperty("lat", latitude);
        object.addProperty("lat", latitude);
        object.addProperty("trackingProtocol", trackingProtocol);
        object.addProperty("reportReason", reportReason);

        return object;
    }
}
