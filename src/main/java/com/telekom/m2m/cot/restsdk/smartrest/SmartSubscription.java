package com.telekom.m2m.cot.restsdk.smartrest;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * SmartSubscription holds the information that is necessary to describe an individual subscription.
 * Both channels and listeners can be part of any number of SmartSubscription instances.
 */
public class SmartSubscription {

    private final SmartCepConnector connector;
    private final String channel;
    private final Set<Integer> messageIds = new HashSet<>();
    private final SmartSubscriptionListener listener;
    private final SortedSet<String> xIds = new TreeSet<>();;


    public SmartSubscription(SmartCepConnector connector,
                             String channel,
                             Collection<Integer> messageIds,
                             SmartSubscriptionListener listener,
                             Collection<String> xIds) {
        this.connector = connector;
        this.channel = channel;
        this.listener = listener;
        this.messageIds.addAll(messageIds);
        this.xIds.addAll(xIds);
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

    // TODO: subscribe with multiple X-IDs
    public SortedSet<String> getxIds() {
        return Collections.unmodifiableSortedSet(xIds);
    }

    /**
     * Check whether this subscription applies to a certain messageId.
     * In the SmartREST-context responses do not contain the channel name in a standardized way, so the only possible
     * matching criteria, to avoid having to send all messages to all listeners, is the messageId (i.e. the ID of
     * the SmartResponseTemplate that created the response line in question).
     *
     * @param messageId
     * @return
     */
    public boolean appliesTo(int messageId) {
        // If there are no messageIds to act as a filter then we want to receive everything:
        if (messageIds.isEmpty()) {
            return true;
        }

        return messageIds.contains(messageId);
    }
}
