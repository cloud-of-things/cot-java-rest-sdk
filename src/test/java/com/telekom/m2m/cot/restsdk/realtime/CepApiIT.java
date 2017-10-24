package com.telekom.m2m.cot.restsdk.realtime;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.telekom.m2m.cot.restsdk.devicecontrol.OperationStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.alarm.Alarm;
import com.telekom.m2m.cot.restsdk.alarm.AlarmApi;
import com.telekom.m2m.cot.restsdk.devicecontrol.DeviceControlApi;
import com.telekom.m2m.cot.restsdk.devicecontrol.Operation;
import com.telekom.m2m.cot.restsdk.event.Event;
import com.telekom.m2m.cot.restsdk.event.EventApi;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.measurement.Measurement;
import com.telekom.m2m.cot.restsdk.measurement.MeasurementApi;
import com.telekom.m2m.cot.restsdk.util.TestHelper;


public class CepApiIT {

    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME,
            TestHelper.TEST_PASSWORD);

    private CepApi cepApi = cotPlat.getCepApi();
    private AlarmApi alarmApi = cotPlat.getAlarmApi();
    private EventApi eventApi = cotPlat.getEventApi();
    private MeasurementApi meaApi = cotPlat.getMeasurementApi();
    private DeviceControlApi devApi = cotPlat.getDeviceControlApi();
    private InventoryApi invApi = cotPlat.getInventoryApi();

    private ManagedObject testObjectForOperation;
    private ManagedObject testObjectForEvent;
    private ManagedObject testObjectForMeasurement;
    private ManagedObject testManagedObject;
    private ManagedObject alarmSource1;
    private ManagedObject alarmSource2;
    private ManagedObject alarmSource3;
    private ManagedObject testObjectForInventory1;
    private ManagedObject testObjectForInventory2;
    private ManagedObject testObjectForCreateUpdateDelete;

    private static final int DELAY_MILLIS = 150;

    CepConnector connector;


    @AfterMethod
    public void tearDown() {
        if (connector != null) {
            connector.disconnect();
        }
    }

    @BeforeClass
    public void setUpClass() {
        testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name");
        alarmSource1 = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name1");
        alarmSource2 = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name2");
        alarmSource3 = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name3");
        testObjectForEvent = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_event_name");
        testObjectForMeasurement = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_measurement_name");
        testObjectForOperation = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_operation_name");
        testObjectForInventory1 = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_object_name1");
        testObjectForInventory2 = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_object_name2");
        testObjectForCreateUpdateDelete = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_object_for_createUpdateDelete");
    }

    @AfterClass
    public void tearDownClass() {
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
        TestHelper.deleteManagedObjectInPlatform(cotPlat, alarmSource1);
        TestHelper.deleteManagedObjectInPlatform(cotPlat, alarmSource2);
        TestHelper.deleteManagedObjectInPlatform(cotPlat, alarmSource3);
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testObjectForEvent);
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testObjectForMeasurement);
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testObjectForOperation);
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testObjectForInventory1);
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testObjectForInventory2);
    }


    // This test will create one subscriber, then creates two alarms (for the
    // same device/channel), then check if
    // the listener got the correct notification for each of them.
    @Test
    public void testSimpleRealTimeAlarms() throws InterruptedException {

        connector = cepApi.getCepConnector();

        final List<String> notedAlarms = new ArrayList<>();

        // Create the subscriptions and listeners:
        connector.subscribe("/alarms/" + testManagedObject.getId());
        connector.addListener(new SubscriptionListener() {
            @Override
            public void onNotification(String channel, Notification notification) {
                notedAlarms.add(notification.getData().toString());
            }

            @Override
            public void onError(String channel, Throwable error) {
                fail("There should have been no errors.");
            }
        });

        // Connect, starting background listener:
        connector.connect();

        Thread.sleep(DELAY_MILLIS);
        alarmApi.create(makeAlarm("com_telekom_TestType1", Alarm.SEVERITY_MINOR, testManagedObject));

        Thread.sleep(DELAY_MILLIS);

        // We need a different type, otherwise our alarm would be de-duplicated
        // by the server (just increasing
        // the count of the first one, but not updating the content):

        alarmApi.create(makeAlarm("com_telekom_TestType2", Alarm.SEVERITY_MINOR, testManagedObject));

        Thread.sleep(DELAY_MILLIS);

        assertEquals(notedAlarms.size(), 2);

        assertTrue(notedAlarms.get(0).contains(testManagedObject.getId()));
        assertTrue(notedAlarms.get(1).contains(testManagedObject.getId()));

    }

    // This test switches subscriptions between two alarm sources and checks
    // that at each time only the
    // currently subscribed notifications are received.
    @Test
    public void testSubUnsub() throws InterruptedException {

        connector = cepApi.getCepConnector();

        // Prepare initial subscription:
        String channel1 = "/alarms/" + alarmSource1.getId();
        connector.subscribe(channel1);

        // The asynchronously received alarms will be stored in this list:
        final List<String> notedAlarms = new ArrayList<>();

        // Prepare initial subscription and listener for the channel of our test
        // device:
        // Prepare listener for the channel of our test device:
        connector.addListener(new SubscriptionListener() {
            @Override
            public void onNotification(String channel, Notification notification) {
                notedAlarms.add(notification.getData().toString());
            }

            @Override
            public void onError(String channel, Throwable error) {
                fail("There should have been no errors.");
            }
        });

        // This alarm, created before we connect & subscribe, should never cause
        // a notification for us, even
        // though it has a source to which we will subscribe and receive
        // notifications later.
        // The queueing starts with the subscription and continues over
        // individual connect-response-cycles.
        // That's why it is possible to reconnect without losing data in the
        // meantime.
        // (It can't continue over new handshakes though (i.e. new clientId))
        alarmApi.create(makeAlarm("com_telekom_TestType0", Alarm.SEVERITY_MINOR, alarmSource1));

        // Connect, starting background listener:
        connector.connect();

        Thread.sleep(DELAY_MILLIS);

        alarmApi.create(makeAlarm("com_telekom_TestType1", Alarm.SEVERITY_MINOR, alarmSource1));
        alarmApi.create(makeAlarm("com_telekom_TestType2", Alarm.SEVERITY_MINOR, alarmSource2));

        Thread.sleep(DELAY_MILLIS);

        // Initially we were subscribed to alarmSource1 only.
        assertEquals(notedAlarms.size(), 1);
        assertTrue(notedAlarms.get(0).contains("com_telekom_TestType1"));

        // Now we reverse the subscriptions, from alarmSource1 to alarmSource2,
        // and we do it twice, to verify that
        // unnecessary sub-/unsub-requests don't cause problems:
        connector.unsubscribe(channel1);
        connector.unsubscribe(channel1);
        String channel2 = "/alarms/" + alarmSource2.getId();
        connector.subscribe(channel2);
        connector.subscribe(channel2);

        Thread.sleep(DELAY_MILLIS);

        // At this time we should be subscribed to alarmSource2 only:
        alarmApi.create(makeAlarm("com_telekom_TestType3", Alarm.SEVERITY_MINOR, alarmSource1));
        alarmApi.create(makeAlarm("com_telekom_TestType4", Alarm.SEVERITY_MINOR, alarmSource2));

        Thread.sleep(DELAY_MILLIS);

        assertEquals(notedAlarms.size(), 2);
        assertTrue(notedAlarms.get(1).contains("com_telekom_TestType4"));

        // Now we switch the subscriptions again, back to alarmSource1:
        connector.unsubscribe(channel2);
        connector.subscribe("/alarms/" + alarmSource1.getId());

        // At this time we should be subscribed to alarmSource1 again, but the
        // alarm com_telekom_TestType3, that was
        // created while we were _not_ subscribed to it's source, will be lost.
        alarmApi.create(makeAlarm("com_telekom_TestType5", Alarm.SEVERITY_MINOR, alarmSource1));
        alarmApi.create(makeAlarm("com_telekom_TestType6", Alarm.SEVERITY_MINOR, alarmSource2));

        Thread.sleep(DELAY_MILLIS);

        assertEquals(notedAlarms.size(), 3);
        assertTrue(notedAlarms.get(2).contains("com_telekom_TestType5"));
    }

    @Test
    public void testMultiChannels() throws InterruptedException {

        connector = cepApi.getCepConnector();

        // Prepare subscriptions for sources 1 and 2, but not for 3:
        connector.subscribe("/alarms/" + alarmSource1.getId());
        connector.subscribe("/alarms/" + alarmSource2.getId());

        // The asynchronously received alarms will be stored in this list:
        final List<String> notedAlarms = new ArrayList<>();

        // Prepare listener for the channel of our test device:
        connector.addListener(new SubscriptionListener() {
            @Override
            public void onNotification(String channel, Notification notification) {
                notedAlarms.add(notification.getData().toString());
            }

            @Override
            public void onError(String channel, Throwable error) {
                fail("There should have been no errors.");
            }
        });

        // Connect, starting background listener:
        connector.connect();

        Thread.sleep(DELAY_MILLIS);

        // Create three alarms, but we are subscribed to only the first two.
        Alarm alarm1 = makeAlarm("com_telekom_TestType1", Alarm.SEVERITY_MINOR, alarmSource1);
        alarmApi.create(alarm1);
        Alarm alarm2 = makeAlarm("com_telekom_TestType2", Alarm.SEVERITY_MINOR, alarmSource2);
        alarmApi.create(alarm2);
        Alarm alarm3 = makeAlarm("com_telekom_TestType3", Alarm.SEVERITY_MINOR, alarmSource3);
        alarmApi.create(alarm3);

        Thread.sleep(DELAY_MILLIS);

        assertEquals(notedAlarms.size(), 2);
        // The data should contain the following fields:
        assertTrue(notedAlarms.get(0).contains("com_telekom_TestType1"));
        assertTrue(notedAlarms.get(1).contains("com_telekom_TestType2"));
    }

    // This test creates two events and a subscriber. The subscriber then
    // subscribes to these events successively. The tests checks whether the
    // subscriber received the correct notification from each event.
    @Test
    public void testSimpleRealTimeEvents() throws InterruptedException {

        connector = cepApi.getCepConnector();

        final List<String> notedEvents = new ArrayList<>();

        connector.subscribe("/events/" + testObjectForEvent.getId());
        connector.addListener(new SubscriptionListener() {
            @Override
            public void onNotification(String channel, Notification notification) {
                notedEvents.add(notification.getData().toString());
            }

            @Override
            public void onError(String channel, Throwable error) {
                fail("There should have been no errors.");
            }
        });

        // Connect, starting background listener:
        connector.connect();

        Thread.sleep(DELAY_MILLIS);
        Event event1 = makeEvent("com_telekom_TestType1", testObjectForEvent);
        eventApi.createEvent(event1);

        Thread.sleep(DELAY_MILLIS);

        Event event2 = makeEvent("com_telekom_TestType2", testObjectForEvent);
        eventApi.createEvent(event2);

        Thread.sleep(DELAY_MILLIS);

        assertEquals(notedEvents.size(), 2);

        assertTrue(notedEvents.get(0).contains(testObjectForEvent.getId()));
        assertTrue(notedEvents.get(1).contains(testObjectForEvent.getId()));

        // when
        eventApi.deleteEvent(event1);
        eventApi.deleteEvent(event2);

        Thread.sleep(DELAY_MILLIS);

        // then
        assertEquals(notedEvents.size(), 4);
        assertTrue(notedEvents.get(2).contains(testObjectForEvent.getId()));
        assertTrue(notedEvents.get(2).contains(event1.getId()));
        assertTrue(notedEvents.get(2).contains("DELETE"));
        assertTrue(notedEvents.get(3).contains(testObjectForEvent.getId()));
        assertTrue(notedEvents.get(3).contains(event2.getId()));
        assertTrue(notedEvents.get(3).contains("DELETE"));
    }

    // This test creates two measurements and a subscriber. The subscriber then
    // subscribes to these measurements successively. The tests checks whether
    // the
    // subscriber received the correct notification from each measurement.
    @Test
    public void testSimpleRealTimeMeasurements() throws InterruptedException {

        connector = cepApi.getCepConnector();

        final List<String> notedMeasurements = new ArrayList<>();

        connector.subscribe("/measurements/" + testObjectForMeasurement.getId());
        connector.addListener(new SubscriptionListener() {
            @Override
            public void onNotification(String channel, Notification notification) {
                notedMeasurements.add(notification.getData().toString());
            }

            @Override
            public void onError(String channel, Throwable error) {
                fail("There should have been no errors.");
            }
        });

        // Connect, starting background listener:
        connector.connect();

        Thread.sleep(DELAY_MILLIS);
        Measurement measurement1 = makeMeasurement("com_telekom_TestType01", testObjectForMeasurement);
        meaApi.createMeasurement(measurement1);

        Thread.sleep(DELAY_MILLIS);

        Measurement measurement2 = makeMeasurement("com_telekom_TestType02", testObjectForMeasurement);
        meaApi.createMeasurement(measurement2);

        Thread.sleep(DELAY_MILLIS);

        assertEquals(notedMeasurements.size(), 2);

        assertTrue(notedMeasurements.get(0).contains(testObjectForMeasurement.getId()));
        assertTrue(notedMeasurements.get(1).contains(testObjectForMeasurement.getId()));

        // when
        meaApi.delete(measurement1);
        meaApi.delete(measurement2);

        Thread.sleep(DELAY_MILLIS);

        // then
        assertEquals(notedMeasurements.size(), 4);
        assertTrue(notedMeasurements.get(2).contains(testObjectForMeasurement.getId()));
        assertTrue(notedMeasurements.get(2).contains(measurement1.getId()));
        assertTrue(notedMeasurements.get(2).contains("DELETE"));
        assertTrue(notedMeasurements.get(3).contains(testObjectForMeasurement.getId()));
        assertTrue(notedMeasurements.get(3).contains(measurement2.getId()));
        assertTrue(notedMeasurements.get(3).contains("DELETE"));
    }

    // This test creates two operations and a subscriber. The subscriber then
    // subscribes to these operations successively. The tests checks whether the
    // subscriber received the correct notification from each operations.
    @Test
    public void testSimpleRealTimeOperation() throws InterruptedException {

        connector = cepApi.getCepConnector();

        final List<String> notedOperations = new ArrayList<>();

        connector.subscribe("/operations/" + testObjectForOperation.getId());
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

        Thread.sleep(DELAY_MILLIS);
        devApi.create(makeOperation("first_operation_attribute", testObjectForOperation));

        Thread.sleep(DELAY_MILLIS);

        devApi.create(makeOperation("second_operation_attribute", testObjectForOperation));

        Thread.sleep(DELAY_MILLIS);

        assertEquals(notedOperations.size(), 2);

        assertTrue(notedOperations.get(0).contains("first_operation_attribute"));
        assertTrue(notedOperations.get(1).contains("second_operation_attribute"));

    }

    // This test will create one subscriber, and an operation. It will check if a
    // notification is received upon creation, update and deletion of the operation.
    @Test
    public void testNotificationsForCreateUpdateDeleteOperation() throws InterruptedException {

        connector = cepApi.getCepConnector();

        final List<String> notedOperations = new ArrayList<>();

        // Create the subscriptions and listeners:
        connector.subscribe("/operations/" + testObjectForOperation.getId());
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

        Thread.sleep(DELAY_MILLIS);
        Operation operation = devApi.create(makeOperation("first_operation_attribute", testObjectForOperation));

        Thread.sleep(DELAY_MILLIS);

        // Now test if we got a notification for creation of the operation:
        assertEquals(notedOperations.size(), 1);
        assertTrue(notedOperations.get(0).contains(testObjectForOperation.getId()));
        assertTrue(notedOperations.get(0).contains("CREATE"));

        // Now let's update the operation and see if we will get notified:
        operation.setStatus(OperationStatus.EXECUTING);
        devApi.update(operation);

        Thread.sleep(DELAY_MILLIS);

        assertEquals(notedOperations.size(), 2);

        assertTrue(notedOperations.get(1).contains(testObjectForOperation.getId()));
        assertTrue(notedOperations.get(1).contains("UPDATE"));
        assertTrue(notedOperations.get(1).contains(OperationStatus.EXECUTING.toString()));

    }

    // This test creates two inventory objects and a subscriber. The subscriber then
    // subscribes to these objects successively. The tests checks whether the
    // subscriber received the correct notification from each object.
    @Test
    public void testSimpleRealTimeInventoryObjects() throws InterruptedException {

        connector = cepApi.getCepConnector();

        final List<String> notedInventoryObjects = new ArrayList<>();

        connector.subscribe("/managedobjects/" + testObjectForInventory1.getId());
        connector.subscribe("/managedobjects/" + testObjectForInventory2.getId());

        connector.addListener(new SubscriptionListener() {
            @Override
            public void onNotification(String channel, Notification notification) {
                notedInventoryObjects.add(notification.getData().toString());
            }

            @Override
            public void onError(String channel, Throwable error) {
                fail("There should have been no errors.");
            }
        });

        // Connect, starting background listener:
        connector.connect();

        Thread.sleep(DELAY_MILLIS);
        invApi.update(prepareForUpdate("first_test_object", "cot_managed_object1", testObjectForInventory1));

        Thread.sleep(DELAY_MILLIS);

        invApi.update(prepareForUpdate("second_test_object", "cot_managed_object2", testObjectForInventory2));

        Thread.sleep(DELAY_MILLIS);

        assertEquals(notedInventoryObjects.size(), 2);

        assertTrue(notedInventoryObjects.get(0).contains("first_test_object"));
        assertTrue(notedInventoryObjects.get(1).contains("second_test_object"));
        assertTrue(notedInventoryObjects.get(0).contains("cot_managed_object1"));
        assertTrue(notedInventoryObjects.get(1).contains("cot_managed_object2"));

        assertFalse(notedInventoryObjects.get(1).contains("first_test_object"));
        assertFalse(notedInventoryObjects.get(0).contains("second_test_object"));
        assertFalse(notedInventoryObjects.get(1).contains("cot_managed_object1"));
        assertFalse(notedInventoryObjects.get(0).contains("cot_managed_object2"));
    }

    
    // This test will create one subscriber, and an inventory object. It will check if a
    // notification is received upon update and deletion of the managed object.
    @Test
    public void testNotificationsForUpdateDelete() throws InterruptedException {

        connector = cepApi.getCepConnector();

        final List<String> notedInventoryObjects = new ArrayList<>();

        connector.subscribe("/managedobjects/" + testObjectForCreateUpdateDelete.getId());

        connector.addListener(new SubscriptionListener() {
            @Override
            public void onNotification(String channel, Notification notification) {
                notedInventoryObjects.add(notification.getData().toString());
            }

            @Override
            public void onError(String channel, Throwable error) {
                fail("There should have been no errors.");
            }
        });

        // Connect, starting background listener:
        connector.connect();

        //Now let's update the object and see if we receive a notification:
        
        Thread.sleep(DELAY_MILLIS);
        invApi.update(prepareForUpdate("first_test_object", "cot_managed_object1", testObjectForCreateUpdateDelete));

        Thread.sleep(DELAY_MILLIS);


        // Now let's update the alarm and see if we will get notified:
        assertEquals(notedInventoryObjects.size(), 1);
        assertTrue(notedInventoryObjects.get(0).contains("UPDATE"));

        Thread.sleep(DELAY_MILLIS);

        // Now let's delete the object and see what happens:
        invApi.delete(testObjectForCreateUpdateDelete.getId());
        
        Thread.sleep(DELAY_MILLIS);

        assertEquals(notedInventoryObjects.size(), 2);
        assertTrue(notedInventoryObjects.get(1).contains("DELETE"));
        
    }
    
    


    private Alarm makeAlarm(String type, String severity, ManagedObject source) {
        Alarm alarm = new Alarm();
        alarm.setText("Strange thing happened!");
        alarm.setTime(new Date());
        alarm.setSource(source);
        alarm.set("foo", "{ \"alt\": " + Math.random() + ", \"lng\": 8.55436, \"lat\": 50.02868 }");
        alarm.setStatus(Alarm.STATE_ACTIVE);
        alarm.setType(type);
        alarm.setSeverity(severity);
        return alarm;
    }

    private Event makeEvent(String type, ManagedObject source) {
        Event event = new Event();
        event.setText("This is a test event.");
        event.setTime(new Date());
        event.setSource(source);
        event.set("foo", "{ \"alt\": " + Math.random() + ", \"lng\": 8.55436, \"lat\": 50.02868 }");
        event.setType(type);
        return event;
    }

    private Measurement makeMeasurement(String type, ManagedObject source) {
        Measurement mea = new Measurement();
        mea.setTime(new Date());
        mea.setSource(source);
        mea.set("foo", "{ \"alt\": " + Math.random() + ", \"lng\": 8.55436, \"lat\": 50.02868 }");
        mea.setType(type);
        return mea;
    }

    private Operation makeOperation(String attribute, ManagedObject source) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("name", new JsonPrimitive("example"));
        Operation operation = new Operation();
        operation.setDeviceId(source.getId());
        operation.set(attribute, jsonObject);
        return operation;

    }

    private ManagedObject prepareForUpdate(String name, String type, ManagedObject source) {
        ManagedObject obj = new ManagedObject();
        obj.setId(source.getId());
        obj.setName(name);
        obj.setType(type);
        return obj;

    }
}
