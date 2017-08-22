package com.telekom.m2m.cot.restsdk.smartrest;


/**
 * A SmartSubscriptionListener receives real time notifications for messageIds, to which it
 * was subscribed (with a {@link SmartSubscription}.
 * Note that in the SmartREST context the notifications cannot be matched to the listeners via channel names, but only
 * by messageId, because the responses do not contain the channel name in a standardized way.
 */
public interface SmartSubscriptionListener {

    /**
     * This method will be called by the {@link SmartCepConnector} for each matching notification that it receives.
     *
     * @param subscription the {@link SmartSubscription} that caused the match.
     *                     Important because the same SmartSubscriptionListener can be registered multiple times.
     * @param notification the {@link SmartNotification} that contains all the matching response lines.
     */
    public void onNotification(SmartSubscription subscription, SmartNotification notification);


    /**
     * If the SmartCepConnector receives an error then it will pass it on to all it's listeners by calling this method.
     */
    public void onError(SmartSubscription subscription, Throwable error);

}
