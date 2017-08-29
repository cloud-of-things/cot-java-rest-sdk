package com.telekom.m2m.cot.restsdk.event;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.Position;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * @author chuhlich
 */
public class EventApiCollectionIT {

    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    private ManagedObject testManagedObject;

    private EventApi eventApi = cotPlat.getEventApi();

    @BeforeMethod
    public void setUp() {
        testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name");
    }

    @AfterMethod
    public void tearDown() {
        eventApi.deleteEvents(Filter.build().bySource(testManagedObject.getId()));
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
    }


    @Test
    public void testMultipleEvents() throws Exception {
        Event testEvent = new Event();
        testEvent.setSource(testManagedObject);
        testEvent.setTime(new Date());
        testEvent.setType("mytype");
        testEvent.setText("Test");
        eventApi.createEvent(testEvent);

        EventCollection eventCollection = eventApi.getEvents();


        Event[] events = eventCollection.getEvents();

        Assert.assertTrue(events.length > 0);

        Event event = events[0];

        Assert.assertTrue(event.getId() != null);
        Assert.assertTrue(event.getId().length() > 0);

        Assert.assertTrue(event.getTime() != null);
        Assert.assertTrue(event.getTime().compareTo(new Date()) < 0);

        Assert.assertTrue(event.getType() != null);
        Assert.assertTrue(event.getType().length() > 0);
    }

    @Test
    public void testMultipleEventsWithPaging() throws Exception {
        // !!! Important !!!
        // Test assumes pageSize default is 5.

        for (int i = 0; i < 6; i++) {
            Event testEvent = new Event();
            testEvent.setSource(testManagedObject);
            testEvent.setTime(new Date(new Date().getTime() - (i * 5000)));
            testEvent.setType("mytype-" + i);
            testEvent.setText("Test" + i);

            eventApi.createEvent(testEvent);
        }

        EventCollection eventCollection = eventApi.getEvents(Filter.build().bySource(testManagedObject.getId()));


        Event[] events = eventCollection.getEvents();

        Assert.assertEquals(events.length, 5);
        Assert.assertTrue(eventCollection.hasNext());
        Assert.assertFalse(eventCollection.hasPrevious());

        eventCollection.next();

        events = eventCollection.getEvents();
        Assert.assertEquals(events.length, 1);

        Assert.assertFalse(eventCollection.hasNext());
        Assert.assertTrue(eventCollection.hasPrevious());

        eventCollection.previous();
        events = eventCollection.getEvents();

        Assert.assertEquals(events.length, 5);

        Assert.assertTrue(eventCollection.hasNext());
        Assert.assertFalse(eventCollection.hasPrevious());

        eventCollection.setPageSize(10);
        events = eventCollection.getEvents();

        Assert.assertEquals(events.length, 6);
        Assert.assertFalse(eventCollection.hasNext());
        Assert.assertFalse(eventCollection.hasPrevious());

    }

    @Test
    public void testDeleteMultipleEventsBySource() throws Exception {
        for (int i = 0; i < 6; i++) {
            Event testEvent = new Event();
            testEvent.setSource(testManagedObject);
            testEvent.setTime(new Date(new Date().getTime() - (i * 5000)));
            testEvent.setType("mytype-" + i);
            testEvent.setText("Test" + i);

            eventApi.createEvent(testEvent);
        }

        EventCollection events = eventApi.getEvents(Filter.build().bySource(testManagedObject.getId()));
        Event[] es = events.getEvents();
        Assert.assertEquals(es.length, 5);

        eventApi.deleteEvents(Filter.build().bySource(testManagedObject.getId()));
        events = eventApi.getEvents(Filter.build().bySource(testManagedObject.getId()));
        es = events.getEvents();
        Assert.assertEquals(es.length, 0);
    }

    @Test
    public void testMultipleEventsBySource() throws Exception {
        Event testEvent = new Event();
        testEvent.setSource(testManagedObject);
        testEvent.setTime(new Date());
        testEvent.setType("mytype");
        testEvent.setText("Test");
        eventApi.createEvent(testEvent);

        EventCollection events = eventApi.getEvents();
        Event[] es = events.getEvents();
        Assert.assertTrue(es.length > 0);
        boolean allEventsFromSource = true;
        for (Event m : es) {
            if (!m.getId().equals(testManagedObject.getId())) {
                allEventsFromSource = false;
            }
        }
        Assert.assertFalse(allEventsFromSource);

        //measurements = mApi.getMeasurementsBySource(testManagedObject.getId());
        events = eventApi.getEvents(Filter.build().bySource(testManagedObject.getId()));
        es = events.getEvents();
        allEventsFromSource = true;
        Assert.assertTrue(es.length > 0);
        for (Event m : es) {
            if (m.getId().equals(testManagedObject.getId())) {
                allEventsFromSource = false;
            }
        }
        Assert.assertTrue(allEventsFromSource);
    }

    @Test
    public void testMultipleEventsByType() throws Exception {
        Event testEvent = new Event();
        testEvent.setSource(testManagedObject);
        testEvent.setTime(new Date());
        testEvent.setType("mysuperspecialtype");
        testEvent.setText("Test");

        eventApi.createEvent(testEvent);

        EventCollection events = eventApi.getEvents();
        Event[] es = events.getEvents();
        Assert.assertTrue(es.length > 0);
        boolean allEventsFromSameType = true;
        for (Event m : es) {
            if (!m.getType().equals(testManagedObject.getType())) {
                allEventsFromSameType = false;
            }
        }
        Assert.assertFalse(allEventsFromSameType);

        events = eventApi.getEvents(Filter.build().byType(testEvent.getType()));
        es = events.getEvents();
        allEventsFromSameType = true;
        Assert.assertTrue(es.length > 0);
        for (Event m : es) {
            if (!m.getType().equals(testEvent.getType())) {
                allEventsFromSameType = false;
            }
        }
        Assert.assertTrue(allEventsFromSameType);
    }

    @Test
    public void testMultipleEventByDate() throws Exception {
        Event testEvent = new Event();
        testEvent.setSource(testManagedObject);
        testEvent.setTime(new Date(new Date().getTime() - (1000 * 60)));
        testEvent.setType("mysuperspecialtype");
        testEvent.setText("Test");

        eventApi.createEvent(testEvent);

        Date yesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
        EventCollection events = eventApi.getEvents(Filter.build().byDate(yesterday, new Date()));


        Event[] es = events.getEvents();
        Assert.assertTrue(es.length > 0);

        Date beforeYesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24) - 10);

        events = eventApi.getEvents(Filter.build().byDate(beforeYesterday, yesterday));
        es = events.getEvents();
        Assert.assertEquals(es.length, 0);
    }


    @Test
    public void testMultipleEventsByDateAndBySource() throws Exception {
        Event testEvent = new Event();
        testEvent.setSource(testManagedObject);
        testEvent.setTime(new Date(new Date().getTime() - (1000 * 60)));
        testEvent.setType("mysuperspecialtype");
        testEvent.setText("Test");

        eventApi.createEvent(testEvent);

        Date yesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
        EventCollection events = eventApi.getEvents(
                Filter.build()
                        .byDate(yesterday, new Date())
                        .bySource(testManagedObject.getId()));


        Event[] es = events.getEvents();
        Assert.assertEquals(es.length, 1);

        Date beforeYesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24) - 10);

        events = eventApi.getEvents(
                Filter.build()
                        .byDate(beforeYesterday, yesterday)
                        .bySource(testManagedObject.getId()));
        es = events.getEvents();
        Assert.assertEquals(es.length, 0);
    }


    @Test
    public void testMultipleEventsByTypeAndBySource() throws Exception {
        Position sts = new Position();
        sts.setAlt(1000.0);
        sts.setLat(50.722607);
        sts.setLon(7.144011);

        Event testEvent = new Event();
        testEvent.setSource(testManagedObject);
        testEvent.setTime(new Date(new Date().getTime() - (1000 * 60)));
        testEvent.setType("mysuperspecialtype");
        testEvent.setText("Test");

        testEvent.set(sts);
        eventApi.createEvent(testEvent);

        EventCollection events = eventApi.getEvents(
                Filter.build()
                        .byType("mysuperspecialtype")
                        .bySource(testManagedObject.getId()));


        Event[] es = events.getEvents();
        Assert.assertEquals(es.length, 1);


        events = eventApi.getEvents(
                Filter.build()
                        .byType("NOT_USED")
                        .bySource(testManagedObject.getId()));
        es = events.getEvents();
        Assert.assertEquals(es.length, 0);
    }


    @Test
    public void testMultipleEventsByFragmentTypeAndBySource() throws Exception {
        Position sts = new Position();
        sts.setAlt(1000.0);
        sts.setLat(50.722607);
        sts.setLon(7.144011);

        Event testEvent = new Event();
        testEvent.setSource(testManagedObject);
        testEvent.setTime(new Date(new Date().getTime() - (1000 * 60)));
        testEvent.setType("mysuperspecialtype");
        testEvent.setText("Test");
        testEvent.set(sts);
        eventApi.createEvent(testEvent);

        EventCollection events = eventApi.getEvents(
                Filter.build()
                        .byFragmentType("com_telekom_m2m_cot_restsdk_util_Position")
                        .bySource(testManagedObject.getId()));


        Event[] es = events.getEvents();
        Assert.assertEquals(es.length, 1);


        events = eventApi.getEvents(
                Filter.build()
                        .byFragmentType("com_telekom_m2m_cot_restsdk_util_Position_not")
                        .bySource(testManagedObject.getId()));
        es = events.getEvents();
        Assert.assertEquals(es.length, 0);
    }
}
