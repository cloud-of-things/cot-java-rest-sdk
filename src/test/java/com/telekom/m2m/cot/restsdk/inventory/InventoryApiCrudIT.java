package com.telekom.m2m.cot.restsdk.inventory;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Created by Patrick Steinert on 30.01.16.
 */
public class InventoryApiCrudIT {

    private static final String MANAGED_OBJECT_NAME = "managedObjectName";
    private static final String CHILD_MANAGED_OBJECT_NAME = "RaspPi 8fef9ec2 Sensor BMP180";

    CloudOfThingsPlatform cloudOfThingsPlatform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    InventoryApi inventoryApi = cloudOfThingsPlatform.getInventoryApi();

    private List<ManagedObject> managedObjectsToDelete = new ArrayList<>();

    @AfterMethod
    public void tearDown() {
        for (ManagedObject managedObject : managedObjectsToDelete) {
            inventoryApi.delete(managedObject.getId());
        }
        managedObjectsToDelete.clear();
    }

    @Test
    public void testCreateManagedObject() {
        ManagedObject mo = new ManagedObject();
        mo.setName(MANAGED_OBJECT_NAME);
        ManagedObject createdMo = inventoryApi.create(mo);
        managedObjectsToDelete.add(createdMo);
        ManagedObject retrievedMo = inventoryApi.get(createdMo.getId());

        assertNotNull(mo.getId(), "Created managed object should have an ID.");
        assertEquals(retrievedMo.getId(), createdMo.getId(), "ID of created managed object and managed object retrieved from cloud should be the same.");
        assertEquals(retrievedMo.getName(), MANAGED_OBJECT_NAME, "Name of created managed object and managed object retrieved from cloud should be the same.");
    }

    @Test
    public void testDeleteManagedObject() {
        ManagedObject mo = new ManagedObject();
        mo.setName(MANAGED_OBJECT_NAME);
        ManagedObject createdMo = inventoryApi.create(mo);
        ManagedObject retrievedMo = inventoryApi.get(createdMo.getId());

        assertNotNull(retrievedMo.getId(), "Created managed object should have an ID.");

        inventoryApi.delete(retrievedMo.getId());
        retrievedMo = inventoryApi.get(createdMo.getId());
        assertNull(retrievedMo, "After deletion managed object should not exist anymore in the cloud.");
    }

    @Test
    public void testRegisterAsChildDevice() {
        ManagedObject mo = new ManagedObject();
        mo.setName(MANAGED_OBJECT_NAME);
        ManagedObject moChild = new ManagedObject();
        moChild.setName(CHILD_MANAGED_OBJECT_NAME);

        ManagedObject createdMo = inventoryApi.create(mo);
        ManagedObject createdMoChild = inventoryApi.create(moChild);
        managedObjectsToDelete.add(createdMo);
        managedObjectsToDelete.add(createdMoChild);

        inventoryApi.registerAsChildDevice(createdMo, createdMoChild);
        ManagedObject retrievedMo = inventoryApi.get(createdMo.getId());
        ManagedObjectReferenceCollection childDevices = retrievedMo.getChildDevices();

        assertNotNull(childDevices.getSelf());

        Iterable<ManagedObjectReference> children = childDevices.get();
        Iterator<ManagedObjectReference> childDevicesIterator = children.iterator();

        assertTrue(childDevicesIterator.hasNext());
        ManagedObject child = childDevicesIterator.next().getManagedObject();
        assertEquals(child.getName(), CHILD_MANAGED_OBJECT_NAME);
    }

    @Test
    public void testCreateReadUpdateDelete() {
        ManagedObject mo = new ManagedObject();
        mo.setName(MANAGED_OBJECT_NAME);
        ManagedObject createdMo = inventoryApi.create(mo);
        managedObjectsToDelete.add(createdMo);
        ManagedObject retrievedMo = inventoryApi.get(createdMo.getId());

        JsonObject obj = new JsonObject();
        obj.add("foo", new JsonPrimitive("bar"));

        retrievedMo.setName("NewName");
        retrievedMo.set("play", obj);

        inventoryApi.update(retrievedMo);
        ManagedObject updatedMo = inventoryApi.get(createdMo.getId());

        assertEquals(updatedMo.getId(), createdMo.getId(), "ID of created and updated managed object should be the same.");
        assertEquals(updatedMo.getName(), "NewName", "Name of the managed object should have been updated in the cloud.");

        Object retrievedObject = retrievedMo.get("play");
        assertNotNull(retrievedObject);
        assertTrue(retrievedObject instanceof JsonObject);
        JsonObject playObj = (JsonObject) retrievedObject;
        assertTrue(playObj.has("foo"));
        assertEquals(playObj.get("foo").getAsString(), "bar");
    }
}