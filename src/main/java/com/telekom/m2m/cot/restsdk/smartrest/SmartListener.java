package com.telekom.m2m.cot.restsdk.smartrest;


/**
 * A SmartListener receives real time notifications for messageIds, to which it
 * was registered in the {@link SmartCepConnector}.
 * <p>
 * Note that in the SmartREST context the notifications cannot be matched to the listeners via channel names, because
 * the responses do not contain the channel name in a standardized way. Therefore every listener currently receives all
 * notifications and needs to filter them by itself.
 * </p>
 */
public interface SmartListener {

    /**
     * This method will be called by the {@link SmartCepConnector} for each matching notification that it receives.
     *
     * @param notification the {@link SmartNotification} that contains the information from one line in the response
     */
    void onNotification(SmartNotification notification);


    /**
     * If the SmartCepConnector receives an error then it will pass it on to all its listeners by calling this method.
     *
     * @param error will usually be a {@link com.telekom.m2m.cot.restsdk.util.CotSdkException}
     */
    void onError(Throwable error);

}
