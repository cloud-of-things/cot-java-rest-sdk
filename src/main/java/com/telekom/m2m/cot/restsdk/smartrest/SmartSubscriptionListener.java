package com.telekom.m2m.cot.restsdk.smartrest;


/**
 * A SmartSubscriptionListener receives realtime notifications for channels to which it was registered.
 */
public interface SmartSubscriptionListener {

    /**
     * This method will be called by the {@link SmartCepConnector} for each matching notification that it receives.
     *
     * @param subscription the {@link SmartSubscription} that caused the match.
     *                     Important because the same SmartSubscriptionListener can be registered for multiple channels.
     * @param notification the {@link SmartNotification} that contains all the matching response lines.
     */
    public void onNotification(SmartSubscription subscription, SmartNotification notification);


    /**
     * If the SmartCepConnector receives an error then it will pass it on to all it's listeners by calling this method.
     */
    public void onError(SmartSubscription subscription, Throwable error);

}
