package com.telekom.m2m.cot.restsdk.inventory;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;

/**
 * Created by breucking on 03.02.16.
 */
public class InventoryApiTest {
    @Test(expectedExceptions = CotSdkException.class)
    public void testGetManagedObjects() throws Exception {
        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        CloudOfThingsPlatform platform = Mockito.mock(CloudOfThingsPlatform.class);
        Mockito.when(platform.getInventoryApi()).thenReturn(new InventoryApi(rc));
        Mockito.when(rc.getResponse(any(String.class), any(String.class), any(String.class))).thenThrow(CotSdkException.class);

        InventoryApi inventoryApi = platform.getInventoryApi();
        inventoryApi.get("foo");
    }
}
