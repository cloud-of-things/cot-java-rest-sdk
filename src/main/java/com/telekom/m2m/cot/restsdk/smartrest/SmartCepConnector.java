package com.telekom.m2m.cot.restsdk.smartrest;


import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static com.telekom.m2m.cot.restsdk.smartrest.SmartRestApi.MSG_REALTIME_HANDSHAKE;
import static com.telekom.m2m.cot.restsdk.smartrest.SmartRestApi.MSG_REALTIME_SUBSCRIBE;
import static com.telekom.m2m.cot.restsdk.smartrest.SmartRestApi.MSG_REALTIME_UNSUBSCRIBE;
import static com.telekom.m2m.cot.restsdk.smartrest.SmartRestApi.MSG_REALTIME_CONNECT;


/**
 * The SmartCepConnector handles subscriptions and holds the connection to the CEP notification service.
 */
public class SmartCepConnector implements Runnable {

    private CloudOfThingsRestClient cloudOfThingsRestClient;

    private String xId;

    private String clientId;

    private boolean connected = false;
    private boolean shallDisconnect = false;

    private List<SmartSubscription> subscriptions = new CopyOnWriteArrayList<>(); // Has to be thread safe because it will be read from the polling thread!


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

        if (channel == null) {
            throw new CotSdkException("Subscription must not have null as it's channel.");
        }

        List<String> xIds = new ArrayList<>();
        xIds.add(xId);
        xIds.addAll(Arrays.asList(additionalXIds));
        SmartSubscription subscription = new SmartSubscription(this, channel, listener, xIds);
        subscriptions.add(subscription);
        return subscription;
    }


    public void unsubscribe(SmartSubscription subscription) {
        subscriptions.remove(subscription);
        String channel = subscription.getChannel();
        // Only if that was the last/only subscription to it's channel do we need to actually tell the server about it:
        if (subscriptions.stream().noneMatch(sub -> sub.getChannel().equals(channel))) {
            doUnsubscribe(subscription);
        }
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

        if (subscriptions.size() == 0) {
            throw new CotSdkException("Create at least one subscription before connecting.");
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
     * Will not disconnect immediately, but only at the next iteration of the loop.
     */
    public void disconnect() {
        shallDisconnect = true;
        // TODO: kill the thread after some time?
    }



    protected String[] doConnect() {
        String[] response = cloudOfThingsRestClient.doSmartRequest(xId, MSG_REALTIME_CONNECT + "," + clientId);
        if (response.length > 0) {
            // The first line can contain leading spaces, periodically sent by the server as a keep-alive signal.
            response[0] = response[0].trim();
        }
        return response;
    }


    protected String doHandshake() {
        String[] response = cloudOfThingsRestClient.doSmartRequest(xId, MSG_REALTIME_HANDSHAKE);
        switch (response.length) {
            case 1:
                return response[0];
            case 0:
                throw new CotSdkException("SmartREST notification handshake failed: empty response => no clientId.");
            default:
                throw new CotSdkException("SmartREST notification handshake failed: ambiguous multi line response: " + Arrays.toString(response));
        }
    }


    protected void doSubscribe() {
        if (clientId == null) {
            throw new CotSdkException("Cannot subscribe to SmartREST notification because we don't have a clientId yet.");
        }

        // We need to subscribe to each unique channel only once.
        // TODO: this doesn't take care of subsets of channels (e.g. "/myModule/*" and "/myModule/channel1"). Is that ok?
        List<String> lines = subscriptions.stream()
                                          .map(SmartSubscription::getChannel)
                                          .distinct()
                                          .map(channel -> MSG_REALTIME_SUBSCRIBE + "," + clientId + "," + channel)
                                          .collect(Collectors.toList());

        cloudOfThingsRestClient.doSmartRequest(xId, String.join("\n", lines));
    }


    protected void doUnsubscribe(SmartSubscription subscription) {
        cloudOfThingsRestClient.doSmartRequest(xId, MSG_REALTIME_UNSUBSCRIBE + "," + clientId + "," + subscription.getChannel());
    }


    @Override
    public void run() {
        connected = true;
        do {
            String response[] = doConnect();
            System.out.println("SmartREST-real-time-response:");
            for (String line : response) {
                System.out.println("- "+line);
                // TODO: distribute response to subscribers.
                // TODO: Consider advice sent from the server.

                // 40,No template for this X-ID
                // 43,1,Invalid Message Identifier
            }
        } while (!shallDisconnect);

        connected = false;
        // Send connect request, wait for response, distribute response to subscribers, connect again...
        // Break on disconnect. Consider advice sent from the server.
    }

}
