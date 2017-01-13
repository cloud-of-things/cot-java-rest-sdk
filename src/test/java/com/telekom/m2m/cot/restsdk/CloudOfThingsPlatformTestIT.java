package com.telekom.m2m.cot.restsdk;

import com.telekom.m2m.cot.restsdk.devicecontrol.CotCredentials;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by Patrick Steinert on 30.01.16.
 */
public class CloudOfThingsPlatformTestIT {

    @Test(enabled = false) // Enable if you have a proxy to test with
    public void testProxyConnection() throws Exception {

    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testBadProxyConnection() throws Exception {
        CloudOfThingsPlatform cotPlatform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD, "127.99.88.77", 1111);
        InventoryApi inventoryApi = cotPlatform.getInventoryApi();
        inventoryApi.get("test");
    }

    @Test
    public void testWithCredentials() throws Exception {
        CotCredentials cotCredentials = new CotCredentials(TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
        CloudOfThingsPlatform cotPlatform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, cotCredentials);
        InventoryApi inventoryApi = cotPlatform.getInventoryApi();
        inventoryApi.get("test");
        Assert.assertNotNull(inventoryApi);
    }

}
