package com.telekom.m2m.cot.restsdk.realtime;

import com.google.gson.JsonObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NotificationTest {
    @Test
    public void testNotification() {
        String value = "939762";
        RealtimeAction action = RealtimeAction.DELETE;
        JsonObject o = new JsonObject();
        o.addProperty("realtimeAction", action.toString());
        o.addProperty("data", value);
        Notification x = new Notification(o);
        Assert.assertEquals(x.getDataPart(), value);
        Assert.assertEquals(x.getRealtimeAction(), action);
    }

    @Test
    public void testNotificationWithFailure() {
        String value = "939762";
        String action = "NODELETED";
        JsonObject o = new JsonObject();
        o.addProperty("realtimeAction", action);
        o.addProperty("data", value + "ERROR");
        Notification x = new Notification(o);
        Assert.assertNotEquals(x.getDataPart(), value);
        try {
            x.getRealtimeAction();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "No enum constant com.telekom.m2m.cot.restsdk.realtime.RealtimeAction." + action);
        }
    }
}
