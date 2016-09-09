package com.telekom.m2m.cot.restsdk.inventory;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.util.Iterator;

/**
 * Created by breucking on 30.01.16.
 */
public class InventoryApiIT {

    @Test
    public void testCreateManagedObject() throws Exception {
        ManagedObject mo = new ManagedObject();
        mo.setName("Hello!");

        CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
        InventoryApi inventoryApi = cotPlat.getInventoryApi();

        ManagedObject createdMo = inventoryApi.create(mo);
        Assert.assertNotNull("Should now have an Id", mo.getId());
    }

    @Test
    public void testGetDevice() throws Exception {
        CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
        InventoryApi inventoryApi = cotPlat.getInventoryApi();

        ManagedObject mo = inventoryApi.get("142300");
        Iterable<ManagedObjectReference> children = mo.getChildDevices().get(5);
        Iterator<ManagedObjectReference> iter = children.iterator();
        Assert.assertTrue(iter.hasNext());
        ManagedObject child = iter.next().getManagedObject();
        Assert.assertEquals(child.getName(), "RaspPi 8fef9ec2 Sensor BMP180");
    }

    @Test
    public void testCreateAndRead() throws Exception {

        ManagedObject mo = new ManagedObject();
        mo.setName("MyTest-testCreateAndRead");

        CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
        InventoryApi inventoryApi = cotPlat.getInventoryApi();
        ManagedObject createdMo = inventoryApi.create(mo);

        Assert.assertNotNull("Should now have an Id", createdMo.getId());

        ManagedObject retrievedMo = inventoryApi.get(createdMo.getId());

        Assert.assertEquals("Should have the same Id", createdMo.getId(), retrievedMo.getId());
        Assert.assertEquals("Should have the same Name", "MyTest-testCreateAndRead", retrievedMo.getName());
    }


}
