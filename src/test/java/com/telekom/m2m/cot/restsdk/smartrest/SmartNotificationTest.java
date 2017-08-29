package com.telekom.m2m.cot.restsdk.smartrest;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;


public class SmartNotificationTest {

    @Test
    public void testConstruction() {
        SmartNotification notification;

        notification = new SmartNotification("999,1,foo,bar,9000", "My-X-Id");
        assertEquals(notification.getMessageId(), 999);
        assertEquals(notification.getData(), "1,foo,bar,9000");
        assertEquals(notification.getxId(), "My-X-Id");
    }

}
