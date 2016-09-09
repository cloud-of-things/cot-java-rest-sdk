package com.telekom.m2m.cot.restsdk;

import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by breucking on 30.01.16.
 */
public class CloudOfThingsPlatformTest {

    @Test
    public void testGetManagemendApi() throws Exception {
        CloudOfThingsPlatform cotPlatform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
        cotPlatform.getManagementApi();
    }

    @Test
    public void testGetInventoryApi() throws Exception {
        CloudOfThingsPlatform cotPlatform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
        InventoryApi inventoryApi = cotPlatform.getInventoryApi();
        Assert.assertNotNull(inventoryApi);
    }
}
