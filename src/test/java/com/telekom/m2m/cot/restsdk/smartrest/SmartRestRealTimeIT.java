package com.telekom.m2m.cot.restsdk.smartrest;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.alarm.Alarm;
import com.telekom.m2m.cot.restsdk.alarm.AlarmApi;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;


public class SmartRestRealTimeIT {

    // This delay (i.e. how long we wait for the server to process alarms and send notifications) seems to work,
    // but of course cannot be 100% reliable for a real integration test:
    private static final int DELAY_MILLIS = 100;

    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

    private SmartRestApi smartRestApi = cotPlat.getSmartRestApi();
    private AlarmApi alarmApi = cotPlat.getAlarmApi();

    private ManagedObject alarmSource1;
    private ManagedObject alarmSource2;
    private ManagedObject alarmSource3;

    private String gId = null;
    private String xId = null;


    @BeforeMethod
    public void setUp() {
        xId = "test-xId" + System.currentTimeMillis();
        alarmSource1 = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name1");
        alarmSource2 = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name2");
        alarmSource3 = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name3");
    }

    @AfterMethod
    public void tearDown() {
        if (gId != null) {
            cotPlat.getInventoryApi().delete(gId);
            gId = null;
        }
        TestHelper.deleteManagedObjectInPlatform(cotPlat, alarmSource1);
        TestHelper.deleteManagedObjectInPlatform(cotPlat, alarmSource2);
        TestHelper.deleteManagedObjectInPlatform(cotPlat, alarmSource3);
    }


    // This test will create one subscriber, then create two alarms (for the same device/channel), then check if
    // the listener got the correct notification for each of them.
    @Test
    public void testMultiAlarms() throws InterruptedException {
        // Create template to parse the notification:
        SmartResponseTemplate responseTemplate = new SmartResponseTemplate(
                "102", "$", "$", new String[]{"$.id", "$.text", "$.type", "$.severity", "$.count"});
        gId = smartRestApi.storeTemplates(
                xId,
                new SmartRequestTemplate[0],
                new SmartResponseTemplate[]{responseTemplate});

        SmartCepConnector connector = smartRestApi.getNotificationsConnector(xId);

        // Prepare subscription:
        connector.subscribe("/alarms/" + alarmSource1.getId(), null);

        // The asynchronously received alarms will be stored in this list:
        final List<String> notedAlarms = new ArrayList<>();

        // Prepare listener for the channel of our test device:
        connector.addListener(new SmartListener() {
            @Override
            public void onNotification(SmartNotification notification) {
                notedAlarms.add(notification.getData());
            }

            @Override
            public void onError(Throwable error) {
                fail("There should have been no errors.");
            }
        });

        // Connect, starting background listener:
        connector.connect();

        Thread.sleep(DELAY_MILLIS);

        Alarm alarm = makeAlarm("com_telekom_TestType1", Alarm.SEVERITY_MINOR, alarmSource1);
        alarmApi.create(alarm);

        Thread.sleep(DELAY_MILLIS);

        // We need a different type, otherwise our alarm would be de-duplicated by the server (just increasing
        // the count of the first one, but not updating the content):
        alarm = makeAlarm("com_telekom_TestType2", Alarm.SEVERITY_MAJOR, alarmSource1);
        alarmApi.create(alarm);

        Thread.sleep(DELAY_MILLIS);

        assertEquals(notedAlarms.size(), 2);
        // According to our SmartResponseTemplate the data should end with the severity and the count:
        assertTrue(notedAlarms.get(0).endsWith("MINOR,1"), "Unexpected data at [0]: "+notedAlarms.get(0));
        assertTrue(notedAlarms.get(1).endsWith("MAJOR,1"), "Unexpected data at [1]: "+notedAlarms.get(1));
    }



    // This test switches subscriptions between two alarm sources and checks that at each time only the
    // currently subscribed notifications are received.
    @Test
    public void testSubUnsub() throws InterruptedException {
        // Create template to parse the notification:
        SmartResponseTemplate responseTemplate = new SmartResponseTemplate(
                "102", "$", "$", new String[]{"$.type"});
        gId = smartRestApi.storeTemplates(
                xId,
                new SmartRequestTemplate[0],
                new SmartResponseTemplate[]{responseTemplate});

        SmartCepConnector connector = smartRestApi.getNotificationsConnector(xId);

        // Prepare initial subscription:
        connector.subscribe("/alarms/" + alarmSource1.getId(), null);

        // The asynchronously received alarms will be stored in this list:
        final List<String> notedAlarms = new ArrayList<>();

        // Prepare listener for the channel of our test device:
        connector.addListener(new SmartListener() {
            @Override
            public void onNotification(SmartNotification notification) {
                notedAlarms.add(notification.getData());
            }

            @Override
            public void onError(Throwable error) {
                fail("There should have been no errors.");
            }
        });

        // This alarm, created before we connect & subscribe, should never cause a notification for us, even
        // though it has a source to which we will subscribe and receive notifications later.
        // The queueing starts with the subscription and continues over individual connect-response-cycles.
        // That's why it is possible to reconnect without losing data in the meantime.
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
        assertTrue(notedAlarms.get(0).endsWith("com_telekom_TestType1"), "Unexpected data at [0]: "+notedAlarms.get(0));

        // Now we reverse the subscriptions, from alarmSource1 to alarmSource2, and we do it twice, to verify that
        // unnecessary sub-/unsub-requests don't cause problems:
        connector.unsubscribe("/alarms/" + alarmSource1.getId());
        connector.unsubscribe("/alarms/" + alarmSource1.getId());
        connector.subscribe("/alarms/" + alarmSource2.getId(), null);
        connector.subscribe("/alarms/" + alarmSource2.getId(), null);

        Thread.sleep(DELAY_MILLIS);

        // At this time we should be subscribed to alarmSource2 only:
        alarmApi.create(makeAlarm("com_telekom_TestType3", Alarm.SEVERITY_MINOR, alarmSource1));
        alarmApi.create(makeAlarm("com_telekom_TestType4", Alarm.SEVERITY_MINOR, alarmSource2));

        Thread.sleep(DELAY_MILLIS);

        assertEquals(notedAlarms.size(), 2);
        assertTrue(notedAlarms.get(1).endsWith("com_telekom_TestType4"), "Unexpected data at [1]: "+notedAlarms.get(1));

        // Now we switch the subscriptions again, back to alarmSource1:
        connector.unsubscribe("/alarms/" + alarmSource2.getId());
        connector.subscribe("/alarms/" + alarmSource1.getId(), null);

        // At this time we should be subscribed to alarmSource1 again, but the alarm com_telekom_TestType3, that was
        // created while we were _not_ subscribed to it's source, will be lost.
        alarmApi.create(makeAlarm("com_telekom_TestType5", Alarm.SEVERITY_MINOR, alarmSource1));
        alarmApi.create(makeAlarm("com_telekom_TestType6", Alarm.SEVERITY_MINOR, alarmSource2));

        Thread.sleep(DELAY_MILLIS);

        assertEquals(notedAlarms.size(), 3);
        assertTrue(notedAlarms.get(2).endsWith("com_telekom_TestType5"), "Unexpected data at [2]: "+notedAlarms.get(2));
    }


    @Test
    public void testMultiChannels() throws InterruptedException {
        // Create template to parse the notification:
        SmartResponseTemplate responseTemplate = new SmartResponseTemplate(
                "102", "$", "$", new String[]{"$.id", "$.text", "$.type", "$.severity", "$.count"});
        gId = smartRestApi.storeTemplates(
                xId,
                new SmartRequestTemplate[0],
                new SmartResponseTemplate[]{responseTemplate});

        SmartCepConnector connector = smartRestApi.getNotificationsConnector(xId);

        // Prepare subscriptions for sources 1 and 2, but not for 3:
        connector.subscribe("/alarms/" + alarmSource1.getId(), null);
        connector.subscribe("/alarms/" + alarmSource2.getId(), null);

        // The asynchronously received alarms will be stored in this list:
        final List<String> notedAlarms = new ArrayList<>();

        // Prepare listener for the channel of our test device:
        connector.addListener(new SmartListener() {
            @Override
            public void onNotification(SmartNotification notification) {
                notedAlarms.add(notification.getData());
            }

            @Override
            public void onError(Throwable error) {
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
        // According to our SmartResponseTemplate the data should end with the severity and the count:
        assertTrue(notedAlarms.get(0).endsWith("com_telekom_TestType1,MINOR,1"), "Unexpected data at [0]: "+notedAlarms.get(0));
        assertTrue(notedAlarms.get(1).endsWith("com_telekom_TestType2,MINOR,1"), "Unexpected data at [1]: "+notedAlarms.get(1));
    }


    @Test
    // TODO: turn into unittest
    public void testMultiListeners() throws InterruptedException {
    }

    @Test
    public void testMultiTemplates() throws InterruptedException {
        // Create templates to parse the notification:
        SmartResponseTemplate responseTemplate1 = new SmartResponseTemplate(
                "200", "$", "$", new String[]{"$.id", "$.text", "$.type", "$.severity", "$.count"});
        SmartResponseTemplate responseTemplate2 = new SmartResponseTemplate(
                "300", "$", "$", new String[]{"$.type", "$.id"});
        gId = smartRestApi.storeTemplates(
                xId,
                new SmartRequestTemplate[0],
                new SmartResponseTemplate[]{responseTemplate1, responseTemplate2});

        SmartCepConnector connector = smartRestApi.getNotificationsConnector(xId);

        // Prepare subscription:
        connector.subscribe("/alarms/" + alarmSource1.getId(), null);

        // The asynchronously received alarms will be stored in this list:
        final List<SmartNotification> notedAlarms = new ArrayList<>();

        // Prepare listener for the channel of our test device:
        connector.addListener(new SmartListener() {
            @Override
            public void onNotification(SmartNotification notification) {
                notedAlarms.add(notification);
            }

            @Override
            public void onError(Throwable error) {
                fail("There should have been no errors.");
            }
        });

        // Connect, starting background listener:
        connector.connect();

        Thread.sleep(DELAY_MILLIS);

        Alarm alarm = makeAlarm("com_telekom_TestType1", Alarm.SEVERITY_MINOR, alarmSource1);
        alarmApi.create(alarm);

        Thread.sleep(DELAY_MILLIS);

        assertEquals(notedAlarms.size(), 2);
        assertEquals(notedAlarms.get(0).getMessageId(), 200);
        assertEquals(notedAlarms.get(1).getMessageId(), 300);
        assertTrue(notedAlarms.get(0).getData().endsWith("MINOR,1"), "Unexpected data at [0]: "+notedAlarms.get(0).getData());
        // Note: the meaning of the first field (1) is unclear. Normally it should be the number of the request.
        //       But a real time connect request doesn't have multiple lines, so it might just always be 1 in this case.
        assertTrue(notedAlarms.get(1).getData().startsWith("1,com_telekom_TestType1"), "Unexpected data at [1]: "+notedAlarms.get(1).getData());
    }


    @Test
    public void testMultiXIds() {
// TODO
    }


    private Alarm makeAlarm(String type, String severity, ManagedObject source) {
        Alarm alarm = new Alarm();
        alarm.setText("Strange thing happened!");
        alarm.setTime(new Date());
        alarm.setSource(source);
        alarm.set("foo", "{ \"alt\": "+Math.random()+", \"lng\": 8.55436, \"lat\": 50.02868 }");
        alarm.setStatus(Alarm.STATE_ACTIVE);
        alarm.setType(type);
        alarm.setSeverity(severity);
        return alarm;
    }

}
