package com.telekom.m2m.cot.restsdk;

import com.telekom.m2m.cot.restsdk.devicecontrol.DeviceControlApi;
import com.telekom.m2m.cot.restsdk.devicecontrol.DeviceCredentials;
import com.telekom.m2m.cot.restsdk.devicecontrol.DeviceCredentialsApi;
import com.telekom.m2m.cot.restsdk.devicecontrol.Operation;
import com.telekom.m2m.cot.restsdk.identity.ExternalId;
import com.telekom.m2m.cot.restsdk.identity.IdentityApi;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNull;

/**
 * Created by breucking on 31.01.16.
 */
public class CotDeviceRegisterIT {

    private DeviceCredentials deviceCredentials;

    private String deviceId;

    private void tearDown() throws Exception {

        if (deviceCredentials != null) {
            CloudOfThingsPlatform platformForDevice = new CloudOfThingsPlatform(
                    TestHelper.TEST_HOST, deviceCredentials.getTenantId(), deviceCredentials.getUsername(), deviceCredentials.getPassword()
            );
            platformForDevice.getInventoryApi().delete(deviceId);
            // TODO: delete device user/ Gerätezugang! (-> https://<tenant>.int2-ram.m2m.telekom.com/apps/devicemanagement/index.html#/deviceusers)
        }

    }

    @Test
    public void testDeviceRegister() throws Exception {
        String deviceId = "mydevice-name";

        CloudOfThingsPlatform platform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
        DeviceControlApi deviceControlApi = platform.getDeviceControlApi();

        DeviceCredentialsApi unregDevCred = CloudOfThingsPlatform.getPlatformToRegisterDevice(TestHelper.TEST_HOST).getDeviceCredentialsApi();

        // Step 1: (devicemanager) Register Device
        Operation operation = new Operation(deviceId);

        try {
            deviceControlApi.createNewDevice(operation);
        } catch (CotSdkException e) {

        }

        // Step 2: (device) Device request
        try {
            unregDevCred.getCredentials(deviceId);
        } catch (CotSdkException e) {

        }

        // Step 3: (devicemanager) Accept request

        deviceControlApi.acceptDevice(deviceId);

        // Step 4: (device) Create MO
        deviceCredentials = unregDevCred.getCredentials(deviceId);

        Assert.assertNotNull(deviceCredentials);
        Assert.assertNotNull(deviceCredentials.getId());
        Assert.assertEquals(deviceCredentials.getId(), deviceId);
        Assert.assertNotNull(deviceCredentials.getPassword());
        Assert.assertNotNull(deviceCredentials.getTenantId());
        Assert.assertNotNull(deviceCredentials.getUsername());

        CloudOfThingsPlatform platformForDevice = new CloudOfThingsPlatform(TestHelper.TEST_HOST, deviceCredentials.getTenantId(), deviceCredentials.getUsername(), deviceCredentials.getPassword());

        //IdentityApi identityApi = platform.getIdentityApi();
        //ExternalId identity = identityApi.getExternalId("");
        //Assert.assertNull(identity, "Should be null, else before running test run leaved garbadge");

        InventoryApi inventory = platformForDevice.getInventoryApi();

        ManagedObject mo = new ManagedObject();
        mo.setName("test_" + deviceId);
        mo.set("c8y_IsDevice", new Object());
        ManagedObject newMo = inventory.create(mo);

        Assert.assertNotNull(newMo.getId());

        this.deviceId = newMo.getId();
        tearDown();
    }

    @Test
    public void testDeviceRegisterWithIdentity() throws Exception {
        String deviceId = "mydevice-name";

        CloudOfThingsPlatform platform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
        DeviceControlApi deviceControlApi = platform.getDeviceControlApi();


        DeviceCredentialsApi unregDevCred = CloudOfThingsPlatform.getPlatformToRegisterDevice("https://management.int2-ram.m2m.telekom.com").getDeviceCredentialsApi();


        // Step 1: (devicemanager) Register Device
        Operation operation = new Operation(deviceId);
        try {
            deviceControlApi.createNewDevice(operation);
        } catch (CotSdkException e) {

        }

        // Step 2: (device) Device request
        try {
            unregDevCred.getCredentials(deviceId);
        } catch (CotSdkException e) {

        }

        // Step 3: (devicemanager) Accept request
        deviceControlApi.acceptDevice(deviceId);

        // Step 4: (device) Create MO
        deviceCredentials = unregDevCred.getCredentials(deviceId);

        Assert.assertNotNull(deviceCredentials);
        Assert.assertNotNull(deviceCredentials.getId());
        Assert.assertNotNull(deviceCredentials.getPassword());
        Assert.assertNotNull(deviceCredentials.getTenantId());
        Assert.assertNotNull(deviceCredentials.getUsername());

        CloudOfThingsPlatform platformForDevice = new CloudOfThingsPlatform(TestHelper.TEST_HOST, deviceCredentials.getTenantId(), deviceCredentials.getUsername(), deviceCredentials.getPassword());

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

        assertNull(devIdentityApi.getExternalId(retrievedExtId));

        this.deviceId = newMo.getId();
        tearDown();

    }

}
