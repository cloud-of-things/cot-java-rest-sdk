package com.telekom.m2m.cot.restsdk.realtime;

import com.google.gson.JsonObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.fail;

public class NotificationTest {
    @Test
    public void testNotification() {
        String value = "939762";
        RealtimeAction action = RealtimeAction.DELETE;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("realtimeAction", action.toString());
        jsonObject.addProperty("data", value);
        Notification notification = new Notification(jsonObject);
        Assert.assertEquals(notification.getPayload(), value);
        Assert.assertEquals(notification.getRealtimeAction(), action);
    }

    @Test
    public void testNotificationWithFailure() {
        String value = "939762";
        String action = "NODELETED";
        JsonObject failJsonObject = new JsonObject();
        failJsonObject.addProperty("realtimeAction", action);
        failJsonObject.addProperty("data", value + "ERROR");
        Notification notification = new Notification(failJsonObject);
        Assert.assertNotEquals(notification.getPayload(), value);
        try {
            notification.getRealtimeAction();
            fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains(action) && e.getMessage().contains("No enum constant"));
        }
    }
}
