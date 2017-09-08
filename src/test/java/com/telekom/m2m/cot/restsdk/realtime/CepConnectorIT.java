package com.telekom.m2m.cot.restsdk.realtime;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.users.UserApi;
import com.telekom.m2m.cot.restsdk.util.TestHelper;

/**
 * Currently, most of the tests for the CepConnector class are implemented in the CepApiIT.
 *
 */
public class CepConnectorIT {
    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME,
            TestHelper.TEST_PASSWORD);
    final UserApi userApi = cotPlat.getUserApi();




}
