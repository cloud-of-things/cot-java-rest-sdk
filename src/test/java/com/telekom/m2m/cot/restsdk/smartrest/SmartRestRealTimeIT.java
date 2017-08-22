package com.telekom.m2m.cot.restsdk.smartrest;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.alarm.Alarm;
import com.telekom.m2m.cot.restsdk.alarm.AlarmApi;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;


public class SmartRestRealTimeIT {

    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

    private SmartRestApi smartRestApi = cotPlat.getSmartRestApi();
    private AlarmApi alarmApi = cotPlat.getAlarmApi();

    private ManagedObject testManagedObject;

    private String gId = null;
    private String xId = null;


    @BeforeMethod
    public void setUp() {
        xId = "test-xId" + System.currentTimeMillis();
    }

    @AfterMethod
    public void tearDown() {
        if (gId != null) {
            cotPlat.getInventoryApi().delete(gId);
        }
    }

    @BeforeClass
    public void setUpClass() {
        testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name");
    }

    @AfterClass
    public void tearDownClass() {
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
    }


    // This test will create one subscriber, then create two alarms (for the same device/channel), then check if
    // the listener got the correct notification for each of them.
    @Test
    public void testSimpleRealTimeAlarms() throws InterruptedException {
        // This delay (i.e. how long we wait for the server to process alarms and send notifications) seems to work,
        // but of course cannot be 100% reliable for a real integration test:
        int delayMillis = 200;

        // Create template to parse the notification:
        SmartResponseTemplate responseTemplate = new SmartResponseTemplate(
                "102", "$", "$", new String[]{"$.id", "$.type", "$.text", "$.severity", "$.count"});
        gId = smartRestApi.storeTemplates(
                xId,
                new SmartRequestTemplate[0],
                new SmartResponseTemplate[]{responseTemplate});

        SmartCepConnector connector = smartRestApi.getNotificationsConnector(xId);

        final List<String> notedAlarms = new ArrayList<>();
        // Prepare subscription and listener for the channel of our test device:
        connector.subscribe("/alarms/" + testManagedObject.getId(), 102, new SmartSubscriptionListener() {
            @Override
            public void onNotification(SmartSubscription subscription, SmartNotification notification) {
                notedAlarms.add(notification.getData());
            }

            @Override
            public void onError(SmartSubscription subscription, Throwable error) {
                fail("There should have been no errors.");
            }
        });

        // Connect, starting background listener:
        connector.connect();

        Thread.sleep(delayMillis);

        Alarm alarm = makeAlarm();
        alarm.setSeverity(Alarm.SEVERITY_MINOR);
        alarm.setType("com_telekom_TestType1");
        alarmApi.create(alarm);

        Thread.sleep(delayMillis);

        alarm = makeAlarm();
        alarm.setSeverity(Alarm.SEVERITY_MAJOR);
        // We need a different type, otherwise our alarm would be de-duplicated by the server (just increasing
        // the count of the first one, but not updating the content):
        alarm.setType("com_telekom_TestType2");
        alarmApi.create(alarm);

        Thread.sleep(delayMillis);

        assertEquals(notedAlarms.size(), 2);
        // According to our SmartResponseTemplate the data should end with the severity and the count:
        assertTrue(notedAlarms.get(0).endsWith("MINOR,1"));
        assertTrue(notedAlarms.get(1).endsWith("MAJOR,1"));
    }

    private Alarm makeAlarm() {
        Alarm alarm = new Alarm();
        alarm.setText("Strange thing happened!");
        alarm.setTime(new Date());
        alarm.setSource(testManagedObject);
        alarm.set("foo", "{ \"alt\": "+Math.random()+", \"lng\": 8.55436, \"lat\": 50.02868 }");
        alarm.setStatus(Alarm.STATE_ACTIVE);
        return alarm;
    }

}
