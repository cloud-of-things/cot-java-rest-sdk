package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;

/**
 * Created by Patrick Steinert on 02.01.17.
 */
public class BulkOperationTest {

    @Test
    public void testBulkOperations() throws Exception {

        String bulkOperationJsonExample = "{\n" +
                "  \"id\" : \"123\",\n" +
                "  \"self\" : \"<<This BulkOperation URL>>\",\n" +
                "  \"groupId\" : \"124301\",\n" +
                "  \"status\" : \"ACTIVE\",\n" +
                "  \"startDate\" : \"2011-09-06T12:03:27\",\n" +
                "  \"operationPrototype\":{\"test\"=>\"TEST1\"},\n" +
                "  \"creationRamp\":15,\n" +
                "  \"progress\":\n" +
                "    {\n" +
                "     \"pending\":0, \"failed\":0, \"executing\":0, \"successful\":0, \"all\":2\n" +
                "    }\n" +
                "}";

        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        CloudOfThingsPlatform platform = Mockito.mock(CloudOfThingsPlatform.class);
        Mockito.when(platform.getDeviceControlApi()).thenReturn(new DeviceControlApi(rc));
        Mockito.when(rc.getResponse(any(String.class), any(String.class), any(String.class))).thenReturn(
                bulkOperationJsonExample);

        DeviceControlApi inventoryApi = platform.getDeviceControlApi();
        BulkOperation bulkOperation = inventoryApi.getBulkOperation("0");

        Assert.assertEquals(bulkOperation.getId(), "123");
        Assert.assertEquals(bulkOperation.getGroupId(), "124301");

    }


}
