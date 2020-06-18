package com.telekom.m2m.cot.restsdk.inventory;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class InventoryApiQueryIT {

    private static final String MANAGED_OBJECT_NAME = "testDevice_" + System.currentTimeMillis();

    private final CloudOfThingsPlatform cloudOfThingsPlatform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    private final InventoryApi inventoryApi = cloudOfThingsPlatform.getInventoryApi();

    private List<ManagedObject> managedObjectsToDelete = new ArrayList<>();

    @AfterMethod
    public void tearDown() {
        for (ManagedObject managedObject: managedObjectsToDelete) {
            try {
                TestHelper.deleteManagedObjectInPlatform(cloudOfThingsPlatform, managedObject);
            } catch (CotSdkException e) {
                assertEquals(e.getHttpStatus(), 404);
            }
        }
    }

    @Test
    public void testQueryString() {
        createManagedObjectInCot(MANAGED_OBJECT_NAME);

        ManagedObjectCollection queryResult = inventoryApi.getManagedObjectsByQuery(String.format("name eq '%s'", MANAGED_OBJECT_NAME), 0);
        assertEquals(queryResult.getManagedObjects().length, 1);
    }

    private ManagedObject createManagedObjectInCot(String name) {
        ManagedObject managedObject = new ManagedObject();
        managedObjectsToDelete.add(managedObject);

        managedObject.setName(name);

        return inventoryApi.create(managedObject);
    }
}