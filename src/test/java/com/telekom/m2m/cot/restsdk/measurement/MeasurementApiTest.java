package com.telekom.m2m.cot.restsdk.measurement;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;

/**
 * Created by breucking on 07.02.16.
 */
public class MeasurementApiTest {

    @Test(expectedExceptions = CotSdkException.class)
    public void testGetEventWithFailure() throws Exception {
        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        CloudOfThingsPlatform platform = Mockito.mock(CloudOfThingsPlatform.class);
        Mockito.when(platform.getMeasurementApi()).thenReturn(new MeasurementApi(rc));
        Mockito.doThrow(CotSdkException.class).when(rc).getResponse(any(String.class), any(String.class), any(String.class));

        MeasurementApi measurementApi = platform.getMeasurementApi();
        measurementApi.getMeasurement("1234");
    }
}
