package com.telekom.m2m.cot.restsdk.alarm;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by breucking on 30.01.16.
 */
public class AlarmApiIT {

    CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
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
    public void testCreateAlarm() throws Exception {

        Alarm alarm = new Alarm();
        alarm.setText("Strange thing happend!");
        alarm.setType("com_telekom_TestType");
        alarm.setTime(new Date());
        alarm.setSource(testManagedObject);
        alarm.set("foo", "{ \"alt\": 99.9, \"lng\": 8.55436, \"lat\": 50.02868 }");
        alarm.setStatus(Alarm.STATE_ACTIVE);
        alarm.setSeverity(Alarm.SEVERITY_MAJOR);

        AlarmApi alarmApi = cotPlat.getAlarmApi();

        Alarm createdAlarm = alarmApi.create(alarm);
        Assert.assertNotNull("Should now have an Id", createdAlarm.getId());
    }

//    @Test
//    public void testCreateAndRead() throws Exception {
//        Date timeOfEventHappening = new Date();
//
//        Event event = new Event();
//        event.setText("Sample Text");
//        event.setType("com_telekom_TestType");
//        event.setTime(timeOfEventHappening);
//        event.setSource(testManagedObject);
//
//        EventApi eventApi = cotPlat.getEventApi();
//
//        Thread.sleep(1000);
//
//        Event createdEvent = eventApi.create(event);
//        Assert.assertNotNull("Should now have an Id", createdEvent.getId());
//
//
//        Event retrievedEvent = eventApi.getEvent(createdEvent.getId());
//        Assert.assertEquals(retrievedEvent.getId(), createdEvent.getId());
//        Assert.assertEquals(retrievedEvent.getType(), "com_telekom_TestType");
//        Assert.assertEquals(retrievedEvent.getText(), "Sample Text");
//        Assert.assertEquals(retrievedEvent.getTime().compareTo(timeOfEventHappening), 0);
//        Assert.assertNotNull(retrievedEvent.getCreationTime());
//        Assert.assertEquals(retrievedEvent.getCreationTime().compareTo(timeOfEventHappening), 1);
//
//    }


}
