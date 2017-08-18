package com.telekom.m2m.cot.restsdk.smartrest;

import java.util.Collections;
import java.util.List;


/**
 * SmartSubscription holds the information that is necessary to describe an individual subscription.
 * Both channels and listeners can be part of any number of SmartSubscription instances.
 */
public class SmartSubscription {

    private final SmartCepConnector connector;
    private final String channel;
    private final SmartSubscriptionListener listener;
    private final List<String> xIds;


    public SmartSubscription(SmartCepConnector connector,
                             String channel,
                             SmartSubscriptionListener listener,
                             List<String> xIds) {
        this.connector = connector;
        this.channel = channel;
        this.listener = listener;
        this.xIds = xIds;
    }


    public void unsubscribe() {
        connector.unsubscribe(this);
    }


    public String getChannel() {
        return channel;
    }

    public SmartSubscriptionListener getListener() {
        return listener;
    }

    public List<String> getxIds() {
        return Collections.unmodifiableList(xIds);
    }
    
}
