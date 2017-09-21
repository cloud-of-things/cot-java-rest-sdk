package com.telekom.m2m.cot.restsdk.library.sensor;

import static org.testng.Assert.assertNotNull;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;
import com.telekom.m2m.cot.restsdk.util.TestHelper;

public class SensorLibraryIT {

    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    private InventoryApi inventoryApi = cotPlat.getInventoryApi();

    private ManagedObject testManagedObject;


    @AfterClass
    public void tearDown() {
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
    }
    
    
    @Test
    public void testSensorsAndMeasurements() throws Exception {
        testManagedObject = new ManagedObject();
        testManagedObject.setName("TestObjectForSensorsAndMeasurements");
        Fragment[] ff = SensorLibraryTest.getFragments();
        for (Fragment f : ff) {
            testManagedObject.addFragment(f);
        }

        inventoryApi.create(testManagedObject);
        assertNotNull(testManagedObject.getId(), "Should now have an Id");

        ManagedObject moOut = inventoryApi.get(testManagedObject.getId());
    
    }

    
}