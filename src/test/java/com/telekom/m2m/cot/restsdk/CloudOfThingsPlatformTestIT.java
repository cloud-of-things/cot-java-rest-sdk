package com.telekom.m2m.cot.restsdk;

import com.telekom.m2m.cot.restsdk.devicecontrol.CotCredentials;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/**
 * Created by Patrick Steinert on 30.01.16.
 */
public class CloudOfThingsPlatformTestIT {

    @Test(enabled = false) // Enable if you have a proxy to test with
    public void testProxyConnection() {

    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testBadProxyConnection() {
        CloudOfThingsPlatform cotPlatform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD, "127.99.88.77", 1111);
        InventoryApi inventoryApi = cotPlatform.getInventoryApi();
        inventoryApi.get("test");
    }

    @Test
    public void testWithCredentials() {
        CotCredentials cotCredentials = new CotCredentials(TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
        CloudOfThingsPlatform cotPlatform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, cotCredentials);

        InventoryApi inventoryApi = cotPlatform.getInventoryApi();
        assertNotNull(inventoryApi);

        inventoryApi.get("test");
    }

    @Test
    public void testCredentialsWithTenantUsername() {
        CotCredentials cotCredentials = new CotCredentials(TestHelper.TEST_TENANT,
                TestHelper.TEST_TENANT + "/" + TestHelper.TEST_USERNAME,
                TestHelper.TEST_PASSWORD);
        CloudOfThingsPlatform cotPlatform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, cotCredentials);

        InventoryApi inventoryApi = cotPlatform.getInventoryApi();
        assertNotNull(inventoryApi);

        inventoryApi.get("test");
    }

    /**
     * @see <a href="https://github.com/cloud-of-things/cot-java-rest-sdk/issues/54">Corresponding Issue</a>
     */
    @Test
    public void testRequestWithManyCloudOfThingsPlatformsNotUsingToMuchResources() {
        final int initialNumberOfThreads = Thread.activeCount();

        // used separate Cloud of Things Platforms to execute different post Request
        for (int i = 0; i < 100; i++) {
            final CloudOfThingsPlatform cotPlatform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
            InventoryApi inventoryApi = cotPlatform.getInventoryApi();
            inventoryApi.get("test");
        }
        final int numberOfThreadsAfterRestCalls = Thread.activeCount();

        final int numberOfCreatedThreads = numberOfThreadsAfterRestCalls - initialNumberOfThreads;
        final int allowedNumberOfAdditionalThreads = 20;
        Assert.assertTrue(
                numberOfCreatedThreads < allowedNumberOfAdditionalThreads,
                "Expected at most " + allowedNumberOfAdditionalThreads + " created threads, but the SDK REST calls created " + numberOfCreatedThreads + " threads."
        );
    }
}
