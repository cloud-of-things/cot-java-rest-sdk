package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.junit.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Patrick Steinert on 30.01.16.
 */
public class DeviceControlApiIT {

    CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    private ManagedObject testManagedObject;

    @BeforeClass
    public void setUp() {
        testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name");
    }

    @AfterClass
    public void tearDown() {
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
    }

    @Test
    public void testCreateAndGetOperation() throws Exception {
        DeviceControlApi deviceControlApi = cotPlat.getDeviceControlApi();

        JsonObject parameters = new JsonObject();
        parameters.add("param1", new JsonPrimitive("1"));

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("name", new JsonPrimitive("example"));
        jsonObject.add("parameters", parameters);

        Operation operation = new Operation();
        operation.setDeviceId(testManagedObject.getId());
        operation.set("com_telekom_m2m_cotcommand", jsonObject);

        Operation createdOperation = deviceControlApi.create(operation);

        Assert.assertNotNull("Should now have an Id", createdOperation.getId());

        Operation retrievedOperation = deviceControlApi.getOperation(createdOperation.getId());

        Assert.assertEquals(retrievedOperation.getDeviceId(), testManagedObject.getId());
        Assert.assertNotNull(retrievedOperation.get("com_telekom_m2m_cotcommand"));
    }

    @Test
    public void testCreateAndUpdateOperation() throws Exception {
        DeviceControlApi deviceControlApi = cotPlat.getDeviceControlApi();

        JsonObject parameters = new JsonObject();
        parameters.add("param1", new JsonPrimitive("1"));

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("name", new JsonPrimitive("example"));
        jsonObject.add("parameters", parameters);

        Operation operation = new Operation();
        operation.setDeviceId(testManagedObject.getId());
        operation.set("com_telekom_m2m_cotcommand", jsonObject);

        Operation createdOperation = deviceControlApi.create(operation);

        Assert.assertNotNull("Should now have an Id", createdOperation.getId());

        createdOperation.setStatus(OperationStatus.EXECUTING);

        Operation updatedOperation = deviceControlApi.update(createdOperation);

    }


}
