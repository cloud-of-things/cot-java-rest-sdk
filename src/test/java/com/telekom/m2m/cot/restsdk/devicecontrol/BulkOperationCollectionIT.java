package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObjectReference;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.*;


/**
 * @author Andreas Dyck
 */
public class BulkOperationCollectionIT {

    private final CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

    private ManagedObject testManagedObject;
    private ManagedObject deviceGroup;

    final DeviceControlApi deviceControlApi = cotPlat.getDeviceControlApi();
    private static final JsonObject jsonObject = new JsonObject();

    static {
        JsonObject parameters = new JsonObject();
        parameters.add("param1", new JsonPrimitive("1"));

        jsonObject.add("name", new JsonPrimitive("example"));
        jsonObject.add("parameters", parameters);
    }

    @BeforeMethod
    public void setUp() {
        testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name");
        deviceGroup = createDeviceGroup();
    }

    @AfterMethod
    public void tearDown() {
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
        InventoryApi inventoryApi = cotPlat.getInventoryApi();
        inventoryApi.delete(deviceGroup.getId());
    }

    @Test
    public void testBulkOperationCollection() {
        // given at least one created operation entry
        final BulkOperation bulkOperation = createBulkOperation();

        deviceControlApi.create(bulkOperation);

        // when
        final BulkOperationCollection bulkOperationCollection = deviceControlApi.getBulkOperationCollection(5);

        // then
        assertNotNull(bulkOperationCollection);

        final BulkOperation[] bulkOperations = bulkOperationCollection.getBulkOperations();

        assertTrue(bulkOperations.length > 0);
        assertEquals(bulkOperations.length, bulkOperationCollection.getJsonArray().size());

        final BulkOperation retrievedBulkOperation = bulkOperations[0];
        final JsonObject jsonObject = bulkOperationCollection.getJsonArray().get(0).getAsJsonObject();

        assertNotNull(retrievedBulkOperation.getId());
        assertFalse(retrievedBulkOperation.getId().isEmpty());
        assertEquals(jsonObject.get("id").getAsString(), retrievedBulkOperation.getId());

        assertNotNull(retrievedBulkOperation.getGroupId());
        assertEquals(jsonObject.get("groupId").getAsString(), retrievedBulkOperation.getGroupId());

        assertNotNull(retrievedBulkOperation.getStatus());
        assertEquals(jsonObject.get("status").getAsString(), retrievedBulkOperation.getStatus());
    }

    private ManagedObject createDeviceGroup() {
        ManagedObject deviceGroup = new ManagedObject();
        deviceGroup.setName("deviceGroup");
        deviceGroup.set("c8y_IsDeviceGroup", new JsonObject());
        deviceGroup.setType("c8y_DeviceGroup");

        InventoryApi inventoryApi = cotPlat.getInventoryApi();

        inventoryApi.create(deviceGroup);

        ManagedObject deviceGroupFromCoT = inventoryApi.get(deviceGroup.getId());
        ManagedObjectReference managedObjectReference = new ManagedObjectReference(testManagedObject);

        inventoryApi.addChildAssetToManagedObject(deviceGroupFromCoT, managedObjectReference);

        return deviceGroupFromCoT;
    }

    private BulkOperation createBulkOperation() {
        final BulkOperation bulkOperation = new BulkOperation();

        final Operation operation = new Operation();
        operation.set("com_telekom_m2m_cotcommand", jsonObject);

        bulkOperation.setGroupId(deviceGroup.getId());
        bulkOperation.setStartDate(new Date(System.currentTimeMillis() + 500));
        bulkOperation.setCreationRamp(1);
        bulkOperation.setOperation(operation);

        return bulkOperation;
    }
}