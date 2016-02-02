package com.telekom.m2m.cot.restsdk.inventory;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.devicecontrol.DeviceControlApi;
import com.telekom.m2m.cot.restsdk.devicecontrol.DeviceCredentials;
import com.telekom.m2m.cot.restsdk.devicecontrol.DeviceCredentialsApi;
import com.telekom.m2m.cot.restsdk.identity.ExternalId;
import com.telekom.m2m.cot.restsdk.identity.IdentityApi;
import com.telekom.m2m.cot.restsdk.inventory.util.TestHelper;
import com.telekom.m2m.cot.restsdk.operation.Operation;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by breucking on 31.01.16.
 */
public class CotDeviceRegisterIT {


    @Test
    public void testDeviceRegister() throws Exception {
        String deviceId = "mydevice-name";

        CloudOfThingsPlatform platform = new CloudOfThingsPlatform(TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
        DeviceControlApi deviceControlApi = platform.getDeviceControlApi();


        DeviceCredentialsApi unregDevCred = CloudOfThingsPlatform.getPlatformToRegisterDevice().getDeviceCredentialsApi();


        // Step 1: (devicemanager) Register Device
        Operation operation = new Operation(deviceId);
        deviceControlApi.createNewDevice(operation);

        // Step 2: (device) Device request
        unregDevCred.getCredentials(deviceId);

        // Step 3: (devicemanager) Accept request
        deviceControlApi.acceptDevice(deviceId);

        // Step 4: (device) Create MO
        DeviceCredentials devCred = unregDevCred.getCredentials(deviceId);

        Assert.assertNotNull(devCred);
        Assert.assertNotNull(devCred.getId());
        Assert.assertNotNull(devCred.getPassword());
        Assert.assertNotNull(devCred.getTenantId());
        Assert.assertNotNull(devCred.getUsername());

        CloudOfThingsPlatform platformForDevice = new CloudOfThingsPlatform(devCred.getTenantId(), devCred.getUsername(), devCred.getPassword());
        IdentityApi identityApi = platform.getIdentityApi();
        ExternalId identity = identityApi.getExternalId(deviceId);

        Assert.assertNull(identity, "Should be null, else before running test run leaved garbadge");

        InventoryApi inventory = platformForDevice.getInventoryApi();

        ManagedObject mo = new ManagedObject();
        mo.setName("test_" + deviceId);
        mo.set("c8y_IsDevice", new Object());
        ManagedObject newMo = inventory.create(mo);

        Assert.assertNotNull(newMo.getId());

        // Step 5: (device) Register the device
//        IdentityApi devIdentityApi = platformForDevice.getIdentityApi();
//        ExternalId externalId = new ExternalId();
//        externalId.setExternalId()
//
//        externalId.setManagedObject(newMo);
//        devIdentityApi.create(externalId);

        // Step 6: (device) Delete Device
        inventory.delete(newMo.getId());

        ManagedObject object = inventory.get(newMo.getId());
        Assert.assertNull(object);



        /*
         public void iHaveManagedObject(long globalId, String extId, String type) {
        ExternalIDRepresentation rep = new ExternalIDRepresentation();
        rep.setExternalId(extId);
        rep.setType(type);
        ManagedObjectRepresentation mo = new ManagedObjectRepresentation();
        GId gId = new GId();
        gId.setValue(Long.toString(globalId));
        mo.setId(gId);
        rep.setManagedObject(mo);
    }
         */
    }

}
