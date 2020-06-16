package com.telekom.m2m.cot.restsdk.event;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.Position;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;

/**
 * Created by Patrick Steinert on 03.02.16.
 */
public class EventApiTest {
    @Test(expectedExceptions = CotSdkException.class)
    public void testGetEventWithFailure() {
        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        CloudOfThingsPlatform platform = Mockito.mock(CloudOfThingsPlatform.class);
        Mockito.when(platform.getEventApi()).thenReturn(new EventApi(rc));
        Mockito.doThrow(CotSdkException.class).when(rc).getResponse(any(String.class), any(String.class), any(String.class));

        EventApi eventApi = platform.getEventApi();
        eventApi.getEvent("foo");
    }

    @Test
    public void testGetEvent() {

        String eventJsonExample = "{\n" +
                "  \"id\" : \"10\",\n" +
                "  \"self\" : \"...\",\n" +
                "  \"time\" : \"2011-09-06T12:03:27.000+02:00\",\n" +
                "  \"creationTime\" : \"2011-09-06T12:03:27.000+02:00\",\n" +
                "  \"type\" : \"com_telekom_DoorSensorEvent\",\n" +
                "  \"text\" : \"Door sensor was triggered.\",\n" +
                "  \"source\" : { \"id\":\"12345\", \"name \": \"test\" },\n" +
                "  \"com_telekom_m2m_cot_restsdk_util_Position\" : { \"alt\": 0.0, \"lon\": 1.0, \"lat\": 2.0}\n" +
                "}";

        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        CloudOfThingsPlatform platform = Mockito.mock(CloudOfThingsPlatform.class);
        Mockito.when(platform.getEventApi()).thenReturn(new EventApi(rc));
        Mockito.when(rc.getResponse(any(String.class), any(String.class), any(String.class))).thenReturn(eventJsonExample);

        EventApi eventApi = platform.getEventApi();
        Event event = eventApi.getEvent("foo");

        Assert.assertEquals(event.getId(), "10");
        Assert.assertEquals(event.getType(), "com_telekom_DoorSensorEvent");
        Assert.assertEquals(event.getText(), "Door sensor was triggered.");
        Assert.assertEquals(event.getCreationTime().compareTo(new Date(1315303407000L)), 0);
        Assert.assertEquals(event.getTime().compareTo(new Date(1315303407000L)), 0);

        Assert.assertEquals(((Position) event.get("com_telekom_m2m_cot_restsdk_util_Position")).getLat(), 2.0);
        //Assert.assertEquals(event.getSource().getId(), "12345");

    }
}
