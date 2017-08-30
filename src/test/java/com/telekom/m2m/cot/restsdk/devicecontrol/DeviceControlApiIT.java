package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;


/**
 * Created by Patrick Steinert on 30.01.16.
 */
public class DeviceControlApiIT {

    CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    private ManagedObject testManagedObject;
    private DeviceControlApi deviceControlApi = cotPlat.getDeviceControlApi();


    @BeforeClass
    public void setUp() {
        testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name");
    }

    @AfterClass
    public void tearDown() {
        deviceControlApi.deleteOperations(Filter.build().byDeviceId(testManagedObject.getId()));
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
    }

    @Test
    public void testCreateAndGetOperation() throws Exception {
        // given
        JsonObject parameters = new JsonObject();
        parameters.add("param1", new JsonPrimitive("1"));

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("name", new JsonPrimitive("example"));
        jsonObject.add("parameters", parameters);

        Operation operation = new Operation();
        operation.setDeviceId(testManagedObject.getId());
        operation.set("com_telekom_m2m_cotcommand", jsonObject);

        // when
        Operation createdOperation = deviceControlApi.create(operation);

        // then
        assertNotNull(createdOperation.getId(), "Should now have an Id");
        assertEquals(createdOperation.getId(), operation.getId());

        // when
        Operation retrievedOperation = deviceControlApi.getOperation(createdOperation.getId());

        // then
        assertEquals(retrievedOperation.getDeviceId(), testManagedObject.getId());
        assertNotNull(retrievedOperation.get("com_telekom_m2m_cotcommand"));
    }

    @Test
    public void testCreateAndUpdateOperation() throws Exception {
        // given
        JsonObject parameters = new JsonObject();
        parameters.add("param1", new JsonPrimitive("1"));

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("name", new JsonPrimitive("example"));
        jsonObject.add("parameters", parameters);

        Operation operation = new Operation();
        operation.setDeviceId(testManagedObject.getId());
        operation.set("com_telekom_m2m_cotcommand", jsonObject);

        // when
        Operation createdOperation = deviceControlApi.create(operation);

        // then
        assertNotNull(createdOperation.getId(), "Should now have an Id");
        assertEquals(createdOperation.getStatus(), null);

        // when
        createdOperation.setStatus(OperationStatus.EXECUTING);

        Operation updatedOperation = deviceControlApi.update(createdOperation);

        // then
        assertEquals(updatedOperation.getStatus(), OperationStatus.EXECUTING);
    }

    @Test
    public void testGetBulkOperation() throws Exception {
        deviceControlApi.getBulkOperation("1");
        // TODO: complete the test when createBulkOperation is implemented
    }
}
