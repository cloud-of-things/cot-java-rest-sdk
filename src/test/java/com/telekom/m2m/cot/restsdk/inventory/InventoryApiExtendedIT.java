package com.telekom.m2m.cot.restsdk.inventory;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.testng.Assert.*;

/**
 * Created by Patrick Steinert on 14.10.16.
 *
 * @author Patrick Steinert
 */
public class InventoryApiExtendedIT {

    private static final String PARENT_MANAGED_OBJECT_NAME = "parentTestManagedObject";
    private static final String CHILD_MANAGED_OBJECT_NAME = "childTestManagedObject";
    private static final String DEVICE_ID_WITH_SUPPORTED_MEASUREMENTS = "38345";
    private static final String DEVICE_ID_WITHOUT_SUPPORTED_MEASUREMENTS = "10575";

    private CloudOfThingsPlatform cloudOfThingsPlatform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    private InventoryApi inventoryApi = cloudOfThingsPlatform.getInventoryApi();

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
    public void testAddAndRemoveChildren() {
        ManagedObject parentManagedObject = createManagedObjectInCot(PARENT_MANAGED_OBJECT_NAME);
        ManagedObject childManagedObject = createManagedObjectInCot(CHILD_MANAGED_OBJECT_NAME);
        managedObjectsToDelete.add(parentManagedObject);
        managedObjectsToDelete.add(childManagedObject);

        ManagedObject parentMoFromCloud = inventoryApi.get(parentManagedObject.getId());
        ManagedObjectReference childMoReference = new ManagedObjectReference(childManagedObject);

        assertNotNull(childManagedObject.getId(), "Created managed object should contain an ID.");

        inventoryApi.addChildDeviceToManagedObject(parentMoFromCloud, childMoReference);
        ManagedObject reloadedParentMoFromCloud = inventoryApi.get(parentManagedObject.getId());
        Iterator<ManagedObjectReference> childDevicesIterator = reloadedParentMoFromCloud.getChildDevices().get().iterator();

        assertTrue(childDevicesIterator.hasNext(), "Parent device should contain one child by now.");

        ManagedObjectReference next = childDevicesIterator.next();

        assertEquals(next.getManagedObject().getId(), childManagedObject.getId());
        assertTrue(next.getSelf().startsWith("http"));

        inventoryApi.removeManagedObjectReference(next);

        reloadedParentMoFromCloud = inventoryApi.get(parentManagedObject.getId());
        childDevicesIterator = reloadedParentMoFromCloud.getChildDevices().get().iterator();
        assertFalse(childDevicesIterator.hasNext());
        try {
            next = childDevicesIterator.next();
            fail("Needs to throw an NSEE b/c no ref anymore.");
        } catch (NoSuchElementException e) {

        }
    }

    @Test
    public void testAddAndRemoveDeviceParents() {
        ManagedObject parentManagedObject = createManagedObjectInCot(PARENT_MANAGED_OBJECT_NAME);
        ManagedObject childManagedObject = createManagedObjectInCot(CHILD_MANAGED_OBJECT_NAME);
        managedObjectsToDelete.add(parentManagedObject);
        managedObjectsToDelete.add(childManagedObject);

        assertNotNull(parentManagedObject.getId(), "Created parent object should contain an ID.");

        ManagedObject parentMoFromCloud = inventoryApi.get(parentManagedObject.getId());
        ManagedObject childMoFromCloud = inventoryApi.get(childManagedObject.getId());
        ManagedObjectReference childMoReference = new ManagedObjectReference(childMoFromCloud);
        inventoryApi.addChildDeviceToManagedObject(parentMoFromCloud, childMoReference);

        //Reload objects
        childMoFromCloud = inventoryApi.get(childManagedObject.getId(), true);
        parentMoFromCloud = inventoryApi.get(parentManagedObject.getId(), true);

        Iterator<ManagedObjectReference> parentDevicesIterator = childMoFromCloud.getParentDevices().get().iterator();

        assertTrue(parentDevicesIterator.hasNext());
        ManagedObjectReference next = parentDevicesIterator.next();
        assertEquals(next.getManagedObject().getId(), parentMoFromCloud.getId());
        assertTrue(next.getSelf().startsWith("http"));

        ManagedObjectReference childDevice = parentMoFromCloud.getChildDevices().get().iterator().next();
        inventoryApi.removeManagedObjectReference(childDevice);

        childMoFromCloud = inventoryApi.get(childManagedObject.getId());
        parentDevicesIterator = childMoFromCloud.getParentDevices().get().iterator();
        assertFalse(parentDevicesIterator.hasNext());
        try {
            next = parentDevicesIterator.next();
            fail("Needs to throw an NSEE b/c no ref anymore.");
        } catch (NoSuchElementException e) {

        }
    }

    @Test
    public void testAddAndRemoveAssetParents() {
        ManagedObject parentMo = createManagedObjectInCot(PARENT_MANAGED_OBJECT_NAME);
        ManagedObject childMo = createManagedObjectInCot(CHILD_MANAGED_OBJECT_NAME);
        managedObjectsToDelete.add(parentMo);
        managedObjectsToDelete.add(childMo);

        assertNotNull(parentMo.getId(), "Created parent object should contain an ID.");
        parentMo = inventoryApi.get(parentMo.getId());

        childMo = inventoryApi.get(childMo.getId());
        ManagedObjectReference childMoReference = new ManagedObjectReference(childMo);

        inventoryApi.addChildAssetToManagedObject(parentMo, childMoReference);

        //Reload objects
        childMo = inventoryApi.get(childMo.getId(), true);
        parentMo = inventoryApi.get(parentMo.getId(), true);

        Iterator<ManagedObjectReference> iterator = childMo.getParentAssets().get().iterator();
        assertTrue(iterator.hasNext());
        ManagedObjectReference next = iterator.next();
        assertEquals(next.getManagedObject().getId(), parentMo.getId());
        assertTrue(next.getSelf().startsWith("http"));

        ManagedObjectReference managedObjectReference = parentMo.getChildAssets().get().iterator().next();
        inventoryApi.removeManagedObjectReference(managedObjectReference);

        childMo = inventoryApi.get(childMo.getId());
        iterator = childMo.getParentAssets().get().iterator();
        assertFalse(iterator.hasNext());
        try {
            next = iterator.next();
            fail("Needs to throw an NSEE b/c no ref anymore.");
        } catch (NoSuchElementException e) {

        }
    }

    @Test
    public void testAddAndRemoveAssets() {
        ManagedObject parentMo = createManagedObjectInCot(PARENT_MANAGED_OBJECT_NAME);
        ManagedObject childMo = createManagedObjectInCot(CHILD_MANAGED_OBJECT_NAME);
        managedObjectsToDelete.add(parentMo);
        managedObjectsToDelete.add(childMo);

        ManagedObject parentMoFromPlatform = inventoryApi.get(parentMo.getId());
        ManagedObjectReference managedObjectReference = new ManagedObjectReference(childMo);

        assertNotNull(childMo.getId(), "Created child object should contain an ID.");

        inventoryApi.addChildAssetToManagedObject(parentMoFromPlatform, managedObjectReference);

        ManagedObject reloadedMo = inventoryApi.get(parentMo.getId());
        Iterator<ManagedObjectReference> iterator = reloadedMo.getChildAssets().get().iterator();
        assertTrue(iterator.hasNext());
        ManagedObjectReference next = iterator.next();
        assertEquals(next.getManagedObject().getId(), childMo.getId());
        assertTrue(next.getSelf().startsWith("http"));

        inventoryApi.removeManagedObjectReference(next);

        reloadedMo = inventoryApi.get(parentMo.getId());
        iterator = reloadedMo.getChildAssets().get().iterator();
        assertFalse(iterator.hasNext());
        try {
            next = iterator.next();
            fail("Needs to throw an NSEE b/c no ref anymore.");
        } catch (NoSuchElementException e) {

        }
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testSupportedMeasurementsNullDeviceId() {
        inventoryApi.getSupportedMeasurements(null);
    }

    @Test(expectedExceptions = CotSdkException.class, expectedExceptionsMessageRegExp = ".*inventory/Not Found.*")
    public void testSupportedMeasurementsDeviceNotFound() {
        inventoryApi.getSupportedMeasurements("nonExistentDeviceId");
    }

    @Test(expectedExceptions = CotSdkException.class, expectedExceptionsMessageRegExp = ".*security/Unauthorized.*")
    public void testSupportedMeasurementsNotAuthorized() {
        InventoryApi inventoryApi = new CloudOfThingsPlatform(TestHelper.TEST_HOST, "fakeUsername", "fakePassword").getInventoryApi();
        inventoryApi.getSupportedMeasurements("deviceId");
    }

    @Test
    public void testNoSupportedMeasurements() {
        ArrayList<String> supportedMeasurements = inventoryApi.getSupportedMeasurements(DEVICE_ID_WITHOUT_SUPPORTED_MEASUREMENTS);

        assertTrue(supportedMeasurements.isEmpty(), String.format("List of supported measurements for device with ID %s should be empty", DEVICE_ID_WITHOUT_SUPPORTED_MEASUREMENTS));
    }

    @Test
    public void testGetSupportedMeasurements() {
        ArrayList<String> supportedMeasurements = new ArrayList<>();
        supportedMeasurements.add("Spannung");
        ArrayList<String> supportedMeasurementsFromCloud = inventoryApi.getSupportedMeasurements(DEVICE_ID_WITH_SUPPORTED_MEASUREMENTS);

        assertEquals(supportedMeasurementsFromCloud, supportedMeasurements);
    }

    private ManagedObject createManagedObjectInCot(String name) {
        ManagedObject mo = new ManagedObject();
        mo.setName(name);

        return inventoryApi.create(mo);
    }
}
