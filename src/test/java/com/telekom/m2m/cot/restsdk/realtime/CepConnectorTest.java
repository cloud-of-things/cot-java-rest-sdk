package com.telekom.m2m.cot.restsdk.realtime;

import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;


public class CepConnectorTest {

    CloudOfThingsRestClient cloudOfThingsRestClient;


    @BeforeMethod
    public void setup() {
        cloudOfThingsRestClient = mock(CloudOfThingsRestClient.class);
    }


    // This test will disconnect a running connect loop, first while it is waiting for a response and then while it
    // is sleeping between connect requests. In both situations a clean shutdown must be possible.
    @Test
    public void testDisconnectM() throws InterruptedException {
        when(cloudOfThingsRestClient.doPostRequest(anyString(), anyString(), anyString())).
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

        CepConnector connector = new CepConnector(cloudOfThingsRestClient, CepApi.NOTIFICATION_PATH);

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

}

