package com.telekom.m2m.cot.restsdk.inventory;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Iterator;

/**
 * Created by Patrick Steinert on 30.01.16.
 */
public class InventoryApiCrudIT {

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
        ManagedObjectReferenceCollection childs = mo.getChildDevices();
        Assert.assertNotNull(childs.getSelf());

        Iterable<ManagedObjectReference> children = childs.get(5);
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

        Assert.assertEquals(retrievedMo.getId(), createdMo.getId(), "Should have the same Id");
        Assert.assertEquals(retrievedMo.getName(), "MyTest-testCreateAndRead", "Should have the same Name");
    }

    @Test
    public void testCreateReadUpdateDelete() throws Exception {

        ManagedObject mo = new ManagedObject();
        mo.setName("MyTest-testCreateAndRead");

        CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
        InventoryApi inventoryApi = cotPlat.getInventoryApi();
        ManagedObject createdMo = inventoryApi.create(mo);

        Assert.assertNotNull("Should now have an Id", createdMo.getId());

        ManagedObject retrievedMo = inventoryApi.get(createdMo.getId());

        Assert.assertEquals(retrievedMo.getId(), createdMo.getId(), "Should have the same Id");
        Assert.assertEquals(retrievedMo.getName(), "MyTest-testCreateAndRead", "Should have the same Name");

        JsonObject obj = new JsonObject();
        obj.add("foo", new JsonPrimitive("bar"));

        retrievedMo.setName("NewName");
        retrievedMo.set("play", obj);

        inventoryApi.update(retrievedMo);

        retrievedMo = inventoryApi.get(createdMo.getId());

        Assert.assertEquals(retrievedMo.getId(), createdMo.getId(), "Should have the same Id");
        Assert.assertEquals(retrievedMo.getName(), "NewName", "Should have the same Name");

        Object play = retrievedMo.get("play");
        Assert.assertNotNull(play);
        Assert.assertTrue(play instanceof JsonObject);
        JsonObject playObj = (JsonObject) play;
        Assert.assertTrue(playObj.has("foo"));
        Assert.assertEquals(playObj.get("foo").getAsString(), "bar");

        inventoryApi.delete(retrievedMo.getId());
        retrievedMo = inventoryApi.get(createdMo.getId());
        Assert.assertNull(retrievedMo);
    }

}