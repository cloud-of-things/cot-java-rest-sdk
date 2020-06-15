package com.telekom.m2m.cot.restsdk;

import com.telekom.m2m.cot.restsdk.devicecontrol.DeviceControlApi;
import com.telekom.m2m.cot.restsdk.devicecontrol.DeviceCredentials;
import com.telekom.m2m.cot.restsdk.devicecontrol.DeviceCredentialsApi;
import com.telekom.m2m.cot.restsdk.identity.ExternalId;
import com.telekom.m2m.cot.restsdk.identity.IdentityApi;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Created by Patrick Steinert on 31.01.16.
 */
public class CotDeviceRegisterIT {

    @Test
    public void testDeviceRegister() {

        String deviceId = "mydevice-name_" + System.currentTimeMillis();

        DeviceControlApi deviceControlApi = cloudOfThingsPlatform.getDeviceControlApi();

        DeviceCredentialsApi unregDevCred = CloudOfThingsPlatform.getPlatformToRegisterDevice(TestHelper.TEST_HOST).getDeviceCredentialsApi();

        // Step 1: (devicemanager) Register Device
        deviceControlApi.createNewDevice(deviceId);

        // Step 2: (device) Device request
        try {
            unregDevCred.getCredentials(deviceId);
            fail("an exception should have been thrown");
        } catch (CotSdkException e) {
            assertEquals(HttpStatus.NOT_FOUND.getCode(), e.getHttpStatus());
        }

        // Step 3: (devicemanager) Accept request
        deviceControlApi.acceptDevice(deviceId);

        // Step 4: (device) Create MO
        DeviceCredentials devCred = unregDevCred.getCredentials(deviceId);

        Assert.assertNotNull(devCred);
        Assert.assertNotNull(devCred.getId());
        Assert.assertEquals(devCred.getId(), deviceId);
        Assert.assertNotNull(devCred.getPassword());
        Assert.assertNotNull(devCred.getTenantId());
        Assert.assertNotNull(devCred.getUsername());

        CloudOfThingsPlatform platformForDevice = new CloudOfThingsPlatform(TestHelper.TEST_HOST, devCred.getUsername(), devCred.getPassword());

        //IdentityApi identityApi = platform.getIdentityApi();
        //ExternalId identity = identityApi.getExternalId("");
        //Assert.assertNull(identity, "Should be null, else before running test run leaved garbadge");

        InventoryApi inventory = platformForDevice.getInventoryApi();

        ManagedObject mo = new ManagedObject();
        mo.setName("test_" + deviceId);
        mo.set("c8y_IsDevice", new Object());
        ManagedObject newMo = inventory.create(mo);

        Assert.assertNotNull(newMo.getId());

        // Step 6: (device) Delete Device
        inventory.delete(newMo.getId());

        ManagedObject object = inventory.get(newMo.getId());
        Assert.assertNull(object);


    }

    private final CloudOfThingsPlatform cloudOfThingsPlatform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

    @Test
    public void testDeviceRegisterWithIdentity() {
        String deviceId = "mydevice-name_" + System.currentTimeMillis();

        DeviceControlApi deviceControlApi = cloudOfThingsPlatform.getDeviceControlApi();

        DeviceCredentialsApi unregDevCred = CloudOfThingsPlatform.getPlatformToRegisterDevice(TestHelper.TEST_HOST).getDeviceCredentialsApi();

        // Step 1: (devicemanager) Register Device
        deviceControlApi.createNewDevice(deviceId);

        // Step 2: (device) Device request
        try {
            unregDevCred.getCredentials(deviceId);
            fail("an exception should have been thrown");
        } catch (CotSdkException e) {
            assertEquals(HttpStatus.NOT_FOUND.getCode(), e.getHttpStatus());
        }

        // Step 3: (devicemanager) Accept request
        deviceControlApi.acceptDevice(deviceId);

        // Step 4: (device) Create MO
        DeviceCredentials devCred = unregDevCred.getCredentials(deviceId);

        Assert.assertNotNull(devCred);
        Assert.assertNotNull(devCred.getId());
        Assert.assertNotNull(devCred.getPassword());
        Assert.assertNotNull(devCred.getTenantId());
        Assert.assertNotNull(devCred.getUsername());

        CloudOfThingsPlatform platformForDevice = new CloudOfThingsPlatform(TestHelper.TEST_HOST, devCred.getUsername(), devCred.getPassword());

        InventoryApi inventory = platformForDevice.getInventoryApi();

        ManagedObject mo = new ManagedObject();
        mo.setName("test_" + deviceId);
        mo.set("c8y_IsDevice", new Object());
        ManagedObject newMo = inventory.create(mo);

        Assert.assertNotNull(newMo.getId());

        // Step 5: (device) Register the device
        String idString = "fantasy-" + TestHelper.getRandom(10);
        IdentityApi devIdentityApi = platformForDevice.getIdentityApi();
        ExternalId externalId = new ExternalId();
        externalId.setExternalId(idString);
        externalId.setType("com_telekom_SerialNumber");
        externalId.setManagedObject(newMo);

        ExternalId retrievedExtId = devIdentityApi.create(externalId);
        Assert.assertEquals(retrievedExtId.getExternalId(), idString);
        Assert.assertEquals(retrievedExtId.getType(), "com_telekom_SerialNumber");

        ExternalId checkExtId = devIdentityApi.getExternalId(retrievedExtId);
        Assert.assertEquals(checkExtId.getExternalId(), retrievedExtId.getExternalId());
        Assert.assertEquals(checkExtId.getType(), retrievedExtId.getType());
        Assert.assertEquals(checkExtId.getManagedObject().getId(), retrievedExtId.getManagedObject().getId());
        Assert.assertEquals(checkExtId.getManagedObject().getName(), retrievedExtId.getManagedObject().getName());

        // Step 6: (device) Delete Identity & Device
        devIdentityApi.delete(retrievedExtId);

        Assert.assertNull(devIdentityApi.getExternalId(retrievedExtId));

        inventory.delete(newMo.getId());

        ManagedObject object = inventory.get(newMo.getId());
        Assert.assertNull(object);

    }

}
