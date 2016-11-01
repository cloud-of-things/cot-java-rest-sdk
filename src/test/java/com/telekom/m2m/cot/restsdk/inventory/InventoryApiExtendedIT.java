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
    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
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
        Iterator<ManagedObjectReference> iter = reloadedMo.getChildDevices().get(1).iterator();
        Assert.assertTrue(iter.hasNext());
        ManagedObjectReference next = iter.next();
        Assert.assertEquals(next.getManagedObject().getId(), createdChildMo.getId());
        Assert.assertTrue(next.getSelf().startsWith("http"));

        inventoryApi.removeManagedObjectReference(next);

        reloadedMo = inventoryApi.get(testManagedObject.getId());
        iter = reloadedMo.getChildDevices().get(1).iterator();
        Assert.assertFalse(iter.hasNext());
        try {
            next = iter.next();
            Assert.fail("Needs to throw an NSEE b/c no ref anymore.");
        } catch (NoSuchElementException e) {

        }
    }

    @Test
    public void testAddAndRemoveParents() throws Exception {

        InventoryApi inventoryApi = cotPlat.getInventoryApi();

        ManagedObject mo = new ManagedObject();
        mo.setName("MyTest-testCreateAndRead");


        ManagedObject createdParentMo = inventoryApi.create(mo);
        Assert.assertNotNull("Should now have an Id", createdParentMo.getId());
        createdParentMo = inventoryApi.get(createdParentMo.getId());

        ManagedObject moFromPlatform = inventoryApi.get(testManagedObject.getId());
        ManagedObjectReference managedObjectReference = new ManagedObjectReference(moFromPlatform);


        inventoryApi.addChildDeviceToManagedObject(createdParentMo, managedObjectReference);

        //Reload objects
        ManagedObject reloadedMo = inventoryApi.get(testManagedObject.getId());
        createdParentMo = inventoryApi.get(createdParentMo.getId());

        Iterator<ManagedObjectReference> iter = reloadedMo.getParentDevices().get(1).iterator();
        Assert.assertTrue(iter.hasNext());
        ManagedObjectReference next = iter.next();
        Assert.assertEquals(next.getManagedObject().getId(), createdParentMo.getId());
        Assert.assertTrue(next.getSelf().startsWith("http"));

        ManagedObjectReference del = createdParentMo.getChildDevices().get(1).iterator().next();
        inventoryApi.removeManagedObjectReference(del);

        reloadedMo = inventoryApi.get(reloadedMo.getId());
        iter = reloadedMo.getParentDevices().get(1).iterator();
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
        Iterator<ManagedObjectReference> iter = reloadedMo.getChildAssets().get(1).iterator();
        Assert.assertTrue(iter.hasNext());
        ManagedObjectReference next = iter.next();
        Assert.assertEquals(next.getManagedObject().getId(), createdChildMo.getId());
        Assert.assertTrue(next.getSelf().startsWith("http"));

        inventoryApi.removeManagedObjectReference(next);

        reloadedMo = inventoryApi.get(testManagedObject.getId());
        iter = reloadedMo.getChildAssets().get(1).iterator();
        Assert.assertFalse(iter.hasNext());
        try {
            next = iter.next();
            Assert.fail("Needs to throw an NSEE b/c no ref anymore.");
        } catch (NoSuchElementException e) {

        }
    }

}
