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

        permission = new DevicePermission(DevicePermission.Api.OPERATION, "c8y_Restart", DevicePermission.Permission.ALL);
        assertEquals(permission.getApi(), DevicePermission.Api.OPERATION);
        assertEquals(permission.getFragmentName(), "c8y_Restart");
        assertEquals(permission.getPermission(), DevicePermission.Permission.ALL);

        permission = new DevicePermission(null, null, null);
        assertEquals(permission.getApi(), DevicePermission.Api.ALL);
        assertEquals(permission.getFragmentName(), DevicePermission.ALL_FRAGMENTS);
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
        new DevicePermission("AUDIT: :*");
    }

    @Test(expectedExceptions = CotSdkException.class, expectedExceptionsMessageRegExp = ".*convert permission.*")
    public void testDevicePermissionInvalidPermission(){
        new DevicePermission("AUDIT:*:WRITE");
    }

}