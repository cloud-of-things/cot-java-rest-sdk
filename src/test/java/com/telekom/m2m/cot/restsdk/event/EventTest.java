package com.telekom.m2m.cot.restsdk.event;

import com.telekom.m2m.cot.restsdk.util.Position;
import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by breucking on 05.02.16.
 */
public class EventTest {

    @Test
    public void testEventSerialization() {

        Position p = new Position();
        p.setAlt(6);
        p.setLat(8);
        p.setLon(2);

        Event e = new Event();
        e.setId("1234");
        e.set("com_telekom_m2m_cot_restsdk_util_Position", p);

        Gson gson = GsonUtils.createGson();
        String json = gson.toJson(e);

        ExtensibleObject o = gson.fromJson(json, ExtensibleObject.class);
        Assert.assertTrue(o.get("com_telekom_m2m_cot_restsdk_util_Position") instanceof Position);

    }

}
