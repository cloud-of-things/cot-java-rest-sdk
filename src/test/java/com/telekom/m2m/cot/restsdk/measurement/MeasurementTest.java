package com.telekom.m2m.cot.restsdk.measurement;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;
import com.telekom.m2m.cot.restsdk.util.SampleTemperatureSensor;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by breucking on 05.02.16.
 */
public class MeasurementTest {

    @Test
    public void testMeasurementSerialization() {
        Date date = new Date();

        SampleTemperatureSensor sts = new SampleTemperatureSensor();

        Measurement measurement = new Measurement();
        measurement.setId("1234567");
        measurement.setTime(date);
        measurement.setType("ASpecialType");
        measurement.set(sts);

        Gson gson = GsonUtils.createGson();
        String json = gson.toJson(measurement);

        Measurement m = new Measurement(gson.fromJson(json, ExtensibleObject.class));

        Assert.assertEquals(m.getId(), "1234567");
        Assert.assertEquals(m.getType(), "ASpecialType");
        Assert.assertEquals(m.getTime().compareTo(date), 0);
        Assert.assertNotNull(m.get("com_telekom_m2m_cot_restsdk_util_SampleTemperatureSensor"));
    }

    @Test
    public void testMeasurementFragmentSerialization() {
        Date date = new Date();


        SampleTemperatureSensor sts = new SampleTemperatureSensor();
        sts.setTemperature(100);

        Gson gs = new Gson();
        String foo = gs.toJson(sts);

        Measurement measument = new Measurement();
        measument.setId("1234567");
        measument.setTime(date);
        measument.setType("ASpecialType");
        measument.set(sts);

        Gson gson = GsonUtils.createGson();
        String json = gson.toJson(measument);

        JsonObject o = gson.fromJson(json, JsonObject.class);

        Assert.assertEquals(o.get("id").getAsString(), "1234567");
        Assert.assertNotNull(o.get("com_telekom_m2m_cot_restsdk_util_SampleTemperatureSensor"));
        JsonObject ret = o.get("com_telekom_m2m_cot_restsdk_util_SampleTemperatureSensor").getAsJsonObject();
        Assert.assertNotNull(ret.get("temperature"));
        Assert.assertEquals(ret.get("temperature").getAsJsonObject().get("value").getAsFloat(), 100.0f);
        Assert.assertEquals(ret.get("temperature").getAsJsonObject().get("unit").getAsString(), "°C");

    }

}
