package com.telekom.m2m.cot.restsdk;

import com.telekom.m2m.cot.restsdk.identity.ExternalId;
import com.telekom.m2m.cot.restsdk.identity.IdentityApi;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by A4328054 on 12.12.2016.
 */
public class IdentityApiIT {
    CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD, "10.206.247.65", 8080);

    @Test
    public void testNotFoundGet() throws Exception {
        IdentityApi idApi = cotPlat.getIdentityApi();
        ExternalId externalId = new ExternalId();
        externalId.setExternalId("456789");
        ExternalId foo = idApi.getExternalId(externalId);
        Assert.assertNull(foo);
    }
}
