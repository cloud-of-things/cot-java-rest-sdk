package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by Patrick Steinert on 19.12.16.
 */
public class DeviceCredentialsApiIT {

    @Test
    public void testDeviceRegister() throws Exception {
        String deviceId = "mydevice-name_" + new Date().getTime();

        CloudOfThingsPlatform platform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
        DeviceControlApi deviceControlApi = platform.getDeviceControlApi();


//        DeviceCredentialsApi unregDevCred = CloudOfThingsPlatform.getPlatformToRegisterDevice(TestHelper.TEST_HOST).getDeviceCredentialsApi();
        DeviceCredentialsApi deviceCredentialsApi = platform.getDeviceCredentialsApi();

        // Step 1: (devicemanager) Register Device
        Operation operation = new Operation(deviceId);
        deviceControlApi.createNewDevice(operation);

        // Step 2: (device) Device request
//        unregDevCred.getCredentials(deviceId);

        NewDeviceRequestCollection result = deviceCredentialsApi.getNewDeviceRequests(100);
        NewDeviceRequest[] requests = result.getNewDeviceRequests();
        Assert.assertTrue(requests.length > 0, "Should be greater than null, because we added a create new device.");
        boolean foundId = false;
        for (int i = 0; i < requests.length; i++) {
            NewDeviceRequest request = requests[i];
            if (request.get("id").equals(deviceId)) {
                foundId = true;
            }
        }
        Assert.assertTrue(foundId);

        deviceCredentialsApi.deleteNewDeviceRequests(deviceId);

        result = deviceCredentialsApi.getNewDeviceRequests(100);
        requests = result.getNewDeviceRequests();
        foundId = false;
        for (int i = 0; i < requests.length; i++) {
            NewDeviceRequest request = requests[i];
            if (request.get("id").equals(deviceId)) {
                foundId = true;
            }
        }
        Assert.assertFalse(foundId);


//        // Step 3: (devicemanager) Accept request
//        deviceControlApi.acceptDevice(deviceId);
//
//        // Step 4: (device) Create MO
//        DeviceCredentials devCred = unregDevCred.getCredentials(deviceId);
//
//        org.testng.Assert.assertNotNull(devCred);
//        org.testng.Assert.assertNotNull(devCred.getId());
//        org.testng.Assert.assertEquals(devCred.getId(), deviceId);
//        org.testng.Assert.assertNotNull(devCred.getPassword());
//        org.testng.Assert.assertNotNull(devCred.getTenantId());
//        org.testng.Assert.assertNotNull(devCred.getUsername());
//
//        CloudOfThingsPlatform platformForDevice = new CloudOfThingsPlatform(TestHelper.TEST_HOST, devCred.getTenantId(), devCred.getUsername(), devCred.getPassword());
//
//        //IdentityApi identityApi = platform.getIdentityApi();
//        //ExternalId identity = identityApi.getExternalId("");
//        //Assert.assertNull(identity, "Should be null, else before running test run leaved garbadge");
//
//        InventoryApi inventory = platformForDevice.getInventoryApi();
//
//        ManagedObject mo = new ManagedObject();
//        mo.setName("test_" + deviceId);
//        mo.set("c8y_IsDevice", new Object());
//        ManagedObject newMo = inventory.create(mo);
//
//        org.testng.Assert.assertNotNull(newMo.getId());
//
//        // Step 6: (device) Delete Device
//        inventory.delete(newMo.getId());
//
//        ManagedObject object = inventory.get(newMo.getId());
//        org.testng.Assert.assertNull(object);

    }

}
