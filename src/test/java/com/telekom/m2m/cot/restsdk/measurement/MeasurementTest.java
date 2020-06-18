package com.telekom.m2m.cot.restsdk.measurement;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.util.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Patrick Steinert on 05.02.16.
 */
public class MeasurementTest {

    private final Gson gson = GsonUtils.createGson();

    private final static String ID = "1234567";
    private final static String TYPE = "ASpecialType";
    private final static String CLASS_TYPE = "com_telekom_m2m_cot_restsdk_util_SampleTemperatureSensor";
    private final static String FRAGMENT = "fragment";

    @Test
    public void testMeasurementSerialization() {
        Date date = new Date();
        SampleTemperatureSensor sampleTemperatureSensor = new SampleTemperatureSensor(1);

        Measurement measurement = new Measurement();
        measurement.setId(ID);
        measurement.setTime(date);
        measurement.setType(TYPE);
        measurement.set(sampleTemperatureSensor);

        String json = gson.toJson(measurement);

        Measurement m = new Measurement(gson.fromJson(json, ExtensibleObject.class));

        Assert.assertEquals(m.getId(), ID);
        Assert.assertEquals(m.getType(), TYPE);
        Assert.assertEquals(m.getTime().compareTo(date), 0);
        Assert.assertNotNull(m.get(CLASS_TYPE));
    }

    @Test
    public void testMeasurementFragmentSerialization() {
        Date date = new Date();
        SampleTemperatureSensor sampleTemperatureSensor = new SampleTemperatureSensor(100);

        Measurement measurement = new Measurement();
        measurement.setId(ID);
        measurement.setTime(date);
        measurement.setType(TYPE);
        measurement.set(sampleTemperatureSensor);

        String serializedMeasurement = gson.toJson(measurement);

        Measurement deserializedMeasurement = new Measurement(gson.fromJson(serializedMeasurement, ExtensibleObject.class));
        String reserializedMeasurement = gson.toJson(deserializedMeasurement);

        Assert.assertEquals(reserializedMeasurement, serializedMeasurement);

        Assert.assertEquals(deserializedMeasurement.getId(), ID);
        Assert.assertEquals(deserializedMeasurement.getType(), TYPE);
        Assert.assertEquals(deserializedMeasurement.getTime().compareTo(date), 0);
        Assert.assertNotNull(deserializedMeasurement.get(CLASS_TYPE));
        Assert.assertNotNull(((SampleTemperatureSensor)deserializedMeasurement.get(CLASS_TYPE)).getTemperature());
        Assert.assertEquals(((SampleTemperatureSensor)deserializedMeasurement.get(CLASS_TYPE)).getTemperature().getValue(), sampleTemperatureSensor.getTemperature().getValue());
        Assert.assertEquals(((SampleTemperatureSensor)deserializedMeasurement.get(CLASS_TYPE)).getTemperature().getUnit(), sampleTemperatureSensor.getTemperature().getUnit());
    }

    @Test
    public void testMeasurementFragmentFloatValueSerialization() {
        Date date = new Date();
        SampleTemperatureSensor sampleTemperatureSensor = new SampleTemperatureSensor(100);

        Measurement measurement = new Measurement();
        measurement.setId(ID);
        measurement.setTime(date);
        measurement.setType(TYPE);
        measurement.set(FRAGMENT, sampleTemperatureSensor);

        String serializedMeasurement = gson.toJson(measurement);

        Measurement deserializedMeasurement = new Measurement(gson.fromJson(serializedMeasurement, ExtensibleObject.class));
        String reserializedMeasurement = gson.toJson(deserializedMeasurement);

        Assert.assertEquals(reserializedMeasurement, serializedMeasurement);

        Assert.assertEquals(deserializedMeasurement.getId(), ID);
        Assert.assertEquals(deserializedMeasurement.getType(), TYPE);
        Assert.assertEquals(deserializedMeasurement.getTime().compareTo(date), 0);
        Assert.assertNotNull(deserializedMeasurement.get(FRAGMENT));
        Assert.assertNotNull(((ExtensibleObject)deserializedMeasurement.get(FRAGMENT)).get("temperature"));
        Assert.assertEquals(((ExtensibleObject)((ExtensibleObject)deserializedMeasurement.get(FRAGMENT)).get("temperature")).get("value"), new BigDecimal("" + sampleTemperatureSensor.getTemperature().getValue()));
        Assert.assertEquals(((ExtensibleObject)((ExtensibleObject)deserializedMeasurement.get(FRAGMENT)).get("temperature")).get("unit"), sampleTemperatureSensor.getTemperature().getUnit());
    }

    @Test
    public void testMeasurementFragmentBigDecimalReserialization() {
        Date date = new Date();

        SamplePowerSensor samplePowerSensor = new SamplePowerSensor(new MeasurementValue(new BigDecimal("-5730346.825124564"), "Wh"));

        Measurement measurement = new Measurement();
        measurement.setId(ID);
        measurement.setTime(date);
        measurement.setType(TYPE);
        measurement.set(FRAGMENT, samplePowerSensor);

        Gson gson = GsonUtils.createGson();
        String serializedMeasurement = gson.toJson(measurement);

        Measurement deserializedMeasurement = new Measurement(gson.fromJson(serializedMeasurement, ExtensibleObject.class));
        String reserializedMeasurement = gson.toJson(deserializedMeasurement);

        Measurement deserializedMeasurement2 = new Measurement(gson.fromJson(reserializedMeasurement, ExtensibleObject.class));
        String reserializedMeasurement2 = gson.toJson(deserializedMeasurement2);

        Assert.assertEquals(reserializedMeasurement, serializedMeasurement);
        Assert.assertEquals(reserializedMeasurement2, reserializedMeasurement);

        Assert.assertNotNull(deserializedMeasurement.get(FRAGMENT));
        Assert.assertNotNull(((ExtensibleObject)deserializedMeasurement.get(FRAGMENT)).get("solar"));
        Assert.assertEquals(((ExtensibleObject)((ExtensibleObject)deserializedMeasurement.get(FRAGMENT)).get("solar")).get("value"), samplePowerSensor.getSolar().getValue());
        Assert.assertEquals(((ExtensibleObject)((ExtensibleObject)deserializedMeasurement.get(FRAGMENT)).get("solar")).get("unit"), samplePowerSensor.getSolar().getUnit());
    }

    @Test
    public void testMeasurementFragmentIntValueSerialization() {
        Date date = new Date();

        final int value = -10;
        final String unit = "A";

        Measurement measurement = new Measurement();
        measurement.setId(ID);
        measurement.setTime(date);
        measurement.setType(TYPE);
        ExtensibleObject fragment = new ExtensibleObject();
        ExtensibleObject temperature = new ExtensibleObject();
        temperature.set("value", value);
        temperature.set("unit", unit);
        fragment.set("current", temperature);
        measurement.set(FRAGMENT, fragment);

        String serializedMeasurement = gson.toJson(measurement);

        Measurement deserializedMeasurement = new Measurement(gson.fromJson(serializedMeasurement, ExtensibleObject.class));
        String reserializedMeasurement = gson.toJson(deserializedMeasurement);

        Assert.assertEquals(reserializedMeasurement, serializedMeasurement);

        Assert.assertEquals(deserializedMeasurement.getId(), ID);
        Assert.assertEquals(deserializedMeasurement.getType(), TYPE);
        Assert.assertEquals(deserializedMeasurement.getTime().compareTo(date), 0);
        Assert.assertNotNull(deserializedMeasurement.get(FRAGMENT));
        Assert.assertNotNull(((ExtensibleObject)deserializedMeasurement.get(FRAGMENT)).get("current"));
        Assert.assertEquals(Integer.parseInt(((ExtensibleObject)((ExtensibleObject)deserializedMeasurement.get(FRAGMENT)).get("current")).get("value").toString()), value);
        Assert.assertEquals(((ExtensibleObject)((ExtensibleObject)deserializedMeasurement.get(FRAGMENT)).get("current")).get("unit"), unit);
    }

}
