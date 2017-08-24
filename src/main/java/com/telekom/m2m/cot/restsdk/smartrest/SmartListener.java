package com.telekom.m2m.cot.restsdk.smartrest;


/**
 * A SmartListener receives real time notifications for messageIds, to which it
 * was registered in the {@link SmartCepConnector}.
 * Note that in the SmartREST context the notifications cannot be matched to the listeners via channel names, but only
 * by messageId, because the responses do not contain the channel name in a standardized way.
 */
public interface SmartListener {

    /**
     * This method will be called by the {@link SmartCepConnector} for each matching notification that it receives.
     *
     * @param notification the {@link SmartNotification} that contains all the matching response lines.
     */
    void onNotification(SmartNotification notification);


    /**
     * If the SmartCepConnector receives an error then it will pass it on to all it's listeners by calling this method.
     */
    void onError(Throwable error);

}
