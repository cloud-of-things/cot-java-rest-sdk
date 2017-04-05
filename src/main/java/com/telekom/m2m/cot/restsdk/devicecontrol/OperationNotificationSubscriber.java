package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.BeyauxWrapper;
import com.telekom.m2m.cot.restsdk.util.Subscriber;
import com.telekom.m2m.cot.restsdk.util.SubscriptionListener;

/**
 * Created by Patrick Steinert on 13.01.17.
 */
public class OperationNotificationSubscriber implements Subscriber<String, Operation> {

    private final CloudOfThingsRestClient cloudOfThingsRestClient;

    public OperationNotificationSubscriber(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
        BeyauxWrapper beyauxWrapper = new BeyauxWrapper();
    }

    @Override
    public void subscribe(String id, SubscriptionListener<String, Operation> subscriptionListener) {

    }

    @Override
    public void disconnect() {

    }
}
