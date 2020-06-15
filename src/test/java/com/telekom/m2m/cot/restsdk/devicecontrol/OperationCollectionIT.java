package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;

import static org.testng.Assert.*;


/**
 * @author steinert
 */
public class OperationCollectionIT {

    private final CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

    private ManagedObject testManagedObjectParent;
    private ManagedObject testManagedObject;

    private final DeviceControlApi deviceControlApi = cotPlat.getDeviceControlApi();
    private static final JsonObject jsonObject = new JsonObject();

    static {
        JsonObject parameters = new JsonObject();
        parameters.add("param1", new JsonPrimitive("1"));

        jsonObject.add("name", new JsonPrimitive("example"));
        jsonObject.add("parameters", parameters);
    }

    @BeforeMethod
    public void setUp() {
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
        deviceControlApi.deleteOperations(Filter.build().byDeviceId(testManagedObject.getId()));

        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObjectParent);
    }

    @Test
    public void testOperationCollection() {
        // given at least one created operation entry
        final Operation operation = new Operation();
        operation.setDeviceId(testManagedObject.getId());
        operation.set("com_telekom_m2m_cotcommand", jsonObject);

        deviceControlApi.create(operation);

        // when
        final OperationCollection operationCollection = deviceControlApi.getOperationCollection(5);

        // then
        assertNotNull(operationCollection);

        final Operation[] operations = operationCollection.getOperations();

        assertTrue(operations.length > 0);
        assertEquals(operations.length, operationCollection.getJsonArray().size());

        final Operation retrievedOperation = operations[0];
        final JsonObject jsonObject = operationCollection.getJsonArray().get(0).getAsJsonObject();

        assertNotNull(retrievedOperation.getId());
        assertFalse(retrievedOperation.getId().isEmpty());
        assertEquals(jsonObject.get("id").getAsString(), retrievedOperation.getId());

        assertNotNull(retrievedOperation.getCreationTime());
        assertTrue(retrievedOperation.getCreationTime().compareTo(new Date()) < 0);

        assertNotNull(retrievedOperation.getStatus());
        assertEquals(jsonObject.get("status").getAsString(), retrievedOperation.getStatus().toString());
    }

    @Test
    public void testDeleteMultipleOperationsByDeviceId() {
        for (int i = 0; i < 6; i++) {
            Operation testOperation = new Operation();
            testOperation.setDeviceId(testManagedObject.getId());
            testOperation.set("com_telekom_m2m_cotcommand", jsonObject);

            deviceControlApi.create(testOperation);
        }

        OperationCollection operations = deviceControlApi.getOperationCollection(Filter.build().byDeviceId(testManagedObject.getId()), 5);
        Operation[] os = operations.getOperations();
        assertEquals(os.length, 5);

        deviceControlApi.deleteOperations(Filter.build().byDeviceId(testManagedObject.getId()));
        operations = deviceControlApi.getOperationCollection(Filter.build().byDeviceId(testManagedObject.getId()), 5);
        os = operations.getOperations();
        assertEquals(os.length, 0);
    }

    @Test
    public void testMultipleOperationsByDeviceId() {
        Operation testOperation = new Operation();
        testOperation.setDeviceId(testManagedObject.getId());
        testOperation.set("com_telekom_m2m_cotcommand", jsonObject);
        deviceControlApi.create(testOperation);

        ManagedObject testManagedObject2 = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name");

        Operation testOperation2 = new Operation();
        testOperation2.setDeviceId(testManagedObject2.getId());
        testOperation2.set("com_telekom_m2m_cotcommand", jsonObject);
        deviceControlApi.create(testOperation2);

        OperationCollection operations = deviceControlApi.getOperationCollection(5);
        Operation[] os = operations.getOperations();
        assertTrue(os.length > 0);
        assertTrue(Arrays.stream(os).anyMatch(operation -> !operation.getDeviceId().equals(testManagedObject.getId())));

        operations = deviceControlApi.getOperationCollection(Filter.build().byDeviceId(testManagedObject.getId()), 20);
        os = operations.getOperations();
        assertTrue(os.length > 0);
        assertTrue(Arrays.stream(os).allMatch(operation -> operation.getDeviceId().equals(testManagedObject.getId())));

        // cleanup
        deviceControlApi.deleteOperations(Filter.build().byDeviceId(testManagedObject2.getId()));
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject2);
    }

    @Test
    public void testMultipleOperationByStatus() {
        Operation testOperation = new Operation();
        testOperation.setDeviceId(testManagedObject.getId());
        testOperation.set("com_telekom_m2m_cotcommand", jsonObject);
        deviceControlApi.create(testOperation);

        OperationCollection operations = deviceControlApi.getOperationCollection(Filter.build().byStatus(OperationStatus.PENDING), 5);
        Operation[] os = operations.getOperations();
        assertTrue(os.length > 0);
        for (Operation o : os) {
            assertTrue(o.getStatus().toString().equalsIgnoreCase(OperationStatus.PENDING.toString()));
        }
    }

    @Test
    public void testMultipleOperationsByDateAndByDeviceId() {
        Operation testOperation = new Operation();
        testOperation.setDeviceId(testManagedObject.getId());
        testOperation.set("com_telekom_m2m_cotcommand", jsonObject);
        deviceControlApi.create(testOperation);

        Date yesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
        Date now = new Date();
        now.setSeconds(now.getSeconds() + 60);
        OperationCollection operations = deviceControlApi.getOperationCollection(
                Filter.build()
                        .byDate(yesterday, now)
                        .byDeviceId(testManagedObject.getId()), 5);


        Operation[] os = operations.getOperations();
        assertEquals(os.length, 1);

        Date beforeYesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24) - 10);

        operations = deviceControlApi.getOperationCollection(
                Filter.build()
                        .byDate(beforeYesterday, yesterday)
                        .byDeviceId(testManagedObject.getId()), 5);
        os = operations.getOperations();
        assertEquals(os.length, 0);
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
        OperationCollection operationCollection = deviceControlApi.getOperationCollection(
                Filter.build()
                        .byAgentId(agentId), 5);

        // then
        final Operation[] operations = operationCollection.getOperations();
        assertEquals(operations.length, 1);

        assertEquals(
                testManagedObject.getId(),
                operations[0].getDeviceId()
        );

    }

}