package com.telekom.m2m.cot.restsdk.event;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;
import com.telekom.m2m.cot.restsdk.util.Position;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by Patrick Steinert on 05.02.16.
 */
public class EventTest {

    @Test
    public void testEventSerialization() {

        Position p = new Position(8, 2, 6);

        Event e = new Event();
        e.setId("1234");
        e.set("com_telekom_m2m_cot_restsdk_util_Position", p);

        Gson gson = GsonUtils.createGson();
        String json = gson.toJson(e);

        ExtensibleObject o = gson.fromJson(json, ExtensibleObject.class);
        Assert.assertTrue(o.get("com_telekom_m2m_cot_restsdk_util_Position") instanceof Position);

    }

    @Test
    public void testEventFragmentSerialization() {
        Date date = new Date();

        Position position = new Position(50.722607, 7.144011, 1000.0);

        Event event= new Event();
        event.setId("300001");
        event.setTime(date);
        event.setType("com_telekom_locationupdateevent");
        event.set(position);

        Gson gson = GsonUtils.createGson();
        String json = gson.toJson(event);

        JsonObject o = gson.fromJson(json, JsonObject.class);

        Assert.assertEquals(o.get("id").getAsString(), "300001");
        Assert.assertNotNull(o.get("com_telekom_m2m_cot_restsdk_util_Position"));
        JsonObject ret = o.get("com_telekom_m2m_cot_restsdk_util_Position").getAsJsonObject();
        Assert.assertEquals(ret.get("alt").getAsDouble(), 1000.0);
        Assert.assertEquals(ret.get("lat").getAsDouble(), 50.722607);
        Assert.assertEquals(ret.get("lon").getAsDouble(), 7.144011);
    }
}
