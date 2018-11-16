package com.telekom.m2m.cot.restsdk.measurement;

import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;

/**
 * Created by Patrick Steinert on 07.02.16.
 */
public class MeasurementApiTest {

    @Test(expectedExceptions = CotSdkException.class)
    public void testGetEventWithFailure() {
        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        Mockito.doThrow(CotSdkException.class).when(rc).getResponse(any(String.class), any(String.class), any(String.class));

        MeasurementApi measurementApi = new MeasurementApi(rc);
        measurementApi.getMeasurement("1234");
    }

}
