package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

import static org.mockito.Matchers.any;

/**
 * Created by breucking on 03.02.16.
 */
public class DeviceControlApiTest {
    @Test(expectedExceptions = CotSdkException.class)
    public void testAcceptDevice() throws Exception {
        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        CloudOfThingsPlatform platform = Mockito.mock(CloudOfThingsPlatform.class);
        Mockito.when(platform.getDeviceControlApi()).thenReturn(new DeviceControlApi(rc));
        Mockito.doThrow(CotSdkException.class).when(rc).doPutRequest(any(String.class), any(String.class), any(String.class));

        DeviceControlApi deviceControlApi = platform.getDeviceControlApi();
        deviceControlApi.acceptDevice("foo");
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testGetOperationWithFailure() throws Exception {
        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        CloudOfThingsPlatform platform = Mockito.mock(CloudOfThingsPlatform.class);
        Mockito.when(platform.getDeviceControlApi()).thenReturn(new DeviceControlApi(rc));
        Mockito.doThrow(CotSdkException.class).when(rc).getResponse(any(String.class), any(String.class), any(String.class));

        DeviceControlApi deviceControlApi = platform.getDeviceControlApi();
        deviceControlApi.getOperation("foo");
    }

    @Test
    public void testGetOperation() throws Exception {

        String eventJsonExample = "{\n" +
                "  \"id\" : \"123\",\n" +
                "  \"self\" : \"<<This Operation URL>>\",\n" +
                "  \"deviceId\" : \"1243\",\n" +
                "  \"status\" : \"PENDING\",\n" +
                "  \"creationTime\" : \"2011-09-06T12:03:27.000+02:00\",\n" +
                "  \"com_telekom_model_WebCamDevice\" : {\n" +
                "    \"name\" : \"take picture\",\n" +
                "    \"parameters\" : {\n" +
                "      \"duration\" : \"5s\",\n" +
                "      \"quality\" : \"HD\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        CloudOfThingsPlatform platform = Mockito.mock(CloudOfThingsPlatform.class);
        Mockito.when(platform.getDeviceControlApi()).thenReturn(new DeviceControlApi(rc));
        Mockito.when(rc.getResponse(any(String.class), any(String.class), any(String.class))).thenReturn(eventJsonExample);

        DeviceControlApi deviceControlApi = platform.getDeviceControlApi();
        Operation operation = deviceControlApi.getOperation("xyz");

        Assert.assertEquals(operation.getId(), "123");
        Assert.assertEquals(operation.getCreationTime().compareTo(new Date(1315303407000L)), 0);
        Assert.assertEquals(operation.getStatus(), "PENDING");
        Assert.assertEquals(operation.getDeviceId(), "1243");

        Object obj = operation.get("com_telekom_model_WebCamDevice");
        Assert.assertNotNull(obj);
    }
}
