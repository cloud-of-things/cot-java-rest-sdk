package com.telekom.m2m.cot.restsdk.identity;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Patrick Steinert on 12.12.2016.
 */
public class IdentityApiIT {
    CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    private ManagedObject testManagedObject;

    @BeforeClass
    public void setUp() {
        testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name");
    }

    @AfterClass
    public void tearDown() {
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
    }

    @Test
    public void testNotFoundGet() throws Exception {
        IdentityApi idApi = cotPlat.getIdentityApi();
        ExternalId externalId = new ExternalId();
        externalId.setExternalId("456789");
        ExternalId foo = idApi.getExternalId(externalId);
        Assert.assertNull(foo);
    }

    @Test
    public void testCreateAndGetAndDelete() throws Exception {
        IdentityApi idApi = cotPlat.getIdentityApi();
        ExternalId externalId = new ExternalId();
        externalId.setExternalId("456789");
        externalId.setType("com_telekom_test");
        externalId.setManagedObject(testManagedObject);

        ExternalId newExtId = idApi.create(externalId);
        Assert.assertNotNull(newExtId.getExternalId());

        ExternalId foundId = idApi.getExternalId(newExtId);
        Assert.assertNotNull(foundId);

        idApi.delete(newExtId);

        foundId = idApi.getExternalId(newExtId);
        Assert.assertNull(foundId);
    }
}
