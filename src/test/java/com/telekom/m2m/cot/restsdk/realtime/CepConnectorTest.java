package com.telekom.m2m.cot.restsdk.realtime;

import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;


public class CepConnectorTest {

    CloudOfThingsRestClient cloudOfThingsRestClient;

    CepConnector connector;
    boolean notedError = false;


    @BeforeMethod
    public void setup() {
        cloudOfThingsRestClient = mock(CloudOfThingsRestClient.class);
    }

    @AfterMethod
    public void tearDown() throws InterruptedException {
        if (connector != null) {
            connector.disconnect();
        }
    }

    // This test will disconnect a running connect loop, first while it is waiting for a response and then while it
    // is sleeping between connect requests. In both situations a clean shutdown must be possible.
    @Test
    public void testDisconnect() throws InterruptedException {
        when(cloudOfThingsRestClient.doPostRequest(anyString(), anyString(), anyString(), anyString())).
                thenReturn("[{\"clientId\":\"My-Client-Id\"}]");
        when(cloudOfThingsRestClient.doRealTimePollingRequest(anyString(), anyString(), anyString(), anyInt())).
                thenAnswer(new Answer<String>() { // First answer, will keep the connect waiting.
                    @Override
                    public String answer(InvocationOnMock invocation) {
                        try {
                            Thread.sleep(1000); // We just delay the mock server response, until the test finishes.
                        } catch (InterruptedException ex) {
                            // That's ok, we interrupted it ourselves here.
                        }
                        return "[]";
                    }
                }).
                thenAnswer(new Answer<String>() { // Second answer, will let the connect finish, so we go to the poll-wait-sleep.
                    @Override
                    public String answer(InvocationOnMock invocation) {
                        return "[]";
                    }
                });

        connector = new CepConnector(cloudOfThingsRestClient, CepApi.NOTIFICATION_PATH);

        // With these settings the interruption will happen when the connect is still waiting:
        connector.setTimeout(1000);
        connector.setInterval(10);

        connector.connect();

        // Wait a bit for the thread to start:
        Thread.sleep(20);

        assertTrue(connector.isConnected());
        assertNotNull(connector.getClientId());

        connector.disconnect();

        // Wait a bit for things to settle down:
        Thread.sleep(20);

        assertFalse(connector.isConnected());
        assertNull(connector.getClientId());

        // Now we reverse the times, so the interruption will happen while the polling thread is sleeping:
        connector.setTimeout(10);
        connector.setInterval(1000);

        connector.connect();

        // Wait a bit for the thread to start:
        Thread.sleep(20);

        assertTrue(connector.isConnected());
        assertNotNull(connector.getClientId());

        connector.disconnect();

        // Wait a bit for things to settle down:
        Thread.sleep(20);

        assertFalse(connector.isConnected());
        assertNull(connector.getClientId());
    }

    @Test
    public void testSubscriptionFailed() throws InterruptedException {
        // return clientId on handshake
        when(cloudOfThingsRestClient.doPostRequest(contains("/meta/handshake"), anyString(), anyString(), anyString())).
                thenReturn("[{\"clientId\" : \"abc123\"}]");

        // return failure on doing initial subscriptions
        when(cloudOfThingsRestClient.doPostRequest(contains("/meta/subscribe"), anyString(), anyString(), anyString())).
                thenReturn("[{\"channel\":\"/meta/subscribe\",\"error\":\"402::Unknown client\",\"successful\":false}]");

        final List<String> notedOperations = new ArrayList<>();
        notedError = false;

        connector = new CepConnector(cloudOfThingsRestClient, CepApi.NOTIFICATION_PATH);

        connector.setTimeout(1000);
        connector.setInterval(10);

        connector.subscribe("/123");
        connector.addListener(new SubscriptionListener() {
            @Override
            public void onNotification(String channel, Notification notification) {
                notedOperations.add(notification.getData().toString());
            }

            @Override
            public void onError(String channel, Throwable error) {
                notedError = true;
            }
        });

        connector.connect();

        // Wait a bit for things to settle down:
        Thread.sleep(20);

        assertTrue(notedError);

    }

    @Test
    public void testConnectionFailed() throws InterruptedException {
        // return clientId on handshake
        when(cloudOfThingsRestClient.doPostRequest(contains("/meta/handshake"), anyString(), anyString(), anyString())).
                thenReturn("[{\"clientId\" : \"abc123\"}]");

        // return failure on connect
        when(cloudOfThingsRestClient.doRealTimePollingRequest(anyString(), anyString(), anyString(), anyInt())).
                thenReturn("[\n" +
                        "  {\n" +
                        "     \"successful\" : false,\n" +
                        "     \"error\" : \"402::Unknown client\",\n" +
                        "     \"channel\" : \"/meta/connect\",\n" +
                        "     \"advice\" : {\n" +
                        "        \"reconnect\" : \"handshake\",\n" +
                        "        \"interval\" : 0\n" +
                        "     }\n" +
                        "  }\n" +
                        "]");

        final List<String> notedOperations = new ArrayList<>();
        notedError = false;

        connector = new CepConnector(cloudOfThingsRestClient, CepApi.NOTIFICATION_PATH);

        connector.setTimeout(1000);
        connector.setInterval(10);

        connector.addListener(new SubscriptionListener() {
            @Override
            public void onNotification(String channel, Notification notification) {
                notedOperations.add(notification.getData().toString());
            }

            @Override
            public void onError(String channel, Throwable error) {
                notedError = true;
            }
        });

        connector.connect();

        // Wait a bit for things to settle down:
        Thread.sleep(20);

        assertTrue(notedError);

    }

}

