package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.Subscriber;
import com.telekom.m2m.cot.restsdk.util.Subscription;
import com.telekom.m2m.cot.restsdk.util.SubscriptionListener;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by Patrick Steinert on 13.01.17.
 */
public class DeviceControlSubscriberIT {
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
    public void testSubscriber() throws Exception {
        DeviceControlApi dcApi = cotPlat.getDeviceControlApi();
        Subscriber<String, Operation> subscriber = dcApi.getNotificationsSubscriber();
        subscriber.subscribe(testManagedObject.getId(), new SubscriptionListener<String, Operation>() {

            @Override
            public void onError(Subscription<String> subscription, Throwable ex) {

            }

            @Override
            public void onNotification(Subscription<String> subscription, Operation notification) {
                Assert.assertEquals(notification.get("foo"), "bar");
            }
        });

        Operation operation = new Operation();
        operation.setDeviceId(testManagedObject.getId());
        operation.setStatus(OperationStatus.PENDING);

        dcApi.create(operation);

        Thread.sleep(2 * 1000);

        Assert.fail();

    }
}
