package com.telekom.m2m.cot.restsdk.devicecontrol;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;


public class NewDeviceRequestTest {

    @Test
    public void invalidStatusThrowsException() {
        NewDeviceRequest request = new NewDeviceRequest(null);

        // valid:
        for (NewDeviceRequestStatus status : NewDeviceRequestStatus.values()) {
            request.setStatus(status.toString());
            assertEquals(status, request.getStatus());
        }

        // invalid:
        try {
            request.setStatus("foo");
            fail("Invalid status 'foo' should have thrown an exception.");
        } catch (IllegalArgumentException e) {
            // ok!
        }
        try {
            request.setStatus("");
            fail("Invalid status '' should have thrown an exception.");
        } catch (IllegalArgumentException e) {
            // ok!
        }        try {
            request.setStatus((String)null);
            fail("Invalid status null should have thrown an exception.");
        } catch (NullPointerException e) {
            // ok!
        }
    }

}
