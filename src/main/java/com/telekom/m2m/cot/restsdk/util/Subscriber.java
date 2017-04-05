package com.telekom.m2m.cot.restsdk.util;

/**
 * Created by Patrick Steinert on 13.01.17.
 */
public interface Subscriber<T, T1> {
    void subscribe(T id, SubscriptionListener<T, T1> subscriptionListener);

    void disconnect();

}
