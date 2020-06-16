package com.telekom.m2m.cot.restsdk.util;

import com.telekom.m2m.cot.restsdk.measurement.MeasurementReading;

/**
 * Created by Patrick Steinert on 06.02.16.
 */
public class SampleTemperatureSensor {

    private final MeasurementReading temperature;

    public SampleTemperatureSensor(final float temperature) {
        this.temperature = new MeasurementReading(temperature, "°C");
    }

    public MeasurementReading getTemperature() {
        return temperature;
    }
}
