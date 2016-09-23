package com.telekom.m2m.cot.restsdk.alarm;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by breucking on 05.02.16.
 */
public class AlarmTest {

    @Test
    public void testAlarmSerialization() {


        Alarm alarm = new Alarm();
        alarm.setId("112233");

        Gson gson = GsonUtils.createGson();
        String json = gson.toJson(alarm);

        ExtensibleObject o = gson.fromJson(json, ExtensibleObject.class);
        Assert.assertEquals(o.get("id"), "112233");

    }

}
