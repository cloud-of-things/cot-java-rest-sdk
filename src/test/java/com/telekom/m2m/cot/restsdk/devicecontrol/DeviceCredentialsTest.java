package com.telekom.m2m.cot.restsdk.devicecontrol;

import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;


public class DeviceCredentialsTest {

    @Test
    public void testToString() {

        final DeviceCredentials deviceCredentials = new DeviceCredentials();

        deviceCredentials.setId("id_" + System.currentTimeMillis());
        deviceCredentials.setUsername("username_" + System.currentTimeMillis());
        deviceCredentials.setPassword("password_" + System.currentTimeMillis());
        deviceCredentials.setTenantId("tenantId_" + System.currentTimeMillis());

        final String stringRepresentation = deviceCredentials.toString();
        assertNotNull(stringRepresentation);
        assertFalse(stringRepresentation.contains(deviceCredentials.getPassword()));
        assertTrue(stringRepresentation.contains(deviceCredentials.getId()));
        assertTrue(stringRepresentation.contains(deviceCredentials.getUsername()));
        assertTrue(stringRepresentation.contains(deviceCredentials.getTenantId()));
    }

}
