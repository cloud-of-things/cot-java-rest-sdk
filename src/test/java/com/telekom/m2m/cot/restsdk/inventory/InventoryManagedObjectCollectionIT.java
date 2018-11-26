package com.telekom.m2m.cot.restsdk.inventory;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.fail;


/**
 * @author steinert
 */
public class InventoryManagedObjectCollectionIT {

    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    private SoftAssert softAssert = new SoftAssert();

    private List<ManagedObject> toBeDeleted = new ArrayList<>();

    @BeforeMethod
    public void setup() {
        toBeDeleted.clear();
    }

    @AfterMethod
    public void tearDown() {

        for (ManagedObject managedObject : toBeDeleted) {
            cotPlat.getInventoryApi().delete(
                    managedObject.getId()
            );
        }

    }

    @Test
    public void testMultipleManagedObjects() {
        // Expects a tenant with already multiple measurements

        InventoryApi inventoryApi = cotPlat.getInventoryApi();

        ManagedObjectCollection moCollection = inventoryApi.getManagedObjects(5);


        ManagedObject[] managedObjects = moCollection.getManagedObjects();

        Assert.assertTrue(managedObjects.length > 0);

        ManagedObject managedObject = managedObjects[0];

        Assert.assertNotNull(managedObject.getId());
        Assert.assertTrue(managedObject.getId().length() > 0);


    }

    @Test
    public void testMultipleManagedObjectsWithPaging() {
        // Expects a tenant with already multiple measurements

        InventoryApi inventoryApi = cotPlat.getInventoryApi();

        String type = "my_special_test_type2";
        for (int i = 0; i < 6; i++) {
            ManagedObject testMo = new ManagedObject();
            testMo.setType(type);
            toBeDeleted.add(testMo);

            inventoryApi.create(testMo);
        }

        ManagedObjectCollection moCollection = inventoryApi.getManagedObjects(Filter.build().byType(type), 5);

        ManagedObject[] managedObjects = moCollection.getManagedObjects();

        softAssert.assertEquals(managedObjects.length, 5, "1: length should be 5");
        softAssert.assertTrue(moCollection.hasNext(), "2: should have next");
        softAssert.assertFalse(moCollection.hasPrevious(), "3: should not have prev because is first");

        moCollection.next();

        managedObjects = moCollection.getManagedObjects();
        softAssert.assertEquals(managedObjects.length, 1, "4: length should be 1");

        softAssert.assertFalse(moCollection.hasNext(), "5: should not have next because is last");
        softAssert.assertTrue(moCollection.hasPrevious(), "6: should have prev");

        moCollection.previous();
        managedObjects = moCollection.getManagedObjects();

        softAssert.assertEquals(managedObjects.length, 5, "7: length should be 5");

        softAssert.assertTrue(moCollection.hasNext(), "8: should have next");
        softAssert.assertFalse(moCollection.hasPrevious(), "9: should not have prev because is first");

        moCollection.setPageSize(10);
        managedObjects = moCollection.getManagedObjects();

        softAssert.assertEquals(managedObjects.length, 6, "10: length should be 6");
        softAssert.assertFalse(moCollection.hasNext(), "11: should not have next because contains all");
        softAssert.assertFalse(moCollection.hasPrevious(), "12: should not have next because contains all");

        softAssert.assertAll();
    }

    @Test
    public void testMultipleManagedObjectsByText() {
        InventoryApi inventoryApi = cotPlat.getInventoryApi();
        ManagedObject testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "my_specialxxyyzz_name");

        ManagedObjectCollection managedObjects = inventoryApi.getManagedObjects(Filter.build().byText("my_specialxxyyzz_name"), 5);
        ManagedObject[] mos = managedObjects.getManagedObjects();
        Assert.assertTrue(mos.length > 0);
        boolean allMosWithName = true;
        for (ManagedObject managedObject : mos) {
            if (!managedObject.getName().contains("specialxxyyzz")) {
                allMosWithName = false;
            }
        }
        softAssert.assertTrue(allMosWithName);
        inventoryApi.delete(testManagedObject.getId());
        softAssert.assertAll();
    }

    @Test
    public void testMultipleManagedObjectsByListOfIDs() {
        InventoryApi inventoryApi = cotPlat.getInventoryApi();
        ManagedObject testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "my_specialxxyyzz_name");

        ManagedObjectCollection managedObjects = inventoryApi.getManagedObjects(Filter.build().byListOfIds("1,2," + testManagedObject.getId() + ",7"), 5);
        ManagedObject[] mos = managedObjects.getManagedObjects();
        Assert.assertEquals(mos.length, 1);
        ManagedObject mo = mos[0];
        softAssert.assertEquals(mo.getId(), testManagedObject.getId());
        softAssert.assertEquals(mo.getName(), "my_specialxxyyzz_name");
        inventoryApi.delete(testManagedObject.getId());
        softAssert.assertAll();
    }

    @Test
    public void testRegisterAsChildDevice() {

        // given
        final InventoryApi inventoryApi = cotPlat.getInventoryApi();

        final ManagedObject parentDevice =
                TestHelper.createRandomManagedObjectInPlatform(cotPlat, "parent_device");
        toBeDeleted.add(parentDevice);
        final ManagedObject childDevice =
                TestHelper.createRandomManagedObjectInPlatform(cotPlat, "child_device");
        toBeDeleted.add(childDevice);

        // when
        inventoryApi.registerAsChildDevice(parentDevice, childDevice);

        // then
        final ManagedObject parentDeviceFromAPI = inventoryApi.get(parentDevice.getId());
        verifyContainsNewChild(parentDeviceFromAPI, childDevice);

    }

    private void verifyContainsNewChild(final ManagedObject parentDevice, final ManagedObject childDevice) {
        for (ManagedObjectReference childDeviceReference : parentDevice.getChildDevices().get()) {
            if (childDevice.getId().equals(childDeviceReference.getManagedObject().getId())) {
                return;
            }
        }
        fail("could not find child device!");
    }

}