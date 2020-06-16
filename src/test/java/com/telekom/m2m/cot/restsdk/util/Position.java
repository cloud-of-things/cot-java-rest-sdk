package com.telekom.m2m.cot.restsdk.util;

/**
 * Created by Patrick Steinert on 05.02.16.
 */
public class Position  {

    double lat;
    double lon;
    double alt;

    public Position(double lat, double lon, double alt) {
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
    }

    public double getAlt() {
        return alt;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return "Position{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", alt=" + alt +
                '}';
    }
}
