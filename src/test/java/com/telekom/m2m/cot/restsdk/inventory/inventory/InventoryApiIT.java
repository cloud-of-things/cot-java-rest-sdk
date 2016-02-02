package com.telekom.m2m.cot.restsdk.inventory.inventory;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.inventory.util.TestHelper;
import org.junit.Assert;
import org.testng.annotations.Test;

/**
 * Created by breucking on 30.01.16.
 */
public class InventoryApiIT {

    @Test
    public void testCreateManagedObject() throws Exception {
        ManagedObject mo = new ManagedObject();
        mo.setName("Hello!");

        CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
        InventoryApi inventoryApi = cotPlat.getInventoryApi();

        ManagedObject createdMo = inventoryApi.create(mo);
        Assert.assertNotNull("Should now have an Id", mo.getId());
    }

    @Test
    public void testCreateAndRead() throws Exception {

        ManagedObject mo = new ManagedObject();
        mo.setName("MyTest-testCreateAndRead");

        CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
        InventoryApi inventoryApi = cotPlat.getInventoryApi();
        ManagedObject createdMo = inventoryApi.create(mo);

        Assert.assertNotNull("Should now have an Id", createdMo.getId());

        ManagedObject retrievedMo = inventoryApi.get(createdMo.getId());

        Assert.assertEquals("Should have the same Id", createdMo.getId(), retrievedMo.getId());
        Assert.assertEquals("Should have the same Name", "MyTest-testCreateAndRead", retrievedMo.getName());
    }

    @Test
    public void testGetManagedObjects() throws Exception {
    }
}
