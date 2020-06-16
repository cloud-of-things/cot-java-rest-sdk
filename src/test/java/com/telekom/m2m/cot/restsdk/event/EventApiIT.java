package com.telekom.m2m.cot.restsdk.event;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Patrick Steinert on 30.01.16.
 */
public class EventApiIT {

    CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
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
    public void testCreateEvent() {

        Event event = new Event();
        event.setText("Sample Text");
        event.setType("com_telekom_TestType");
        event.setTime(new Date());
        event.setSource(testManagedObject);
        event.set("foo", "{ \"alt\": 99.9, \"lng\": 8.55436, \"lat\": 50.02868 }");

        EventApi eventApi = cotPlat.getEventApi();
        eventApi.createEvent(event);

        Assert.assertNotNull(event.getId(), "Should now have an Id");
    }

    @Test
    public void testCreateAndRead() {
        Calendar timeOfEventHappening = Calendar.getInstance();
        timeOfEventHappening.add(Calendar.SECOND, -10); // To adjust for smallish clock-mismatch between local machine and CoT.

        Event event = new Event();
        event.setText("Sample Text");
        event.setType("com_telekom_TestType");
        event.setTime(timeOfEventHappening.getTime());
        event.setSource(testManagedObject);

        EventApi eventApi = cotPlat.getEventApi();

        Event createdEvent = eventApi.createEvent(event);
            
        Assert.assertNotNull(createdEvent.getId(), "Should now have an Id");

        Event retrievedEvent = eventApi.getEvent(createdEvent.getId());
        Assert.assertEquals(retrievedEvent.getId(), createdEvent.getId());
        Assert.assertEquals(retrievedEvent.getType(), "com_telekom_TestType");
        Assert.assertEquals(retrievedEvent.getText(), "Sample Text");
        Assert.assertEquals(retrievedEvent.getTime().compareTo(timeOfEventHappening.getTime()), 0);
        Assert.assertNotNull(retrievedEvent.getCreationTime());

        Assert.assertTrue(
                retrievedEvent.getCreationTime().after(timeOfEventHappening.getTime()),
                String.format(
                        "retrievedEvent.getCreationTime(): %s, timeOfEventHappening: %s",
                        retrievedEvent.getCreationTime(),
                        timeOfEventHappening
                )
        );

    }
    @Test
    public void testGetEventReturnNull() {
        // This test checks whether the getEvent() method returns a null when the event does not exist in the cloud.

        Calendar timeOfEventHappening = Calendar.getInstance();

        Event event = new Event();
        event.setText("Sample Text");
        event.setType("com_telekom_TestType");
        event.setTime(timeOfEventHappening.getTime());
        event.setSource(testManagedObject);

        EventApi eventApi = cotPlat.getEventApi();

        Event createdEvent = eventApi.createEvent(event);
                
        String id= createdEvent.getId();
        
        //The event that exist in the cloud should be gettable:        
        Assert.assertNotNull(eventApi.getEvent(id));
        
        //Now lets try to "get" an event that does not exist in the cloud, get method should return a null:
        Assert.assertNull(eventApi.getEvent("theIdThatDoesNotExist"));
    }

}
