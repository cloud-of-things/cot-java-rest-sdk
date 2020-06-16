package com.telekom.m2m.cot.restsdk.event;

import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.Position;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.Nonnull;
import java.util.Date;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

/**
 * Created by Patrick Steinert on 03.02.16.
 */
public class EventApiTest {

    private static final String EVENT_JSON_EXAMPLE = "{\n" +
            "  \"id\" : \"10\",\n" +
            "  \"self\" : \"...\",\n" +
            "  \"time\" : \"2011-09-06T12:03:27.000+02:00\",\n" +
            "  \"creationTime\" : \"2011-09-06T12:03:27.000+02:00\",\n" +
            "  \"type\" : \"com_telekom_DoorSensorEvent\",\n" +
            "  \"text\" : \"Door sensor was triggered.\",\n" +
            "  \"source\" : { \"id\":\"12345\", \"name \": \"test\" },\n" +
            "  \"com_telekom_m2m_cot_restsdk_util_Position\" : { \"alt\": 0.0, \"lon\": 1.0, \"lat\": 2.0}\n" +
            "}";

    private EventApi eventApi;
    private CloudOfThingsRestClient client;

    @BeforeMethod
    public void setup() {
        client = createClient();
        eventApi = new EventApi(client);
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testGetEventWithFailure() {
        eventApi.getEvent("foo");
    }

    @Test
    public void testGetEvent() {
        Event event = eventApi.getEvent("10");

        assertEquals(event.getId(), "10");
        assertEquals(event.getType(), "com_telekom_DoorSensorEvent");
        assertEquals(event.getText(), "Door sensor was triggered.");
        assertEquals(event.getCreationTime().compareTo(new Date(1315303407000L)), 0);
        assertEquals(event.getTime().compareTo(new Date(1315303407000L)), 0);

        assertEquals(((Position) event.get(Position.class)).getLat(), 2.0);
        assertEquals(event.getSource().getId(), "12345");
    }

    @Test
    public void testUpdateEvent() {
        Event event = eventApi.getEvent("10");
        event.setText("Window sensor was triggered.");
        ((Position) event.get(Position.class)).setLat(3.0);
        eventApi.update(event);

        verify(client, times(1)).doPutRequest(contains("Window sensor was triggered."), anyString(), anyString());
        verify(client, times(1)).doPutRequest(contains("3.0"), anyString(), anyString());
        verify(client, times(0)).doPutRequest(contains("time"), anyString(), anyString());
        verify(client, times(0)).doPutRequest(contains("creationTime"), anyString(), anyString());
        verify(client, times(0)).doPutRequest(contains("type"), anyString(), anyString());
        verify(client, times(0)).doPutRequest(contains("source"), anyString(), anyString());
    }

    /**
     * @return A mocked Cloud of Things REST client.
     */
    @Nonnull
    private CloudOfThingsRestClient createClient() {
        final CloudOfThingsRestClient client = Mockito.mock(CloudOfThingsRestClient.class);
        Mockito.doReturn(EVENT_JSON_EXAMPLE).when(client).getResponse(eq("10"), any(String.class), any(String.class));
        Mockito.doThrow(CotSdkException.class).when(client).getResponse(eq("foo"), any(String.class), any(String.class));

        return client;
    }

}
