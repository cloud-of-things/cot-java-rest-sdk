package com.telekom.m2m.cot.restsdk.inventory;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

/**
 * Created by Patrick Steinert on 03.02.16.
 */
public class InventoryApiTest {
    @Test(expectedExceptions = CotSdkException.class)
    public void testExceptionHandlingGetManagedObjects() {
        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        CloudOfThingsPlatform platform = Mockito.mock(CloudOfThingsPlatform.class);
        Mockito.when(platform.getInventoryApi()).thenReturn(new InventoryApi(rc));
        Mockito.when(rc.getResponse(any(String.class), any(String.class), any(String.class))).thenThrow(CotSdkException.class);

        InventoryApi inventoryApi = platform.getInventoryApi();
        inventoryApi.get("foo");
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testHttpExceptionHandlingGetManagedObjects() {
        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        CloudOfThingsPlatform platform = Mockito.mock(CloudOfThingsPlatform.class);
        Mockito.when(platform.getInventoryApi()).thenReturn(new InventoryApi(rc));
        Mockito.when(rc.getResponse(any(String.class), any(String.class), any(String.class))).thenThrow(CotSdkException.class);

        InventoryApi inventoryApi = platform.getInventoryApi();
        inventoryApi.get("foo");
        Assert.fail("Needs to Fail on return codes 422 and 402");
    }

    @Test
    public void testUpdateManageObjects() {
        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        CloudOfThingsPlatform platform = Mockito.mock(CloudOfThingsPlatform.class);
        Mockito.when(platform.getInventoryApi()).thenReturn(new InventoryApi(rc));
        //Mockito.when(rc.getResponse(any(String.class), any(String.class), any(String.class))).thenThrow(CotSdkException.class);


        ManagedObject mo = new ManagedObject();

        InventoryApi inventoryApi = platform.getInventoryApi();
        inventoryApi.update(mo);

        Mockito.verify(rc, times(1)).doPutRequest(anyString(), anyString(), anyString());
    }

    @Test
    public void testRegisterAsChildDevice() {

        // given
        final CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        final CloudOfThingsPlatform platform = Mockito.mock(CloudOfThingsPlatform.class);
        Mockito.when(platform.getInventoryApi()).thenReturn(new InventoryApi(rc));

        final ManagedObject parentDevice = new ManagedObject();
        final ManagedObject childDevice = new ManagedObject();

        // when
        final InventoryApi inventoryApi = platform.getInventoryApi();
        inventoryApi.registerAsChildDevice(parentDevice, childDevice);

        // then
        Mockito.verify(rc).doPostRequest(anyString(), anyString(), anyString(), anyString());
    }

}
