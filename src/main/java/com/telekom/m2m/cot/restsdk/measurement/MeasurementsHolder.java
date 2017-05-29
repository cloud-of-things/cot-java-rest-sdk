package com.telekom.m2m.cot.restsdk.measurement;

import java.util.List;

/**
 * Holds a list of {@link Measurement}s.
 */
class MeasurementsHolder {

    private List<Measurement> measurements;

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(final List<Measurement> measurements) {
        this.measurements = measurements;
    }
}
