package com.telekom.m2m.cot.restsdk.util;

import java.math.BigDecimal;

public class MeasurementValue {
    private final BigDecimal value;
    private final String unit;

    public MeasurementValue(BigDecimal value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }
}
