package com.telekom.m2m.cot.restsdk.inventory;


import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.inventory.util.TestHelper;
import org.testng.annotations.Test;

/**
 * Created by breucking on 30.01.16.
 */
public class CloudOfThingsRestClientTest {

    @Test
    public void testConnection() throws Exception {
        CloudOfThingsRestClient cotRestClient = new CloudOfThingsRestClient(TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    }
}
