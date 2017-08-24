package com.telekom.m2m.cot.restsdk.smartrest;


import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.telekom.m2m.cot.restsdk.smartrest.SmartRestApi.MSG_REALTIME_ADVICE;
import static com.telekom.m2m.cot.restsdk.smartrest.SmartRestApi.MSG_REALTIME_HANDSHAKE;
import static com.telekom.m2m.cot.restsdk.smartrest.SmartRestApi.MSG_REALTIME_SUBSCRIBE;
import static com.telekom.m2m.cot.restsdk.smartrest.SmartRestApi.MSG_REALTIME_UNSUBSCRIBE;
import static com.telekom.m2m.cot.restsdk.smartrest.SmartRestApi.MSG_REALTIME_CONNECT;


/**
 * The SmartCepConnector handles subscriptions and holds the connection to the CEP notification service.
 * It passes on notifications to {@link SmartListener}s.
 */
public class SmartCepConnector implements Runnable {

    private CloudOfThingsRestClient cloudOfThingsRestClient;

    private String xId;

    private String clientId;


    // Read timeout in milliseconds for the connect request:
    private int timeout = 60000;

    // Interval in milliseconds between connect requests:
    private int interval = 100;

    //
    private boolean connected = false;

    private volatile boolean shallDisconnect = false; // Volatile to be synced with the polling thread.

    private Thread pollingThread;


    // Have to be thread safe because they will be used from the polling thread too:
    private Map<String, Set<String>> subscriptions = new ConcurrentHashMap<>();
    private Set<SmartListener> listeners = new CopyOnWriteArraySet<>();


    /**
     * Construct a new SmartCepConnector with a default X-Id. That Id determines which response templates will be used.
     * Additional X-Ids can be specified for each subscription.
     *
     * @param cloudOfThingsRestClient the client to use for connection to the cloud
     * @param xId default X-Id for all requests. Additional X-Ids are possible per subscription.
     */
    public SmartCepConnector(CloudOfThingsRestClient cloudOfThingsRestClient, String xId) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
        this.xId = xId;
    }


    /**
     * Subscribe to a notification channel. Will take effect immediately, even for currently running connect requests.
     * Subscribing to the same channel twice will just add any additional X-Ids.
     *
     * @param channel the name of the channel. No wildcards are allowed for SmartREST.
     * @param additionalXIds additionalXIds that are to be registered for this channel.
     */
    public void subscribe(String channel, Set<String> additionalXIds) {
        // Wildcards in channel names are not supported by SmartREST:
        if (channel == null || channel.contains("*")) {
            throw new CotSdkException("Invalid channel name '"+channel+"'");
        }
        Set<String> xIds = subscriptions.get(channel);
        if (xIds == null) {
            xIds = new CopyOnWriteArraySet<>();
            subscriptions.put(channel, xIds);
        }
        if (additionalXIds != null) {
            xIds.addAll(additionalXIds);
        }

        // If we already have a clientId we should immediately send the subscribe request:
        if (clientId != null) {
            cloudOfThingsRestClient.doSmartRealTimeRequest(xId, MSG_REALTIME_SUBSCRIBE +
                                                                "," + clientId +
                                                                "," + channel +
                                                                ((xIds.size() == 0) ? "" : "," + String.join(",", xIds)));
        }
    }


    /**
     * Unsubscribe from a channel. Will take effect immediately, even for currently running connect requests.
     *
     * @param channel the name of the channel.
     */
    public void unsubscribe(String channel) {
        subscriptions.remove(channel);

        // If we already have a clientId we should immediately send the unsubscribe request:
        if (clientId != null) {
            cloudOfThingsRestClient.doSmartRealTimeRequest(xId, MSG_REALTIME_UNSUBSCRIBE + "," + clientId + "," + channel);
        }
    }


    public void addListener(SmartListener listener) {
        listeners.add(listener);
    }


    public void removeListener(SmartListener listener) {
        listeners.remove(listener);
    }


    /**
     * Connect to the cloud server and start the asynchronous polling loop.
     * To stop it call {@link #disconnect()}.
     *
     * This convenience method should be sufficient for most simple cases. If not, then you can extend the this
     * class and build your own connection handling using the protected do*-methods.
     */
    public void connect() {
        shallDisconnect = false;

        if (connected || (clientId != null)) {
            throw new CotSdkException("Already connected. Please disconnect first.");
        }

        // If there's no connection possible at all we want to fail fast, synchronously:
        clientId = doHandshake();

        if (clientId == null) {
            throw new CotSdkException("Handshake failed, could not get clientId.");
        }

        pollingThread = new Thread(this);
        pollingThread.start();
    }


    /**
     * Break the polling loop and disconnect from the cloud server.
     * Will try to interrupt the polling thread.
     */
    public void disconnect() {
        shallDisconnect = true;
        pollingThread.interrupt();
        try {
            pollingThread.join(1000); // One second should be more than enough to end the loop.
        } catch (InterruptedException ex) {
            throw new CotSdkException("Real time polling thread didn't finish properly when asked to disconnect.", ex);
        }
    }


    /**
     * Get the clientId that was assigned by the server during handshake.
     * @return the clientId or null, if we are not currently connected.
     */
    public String getClientId() {
        return clientId;
    }


    /**
     * Whether there is currently a polling thread connected to the server.
     * @return true = yes; false = no
     */
    public boolean isConnected() {
        return connected;
    }


    public int getTimeout() {
        return timeout;
    }

    /**
     * Set the read timeout for the polling connect request.
     * Can also be overwritten by advice messages sent from the server.
     * Default is 60000.
     * @param timeout the timeout in milliseconds
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getInterval() {
        return interval;
    }

    /**
     * Set the time that the polling thread waits before it reconnects, after receiving a response.
     * Can also be overwritten by advice messages sent from the server.
     * Default is 100.
     * @param interval the waiting interval in milliseconds
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }


    @Override
    public void run() {
        connected = true;
        try {
            postInitialSubscriptions();
            do {
                String response[] = doConnect();
                for (String line : response) {
                    // TODO: check for errors
                    // 40,No template for this X-ID (wenn es noch keine responsetemplates gibt)
                    // 40,,/alarms/177595925,Could not find any templates subscribed for the channel
                    // 43,1,Invalid Message Identifier (cep-messages an /s geschickt)
                    // more ?
                    // TODO: break after error? Handle errors?
                    // TODO: send errors to all listeners?

                    SmartNotification notification = new SmartNotification(line);

                    // System-messages (<100) are not passed on to any listeners:
                    if (notification.getMessageId() >= 100) {
                        for (SmartListener listener : listeners) {
                            listener.onNotification(notification);
                        }
                    }

                    if (MSG_REALTIME_ADVICE.equals(notification.getMessageId()+"")) {
                        handleAdvice(notification);
                    }
                }
                try {
                    if (!shallDisconnect) {
                        Thread.sleep(interval);
                    }
                } catch (InterruptedException e) {
                    shallDisconnect = true;
                }
            } while (!shallDisconnect);
        } finally {
            connected = false;
            clientId = null;
        }
    }


    protected String[] doConnect() {
        String[] response = cloudOfThingsRestClient.doSmartRealTimePollingRequest(xId, MSG_REALTIME_CONNECT + "," + clientId, timeout);
        if (response.length > 0) {
            // The first line can contain leading spaces, periodically sent by the server as a keep-alive signal.
            response[0] = response[0].trim();
        }
        return response;
    }


    protected String doHandshake() {
        String[] response = cloudOfThingsRestClient.doSmartRealTimeRequest(xId, MSG_REALTIME_HANDSHAKE);
        switch (response.length) {
            case 1:
                // TODO: 43,1,Invalid Message Identifier?!?
                return response[0].trim();
            case 0:
                throw new CotSdkException("SmartREST notification handshake failed: empty response => no clientId.");
            default:
                throw new CotSdkException("SmartREST notification handshake failed: ambiguous multi line response: " + Arrays.toString(response));
        }
    }


    protected void postInitialSubscriptions() {
        if (clientId == null) {
            throw new CotSdkException("Cannot subscribe to SmartREST notification because we don't have a clientId yet.");
        }

        for (Map.Entry<String, Set<String>> entry: subscriptions.entrySet()) {
            String xIds = String.join(",", entry.getValue());
            cloudOfThingsRestClient.doSmartRealTimeRequest(xId,
                    MSG_REALTIME_SUBSCRIBE + ","
                            + clientId + ","
                            + entry.getKey()
                            + ((xIds.length() == 0) ? "" : "," + xIds));
        }
    }


    /**
     * Override this method if You don't want the server advice to automatically change the read timeout for the
     * connect request.
     * @param timeout the timeout, that the server recommended
     */
    protected void setTimeoutByAdvice(int timeout) {
        setTimeout(timeout);
    }

    /**
     * Override this method if You don't want the server advice to automatically change the interval.
     * @param interval the interval, that the server recommended
     */
    protected void setIntervalByAdvice(int interval) {
        setInterval(interval);
    }


    protected void handleAdvice(SmartNotification notification) {
        String[] parts = notification.getData().split(",");

        // For unknown reasons the advice line seems to have an additional undocumented first field too:
        // e.g. "86,,<timeout>,<interval>,<reconnect policy>"
        // instead of "86,<timeout>,<interval>,<reconnect policy>"

        if (!parts[1].isEmpty()) {
            setTimeoutByAdvice(Integer.parseInt(parts[1]));
        }

        if (!parts[2].isEmpty()) {
            setIntervalByAdvice(Integer.parseInt(parts[2]));
        }

        String reconnectPolicy = parts[3];
        switch (reconnectPolicy) {
            case "none" :
                shallDisconnect = true;
                break;
            case "handshake" :
                clientId = doHandshake();
                postInitialSubscriptions();
                break;
            case "retry" :
            default:
                // Nothing to do, just continue with the next iteration.
        }
    }

}
