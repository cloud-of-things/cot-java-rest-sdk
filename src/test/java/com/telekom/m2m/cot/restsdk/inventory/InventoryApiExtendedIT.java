package com.telekom.m2m.cot.restsdk.inventory;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Patrick Steinert on 14.10.16.
 *
 * @author Patrick Steinert
 */
public class InventoryApiExtendedIT {
    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    private ManagedObject testManagedObject;

    @BeforeClass
    public void setUp() {
        testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name");
    }

    @AfterClass
    public void tearDown() {
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
    }

    @Test
    public void testAddAndRemoveChildren() throws Exception {

        InventoryApi inventoryApi = cotPlat.getInventoryApi();

        ManagedObject mo = new ManagedObject();
        mo.setName("MyTest-testCreateAndRead");


        ManagedObject createdChildMo = inventoryApi.create(mo);
        ManagedObject moFromPlatform = inventoryApi.get(testManagedObject.getId());
        ManagedObjectReference managedObjectReference = new ManagedObjectReference(createdChildMo);

        Assert.assertNotNull("Should now have an Id", createdChildMo.getId());

        inventoryApi.addChildDeviceToManagedObject(moFromPlatform, managedObjectReference);

        ManagedObject reloadedMo = inventoryApi.get(testManagedObject.getId());
        Iterator<ManagedObjectReference> iter = reloadedMo.getChildDevices().get().iterator();
        Assert.assertTrue(iter.hasNext());
        ManagedObjectReference next = iter.next();
        Assert.assertEquals(next.getManagedObject().getId(), createdChildMo.getId());
        Assert.assertTrue(next.getSelf().startsWith("http"));

        inventoryApi.removeManagedObjectReference(next);

        reloadedMo = inventoryApi.get(testManagedObject.getId());
        iter = reloadedMo.getChildDevices().get().iterator();
        Assert.assertFalse(iter.hasNext());
        try {
            next = iter.next();
            Assert.fail("Needs to throw an NSEE b/c no ref anymore.");
        } catch (NoSuchElementException e) {

        }
    }

    @Test
    public void testAddAndRemoveDeviceParents() throws Exception {

        InventoryApi inventoryApi = cotPlat.getInventoryApi();

        ManagedObject mo = new ManagedObject();
        mo.setName("MyTest-testCreateAndRead");


        ManagedObject parentMo = inventoryApi.create(mo);
        Assert.assertNotNull("Should now have an Id", parentMo.getId());
        parentMo = inventoryApi.get(parentMo.getId());

        ManagedObject childMo = inventoryApi.get(testManagedObject.getId());
        ManagedObjectReference refToChild = new ManagedObjectReference(childMo);


        inventoryApi.addChildDeviceToManagedObject(parentMo, refToChild);

        //Reload objects
        childMo = inventoryApi.get(childMo.getId(), true);
        parentMo = inventoryApi.get(parentMo.getId(), true);

        Iterator<ManagedObjectReference> iter = childMo.getParentDevices().get().iterator();
        Assert.assertTrue(iter.hasNext());
        ManagedObjectReference next = iter.next();
        Assert.assertEquals(next.getManagedObject().getId(), parentMo.getId());
        Assert.assertTrue(next.getSelf().startsWith("http"));

        ManagedObjectReference del = parentMo.getChildDevices().get().iterator().next();
        inventoryApi.removeManagedObjectReference(del);

        childMo = inventoryApi.get(childMo.getId());
        iter = childMo.getParentDevices().get().iterator();
        Assert.assertFalse(iter.hasNext());
        try {
            next = iter.next();
            Assert.fail("Needs to throw an NSEE b/c no ref anymore.");
        } catch (NoSuchElementException e) {

        }
    }

    @Test
    public void testAddAndRemoveAssetParents() throws Exception {

        InventoryApi inventoryApi = cotPlat.getInventoryApi();

        ManagedObject mo = new ManagedObject();
        mo.setName("MyTest-testCreateAndRead");


        ManagedObject parentMo = inventoryApi.create(mo);
        Assert.assertNotNull("Should now have an Id", parentMo.getId());
        parentMo = inventoryApi.get(parentMo.getId());

        ManagedObject childMo = inventoryApi.get(testManagedObject.getId());
        ManagedObjectReference refToChild = new ManagedObjectReference(childMo);


        inventoryApi.addChildAssetToManagedObject(parentMo, refToChild);

        //Reload objects
        childMo = inventoryApi.get(childMo.getId(), true);
        parentMo = inventoryApi.get(parentMo.getId(), true);

        Iterator<ManagedObjectReference> iter = childMo.getParentAssets().get().iterator();
        Assert.assertTrue(iter.hasNext());
        ManagedObjectReference next = iter.next();
        Assert.assertEquals(next.getManagedObject().getId(), parentMo.getId());
        Assert.assertTrue(next.getSelf().startsWith("http"));

        ManagedObjectReference del = parentMo.getChildAssets().get().iterator().next();
        inventoryApi.removeManagedObjectReference(del);

        childMo = inventoryApi.get(childMo.getId());
        iter = childMo.getParentAssets().get().iterator();
        Assert.assertFalse(iter.hasNext());
        try {
            next = iter.next();
            Assert.fail("Needs to throw an NSEE b/c no ref anymore.");
        } catch (NoSuchElementException e) {

        }
    }

    @Test
    public void testAddAndRemoveAssets() throws Exception {

        InventoryApi inventoryApi = cotPlat.getInventoryApi();

        ManagedObject mo = new ManagedObject();
        mo.setName("MyTest-testCreateAndRead");


        ManagedObject createdChildMo = inventoryApi.create(mo);
        ManagedObject moFromPlatform = inventoryApi.get(testManagedObject.getId());
        ManagedObjectReference managedObjectReference = new ManagedObjectReference(createdChildMo);

        Assert.assertNotNull("Should now have an Id", createdChildMo.getId());

        inventoryApi.addChildAssetToManagedObject(moFromPlatform, managedObjectReference);

        ManagedObject reloadedMo = inventoryApi.get(testManagedObject.getId());
        Iterator<ManagedObjectReference> iter = reloadedMo.getChildAssets().get().iterator();
        Assert.assertTrue(iter.hasNext());
        ManagedObjectReference next = iter.next();
        Assert.assertEquals(next.getManagedObject().getId(), createdChildMo.getId());
        Assert.assertTrue(next.getSelf().startsWith("http"));

        inventoryApi.removeManagedObjectReference(next);

        reloadedMo = inventoryApi.get(testManagedObject.getId());
        iter = reloadedMo.getChildAssets().get().iterator();
        Assert.assertFalse(iter.hasNext());
        try {
            next = iter.next();
            Assert.fail("Needs to throw an NSEE b/c no ref anymore.");
        } catch (NoSuchElementException e) {

        }
    }

}
