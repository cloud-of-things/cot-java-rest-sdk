package com.telekom.m2m.cot.restsdk.alarm;

import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.FilterBy;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static org.testng.Assert.assertEquals;

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
        cotPlat.getAlarmApi().deleteAlarms(Filter.build().bySource(testManagedObject.getId()));
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
	public void testVariousFiltersViaAlarms() throws Exception {

		// Let's test "filter by source" feature by using alarms:
		// given
		String text = "new alarm created_for_filter_testing";
		String type = "com_telekom_alarm_TestType_For_Filter";

		Alarm alarm = new Alarm();
		alarm.setText(text);
		alarm.setType(type);
		alarm.setTime(new Date());
		alarm.setSource(testManagedObject);
		alarm.setStatus(Alarm.STATE_ACTIVE);
		alarm.setSeverity(Alarm.SEVERITY_MAJOR);
		Filter.FilterBuilder filterBuilder = Filter.build().bySource(testManagedObject.getId());

		// when:
		AlarmApi alarmApi = cotPlat.getAlarmApi();
		alarmApi.create(alarm);
		AlarmCollection alarms = alarmApi.getAlarms(filterBuilder, 5);

		// then:
		// Since the id of the alarm is unique, the alarm collection array should have a
		// length of 1:

		Assert.assertEquals(alarms.getAlarms().length, 1);

		// Now let's make sure that it is indeed the alarm that we have just created:
		Alarm[] alarmarray = alarms.getAlarms();
		Assert.assertEquals(alarmarray[0].getType(), type);

		// Now let's try to get an alarm that is create in a given date range by using
		// filters:
		// given:
		String typeForDate = "com_telekom_alarm_TestType_For_Date_Filter";

		Alarm alarm2 = new Alarm();
		alarm2.setText(text);
		alarm2.setType(typeForDate);

		SimpleDateFormat dateformat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
		String nominalDateStr = "12-12-2012 12:12:12";
		String beforeDateStr = "11-12-2012 12:12:12";
		String afterDateStr = "13-12-2012 12:12:12";
		Date nominalDate = dateformat.parse(nominalDateStr);
		Date beforeDate = dateformat.parse(beforeDateStr);
		Date afterDate = dateformat.parse(afterDateStr);
		alarm2.setTime(nominalDate);

		alarm2.setSource(testManagedObject);
		alarm2.setStatus(Alarm.STATE_ACTIVE);
		alarm2.setSeverity(Alarm.SEVERITY_MAJOR);

		// when:
		Filter.FilterBuilder filterBuilderForDate = Filter.build().byDate(beforeDate, afterDate).bySource(testManagedObject.getId());
		alarmApi.create(alarm2);
		alarms = alarmApi.getAlarms(filterBuilderForDate, 5);

		// then:
		// Now let's make sure that the alarm collection contains only one alarm, which
		// is the alarm we created with a specific time stamp:

		assertEquals(alarms.getAlarms().length, 1);

		// Now let's make sure that the alarm that is stored in the alarmCollection
		// array is indeed the alarm that we created:
		Alarm[] alarmarray2 = alarms.getAlarms();
		assertEquals(alarmarray2[0].getTime(), alarm2.getTime());

		// now let's test the status filter with alarms:
		// At first, let's get all the alarms with a given status. Since there can be
		// many alarms in the cloud with a given status,
		// let's create a new alarm with the same status and then let's get all the
		// alarms from the cloud with the same status again. This time the number of
		// alarms with that status should be +1 (of course this would not work if
		// someone creates an alarm with the same status at the same time as ths code
		// runs)

		Filter.FilterBuilder filterBuilderForStatus = Filter.build().byStatus(Alarm.STATE_ACTIVE);

		alarms = alarmApi.getAlarms(filterBuilderForStatus, 500);
		Alarm[] alarmsWithCertainStatus = alarms.getAlarms();
		int alarmCounter = alarmsWithCertainStatus.length;

		// Now let's create an alarm with the same status:
		Alarm alarmWithCertainStatus = new Alarm();
		alarmWithCertainStatus.setStatus(Alarm.STATE_ACTIVE);
		String typeForStatus = "com_telekom_alarm_TestType_For_Status_Filter";

		alarmWithCertainStatus.setText(text);
		alarmWithCertainStatus.setType(typeForStatus);
		alarmWithCertainStatus.setTime(new Date());

		alarmWithCertainStatus.setSource(testManagedObject);
		alarmWithCertainStatus.setStatus(Alarm.STATE_ACTIVE);
		alarmWithCertainStatus.setSeverity(Alarm.SEVERITY_MAJOR);

		alarmApi.create(alarmWithCertainStatus);

		// Now let's get the alarms again:
		alarms = alarmApi.getAlarms(filterBuilderForStatus, 500);

		// Now let's check if that worked:

		assertEquals(alarmCounter + 1, alarms.getAlarms().length);

		// Now let's test the "fitler by type" feature:

		// given:
		Filter.FilterBuilder filterBuilderForType = Filter.build().byType(type).bySource(testManagedObject.getId());

		// when:
		alarms = alarmApi.getAlarms(filterBuilderForType, 50);

		// then:
		assertEquals(alarms.getAlarms().length, 1);

		Alarm[] alarmsWithCertainType = alarms.getAlarms();
		assertEquals(alarmsWithCertainType[0].getType(), type);


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
        testAlarm1.setType("mytype1");
        testAlarm1.setText("Test");
        testAlarm1.setStatus(Alarm.STATE_ACTIVE);
        testAlarm1.setSeverity(Alarm.SEVERITY_MAJOR);

        alarmApi.create(testAlarm1);

        Alarm testAlarm2 = new Alarm();
        testAlarm2.setSource(testManagedObject);
        testAlarm2.setTime(new Date(new Date().getTime() - (2 * 5000)));
        testAlarm2.setType("mytype2");
        testAlarm2.setText("Test");
        testAlarm2.setStatus(Alarm.STATE_ACKNOWLEDGED);
        testAlarm2.setSeverity(Alarm.SEVERITY_MAJOR);

        alarmApi.create(testAlarm2);

        AlarmCollection alarms = alarmApi.getAlarms(Filter.build().bySource(testManagedObject.getId()), 5);
        Alarm[] as = alarms.getAlarms();
        Assert.assertTrue(as.length > 0);
        boolean allAlarmsFromSameStatus = true;

        // The alarms have a different type, otherwise they would be deduplicated and counted (same source and severity).
        // Only one of them may be ACTIVE, the other one is ACKNOWLEDGED but we don't know the ordering of the collection.
        for (Alarm a : as) {
            if (!a.getStatus().equals(Alarm.STATE_ACTIVE)) {
                allAlarmsFromSameStatus = false;
            }
        }
        Assert.assertFalse(allAlarmsFromSameStatus);

        alarms = alarmApi.getAlarms(Filter.build().byStatus(Alarm.STATE_ACTIVE), 5);
        as = alarms.getAlarms();
        Assert.assertTrue(as.length > 0);
        for (Alarm a : as) {
            Assert.assertEquals(a.getStatus(), Alarm.STATE_ACTIVE);
        }
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

    /**
     * fails, because filter byText is not allowed in alarm api
     */
    @Test(expectedExceptions = CotSdkException.class)
    public void testFilterNotALlowed() {
        AlarmApi alarmApi = cotPlat.getAlarmApi();
        AlarmCollection alarms = alarmApi.getAlarms(Filter.build().byText(Alarm.STATE_ACTIVE), 5);
    }

    @Test
    public void testAlarmCollectionWithFilterSetFilter() throws Exception {
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
        final Filter.FilterBuilder filterBuilder = Filter.build().setFilter(FilterBy.BYSOURCE, testManagedObject.getId());

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
    public void testMultipleAlarmsBySourceAndStatusSetFilters() throws Exception {
        AlarmApi alarmApi = cotPlat.getAlarmApi();

        Alarm testAlarm = new Alarm();
        testAlarm.setSource(testManagedObject);
        testAlarm.setTime(new Date());
        testAlarm.setType("mytype");
        testAlarm.setText("Test");
        testAlarm.setStatus(Alarm.STATE_ACTIVE);
        testAlarm.setSeverity(Alarm.SEVERITY_MAJOR);
        alarmApi.create(testAlarm);
        HashMap<FilterBy, String> filters = new HashMap<>();
        filters.put(FilterBy.BYSTATUS, Alarm.STATE_ACTIVE);
        filters.put(FilterBy.BYSOURCE, testManagedObject.getId());
        AlarmCollection alarms = alarmApi.getAlarms(Filter.build().setFilters(filters), 5);

        Alarm[] as = alarms.getAlarms();
        Assert.assertEquals(as.length, 1);

        alarms = alarmApi.getAlarms(Filter.build().byStatus(Alarm.STATE_ACKNOWLEDGED)
                .bySource(testManagedObject.getId()), 5);

        as = alarms.getAlarms();
        Assert.assertEquals(as.length, 0);
    }
}
