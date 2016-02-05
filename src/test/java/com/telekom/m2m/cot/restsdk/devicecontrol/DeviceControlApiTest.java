package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;

/**
 * Created by breucking on 03.02.16.
 */
public class DeviceControlApiTest {
    @Test(expectedExceptions = CotSdkException.class)
    public void testGetManagedObjects() throws Exception {
        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        CloudOfThingsPlatform platform = Mockito.mock(CloudOfThingsPlatform.class);
        Mockito.when(platform.getDeviceControlApi()).thenReturn(new DeviceControlApi(rc));
        Mockito.doThrow(CotSdkException.class).when(rc).doPutRequest(any(String.class), any(String.class), any(String.class));

        DeviceControlApi deviceControlApi = platform.getDeviceControlApi();
        deviceControlApi.acceptDevice("foo");
    }
}
