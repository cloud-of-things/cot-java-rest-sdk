package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * @author steinert
 */
public class DeviceControlApiOperationsCollectionIT {

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
    public void testMultipleEvents() throws Exception {
        // Expects a tenant with already multiple measurements

        DeviceControlApi deviceControlApi = cotPlat.getDeviceControlApi();

        OperationCollection operationCollection = deviceControlApi.getOperations();


        Operation[] operations = operationCollection.getOperations();

        Assert.assertTrue(operations.length > 0);

        Operation operation = operations[0];

        Assert.assertTrue(operation.getId() != null);
        Assert.assertTrue(operation.getId().length() > 0);

        Assert.assertTrue(operation.getCreationTime() != null);
        Assert.assertTrue(operation.getCreationTime().compareTo(new Date()) < 0);

        Assert.assertTrue(operation.getStatus() != null);
        Assert.assertTrue(operation.getStatus().length() > 0);
    }

//    @Test
//    public void testMultipleEventsWithPaging() throws Exception {
//        // Expects a tenant with already multiple measurements
//
//        // !!! Important !!!
//        // Test assumes pageSize default is 5.
//
//        EventApi eventApi = cotPlat.getEventApi();
//
//        for (int i = 0; i < 6; i++) {
//            Event testEvent = new Event();
//            testEvent.setSource(testManagedObject);
//            testEvent.setTime(new Date(new Date().getTime() - (i * 5000)));
//            testEvent.setType("mytype-" + i);
//            testEvent.setText("Test" + i);
//
//            eventApi.createEvent(testEvent);
//        }
//
//        EventCollection eventCollection = eventApi.getEvents(Filter.build().bySource(testManagedObject.getId()));
//
//
//        Event[] events = eventCollection.getEvents();
//
//        Assert.assertEquals(events.length, 5);
//        Assert.assertTrue(eventCollection.hasNext());
//        Assert.assertFalse(eventCollection.hasPrevious());
//
//        eventCollection.next();
//
//        events = eventCollection.getEvents();
//        Assert.assertEquals(events.length, 1);
//
//        Assert.assertFalse(eventCollection.hasNext());
//        Assert.assertTrue(eventCollection.hasPrevious());
//
//        eventCollection.previous();
//        events = eventCollection.getEvents();
//
//        Assert.assertEquals(events.length, 5);
//
//        Assert.assertTrue(eventCollection.hasNext());
//        Assert.assertFalse(eventCollection.hasPrevious());
//
//        eventCollection.setPageSize(10);
//        events = eventCollection.getEvents();
//
//        Assert.assertEquals(events.length, 6);
//        Assert.assertFalse(eventCollection.hasNext());
//        Assert.assertFalse(eventCollection.hasPrevious());
//
//    }
//
//    @Test
//    public void testDeleteMultipleEventsBySource() throws Exception {
//        EventApi eApi = cotPlat.getEventApi();
//
//        for (int i = 0; i < 6; i++) {
//            Event testEvent = new Event();
//            testEvent.setSource(testManagedObject);
//            testEvent.setTime(new Date(new Date().getTime() - (i * 5000)));
//            testEvent.setType("mytype-" + i);
//            testEvent.setText("Test" + i);
//
//            eApi.createEvent(testEvent);
//        }
//
//        EventCollection events = eApi.getEvents(Filter.build().bySource(testManagedObject.getId()));
//        Event[] es = events.getEvents();
//        Assert.assertEquals(es.length, 5);
//
//        eApi.deleteEvents(Filter.build().bySource(testManagedObject.getId()));
//        events = eApi.getEvents(Filter.build().bySource(testManagedObject.getId()));
//        es = events.getEvents();
//        Assert.assertEquals(es.length, 0);
//    }
//
//    @Test
//    public void testMultipleEventsBySource() throws Exception {
//        EventApi eApi = cotPlat.getEventApi();
//
//        Event testEvent = new Event();
//        testEvent.setSource(testManagedObject);
//        testEvent.setTime(new Date());
//        testEvent.setType("mytype");
//        testEvent.setText("Test");
//        eApi.createEvent(testEvent);
//
//        EventCollection events = eApi.getEvents();
//        Event[] es = events.getEvents();
//        Assert.assertTrue(es.length > 0);
//        boolean allEventsFromSource = true;
//        for (Event m : es) {
//            if (!m.getId().equals(testManagedObject.getId())) {
//                allEventsFromSource = false;
//            }
//        }
//        Assert.assertFalse(allEventsFromSource);
//
//        //measurements = mApi.getMeasurementsBySource(testManagedObject.getId());
//        events = eApi.getEvents(Filter.build().bySource(testManagedObject.getId()));
//        es = events.getEvents();
//        allEventsFromSource = true;
//        Assert.assertTrue(es.length > 0);
//        for (Event m : es) {
//            if (m.getId().equals(testManagedObject.getId())) {
//                allEventsFromSource = false;
//            }
//        }
//        Assert.assertTrue(allEventsFromSource);
//    }
//
//    @Test
//    public void testMultipleEventsByType() throws Exception {
//        EventApi eApi = cotPlat.getEventApi();
//
//        Event testEvent = new Event();
//        testEvent.setSource(testManagedObject);
//        testEvent.setTime(new Date());
//        testEvent.setType("mysuperspecialtype");
//        testEvent.setText("Test");
//
//        eApi.createEvent(testEvent);
//
//        EventCollection events = eApi.getEvents();
//        Event[] es = events.getEvents();
//        Assert.assertTrue(es.length > 0);
//        boolean allEventsFromSameType = true;
//        for (Event m : es) {
//            if (!m.getType().equals(testManagedObject.getType())) {
//                allEventsFromSameType = false;
//            }
//        }
//        Assert.assertFalse(allEventsFromSameType);
//
//        events = eApi.getEvents(Filter.build().byType(testEvent.getType()));
//        es = events.getEvents();
//        allEventsFromSameType = true;
//        Assert.assertTrue(es.length > 0);
//        for (Event m : es) {
//            if (!m.getType().equals(testEvent.getType())) {
//                allEventsFromSameType = false;
//            }
//        }
//        Assert.assertTrue(allEventsFromSameType);
//    }
//
//    @Test
//    public void testMultipleEventByDate() throws Exception {
//        EventApi eApi = cotPlat.getEventApi();
//
//        Event testEvent = new Event();
//        testEvent.setSource(testManagedObject);
//        testEvent.setTime(new Date(new Date().getTime() - (1000 * 60)));
//        testEvent.setType("mysuperspecialtype");
//        testEvent.setText("Test");
//
//        eApi.createEvent(testEvent);
//
//        Date yesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
//        EventCollection events = eApi.getEvents(Filter.build().byDate(yesterday, new Date()));
//
//
//        Event[] es = events.getEvents();
//        Assert.assertTrue(es.length > 0);
//
//        Date beforeYesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24) - 10);
//
//        events = eApi.getEvents(Filter.build().byDate(beforeYesterday, yesterday));
//        es = events.getEvents();
//        Assert.assertEquals(es.length, 0);
//    }
//
//
//    @Test
//    public void testMultipleEventsByDateAndBySource() throws Exception {
//        EventApi eApi = cotPlat.getEventApi();
//
//        Event testEvent = new Event();
//        testEvent.setSource(testManagedObject);
//        testEvent.setTime(new Date(new Date().getTime() - (1000 * 60)));
//        testEvent.setType("mysuperspecialtype");
//        testEvent.setText("Test");
//
//        eApi.createEvent(testEvent);
//
//        Date yesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
//        EventCollection events = eApi.getEvents(
//                Filter.build()
//                        .byDate(yesterday, new Date())
//                        .bySource(testManagedObject.getId()));
//
//
//        Event[] es = events.getEvents();
//        Assert.assertEquals(es.length, 1);
//
//        Date beforeYesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24) - 10);
//
//        events = eApi.getEvents(
//                Filter.build()
//                        .byDate(beforeYesterday, yesterday)
//                        .bySource(testManagedObject.getId()));
//        es = events.getEvents();
//        Assert.assertEquals(es.length, 0);
//    }
//
//
//    @Test
//    public void testMultipleEventsByTypeAndBySource() throws Exception {
//        EventApi eApi = cotPlat.getEventApi();
//
//        Position sts = new Position();
//        sts.setAlt(1000.0);
//        sts.setLat(50.722607);
//        sts.setLon(7.144011);
//
//        Event testEvent = new Event();
//        testEvent.setSource(testManagedObject);
//        testEvent.setTime(new Date(new Date().getTime() - (1000 * 60)));
//        testEvent.setType("mysuperspecialtype");
//        testEvent.setText("Test");
//
//        testEvent.set(sts);
//        eApi.createEvent(testEvent);
//
//        EventCollection events = eApi.getEvents(
//                Filter.build()
//                        .byType("mysuperspecialtype")
//                        .bySource(testManagedObject.getId()));
//
//
//        Event[] es = events.getEvents();
//        Assert.assertEquals(es.length, 1);
//
//
//        events = eApi.getEvents(
//                Filter.build()
//                        .byType("NOT_USED")
//                        .bySource(testManagedObject.getId()));
//        es = events.getEvents();
//        Assert.assertEquals(es.length, 0);
//    }
//
//
//    @Test
//    public void testMultipleEventsByFragmentTypeAndBySource() throws Exception {
//        EventApi eApi = cotPlat.getEventApi();
//
//        Position sts = new Position();
//        sts.setAlt(1000.0);
//        sts.setLat(50.722607);
//        sts.setLon(7.144011);
//
//        Event testEvent = new Event();
//        testEvent.setSource(testManagedObject);
//        testEvent.setTime(new Date(new Date().getTime() - (1000 * 60)));
//        testEvent.setType("mysuperspecialtype");
//        testEvent.setText("Test");
//        testEvent.set(sts);
//        eApi.createEvent(testEvent);
//
//        EventCollection events = eApi.getEvents(
//                Filter.build()
//                        .byFragmentType("com_telekom_m2m_cot_restsdk_util_Position")
//                        .bySource(testManagedObject.getId()));
//
//
//        Event[] es = events.getEvents();
//        Assert.assertEquals(es.length, 1);
//
//
//        events = eApi.getEvents(
//                Filter.build()
//                        .byFragmentType("com_telekom_m2m_cot_restsdk_util_Position_not")
//                        .bySource(testManagedObject.getId()));
//        es = events.getEvents();
//        Assert.assertEquals(es.length, 0);
//    }
}
