package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObjectReference;
import com.telekom.m2m.cot.restsdk.realtime.CepConnector;
import com.telekom.m2m.cot.restsdk.realtime.Notification;
import com.telekom.m2m.cot.restsdk.realtime.SubscriptionListener;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.*;


/**
 * Created by Patrick Steinert on 30.01.16.
 */
public class DeviceControlApiIT {

    private static final int DELAY_MILLIS = 100;

    CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    private ManagedObject testManagedObject;
    private final DeviceControlApi deviceControlApi = cotPlat.getDeviceControlApi();

    CepConnector connector;


    @BeforeClass
    public void setUp() {
        testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name");
    }

    @AfterClass
    public void tearDown() {
        deviceControlApi.deleteOperations(Filter.build().byDeviceId(testManagedObject.getId()));
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
        if (connector != null) {
            connector.disconnect();
        }
    }

    @Test
    public void testCreateAndGetOperation() {
        // given
        Operation operation = createOperation("com_telekom_m2m_cotcommand");

        // when
        Operation createdOperation = deviceControlApi.create(operation);

        // then
        assertNotNull(createdOperation.getId(), "Should now have an Id");
        assertEquals(createdOperation.getId(), operation.getId());

        // when
        Operation retrievedOperation = deviceControlApi.getOperation(createdOperation.getId());

        // then
        assertEquals(retrievedOperation.getDeviceId(), testManagedObject.getId());
        assertNotNull(retrievedOperation.get("com_telekom_m2m_cotcommand"));
    }

    @Test
    public void testCreateAndUpdateOperation() {
        Operation updatedOperation;

        // given
        Operation operation = createOperation("com_telekom_m2m_cotcommand");

        // when
        Operation createdOperation = deviceControlApi.create(operation);

        // then
        assertNotNull(createdOperation.getId(), "Should now have an Id");
        assertNull(createdOperation.getStatus());

        // when
        createdOperation.setStatus(OperationStatus.EXECUTING);

        deviceControlApi.update(createdOperation);
        updatedOperation = deviceControlApi.getOperation(createdOperation.getId());

        // then
        assertEquals(updatedOperation.getStatus(), OperationStatus.EXECUTING);

        final String failureReason = "AN ERROR HAS OCCURED";
        //when
        createdOperation.setStatus(OperationStatus.FAILED);
        createdOperation.setFailureReason(failureReason);

        deviceControlApi.update(createdOperation);
        updatedOperation = deviceControlApi.getOperation(createdOperation.getId());
        assertEquals(updatedOperation.getStatus(), OperationStatus.FAILED);

        //then
        assertEquals(updatedOperation.getFailureReason(), failureReason);

        //when
        // first step: remove failureReason before changing status
        createdOperation.setFailureReason(null);
        deviceControlApi.update(createdOperation);

        //then
        updatedOperation = deviceControlApi.getOperation(createdOperation.getId());
        // when failure reason was set to null it will be handled as an empty string
        // because cot ignores null values for failure reason
        assertEquals(updatedOperation.getFailureReason(), "");

        // second step: change status
        createdOperation.setStatus(OperationStatus.PENDING);
        deviceControlApi.update(createdOperation);

        updatedOperation = deviceControlApi.getOperation(createdOperation.getId());
        assertEquals(updatedOperation.getStatus(), OperationStatus.PENDING);

    }

    @Test
    public void testCreateAndRetrieveBulkOperation() throws Exception {
        // given
        ManagedObject deviceGroup = createDeviceGroup();
        Date startDate = new Date(System.currentTimeMillis() + 500);
        BulkOperation bulkOperation = createBulkOperation(deviceGroup, startDate);

        // when
        BulkOperation createdBulkOperation = deviceControlApi.create(bulkOperation);

        // then
        assertNotNull(createdBulkOperation);
        assertNotNull(createdBulkOperation.getId());

        // when
        BulkOperation retrievedBulkOperation = deviceControlApi.getBulkOperation(createdBulkOperation.getId());

        // then
        assertEquals(retrievedBulkOperation.getId(), createdBulkOperation.getId());
        assertEquals(retrievedBulkOperation.getCreationRamp().intValue(), 1);
        assertEquals(retrievedBulkOperation.getGroupId(), deviceGroup.getId());
        assertNotNull(retrievedBulkOperation.getOperation().get("com_telekom_m2m_cotcommand"));
        assertNotNull(retrievedBulkOperation.getProgress());
        assertEquals(retrievedBulkOperation.getProgress().getNumberOfDevices().intValue(), 1);
        assertEquals(retrievedBulkOperation.getProgress().getNumberOfSuccessfulDevices().intValue(), 0);
        assertEquals(retrievedBulkOperation.getProgress().getNumberOfPendingDevices().intValue(), 0);
        assertEquals(retrievedBulkOperation.getProgress().getNumberOfFailedDevices().intValue(), 0);
        assertEquals(retrievedBulkOperation.getProgress().getNumberOfExecutingDevices().intValue(), 0);
        assertEquals(retrievedBulkOperation.getStartDate().getTime(), startDate.getTime());
        assertEquals(retrievedBulkOperation.getStatus(), BulkOperation.STATUS_ACTIVE);
        assertNotNull(retrievedBulkOperation.get("self"));

        // when we wait until bulk operation starts
        Thread.sleep(500);
        retrievedBulkOperation = deviceControlApi.getBulkOperation(createdBulkOperation.getId());

        // then
        assertEquals(retrievedBulkOperation.getProgress().getNumberOfDevices().intValue(), 1);
        assertEquals(retrievedBulkOperation.getProgress().getNumberOfSuccessfulDevices().intValue(), 0);
        assertEquals(retrievedBulkOperation.getProgress().getNumberOfPendingDevices().intValue(), 1);
        assertEquals(retrievedBulkOperation.getProgress().getNumberOfFailedDevices().intValue(), 0);
        assertEquals(retrievedBulkOperation.getProgress().getNumberOfExecutingDevices().intValue(), 0);
        assertEquals(retrievedBulkOperation.getStatus(), BulkOperation.STATUS_IN_PROGRESS);

        // when we wail until bulk operation completes
        Thread.sleep(1000);
        retrievedBulkOperation = deviceControlApi.getBulkOperation(createdBulkOperation.getId());

        // then
        assertEquals(retrievedBulkOperation.getProgress().getNumberOfDevices().intValue(), 1);
        assertEquals(retrievedBulkOperation.getProgress().getNumberOfSuccessfulDevices().intValue(), 0);
        assertEquals(retrievedBulkOperation.getProgress().getNumberOfPendingDevices().intValue(), 1);
        assertEquals(retrievedBulkOperation.getProgress().getNumberOfFailedDevices().intValue(), 0);
        assertEquals(retrievedBulkOperation.getProgress().getNumberOfExecutingDevices().intValue(), 0);
        assertEquals(retrievedBulkOperation.getStatus(), BulkOperation.STATUS_COMPLETED);

        // cleanup
        InventoryApi inventoryApi = cotPlat.getInventoryApi();
        inventoryApi.delete(deviceGroup.getId());
    }

    @Test
    public void testUpdateAndDeleteActiveBulkOperation() {
        // given
        ManagedObject deviceGroup = createDeviceGroup();
        Date startDate = new Date(System.currentTimeMillis() + 500000);
        BulkOperation bulkOperation = createBulkOperation(deviceGroup, startDate);

        BulkOperation createdBulkOperation = deviceControlApi.create(bulkOperation);

        // when
        Date changedStartDate = new Date(System.currentTimeMillis() + 24*60*60*1000);
        createdBulkOperation.setStartDate(changedStartDate);
        createdBulkOperation.setCreationRamp(33);
        deviceControlApi.update(createdBulkOperation);
        BulkOperation retrievedBulkOperation = deviceControlApi.getBulkOperation(createdBulkOperation.getId());

        // then the updated bulkOperation gets a new Id, that's why there is no more bulkOperation with old Id
        assertNull(retrievedBulkOperation);

        // when we try to get createdBulkOperation with next id
        retrievedBulkOperation = deviceControlApi.getBulkOperation(String.valueOf(Integer.parseInt(createdBulkOperation.getId())+1));

        // then we could get a bulkOperation but we can not make sure that that's the updated bulkOperation
        assertNotNull(retrievedBulkOperation.getId());
        // so this assertion can fail when in the meantime another bulkOperation was created
        assertEquals(retrievedBulkOperation.getCreationRamp().intValue(), 33);
        assertEquals(retrievedBulkOperation.getStartDate().compareTo(changedStartDate), 0);

        // when
        deviceControlApi.deleteBulkOperation(retrievedBulkOperation.getId());
        BulkOperation retrievedAgainBulkOperation = deviceControlApi.getBulkOperation(retrievedBulkOperation.getId());

        // then
        assertNotNull(retrievedAgainBulkOperation);
        assertEquals(retrievedAgainBulkOperation.getStatus(), BulkOperation.STATUS_DELETED);

        // cleanup
        InventoryApi inventoryApi = cotPlat.getInventoryApi();
        inventoryApi.delete(deviceGroup.getId());
    }

    // This test creates two operations and a subscriber. The subscriber then
    // subscribes to these operations successively. The tests checks whether the
    // subscriber received the correct notification from each operations.
    @Test
    public void testNotificationConnector() throws InterruptedException {

        // given
        connector = deviceControlApi.getNotificationConnector();

        final List<String> notedOperations = new ArrayList<>();

        connector.subscribe("/" + testManagedObject.getId());
        connector.addListener(new SubscriptionListener() {
            @Override
            public void onNotification(String channel, Notification notification) {
                notedOperations.add(notification.getData().toString());
            }

            @Override
            public void onError(String channel, Throwable error) {
                fail("There should have been no errors.");
            }
        });

        // Connect, starting background listener:
        connector.connect();

        // when
        Thread.sleep(DELAY_MILLIS);
        deviceControlApi.create(createOperation("first_operation_attribute"));

        Thread.sleep(DELAY_MILLIS);

        deviceControlApi.create(createOperation("second_operation_attribute"));

        Thread.sleep(DELAY_MILLIS);

        // then
        assertEquals(notedOperations.size(), 2);

        assertTrue(notedOperations.get(0).contains("first_operation_attribute"));
        assertTrue(notedOperations.get(1).contains("second_operation_attribute"));
    }

    @Test
    public void testInjectionWithQuotes() {
        Operation operation = createOperation("name");
        Operation createdOperation = deviceControlApi.create(operation);

        createdOperation.setStatus(OperationStatus.FAILED);
        createdOperation.setFailureReason("\"AN ERROR HAS OCCURED\"");

        Operation testOperation = deviceControlApi.update(createdOperation);

        assertNotNull(testOperation);
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

    private Operation createOperation(String operationName) {
        JsonObject parameters = new JsonObject();
        parameters.add("param1", new JsonPrimitive("1"));

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("name", new JsonPrimitive("example"));
        jsonObject.add("parameters", parameters);

        Operation operation = new Operation();
        operation.setDeviceId(testManagedObject.getId());
        operation.set(operationName, jsonObject);

        return operation;
    }

    private BulkOperation createBulkOperation(ManagedObject deviceGroup, Date startDate) {
        Operation operation = createOperation("com_telekom_m2m_cotcommand");
        Operation createdOperation = deviceControlApi.create(operation);
        Operation retrievedOperation = deviceControlApi.getOperation(createdOperation.getId());

        BulkOperation bulkOperation = new BulkOperation();

        bulkOperation.setGroupId(deviceGroup.getId());
        bulkOperation.setStartDate(startDate);
        bulkOperation.setCreationRamp(1);
        bulkOperation.setOperation(retrievedOperation);

        return bulkOperation;
    }

}
