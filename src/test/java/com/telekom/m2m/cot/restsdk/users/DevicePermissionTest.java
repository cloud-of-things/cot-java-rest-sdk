package com.telekom.m2m.cot.restsdk.users;

import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;


public class DevicePermissionTest {

    @Test
    public void testDevicePermission() {
        DevicePermission permission = new DevicePermission("MANAGED_OBJECT:*:ADMIN");
        assertEquals(permission.getApi(), DevicePermission.Api.MANAGED_OBJECT);
        assertEquals(permission.getFragmentName(), "*");
        assertEquals(permission.getPermission(), DevicePermission.Permission.ADMIN);

        // test setter/getter
        permission.setApi(DevicePermission.Api.MEASUREMENT);
        assertEquals(permission.getApi(), DevicePermission.Api.MEASUREMENT);

        permission.setFragmentName("c8y_Restart");
        assertEquals(permission.getFragmentName(), "c8y_Restart");

        permission.setPermission(DevicePermission.Permission.ALL);
        assertEquals(permission.getPermission(), DevicePermission.Permission.ALL);
    }

    @Test(expectedExceptions = CotSdkException.class, expectedExceptionsMessageRegExp = ".*structure is empty.*")
    public void testDevicePermissionEmpty(){
        new DevicePermission(null);
    }

    @Test(expectedExceptions = CotSdkException.class, expectedExceptionsMessageRegExp = ".*not conform to.*")
    public void testDevicePermissionInvalidStructure(){
        new DevicePermission("AUDIT:*");
    }

    @Test(expectedExceptions = CotSdkException.class, expectedExceptionsMessageRegExp = ".*convert Api.*")
    public void testDevicePermissionInvalidApi(){
        new DevicePermission("anEVENT:*:*");
    }

    @Test(expectedExceptions = CotSdkException.class, expectedExceptionsMessageRegExp = ".*Fragment name is empty.*")
    public void testDevicePermissionEmptyFragment(){
        new DevicePermission("AUDIT::*");
    }

    @Test(expectedExceptions = CotSdkException.class, expectedExceptionsMessageRegExp = ".*convert permission.*")
    public void testDevicePermissionInvalidPermission(){
        new DevicePermission("AUDIT:*:WRITE");
    }

}