package com.telekom.m2m.cot.restsdk.util;

import com.telekom.m2m.cot.restsdk.measurement.MeasurementReading;

/**
 * Created by breucking on 06.02.16.
 */
public class SampleTemperatureSensor {

    private MeasurementReading temperature;

    public SampleTemperatureSensor() {

    }

    public SampleTemperatureSensor(MeasurementReading temperature) {
        this.temperature = temperature;
    }

    public MeasurementReading getTemperature() {
        return temperature;
    }


    public void setTemperature(float temperature) {
        this.temperature = new MeasurementReading("°C");
        this.temperature.setValue(temperature);
    }

}
