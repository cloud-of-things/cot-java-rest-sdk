package com.telekom.m2m.cot.restsdk.realtime;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.alarm.Alarm;
import com.telekom.m2m.cot.restsdk.alarm.AlarmApi;
import com.telekom.m2m.cot.restsdk.event.Event;
import com.telekom.m2m.cot.restsdk.event.EventApi;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.TestHelper;

public class CepApiIT {

    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME,
            TestHelper.TEST_PASSWORD);

    private CepApi cepApi = cotPlat.getCepApi();
    private AlarmApi alarmApi = cotPlat.getAlarmApi();
    private EventApi eventApi = cotPlat.getEventApi();
    private ManagedObject testObjectForEvent;

    private ManagedObject testManagedObject;

    private ManagedObject alarmSource1;
    private ManagedObject alarmSource2;
    private ManagedObject alarmSource3;

    private static final int DELAY_MILLIS = 100;

    @BeforeClass
    public void setUpClass() {
        testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name");
        alarmSource1 = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name1");
        alarmSource2 = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name2");
        alarmSource3 = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name3");
        testObjectForEvent = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_event_name");

    }

    @AfterClass
    public void tearDownClass() {
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
        alarmSource1 = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name1");
        alarmSource2 = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name2");
        alarmSource3 = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name3");
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testObjectForEvent);

    }

    // This test will create one subscriber, then create two alarms (for the
    // same device/channel), then check if
    // the listener got the correct notification for each of them.
    @Test
    public void testSimpleRealTimeAlarms() throws InterruptedException {

        CepConnector connector = cepApi.getCepConnector();

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

        CepConnector connector = cepApi.getCepConnector();

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
        // (It cann't continue over new handshakes though (i.e. new clientId))
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

        CepConnector connector = cepApi.getCepConnector();

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

        CepConnector connector = cepApi.getCepConnector();

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
        eventApi.createEvent(makeEvent("com_telekom_TestType1", testObjectForEvent));

        Thread.sleep(DELAY_MILLIS);

        eventApi.createEvent(makeEvent("com_telekom_TestType2", testObjectForEvent));

        Thread.sleep(DELAY_MILLIS);

        assertEquals(notedEvents.size(), 2);

        assertTrue(notedEvents.get(0).contains(testObjectForEvent.getId()));
        assertTrue(notedEvents.get(1).contains(testObjectForEvent.getId()));

    }

    @Test
    public void testGenericCepApiMethods() throws InterruptedException {

        // given (testing getModules)
        ModuleCollection collection = cepApi.getModules();

        // TODO: the below tests does not work yet, as currently our user cannot
        // access /cep or /cep/modules.

        // when:
        /*
         * Module[] arrayOfModules = collection.getModules();
         * 
         * // then: assertTrue(arrayOfModules.length > 1,
         * "There must be quite many of modules.");
         * 
         * // We demanded the moduleCollection (arrayOfModules) from the cloud
         * // without filters which means that the arrayOfModules should contain
         * // all modules in the cloud. Now let's access one of the modules in
         * the // array:
         * 
         * Module module = arrayOfModules[0]; String id=module.getId(); //Now,
         * let's try to retrieve the same module individually from the cloud
         * using the getModule() method. Then we can compare both modules. This
         * way we can prove both methods (getModule() and getModules()) at the
         * same time. Module ModuleFromCloud =cepApi.getModule(id);
         * 
         * 
         * //Now let's compare some of their fields:
         * assertEquals(ModuleFromCloud.getName(), module.getName(),
         * "The module in the collection should be same as the module retrieved individually."
         * ); assertEquals(ModuleFromCloud.getId(), module.getId(),
         * "The module in the collection should be same as the module retrieved individually."
         * ); assertEquals(ModuleFromCloud.getStatus(), module.getStatus(),
         * "The module in the collection should be same as the module retrieved individually."
         * );
         */
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

}
