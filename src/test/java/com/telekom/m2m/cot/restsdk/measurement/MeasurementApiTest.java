package com.telekom.m2m.cot.restsdk.measurement;

import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.Filter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Created by Patrick Steinert on 07.02.16.
 */
public class MeasurementApiTest {

    /**
     * System under test.
     */
    private MeasurementApi measurementApi;

    /**
     * Mocked client that is used by the test subject.
     */
    private CloudOfThingsRestClient restClient;

    @BeforeMethod
    public void setup() {
        restClient = mock(CloudOfThingsRestClient.class);
        measurementApi = new MeasurementApi(restClient);
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testGetEventWithFailure() {
        doThrow(CotSdkException.class)
            .when(restClient)
            .getResponse(any(String.class), any(String.class), any(String.class));

        measurementApi.getMeasurement("1234");
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testThrowsExceptionIfClientReturnsNull() {
        doReturn(null)
            .when(restClient)
            .getResponse(any(String.class), any(String.class), any(String.class));

        measurementApi.getMeasurement("1234");
    }

    @Test
    public void deleteMeasurementsDoesNotThrowExceptionIfNoFilterIsPassed() {
        measurementApi.deleteMeasurements(null);
    }

    @Test
    public void deleteMeasurementsDoesNotThrowExceptionIfFiltersAreProvided() {
        measurementApi.deleteMeasurements(Filter.build().bySource("test"));
    }
}
