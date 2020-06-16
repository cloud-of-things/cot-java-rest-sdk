package com.telekom.m2m.cot.restsdk;

import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by Patrick Steinert on 30.01.16.
 */
public class CloudOfThingsPlatformTest {
    private final static String TEST_HOST = "https://test.m2m.telekom.com";
    private final static String TEST_USERNAME = "tester";
    private final static String TEST_PASSWORD = "anything-goes";

    @Test
    public void testGetInventoryApi() {
        CloudOfThingsPlatform cotPlatform = new CloudOfThingsPlatform(TEST_HOST, TEST_USERNAME, TEST_PASSWORD);
        InventoryApi inventoryApi = cotPlatform.getInventoryApi();
        Assert.assertNotNull(inventoryApi);
    }

}
