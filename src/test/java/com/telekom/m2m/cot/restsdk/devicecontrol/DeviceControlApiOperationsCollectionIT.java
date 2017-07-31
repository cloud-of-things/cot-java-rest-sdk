package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * @author steinert
 */
public class DeviceControlApiOperationsCollectionIT {

    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

    private ManagedObject testManagedObjectParent;
    private ManagedObject testManagedObject;

    private static JsonObject jsonObject = new JsonObject();

    static {
        JsonObject parameters = new JsonObject();
        parameters.add("param1", new JsonPrimitive("1"));

        jsonObject.add("name", new JsonPrimitive("example"));
        jsonObject.add("parameters", parameters);
    }

    @BeforeMethod
    public void setUp() {
        testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name");

        testManagedObjectParent =
                TestHelper.createRandomManagedObjectInPlatform(
                        cotPlat,
                        TestHelper.createManagedObject("parent_agent_device", true)
                );
        testManagedObject =
                TestHelper.createRandomManagedObjectInPlatform(
                        cotPlat,
                        TestHelper.createManagedObject("fake_name", false)
                );

        TestHelper.registerAsChildDevice(cotPlat, testManagedObjectParent, testManagedObject);

    }

    @AfterMethod
    public void tearDown() {
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObjectParent);
    }

    @Test
    public void testOperationCollection() throws Exception {
        // given at least one created operation entry
        final Operation operation = new Operation();
        operation.setDeviceId(testManagedObject.getId());
        operation.set("com_telekom_m2m_cotcommand", jsonObject);

        final DeviceControlApi deviceControlApi = cotPlat.getDeviceControlApi();

        deviceControlApi.create(operation);

        // when
        final OperationCollection operationCollection = deviceControlApi.getOperations(5);

        // then
        Assert.assertNotNull(operationCollection);

        final Operation[] operations = operationCollection.getOperations();

        Assert.assertTrue(operations.length > 0);
        Assert.assertEquals(operations.length, operationCollection.getJsonArray().size());

        final Operation retrievedOperation = operations[0];
        final JsonObject jsonObject = operationCollection.getJsonArray().get(0).getAsJsonObject();

        Assert.assertTrue(retrievedOperation.getId() != null);
        Assert.assertFalse(retrievedOperation.getId().isEmpty());
        Assert.assertTrue(retrievedOperation.getId().equals(jsonObject.get("id").getAsString()));

        Assert.assertTrue(retrievedOperation.getCreationTime() != null);
        Assert.assertTrue(retrievedOperation.getCreationTime().compareTo(new Date()) < 0);

        Assert.assertTrue(retrievedOperation.getStatus() != null);
        Assert.assertTrue(retrievedOperation.getStatus().toString().equals(jsonObject.get("status").getAsString()));
    }

    @Test
    public void testDeleteMultipleOperationsBySource() throws Exception {
        DeviceControlApi deviceControlApi = cotPlat.getDeviceControlApi();

        for (int i = 0; i < 6; i++) {
            Operation testOperation = new Operation();
            testOperation.setDeviceId(testManagedObject.getId());
            testOperation.set("com_telekom_m2m_cotcommand", jsonObject);

            deviceControlApi.create(testOperation);
        }

        OperationCollection operations = deviceControlApi.getOperations(Filter.build().byDeviceId(testManagedObject.getId()), 5);
        Operation[] os = operations.getOperations();
        Assert.assertEquals(os.length, 5);

        deviceControlApi.deleteOperations(Filter.build().byDeviceId(testManagedObject.getId()));
        operations = deviceControlApi.getOperations(Filter.build().byDeviceId(testManagedObject.getId()), 5);
        os = operations.getOperations();
        Assert.assertEquals(os.length, 0);
    }

    @Test
    public void testMultipleOperationsBySource() throws Exception {
        DeviceControlApi dcApi = cotPlat.getDeviceControlApi();

        Operation testOperation = new Operation();
        testOperation.setDeviceId(testManagedObject.getId());
        testOperation.set("com_telekom_m2m_cotcommand", jsonObject);
        dcApi.create(testOperation);

        OperationCollection operations = dcApi.getOperations(5);
        Operation[] os = operations.getOperations();
        Assert.assertTrue(os.length > 0);
        boolean allOperationsFromSource = true;
        for (Operation o : os) {
            if (!o.getDeviceId().equals(testManagedObject.getId())) {
                allOperationsFromSource = false;
            }
        }
        Assert.assertFalse(allOperationsFromSource);

        operations = dcApi.getOperations(Filter.build().byDeviceId(testManagedObject.getId()), 20);
        os = operations.getOperations();
        allOperationsFromSource = true;
        Assert.assertTrue(os.length > 0);
        for (Operation o : os) {
            if (!o.getDeviceId().equals(testManagedObject.getId())) {
                allOperationsFromSource = false;
            }
        }
        Assert.assertTrue(allOperationsFromSource);
    }

    @Test
    public void testMultipleOpearationByStatus() throws Exception {
        DeviceControlApi dcApi = cotPlat.getDeviceControlApi();

        Operation testOperation = new Operation();
        testOperation.setDeviceId(testManagedObject.getId());
        testOperation.set("com_telekom_m2m_cotcommand", jsonObject);
        dcApi.create(testOperation);

        // Test could be flaky, because can't predict if first 50
        // operations can not have same status
        OperationCollection operations = dcApi.getOperations(50);
        Operation[] os = operations.getOperations();
        Assert.assertTrue(os.length > 0);
        boolean allOperationWithSameStatus = true;
        for (Operation o : os) {
            if (!o.getStatus().equals(OperationStatus.FAILED)) {
                allOperationWithSameStatus = false;
            }
        }
        Assert.assertFalse(allOperationWithSameStatus);

        operations = dcApi.getOperations(Filter.build().byStatus(OperationStatus.SUCCESSFUL), 5);
        os = operations.getOperations();
        allOperationWithSameStatus = true;
        Assert.assertTrue(os.length > 0);
        for (Operation o : os) {
            if (!o.getStatus().toString().equalsIgnoreCase(OperationStatus.SUCCESSFUL.toString())) {
                allOperationWithSameStatus = false;
            }
        }
        Assert.assertTrue(allOperationWithSameStatus);
    }

    @Test
    public void testMultipleOperationsByDateAndByDeviceId() throws Exception {
        DeviceControlApi dcApi = cotPlat.getDeviceControlApi();

        Operation testOperation = new Operation();
        testOperation.setDeviceId(testManagedObject.getId());
        testOperation.set("com_telekom_m2m_cotcommand", jsonObject);
        dcApi.create(testOperation);

        Date yesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
        Date now = new Date();
        now.setSeconds(now.getSeconds() + 60);
        OperationCollection operations = dcApi.getOperations(
                Filter.build()
                        .byDate(yesterday, now)
                        .byDeviceId(testManagedObject.getId()), 5);


        Operation[] os = operations.getOperations();
        Assert.assertEquals(os.length, 1);

        Date beforeYesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24) - 10);

        operations = dcApi.getOperations(
                Filter.build()
                        .byDate(beforeYesterday, yesterday)
                        .byDeviceId(testManagedObject.getId()), 5);
        os = operations.getOperations();
        Assert.assertEquals(os.length, 0);
    }

    @Test
    public void testOperationByAgentId() {

        // given
        final DeviceControlApi deviceControlApi = cotPlat.getDeviceControlApi();

        final Operation testOperation = new Operation();
        testOperation.setDeviceId(testManagedObject.getId());
        testOperation.set("com_telekom_m2m_cotcommand", jsonObject);
        deviceControlApi.create(testOperation);

        final String agentId = testManagedObjectParent.getId();

        // when
        OperationCollection operationCollection = deviceControlApi.getOperations(
                Filter.build()
                        .byAgentId(agentId), 5);

        // then
        final Operation[] operations = operationCollection.getOperations();
        Assert.assertEquals(operations.length, 1);

        Assert.assertEquals(
                testManagedObject.getId(),
                operations[0].getDeviceId()
        );

    }

}