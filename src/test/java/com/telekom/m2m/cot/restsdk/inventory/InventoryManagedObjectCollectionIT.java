package com.telekom.m2m.cot.restsdk.inventory;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author steinert
 */
public class InventoryManagedObjectCollectionIT {

    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
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
    }

    @AfterMethod
    public void tearDown() {
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
    }


    @Test
    public void testMultipleManagedObjects() throws Exception {
        // Expects a tenant with already multiple measurements

        InventoryApi inventoryApi = cotPlat.getInventoryApi();

        ManagedObjectCollection moCollection = inventoryApi.getManagedObjects(5);


        ManagedObject[] operations = moCollection.getManagedObjects();

        Assert.assertTrue(operations.length > 0);

        ManagedObject managedObject = operations[0];

        Assert.assertTrue(managedObject.getId() != null);
        Assert.assertTrue(managedObject.getId().length() > 0);


    }
//
//    @Test
//    public void testMultipleOperationssWithPaging() throws Exception {
//        // Expects a tenant with already multiple measurements
//
//        // !!! Important !!!
//        // Test assumes pageSize default is 5.
//
//        DeviceControlApi deviceControlApi = cotPlat.getDeviceControlApi();
//
//        for (int i = 0; i < 6; i++) {
//            Operation testOperation = new Operation();
//            testOperation.setDeviceId(testManagedObject.getId());
//            testOperation.set("com_telekom_m2m_cotcommand", jsonObject);
//
//            deviceControlApi.create(testOperation);
//        }
//
//        OperationCollection operationCollection = deviceControlApi.getOperations(Filter.build().byDeviceId(testManagedObject.getId()), 5);
//
//
//        Operation[] operations = operationCollection.getOperations();
//
//        Assert.assertEquals(operations.length, 5);
//        Assert.assertTrue(operationCollection.hasNext());
//        Assert.assertFalse(operationCollection.hasPrevious());
//
//        operationCollection.next();
//
//        operations = operationCollection.getOperations();
//        Assert.assertEquals(operations.length, 1);
//
//        Assert.assertFalse(operationCollection.hasNext());
//        Assert.assertTrue(operationCollection.hasPrevious());
//
//        operationCollection.previous();
//        operations = operationCollection.getOperations();
//
//        Assert.assertEquals(operations.length, 5);
//
//        Assert.assertTrue(operationCollection.hasNext());
//        Assert.assertFalse(operationCollection.hasPrevious());
//
//        operationCollection.setPageSize(10);
//        operations = operationCollection.getOperations();
//
//        Assert.assertEquals(operations.length, 6);
//        Assert.assertFalse(operationCollection.hasNext());
//        Assert.assertFalse(operationCollection.hasPrevious());
//
//    }
//
//    @Test
//    public void testDeleteMultipleOperationsBySource() throws Exception {
//        DeviceControlApi deviceControlApi = cotPlat.getDeviceControlApi();
//
//        for (int i = 0; i < 6; i++) {
//            Operation testOperation = new Operation();
//            testOperation.setDeviceId(testManagedObject.getId());
//            testOperation.set("com_telekom_m2m_cotcommand", jsonObject);
//
//            deviceControlApi.create(testOperation);
//        }
//
//        OperationCollection operations = deviceControlApi.getOperations(Filter.build().byDeviceId(testManagedObject.getId()), 5);
//        Operation[] os = operations.getOperations();
//        Assert.assertEquals(os.length, 5);
//
//        deviceControlApi.deleteOperations(Filter.build().byDeviceId(testManagedObject.getId()));
//        operations = deviceControlApi.getOperations(Filter.build().byDeviceId(testManagedObject.getId()), 5);
//        os = operations.getOperations();
//        Assert.assertEquals(os.length, 0);
//    }
//
//    @Test
//    public void testMultipleOperationsBySource() throws Exception {
//        DeviceControlApi dcApi = cotPlat.getDeviceControlApi();
//
//        Operation testOperation = new Operation();
//        testOperation.setDeviceId(testManagedObject.getId());
//        testOperation.set("com_telekom_m2m_cotcommand", jsonObject);
//        dcApi.create(testOperation);
//
//        OperationCollection operations = dcApi.getOperations(5);
//        Operation[] os = operations.getOperations();
//        Assert.assertTrue(os.length > 0);
//        boolean allOperationsFromSource = true;
//        for (Operation o : os) {
//            if (!o.getDeviceId().equals(testManagedObject.getId())) {
//                allOperationsFromSource = false;
//            }
//        }
//        Assert.assertFalse(allOperationsFromSource);
//
//        operations = dcApi.getOperations(Filter.build().byDeviceId(testManagedObject.getId()), 20);
//        os = operations.getOperations();
//        allOperationsFromSource = true;
//        Assert.assertTrue(os.length > 0);
//        for (Operation o : os) {
//            if (!o.getDeviceId().equals(testManagedObject.getId())) {
//                allOperationsFromSource = false;
//            }
//        }
//        Assert.assertTrue(allOperationsFromSource);
//    }
//
//    @Test
//    public void testMultipleOpearationByStatus() throws Exception {
//        DeviceControlApi dcApi = cotPlat.getDeviceControlApi();
//
//        Operation testOperation = new Operation();
//        testOperation.setDeviceId(testManagedObject.getId());
//        testOperation.set("com_telekom_m2m_cotcommand", jsonObject);
//        dcApi.create(testOperation);
//
//        // Test could be flaky, because can't predict if first 50
//        // operations can not have same status
//        OperationCollection operations = dcApi.getOperations(50);
//        Operation[] os = operations.getOperations();
//        Assert.assertTrue(os.length > 0);
//        boolean allOperationWithSameStatus = true;
//        for (Operation o : os) {
//            if (!o.getStatus().equals(OperationStatus.FAILED)) {
//                allOperationWithSameStatus = false;
//            }
//        }
//        Assert.assertFalse(allOperationWithSameStatus);
//
//        operations = dcApi.getOperations(Filter.build().byStatus(OperationStatus.SUCCESSFUL), 5);
//        os = operations.getOperations();
//        allOperationWithSameStatus = true;
//        Assert.assertTrue(os.length > 0);
//        for (Operation o : os) {
//            if (!o.getStatus().toString().equalsIgnoreCase(OperationStatus.SUCCESSFUL.toString())) {
//                allOperationWithSameStatus = false;
//            }
//        }
//        Assert.assertTrue(allOperationWithSameStatus);
//    }
//
//    @Test
//    public void testMultipleOperationsByDateAndByDeviceId() throws Exception {
//        DeviceControlApi dcApi = cotPlat.getDeviceControlApi();
//
//        Operation testOperation = new Operation();
//        testOperation.setDeviceId(testManagedObject.getId());
//        testOperation.set("com_telekom_m2m_cotcommand", jsonObject);
//        dcApi.create(testOperation);
//
//        Date yesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
//        OperationCollection events = dcApi.getOperations(
//                Filter.build()
//                        .byDate(yesterday, new Date())
//                        .byDeviceId(testManagedObject.getId()), 5);
//
//
//        Operation[] os = events.getOperations();
//        Assert.assertEquals(os.length, 1);
//
//        Date beforeYesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24) - 10);
//
//        events = dcApi.getOperations(
//                Filter.build()
//                        .byDate(beforeYesterday, yesterday)
//                        .byDeviceId(testManagedObject.getId()), 5);
//        os = events.getOperations();
//        Assert.assertEquals(os.length, 0);
//    }

}