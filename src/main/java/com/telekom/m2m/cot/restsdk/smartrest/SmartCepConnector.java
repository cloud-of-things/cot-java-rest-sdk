package com.telekom.m2m.cot.restsdk.smartrest;


import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * The SmartCepConnector handles subscriptions and holds the connection to the CEP notification service.
 */
public class SmartCepConnector implements Runnable {

    private CloudOfThingsRestClient cloudOfThingsRestClient;

    private String xId;

    private String clientId;

    private boolean connected = false;
    private boolean shallDisconnect = false;

    private List<SmartSubscription> subscriptions = new ArrayList<>();


    /**
     * Construct a new SmartCepConnector with a default X-Id. That Id determines which response templates will be used.
     * Additional X-Ids can be specified for each subscription.
     *
     * @param cloudOfThingsRestClient the client to use for connection to the cloud
     * @param xId default X-Id for all subscriptions.
     */
    public SmartCepConnector(CloudOfThingsRestClient cloudOfThingsRestClient, String xId) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
        this.xId = xId;
    }


    /**
     * Subscribe to a notification channel.
     * The actual subscription isn't sent to the server until {@link #connect()} is called.
     *
     * @param channel Can also contain a wildcard, e.g. to subscribe to all channels in a module (like "/alarms/*").
     * @param listener The {@link SmartSubscriptionListener} that is to evaluate the notifications from this channel.
     * @param additionalXIds X-Ids of any additional {@link SmartResponseTemplate} that shall be used to evaluate
     *                       the responses for this subscription.
     * @return
     */
    public SmartSubscription subscribe(String channel,
                                       SmartSubscriptionListener listener,
                                       String... additionalXIds) {

        List<String> xIds = Arrays.asList(additionalXIds);
        xIds.add(0, xId);
        SmartSubscription subscription = new SmartSubscription(this, channel, listener, xIds);
        subscriptions.add(subscription);
        return subscription;
    }


    public void unsubscribe(SmartSubscription subscription) {
        subscriptions.remove(subscription);
        doUnsubscribe(subscription);
    }


    /**
     * Connect to the cloud server and start the asynchronous polling loop.
     * To stop it call {@link #disconnect()}.
     *
     * This convenience method should be sufficient for most simple cases. If not, then you can extend the this
     * class and build your own connection handling using the protected do*-methods.
     */
    public void connect() {
        if (connected) {
            throw new CotSdkException("Already connected. Please disconnect first.");
        }

        if (clientId == null) {
            clientId = doHandshake();
        }

        if (clientId == null) {
            throw new CotSdkException("Handshake failed, could not get clientId.");
        }

        doSubscribe();

        new Thread(this).start();
    }


    /**
     * Break the polling loop and disconnect from the cloud server.
     */
    public void disconnect() {
        shallDisconnect = true;
    }


    protected String doConnect() {
        // TODO
        // Send connect request to the server and return response body
        return null;
    }

    protected String doHandshake() {
        // TODO
        // Send handshake request to the server and receive clientId.
        return null;
    }

    protected void doSubscribe() {
        // TODO
        // Send all subscription requests to the server
    }

    protected void doUnsubscribe(SmartSubscription subscription) {
        // TODO
        // Send an unsubscribe request to the server
    }


    @Override
    public void run() {
        // TODO
        // Send connect request, wait for response, distribute response to subscribers, connect again...
        // Break on disconnect. Consider advice sent from the server.
    }

}
