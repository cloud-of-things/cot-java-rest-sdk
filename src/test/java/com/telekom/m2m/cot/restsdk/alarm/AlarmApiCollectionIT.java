package com.telekom.m2m.cot.restsdk.alarm;

import com.google.gson.JsonObject;
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

    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
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
    public void testAlarmCollection() throws Exception {
        // given at least one created alarm entry
        final String text = "new alarm created";
        final String type = "com_telekom_alarm_TestType";

        final Alarm alarm = new Alarm();
        alarm.setText(text);
        alarm.setType(type);
        alarm.setTime(new Date());
        alarm.setSource(testManagedObject);
        alarm.setStatus(Alarm.STATE_ACTIVE);
        alarm.setSeverity(Alarm.SEVERITY_MAJOR);

        final AlarmApi alarmApi = cotPlat.getAlarmApi();

        alarmApi.create(alarm);

        // when
        final AlarmCollection alarmCollection = alarmApi.getAlarms(5);

        // then
        Assert.assertNotNull(alarmCollection);

        final Alarm[] alarms = alarmCollection.getAlarms();

        Assert.assertTrue(alarms.length > 0);
        Assert.assertEquals(alarms.length, alarmCollection.getJsonArray().size());

        final Alarm retrievedAlarm = alarms[0];
        final JsonObject jsonObject = alarmCollection.getJsonArray().get(0).getAsJsonObject();

        Assert.assertTrue(retrievedAlarm.getId() != null);
        Assert.assertFalse(retrievedAlarm.getId().isEmpty());
        Assert.assertTrue(retrievedAlarm.getId().equals(jsonObject.get("id").getAsString()));

        Assert.assertTrue(retrievedAlarm.getTime() != null);
        Assert.assertTrue(retrievedAlarm.getTime().compareTo(new Date()) < 0);

        Assert.assertTrue(retrievedAlarm.getType() != null);
        Assert.assertFalse(retrievedAlarm.getType().isEmpty());
        Assert.assertTrue(retrievedAlarm.getType().equals(jsonObject.get("type").getAsString()));
    }

    @Test
    public void testAlarmCollectionWithFilter() throws Exception {
        // given
        final String text = "new alarm created";
        final String type = "com_telekom_alarm_TestType";

        final Alarm alarm = new Alarm();
        alarm.setText(text);
        alarm.setType(type);
        alarm.setTime(new Date());
        alarm.setSource(testManagedObject);
        alarm.setStatus(Alarm.STATE_ACTIVE);
        alarm.setSeverity(Alarm.SEVERITY_MAJOR);
        final Filter.FilterBuilder filterBuilder = Filter.build().bySource(testManagedObject.getId());

        final AlarmApi alarmApi = cotPlat.getAlarmApi();

        alarmApi.create(alarm);

        // when
        final AlarmCollection alarmCollection = alarmApi.getAlarms(filterBuilder, 5);

        // then
        Assert.assertNotNull(alarmCollection);

        final Alarm[] alarms = alarmCollection.getAlarms();

        Assert.assertEquals(alarms.length, 1);
        Assert.assertEquals(alarms.length, alarmCollection.getJsonArray().size());

        final Alarm retrievedAlarm = alarms[0];
        final JsonObject jsonObject = alarmCollection.getJsonArray().get(0).getAsJsonObject();

        Assert.assertTrue(retrievedAlarm.getId() != null);
        Assert.assertFalse(retrievedAlarm.getId().isEmpty());
        Assert.assertTrue(retrievedAlarm.getId().equals(jsonObject.get("id").getAsString()));

        Assert.assertTrue(retrievedAlarm.getTime() != null);
        Assert.assertTrue(retrievedAlarm.getTime().compareTo(new Date()) < 0);

        Assert.assertTrue(retrievedAlarm.getType() != null);
        Assert.assertFalse(retrievedAlarm.getType().isEmpty());
        Assert.assertTrue(retrievedAlarm.getType().equals(jsonObject.get("type").getAsString()));
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

        AlarmCollection alarms = alarmApi.getAlarms(Filter.build().bySource(testManagedObject.getId()), 5);
        Alarm[] as = alarms.getAlarms();
        Assert.assertEquals(as.length, 5);

        alarmApi.deleteAlarms(Filter.build().bySource(testManagedObject.getId()));
        alarms = alarmApi.getAlarms(Filter.build().bySource(testManagedObject.getId()), 5);
        as = alarms.getAlarms();
        Assert.assertEquals(as.length, 0);
    }

    @Test
    public void testMultipleAlarmsByStatus() throws Exception {
        AlarmApi alarmApi = cotPlat.getAlarmApi();

        Alarm testAlarm1 = new Alarm();
        testAlarm1.setSource(testManagedObject);
        testAlarm1.setTime(new Date(new Date().getTime() - (2 * 5000)));
        testAlarm1.setType("mytype");
        testAlarm1.setText("Test");
        testAlarm1.setStatus(Alarm.STATE_ACTIVE);
        testAlarm1.setSeverity(Alarm.SEVERITY_MAJOR);

        alarmApi.create(testAlarm1);

        Alarm testAlarm2 = new Alarm();
        testAlarm2.setSource(testManagedObject);
        testAlarm2.setTime(new Date(new Date().getTime() - (2 * 5000)));
        testAlarm2.setType("mytype");
        testAlarm2.setText("Test");
        testAlarm2.setStatus(Alarm.STATE_ACKNOWLEDGED);
        testAlarm2.setSeverity(Alarm.SEVERITY_MAJOR);

        alarmApi.create(testAlarm2);

        AlarmCollection alarms = alarmApi.getAlarms(50);
        Alarm[] as = alarms.getAlarms();
        Assert.assertTrue(as.length > 0);
        boolean allAlarmsFromSameStatus = true;
        for (Alarm a : as) {
            if (!a.getStatus().equals(Alarm.STATE_ACTIVE)) {
                allAlarmsFromSameStatus = false;
            }
        }
        Assert.assertFalse(allAlarmsFromSameStatus);

        alarms = alarmApi.getAlarms(Filter.build().byStatus(Alarm.STATE_ACTIVE), 50);
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
                .bySource(testManagedObject.getId()), 5);

        Alarm[] as = alarms.getAlarms();
        Assert.assertEquals(as.length, 1);

        alarms = alarmApi.getAlarms(Filter.build().byStatus(Alarm.STATE_ACKNOWLEDGED)
                .bySource(testManagedObject.getId()), 5);

        as = alarms.getAlarms();
        Assert.assertEquals(as.length, 0);
    }
}
