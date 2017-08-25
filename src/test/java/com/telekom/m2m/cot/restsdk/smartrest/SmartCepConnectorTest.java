package com.telekom.m2m.cot.restsdk.smartrest;


import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.telekom.m2m.cot.restsdk.smartrest.SmartRestApi.MSG_REALTIME_CONNECT;
import static com.telekom.m2m.cot.restsdk.smartrest.SmartRestApi.MSG_REALTIME_HANDSHAKE;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;


public class SmartCepConnectorTest {

    CloudOfThingsRestClient cloudOfThingsRestClient;


    @BeforeMethod
    public void setup() {
        cloudOfThingsRestClient = mock(CloudOfThingsRestClient.class);
    }


    @Test
    public void testMultiListeners() throws InterruptedException {
        SmartCepConnector connector =  new SmartCepConnector(cloudOfThingsRestClient, "My-X-Id");

        when(cloudOfThingsRestClient.doSmartRealTimeRequest("My-X-Id", MSG_REALTIME_HANDSHAKE)).
                thenReturn(new String[]{"My-Client-Id"});
        when(cloudOfThingsRestClient.doSmartRealTimePollingRequest("My-X-Id", MSG_REALTIME_CONNECT + "," + "My-Client-Id", connector.getTimeout())).
                thenReturn(new String[]{"100,1,my-notification",
                        "101,1,my-extra-data,foo",
                        "86,,1000,10000,retry"}). // This is a control message, that should not go to the listeners.
                thenAnswer(new Answer<String[]>() { // This is for the second polling loop iteration.
                    @Override
                    public String[] answer(InvocationOnMock invocation) throws InterruptedException {
                        Thread.sleep(75); // We just delay the mock server response, until the test finishes.
                        return null;
                    }
                });

        // Prepare subscription:
        connector.subscribe("/alarms/mock-channel", null);

        // The asynchronously received alarms will be stored in this list:
        final List<String> notedAlarms = new ArrayList<>();

        SmartListener listener1 = new TestSmartListener("L1", notedAlarms);
        SmartListener listener2 = new TestSmartListener("L2", notedAlarms);
        SmartListener listener3 = new TestSmartListener("L3", notedAlarms);

        connector.addListener(listener1);
        connector.addListener(listener2);
        connector.addListener(listener3);

        connector.connect();

        // That should be long enough to start the thread, call the cloudOfThingsRestClient mock and
        // distribute the notifications:
        Thread.sleep(50);

        assertEquals(notedAlarms.size(), 6, "Each of our three listeners should have gotten 2 notifications.");
        assertTrue(notedAlarms.contains("L1_100,1,my-notification"));
        assertTrue(notedAlarms.contains("L2_100,1,my-notification"));
        assertTrue(notedAlarms.contains("L3_100,1,my-notification"));
        assertTrue(notedAlarms.contains("L1_101,1,my-extra-data,foo"));
        assertTrue(notedAlarms.contains("L2_101,1,my-extra-data,foo"));
        assertTrue(notedAlarms.contains("L3_101,1,my-extra-data,foo"));
    }


    @Test
    public void testAdvice() throws InterruptedException {
        SmartCepConnector connector =  new SmartCepConnector(cloudOfThingsRestClient, "My-X-Id");

        int initialTimeout = 10;
        connector.setTimeout(initialTimeout);
        int initialInterval = 10;
        connector.setInterval(initialInterval);

        when(cloudOfThingsRestClient.doSmartRealTimeRequest("My-X-Id", MSG_REALTIME_HANDSHAKE)).
                thenReturn(new String[]{"My-Client-Id"});
        when(cloudOfThingsRestClient.doSmartRealTimePollingRequest(eq("My-X-Id"), eq(MSG_REALTIME_CONNECT + "," + "My-Client-Id"), anyInt())).
                thenReturn(new String[]{"100,1,my-notification",
                        "101,1,my-extra-data,foo",
                        "86,,1000,100,retry"}).
                thenAnswer(new Answer<String[]>() { // This is for the second polling loop iteration.
                    @Override
                    public String[] answer(InvocationOnMock invocation) throws InterruptedException {
                        Thread.sleep(100); // We just delay the mock server response, until the test finishes.
                        return null;
                    }
                });

        connector.connect();

        Thread.sleep(50);

        assertEquals(connector.getTimeout(), 1000);
        assertEquals(connector.getInterval(), 100);
    }


    @Test
    public void testErrors() throws InterruptedException {
        SmartCepConnector connector =  new SmartCepConnector(cloudOfThingsRestClient, "My-X-Id");

        when(cloudOfThingsRestClient.doSmartRealTimeRequest("My-X-Id", MSG_REALTIME_HANDSHAKE)).
                thenReturn(new String[]{"My-Client-Id"});
        when(cloudOfThingsRestClient.doSmartRealTimePollingRequest(eq("My-X-Id"), eq(MSG_REALTIME_CONNECT + "," + "My-Client-Id"), anyInt())).
                thenReturn(new String[]{"40,No template for this X-ID"}).
                thenAnswer(new Answer<String[]>() { // This is for the second polling loop iteration.
                    @Override
                    public String[] answer(InvocationOnMock invocation) throws InterruptedException {
                        Thread.sleep(100); // We just delay the mock server response, until the test finishes.
                        return null;
                    }
                });

        // The asynchronously received exceptions will be stored in this list:
        final List<Throwable> notedExceptions = new ArrayList<>();

        // Prepare listener for the channel of our test device:
        connector.addListener(new SmartListener() {
            @Override
            public void onNotification(SmartNotification notification) {
                fail("There should have been no notifications.");
            }

            @Override
            public void onError(Throwable error) {
                notedExceptions.add(error);
            }
        });
        connector.connect();

        Thread.sleep(50);

        assertEquals(notedExceptions.size(), 1);
        assertTrue(notedExceptions.get(0).getMessage().contains("40,No template for this X-ID"));
    }


    // This test will disconnect a running connect loop, first while it is waiting for a response and then while it
    // is sleeping between connect requests. In both situations a clean shutdown must be possible.
    @Test
    public void testDisconnect() throws InterruptedException {
        when(cloudOfThingsRestClient.doSmartRealTimeRequest("My-X-Id", MSG_REALTIME_HANDSHAKE)).
                thenReturn(new String[]{"My-Client-Id"});
        when(cloudOfThingsRestClient.doSmartRealTimePollingRequest(eq("My-X-Id"), eq(MSG_REALTIME_CONNECT + "," + "My-Client-Id"), anyInt())).
                thenAnswer(new Answer<String[]>() { // First answer, will keep the connect waiting.
                    @Override
                    public String[] answer(InvocationOnMock invocation) {
                        try {
                            Thread.sleep(1000); // We just delay the mock server response, until the test finishes.
                        } catch (InterruptedException ex) {
                            // That's ok, we interrupted it ourselves here.
                        }
                        return new String[]{};
                    }
                }).
                thenAnswer(new Answer<String[]>() { // Second answer, will let the connect finish, so we go to the poll-wait-sleep.
                    @Override
                    public String[] answer(InvocationOnMock invocation) {
                        return new String[]{};
                    }
                });

        SmartCepConnector connector =  new SmartCepConnector(cloudOfThingsRestClient, "My-X-Id");

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


    public class TestSmartListener implements SmartListener {

        private String name;
        private List<String> notedAlarms;

        public TestSmartListener(String name, List<String> notedAlarms) {
            this.name = name;
            this.notedAlarms = notedAlarms;
        }

        @Override
        public void onNotification(SmartNotification notification) {
            notedAlarms.add(name + "_" + notification.getMessageId() + "," + notification.getData());
        }

        @Override
        public void onError(Throwable error) {
            fail("We expect no errors in these tests.");
        }
    }

}
