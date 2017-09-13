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

        CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
        InventoryApi inventoryApi = cotPlat.getInventoryApi();

        ManagedObject createdMo = inventoryApi.create(mo);
        Assert.assertNotNull("Should now have an Id", mo.getId());
    }

    @Test
    public void testCreateAndRead() throws Exception {

        ManagedObject mo = new ManagedObject();
        final String moName = "MyTest-testCreateAndRead";
        mo.setName(moName);

        ManagedObject moChild = new ManagedObject();
        final String moChildName = "RaspPi 8fef9ec2 Sensor BMP180";
        moChild.setName(moChildName);

        CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
        InventoryApi inventoryApi = cotPlat.getInventoryApi();
        ManagedObject createdMo = inventoryApi.create(mo);
        ManagedObject createdMoChild = inventoryApi.create(moChild);

        inventoryApi.registerAsChildDevice(createdMo, createdMoChild);

        Assert.assertNotNull("Should now have an Id", createdMo.getId());

        ManagedObject retrievedMo = inventoryApi.get(createdMo.getId());

        Assert.assertEquals(retrievedMo.getId(), createdMo.getId(), "Should have the same Id");
        Assert.assertEquals(retrievedMo.getName(), moName, "Should have the same Name");

        ManagedObjectReferenceCollection childDevices = retrievedMo.getChildDevices();
        Assert.assertNotNull(childDevices.getSelf());

        Iterable<ManagedObjectReference> children = childDevices.get();
        Iterator<ManagedObjectReference> iter = children.iterator();

        Assert.assertTrue(iter.hasNext());
        ManagedObject child = iter.next().getManagedObject();
        Assert.assertEquals(child.getName(), moChildName);

    }

    @Test
    public void testCreateReadUpdateDelete() throws Exception {

        ManagedObject mo = new ManagedObject();
        mo.setName("MyTest-testCreateAndRead");

        CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
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

        ManagedObject aretrievedMo = inventoryApi.get(createdMo.getId());

        Assert.assertEquals(aretrievedMo.getId(), createdMo.getId(), "Should have the same Id");
        Assert.assertEquals(aretrievedMo.getName(), "NewName", "Should have the same Name");

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