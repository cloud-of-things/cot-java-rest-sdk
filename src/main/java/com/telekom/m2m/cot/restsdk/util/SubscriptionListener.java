package com.telekom.m2m.cot.restsdk.util;

/**
 * Created by Patrick Steinert on 13.01.17.
 */
public interface SubscriptionListener<T, R> {
    void onError(Subscription<T> subscription, Throwable ex);

    void onNotification(Subscription<T> subscription, R notification);
}
