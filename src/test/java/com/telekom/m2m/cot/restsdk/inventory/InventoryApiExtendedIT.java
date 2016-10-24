package com.telekom.m2m.cot.restsdk.inventory;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Iterator;

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
    public void testAddChildren() throws Exception {

        InventoryApi inventoryApi = cotPlat.getInventoryApi();

        ManagedObject mo = new ManagedObject();
        mo.setName("MyTest-testCreateAndRead");


        ManagedObject createdChildMo = inventoryApi.create(mo);
        ManagedObject moFromPlatform = inventoryApi.get(testManagedObject.getId());
        ManagedObjectReference managedObjectReference = new ManagedObjectReference(createdChildMo);

        Assert.assertNotNull("Should now have an Id", createdChildMo.getId());

        inventoryApi.addChildToManagedObject(moFromPlatform, managedObjectReference);

        ManagedObject reloadedMo = inventoryApi.get(testManagedObject.getId());
        Iterator<ManagedObjectReference> iter = reloadedMo.getChildDevices().get(1).iterator();
        Assert.assertTrue(iter.hasNext());
        ManagedObjectReference next = iter.next();
        Assert.assertEquals(next.getManagedObject().getId(), createdChildMo.getId());

    }

}
