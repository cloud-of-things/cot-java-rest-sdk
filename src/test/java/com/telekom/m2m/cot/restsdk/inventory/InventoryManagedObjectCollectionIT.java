package com.telekom.m2m.cot.restsdk.inventory;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;

/**
 * @author steinert
 */
public class InventoryManagedObjectCollectionIT {

    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    private SoftAssert softAssert = new SoftAssert();

    @Test
    public void testMultipleManagedObjects() throws Exception {
        // Expects a tenant with already multiple measurements

        InventoryApi inventoryApi = cotPlat.getInventoryApi();

        ManagedObjectCollection moCollection = inventoryApi.getManagedObjects(5);


        ManagedObject[] managedObjects = moCollection.getManagedObjects();

        Assert.assertTrue(managedObjects.length > 0);

        ManagedObject managedObject = managedObjects[0];

        Assert.assertTrue(managedObject.getId() != null);
        Assert.assertTrue(managedObject.getId().length() > 0);


    }

    @Test
    public void testMultipleManagedObjectsWithPaging() throws Exception {
        // Expects a tenant with already multiple measurements

        // !!! Important !!!
        // Test assumes pageSize default is 5.

        InventoryApi inventoryApi = cotPlat.getInventoryApi();
        ArrayList<ManagedObject> list = new ArrayList<ManagedObject>();

        for (int i = 0; i < 6; i++) {
            ManagedObject testMo = new ManagedObject();
            testMo.setType("my_special_test_type");

            list.add(inventoryApi.create(testMo));

        }

        ManagedObjectCollection moCollection = inventoryApi.getManagedObjects(Filter.build().byType("my_special_test_type"), 5);


        ManagedObject[] managedObjects = moCollection.getManagedObjects();


        softAssert.assertEquals(managedObjects.length, 5, "length should be 5");
        softAssert.assertTrue(moCollection.hasNext(), "should have next");
        softAssert.assertFalse(moCollection.hasPrevious(), "should not have prev because is first");

        moCollection.next();

        managedObjects = moCollection.getManagedObjects();
        softAssert.assertEquals(managedObjects.length, 1, "length should be 1");

        softAssert.assertFalse(moCollection.hasNext(), "should not have nextbecause is last");
        softAssert.assertTrue(moCollection.hasPrevious(), "should have prev");

        moCollection.previous();
        managedObjects = moCollection.getManagedObjects();

        softAssert.assertEquals(managedObjects.length, 5, "length should be 5");

        softAssert.assertTrue(moCollection.hasNext(), "should have next");
        softAssert.assertFalse(moCollection.hasPrevious(), "should not have prev because is first");

        moCollection.setPageSize(10);
        managedObjects = moCollection.getManagedObjects();

        softAssert.assertEquals(managedObjects.length, 6, "length should be 6");
        softAssert.assertFalse(moCollection.hasNext(), "should not have next because contains all");
        softAssert.assertFalse(moCollection.hasPrevious(), "should not have next because contains all");

        // Cleanup
        for (ManagedObject mo : list) {
            inventoryApi.delete(mo.getId());
        }

        softAssert.assertAll();

    }

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

    @Test
    public void testMultipleManagedObjectsByText() throws Exception {
        InventoryApi inventoryApi = cotPlat.getInventoryApi();
        ManagedObject testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "my_specialxxyyzz_name");

        ManagedObjectCollection managedObjects = inventoryApi.getManagedObjects(Filter.build().byText("my_specialxxyyzz_name"), 5);
        ManagedObject[] mos = managedObjects.getManagedObjects();
        Assert.assertTrue(mos.length > 0);
        boolean allMosWithName = true;
        for (ManagedObject managedObject : mos) {
            if (!managedObject.getName().contains("specialxxyyzz")) {
                allMosWithName = false;
            }
        }
        softAssert.assertTrue(allMosWithName);
        inventoryApi.delete(testManagedObject.getId());
        softAssert.assertAll();
    }

    @Test
    public void testMultipleManagedObjectsByListOfIDs() throws Exception {
        InventoryApi inventoryApi = cotPlat.getInventoryApi();
        ManagedObject testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "my_specialxxyyzz_name");

        ManagedObjectCollection managedObjects = inventoryApi.getManagedObjects(Filter.build().byListOfIds("1,2," + testManagedObject.getId() + ",7"), 5);
        ManagedObject[] mos = managedObjects.getManagedObjects();
        Assert.assertEquals(mos.length, 1);
        ManagedObject mo = mos[0];
        softAssert.assertEquals(mo.getId(), testManagedObject.getId());
        softAssert.assertEquals(mo.getName(), "my_specialxxyyzz_name");
        inventoryApi.delete(testManagedObject.getId());
        softAssert.assertAll();
    }

//    @Test
//    public void testMultipleManagedObjectsByFragment() throws Exception {
//        InventoryApi inventoryApi = cotPlat.getInventoryApi();
//        ManagedObject testManagedObject  = TestHelper.createManagedObject("IT-Test-MO_DELETE");
//        testManagedObject = inventoryApi.create(testManagedObject);
//
//        SampleTemperatureSensor sts = new SampleTemperatureSensor();
//        sts.setTemperature(10);
//
//        Measurement measurement = new Measurement();
//        measurement.setTime(new Date());
//        measurement.setType("com_telekom_TestType");
//        measurement.setSource(testManagedObject);
//        measurement.set(sts);
//
//        MeasurementApi measurementApi = cotPlat.getMeasurementApi();
//        Measurement createdMeasurements = measurementApi.createMeasurement(measurement);
//
//        testManagedObject = inventoryApi.get(testManagedObject.getId());
//
//        ManagedObjectCollection managedObjects = inventoryApi.getManagedObjects(Filter.build().byFragmentType("com_telekom_m2m_cot_restsdk_util_SampleTemperatureSensor"), 5);
//        ManagedObject[] mos = managedObjects.getManagedObjects();
//        Assert.assertTrue(mos.length > 0);
//        boolean allOperationWithSameStatus = true;
//        for (ManagedObject o : mos) {
//            if (!o.getName().equals("IT-Test-MO_DELETE")) {
//                allOperationWithSameStatus = false;
//            }
//        }
//        softAssert.assertFalse(allOperationWithSameStatus, "Don't got the wanted MOs");
//
//        inventoryApi.delete(testManagedObject.getId());
//
//        softAssert.assertAll();
//
//    }

}