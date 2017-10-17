package com.telekom.m2m.cot.restsdk.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.alarm.Alarm;
import com.telekom.m2m.cot.restsdk.alarm.AlarmApi;
import com.telekom.m2m.cot.restsdk.alarm.AlarmCollection;
import com.telekom.m2m.cot.restsdk.event.Event;
import com.telekom.m2m.cot.restsdk.event.EventApi;
import com.telekom.m2m.cot.restsdk.event.EventCollection;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.users.User;
import com.telekom.m2m.cot.restsdk.users.UserApi;
import com.telekom.m2m.cot.restsdk.users.UserCollection;
import com.telekom.m2m.cot.restsdk.util.TestHelper;

import junit.framework.Assert;

public class FilterIT {

	private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME,
			TestHelper.TEST_PASSWORD);

	private AlarmApi alarmApi = cotPlat.getAlarmApi();
	private EventApi eventApi = cotPlat.getEventApi();
	private UserApi userApi = cotPlat.getUserApi();

	private String tenant = TestHelper.TEST_TENANT;

	private String password = "test-password";
	private List<User> usersToDelete = new ArrayList<>();

	private ManagedObject testManagedObject;

	@BeforeMethod
	public void setUp() {
		testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name");
	}

	@AfterMethod
	public void tearDown() {
		TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
		// We need this in case a test failed in the middle, causing it to
		// skip the delete call.
		for (User user : usersToDelete) {
			try {
				userApi.deleteUser(user, tenant);
			} catch (CotSdkException ex) {
				// This exception is ok, because then the test method managed to
				// delete its own user (should be the norm):
				assertEquals(ex.getHttpStatus(), 404);
			}
		}
		usersToDelete.clear();

	}

	@Test
	public void testFiltersViaUsers() throws Exception {

		// FOR THE DEVELOPERS (to be deleted during merge request): this test and the associated modifications in the Filter
		// and UserApi classes will not be merged with the develop. This is merely a
		// test to prove a point. Please do have a look at it and give feedback.

		// This test demonstrates that the filters does not work for a user object.
		// In order to confirm this, all possible fields that can be filtered have been
		// tested.
		// Below is an example test to prove this:
		// For this test, the field userName (which is also identical to field id) is
		// used.
		// Let's create a user with a specific userName and then let's call the
		// collection of
		// users with that userName. Since the userName is unique, this collection of
		// users should contain only one user (which is the one that is just created).
		// However, as filters do not work for users, the collection will contain all
		// the users in a given tenant. As said earlier, this can be tested for other
		// fields for
		// users as well.

		// given:
		User user = new User();
		user.setUserName("userNameForFilters");
		user.setLastName("lastNameForFilter");
		user.setFirstName("firsName");
		user.setPassword(password);
		userApi.createUser(user, tenant);
		usersToDelete.add(user);

		// when:
		final Filter.FilterBuilder filterBuilder = Filter.build().byUserName("userNameForFilters");
		UserCollection users = userApi.getUsersWithFilters(filterBuilder, tenant);

		// Now, let's prove that this collection holds more than one user:
		assertTrue(users.getUsers().length > 1);

	}

	@Test
	public void testFiltersViaAlarms() throws Exception {

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
		Filter.FilterBuilder filterBuilderForDate = Filter.build().byDate(beforeDate, afterDate);
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
		Filter.FilterBuilder filterBuilderForType = Filter.build().byType(type);

		// when:
		alarms = alarmApi.getAlarms(filterBuilderForType, 50);

		// then:
		assertEquals(alarms.getAlarms().length, 1);

		Alarm[] alarmsWithCertainType = alarms.getAlarms();
		assertEquals(alarmsWithCertainType[0].getType(), type);

		// Now let's test the "filter by text" feature:
		// FOR THE DEVELOPERS (to be deleted during merge request): this filter does not work, it is demonstrated below:
		// given:
		Filter.FilterBuilder filterBuilderForText = Filter.build().byText(text);
		alarms = alarmApi.getAlarms(filterBuilderForText, 500);

		// then:
		// Two alarms are created in this test and both of them have the same text.
		// However the filter will fail and when we call the collection of alarms using
		// this filter, it will retrieve all alarms in the cloud:
		assertTrue(alarms.getAlarms().length > 2);

		Alarm[] alarmsWithCertainText = alarms.getAlarms();
		boolean allAlarmsWithSameText = true;

		// A better check:
		for (Alarm alarmForLoop : alarmsWithCertainText) {

			if (!alarmForLoop.getText().equals(text)) {
				allAlarmsWithSameText = false;
			}
		}

		// The assertion verifies that in the array of alarms, some of them does not
		// have the text we set.
		assertFalse(allAlarmsWithSameText);

	}

	@Test
	public void testFiltersViaEvents() throws Exception {

		// given:
		Event event = new Event();
		String text = "Sample Text For Filter Testing";
		String type = "com_telekom_TestType_For_Filter_Testing";
		event.setText(text);
		event.setType(type);
		event.setTime(new Date());
		event.setSource(testManagedObject);
		event.set("foo", "{ \"alt\": 99.9, \"lng\": 8.55436, \"lat\": 50.02868 }");

		eventApi.createEvent(event);

		// The "filter by text" feature does not work for events. It is demonstrated
		// below:
		Filter.FilterBuilder filterBuilderForText = Filter.build().byText(text);

		EventCollection events = eventApi.getEvents(filterBuilderForText);

		// In the cloud there must be only one event with this text, however since the
		// filter does not work, there array contains all events:
		assertTrue(events.getEvents().length > 1);

		// A better check:
		Event[] eventsWithCertainText = events.getEvents();
		boolean allEventsWithSameText = true;

		for (Event eventForLoop : eventsWithCertainText) {

			if (!eventForLoop.getText().equals(text)) {
				allEventsWithSameText = false;
			}
		}

		assertFalse(allEventsWithSameText);

		// Now let's check whether the "filter by type" functionality works with events:
		Filter.FilterBuilder filterBuilderForType = Filter.build().byType(type);

		events = eventApi.getEvents(filterBuilderForType);

		assertTrue(events.getEvents().length == 1);

		// A better check:
		Event[] arrayofEvents = events.getEvents();

		assertEquals(arrayofEvents[0].getType(), type);

	}

}
