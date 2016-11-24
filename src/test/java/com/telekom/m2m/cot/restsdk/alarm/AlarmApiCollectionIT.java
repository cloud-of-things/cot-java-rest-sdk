package com.telekom.m2m.cot.restsdk.alarm;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * @author chuhlich
 */
public class AlarmApiCollectionIT {

    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    private ManagedObject testManagedObject;

    @BeforeMethod
    public void setUp() {
        testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name");
    }

    @AfterMethod
    public void tearDown() {
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
    }


    @Test
    public void testMultipleAlarms() throws Exception {
        // Expects a tenant with already multiple measurements

        AlarmApi alarmApi = cotPlat.getAlarmApi();

        AlarmCollection alarmCollection = alarmApi.getAlarms();


        Alarm[] alarms = alarmCollection.getAlarms();

        Assert.assertTrue(alarms.length > 0);

        Alarm alarm = alarms[0];

        Assert.assertTrue(alarm.getId() != null);
        Assert.assertTrue(alarm.getId().length() > 0);

        Assert.assertTrue(alarm.getTime() != null);
        Assert.assertTrue(alarm.getTime().compareTo(new Date()) < 0);

        Assert.assertTrue(alarm.getType() != null);
        Assert.assertTrue(alarm.getType().length() > 0);
    }

    @Test
    public void testMultipleAlarmsWithPaging() throws Exception {
        // Expects a tenant with already multiple alarms

        // !!! Important !!!
        // Test assumes pageSize default is 5.

        AlarmApi alarmApi = cotPlat.getAlarmApi();

        for (int i = 0; i < 6; i++) {
            Alarm testAlarm = new Alarm();
            testAlarm.setSource(testManagedObject);
            testAlarm.setTime(new Date(new Date().getTime() - (i * 5000)));
            testAlarm.setType("mytype-" + i);
            testAlarm.setText("Test" + i);
            testAlarm.setStatus(Alarm.STATE_ACTIVE);
            testAlarm.setSeverity(Alarm.SEVERITY_MAJOR);


            alarmApi.create(testAlarm);
        }

        AlarmCollection alarmCollection = alarmApi.getAlarms(Filter.build().bySource(testManagedObject.getId()));


        Alarm[] alarms = alarmCollection.getAlarms();

        Assert.assertEquals(alarms.length, 5);
        Assert.assertTrue(alarmCollection.hasNext());
        Assert.assertFalse(alarmCollection.hasPrevious());

        alarmCollection.next();

        alarms = alarmCollection.getAlarms();
        Assert.assertEquals(alarms.length, 1);

        Assert.assertFalse(alarmCollection.hasNext());
        Assert.assertTrue(alarmCollection.hasPrevious());

        alarmCollection.previous();
        alarms = alarmCollection.getAlarms();

        Assert.assertEquals(alarms.length, 5);

        Assert.assertTrue(alarmCollection.hasNext());
        Assert.assertFalse(alarmCollection.hasPrevious());

        alarmCollection.setPageSize(10);
        alarms = alarmCollection.getAlarms();

        Assert.assertEquals(alarms.length, 6);
        Assert.assertFalse(alarmCollection.hasNext());
        Assert.assertFalse(alarmCollection.hasPrevious());

    }

    @Test
    public void testDeleteMultipleAlarmsBySource() throws Exception {
        AlarmApi alarmApi = cotPlat.getAlarmApi();

        for (int i = 0; i < 6; i++) {
            Alarm testAlarm = new Alarm();
            testAlarm.setSource(testManagedObject);
            testAlarm.setTime(new Date(new Date().getTime() - (i * 5000)));
            testAlarm.setType("mytype-" + i);
            testAlarm.setText("Test" + i);
            testAlarm.setStatus(Alarm.STATE_ACTIVE);
            testAlarm.setSeverity(Alarm.SEVERITY_MAJOR);

            alarmApi.create(testAlarm);
        }

        AlarmCollection alarms = alarmApi.getAlarms(Filter.build().bySource(testManagedObject.getId()));
        Alarm[] as = alarms.getAlarms();
        Assert.assertEquals(as.length, 5);

        alarmApi.deleteAlarms(Filter.build().bySource(testManagedObject.getId()));
        alarms = alarmApi.getAlarms(Filter.build().bySource(testManagedObject.getId()));
        as = alarms.getAlarms();
        Assert.assertEquals(as.length, 0);
    }

    @Test
    public void testMultipleAlarmsByStatus() throws Exception {
        AlarmApi alarmApi = cotPlat.getAlarmApi();

        Alarm testAlarm = new Alarm();
        testAlarm.setSource(testManagedObject);
        testAlarm.setTime(new Date(new Date().getTime() - (2 * 5000)));
        testAlarm.setType("mytype");
        testAlarm.setText("Test");
        testAlarm.setStatus(Alarm.STATE_ACTIVE);
        testAlarm.setSeverity(Alarm.SEVERITY_MAJOR);

        alarmApi.create(testAlarm);

        AlarmCollection alarms = alarmApi.getAlarms();
        Alarm[] as = alarms.getAlarms();
        Assert.assertTrue(as.length > 0);
        boolean allAlarmsFromSameStatus = true;
        for (Alarm a : as) {
            if (!a.getStatus().equals(Alarm.STATE_ACTIVE)) {
                allAlarmsFromSameStatus = false;
            }
        }
        Assert.assertFalse(allAlarmsFromSameStatus);

        alarms = alarmApi.getAlarms(Filter.build().byStatus(Alarm.STATE_ACTIVE));
        as = alarms.getAlarms();
        allAlarmsFromSameStatus = true;
        Assert.assertTrue(as.length > 0);
        for (Alarm a : as) {
            if (!a.getStatus().equals(Alarm.STATE_ACTIVE)) {
                allAlarmsFromSameStatus = false;
            }
        }
        Assert.assertTrue(allAlarmsFromSameStatus);
    }

    @Test
    public void testMultipleAlarmsByTime() throws Exception {
        AlarmApi alarmApi = cotPlat.getAlarmApi();

        Alarm testAlarm = new Alarm();
        testAlarm.setSource(testManagedObject);
        testAlarm.setTime(new Date(new Date().getTime() - (1000 * 60)));
        testAlarm.setType("mytype");
        testAlarm.setText("Test");
        testAlarm.setStatus(Alarm.STATE_ACTIVE);
        testAlarm.setSeverity(Alarm.SEVERITY_MAJOR);

        Date yesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
        AlarmCollection alarms = alarmApi.getAlarms(Filter.build().byDate(yesterday, new Date()));


        Alarm[] as = alarms.getAlarms();
        Assert.assertTrue(as.length > 0);

        Date beforeYesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24) - 10);

        alarms = alarmApi.getAlarms(Filter.build().byDate(beforeYesterday, yesterday));
        as = alarms.getAlarms();
        Assert.assertEquals(as.length, 0);
    }


    @Test
    public void testMultipleAlarmsByTimeAndBySource() throws Exception {
        AlarmApi alarmApi = cotPlat.getAlarmApi();

        Alarm testAlarm = new Alarm();
        testAlarm.setSource(testManagedObject);
        testAlarm.setTime(new Date());
        testAlarm.setType("mytype");
        testAlarm.setText("Test");
        testAlarm.setStatus(Alarm.STATE_ACTIVE);
        testAlarm.setSeverity(Alarm.SEVERITY_MAJOR);
        alarmApi.create(testAlarm);


        Date yesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
        AlarmCollection alarms = alarmApi.getAlarms(Filter.build().byDate(yesterday, new Date())
                .bySource(testManagedObject.getId()));


        Alarm[] as = alarms.getAlarms();
        Assert.assertEquals(as.length, 1);

        Date beforeYesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24) - 10);

        alarms = alarmApi.getAlarms(Filter.build().byDate(beforeYesterday, yesterday)
                .bySource(testManagedObject.getId()));

        as = alarms.getAlarms();
        Assert.assertEquals(as.length, 0);
    }

    @Test
    public void testMultipleAlarmsBySourceAndStatus() throws Exception {
        AlarmApi alarmApi = cotPlat.getAlarmApi();

        Alarm testAlarm = new Alarm();
        testAlarm.setSource(testManagedObject);
        testAlarm.setTime(new Date());
        testAlarm.setType("mytype");
        testAlarm.setText("Test");
        testAlarm.setStatus(Alarm.STATE_ACTIVE);
        testAlarm.setSeverity(Alarm.SEVERITY_MAJOR);
        alarmApi.create(testAlarm);

        AlarmCollection alarms = alarmApi.getAlarms(Filter.build().byStatus(Alarm.STATE_ACTIVE)
                .bySource(testManagedObject.getId()));

        Alarm[] as = alarms.getAlarms();
        Assert.assertEquals(as.length, 1);

        alarms = alarmApi.getAlarms(Filter.build().byStatus(Alarm.STATE_ACKNOWLEDGED)
                .bySource(testManagedObject.getId()));

        as = alarms.getAlarms();
        Assert.assertEquals(as.length, 0);
    }
}
