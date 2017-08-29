package com.telekom.m2m.cot.restsdk.measurement;

/**
 * Created by Patrick Steinert on 07.02.16.
 */
public class MeasurementReading {
    private float value;
    private String unit;
    
    public MeasurementReading() {
        super();
    }

    public MeasurementReading(float value, String unit) {

        this.value = value;
        this.unit = unit;
    }

    public MeasurementReading(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

}
