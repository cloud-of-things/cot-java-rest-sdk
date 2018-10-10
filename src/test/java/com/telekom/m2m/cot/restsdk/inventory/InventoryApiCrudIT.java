package com.telekom.m2m.cot.restsdk.inventory;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Iterator;

import static org.testng.Assert.*;

/**
 * Created by Patrick Steinert on 30.01.16.
 */
public class InventoryApiCrudIT {

    private static final String MANAGED_OBJECT_NAME = "managedObjectName";
    private static final String CHILD_MANAGED_OBJECT_NAME = "RaspPi 8fef9ec2 Sensor BMP180";

    private CloudOfThingsPlatform cloudOfThingsPlatform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    private InventoryApi inventoryApi = cloudOfThingsPlatform.getInventoryApi();

    private ManagedObject managedObject;
    private ManagedObject childManagedObject;

    @BeforeClass
    public void setUp() {
        managedObject = TestHelper.createRandomManagedObjectInPlatform(cloudOfThingsPlatform, MANAGED_OBJECT_NAME);
        childManagedObject = TestHelper.createRandomManagedObjectInPlatform(cloudOfThingsPlatform, CHILD_MANAGED_OBJECT_NAME);
    }

    @AfterMethod
    public void tearDown() {
        TestHelper.deleteManagedObjectInPlatform(cloudOfThingsPlatform, managedObject);
        TestHelper.deleteManagedObjectInPlatform(cloudOfThingsPlatform, childManagedObject);
    }

    @Test
    public void testCreateManagedObject() {
        ManagedObject retrievedMo = inventoryApi.get(managedObject.getId());

        assertNotNull(managedObject.getId(), "Created managed object should have an ID.");
        assertEquals(retrievedMo.getId(), managedObject.getId(), "ID of created managed object and managed object retrieved from cloud should be the same.");
        assertEquals(retrievedMo.getName(), MANAGED_OBJECT_NAME, "Name of created managed object and managed object retrieved from cloud should be the same.");
    }

    @Test
    public void testDeleteManagedObject() {
        ManagedObject retrievedMo = inventoryApi.get(managedObject.getId());

        assertNotNull(retrievedMo.getId(), "Created managed object should have an ID.");

        inventoryApi.delete(retrievedMo.getId());
        retrievedMo = inventoryApi.get(managedObject.getId());

        assertNull(retrievedMo, "After deletion managed object should not exist anymore in the cloud.");
    }

    @Test
    public void testRegisterAsChildDevice() {
        inventoryApi.registerAsChildDevice(managedObject, childManagedObject);
        ManagedObject retrievedMo = inventoryApi.get(managedObject.getId());
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
        ManagedObject retrievedMo = inventoryApi.get(managedObject.getId());

        JsonObject obj = new JsonObject();
        obj.add("foo", new JsonPrimitive("bar"));

        retrievedMo.setName("NewName");
        retrievedMo.set("play", obj);

        inventoryApi.update(retrievedMo);
        ManagedObject updatedMo = inventoryApi.get(managedObject.getId());

        assertEquals(updatedMo.getId(), managedObject.getId(), "ID of created and updated managed object should be the same.");
        assertEquals(updatedMo.getName(), "NewName", "Name of the managed object should have been updated in the cloud.");

        Object retrievedObject = retrievedMo.get("play");
        assertNotNull(retrievedObject);
        assertTrue(retrievedObject instanceof JsonObject);
        JsonObject playObj = (JsonObject) retrievedObject;
        assertTrue(playObj.has("foo"));
        assertEquals(playObj.get("foo").getAsString(), "bar");
    }
}