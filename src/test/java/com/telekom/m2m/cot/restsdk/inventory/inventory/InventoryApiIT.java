package com.telekom.m2m.cot.restsdk.inventory.inventory;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.inventory.util.TestHelper;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import org.junit.Assert;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;

/**
 * Created by breucking on 30.01.16.
 */
public class InventoryApiIT {

    @Test
    public void testCreateManagedObject() throws Exception {
        ManagedObject mo = new ManagedObject();
        mo.setName("Hello!");

        CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
        InventoryApi inventoryApi = cotPlat.getInventoryApi();

        ManagedObject createdMo = inventoryApi.create(mo);
        Assert.assertNotNull("Should now have an Id", mo.getId());
    }

    @Test
    public void testCreateAndRead() throws Exception {

        ManagedObject mo = new ManagedObject();
        mo.setName("MyTest-testCreateAndRead");

        CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
        InventoryApi inventoryApi = cotPlat.getInventoryApi();
        ManagedObject createdMo = inventoryApi.create(mo);

        Assert.assertNotNull("Should now have an Id", createdMo.getId());

        ManagedObject retrievedMo = inventoryApi.get(createdMo.getId());

        Assert.assertEquals("Should have the same Id", createdMo.getId(), retrievedMo.getId());
        Assert.assertEquals("Should have the same Name", "MyTest-testCreateAndRead", retrievedMo.getName());
    }

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
