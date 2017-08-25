package com.telekom.m2m.cot.restsdk.smartrest;


import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.telekom.m2m.cot.restsdk.smartrest.SmartRestApi.MSG_REALTIME_ADVICE;
import static com.telekom.m2m.cot.restsdk.smartrest.SmartRestApi.MSG_REALTIME_HANDSHAKE;
import static com.telekom.m2m.cot.restsdk.smartrest.SmartRestApi.MSG_REALTIME_SUBSCRIBE;
import static com.telekom.m2m.cot.restsdk.smartrest.SmartRestApi.MSG_REALTIME_UNSUBSCRIBE;
import static com.telekom.m2m.cot.restsdk.smartrest.SmartRestApi.MSG_REALTIME_CONNECT;
import static com.telekom.m2m.cot.restsdk.smartrest.SmartRestApi.MSG_REALTIME_XID;


/**
 * The SmartCepConnector handles subscriptions and holds the connection to the CEP notification service.
 * <br>
 * It passes on notifications to {@link SmartListener}s.
 * <br>
 * Basic usage is like this:
 * <ul>
 *     <li>store at least one {@link SmartResponseTemplate}, that can extract data from the notifications that you are interested in.</li>
 *     <li>call {@link SmartCepConnector#subscribe(String, Set)} to subscribe to a channel.</li>
 *     <li>extend {@link SmartListener} and do something with {@link SmartNotification}s passed to {@link SmartListener#onNotification(SmartNotification)}.</li>
 *     <li>add the {@link SmartListener} via {@link SmartCepConnector#addListener(SmartListener)}.</li>
 *     <li>call {@link SmartCepConnector#connect()} to establish the connection and start the background polling thread.</li>
 * </ul>
 *
 * <p>
 * See SmartCepConnectorTest and SmartRestRealTimeIT for examples.
 * </p>
 */
public class SmartCepConnector implements Runnable {

    private static final List<Integer> KNOWN_ERROR_MESSAGES = Arrays.asList(40, 41, 42, 43, 45, 50);

    private static final int DEFAULT_READ_TIMEOUT_MILLIS = 60000;
    private static final int DEFAULT_RECONNECT_INTERVAL_MILLIS = 100;

    private static final int THREAD_JOIN_GRACE_MILLIS = 1000;


    private CloudOfThingsRestClient cloudOfThingsRestClient;

    private String xId;

    private String clientId;


    // Read timeout in milliseconds for the connect request:
    private int timeout = DEFAULT_READ_TIMEOUT_MILLIS;

    // Interval in milliseconds between connect requests:
    private int interval = DEFAULT_RECONNECT_INTERVAL_MILLIS;

    //
    private boolean connected = false;

    private volatile boolean shallDisconnect = false; // Volatile to be synced with the polling thread.

    private Thread pollingThread;


    // Have to be thread safe because they will be used from the polling thread too:
    private Map<String, Set<String>> subscriptions = new ConcurrentHashMap<>(); // mapping channel names to sets of X-Ids.
    private Set<SmartListener> listeners = new CopyOnWriteArraySet<>();


    /**
     * Construct a new SmartCepConnector with a default X-Id. That Id determines which response templates will be used.
     * <br>
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
     * <br>
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


    /**
     * Add a {@link SmartListener} that will from then on be called for all the notifications that this connector
     * receives.
     * <br>
     * Adding the same listener multiple times has no additional effect.
     *
     * @param listener
     */
    public void addListener(SmartListener listener) {
        listeners.add(listener);
    }


    /**
     * Remove a previously registered {@link SmartListener}.
     * <br>
     * Trying to remove a listener that doesn't exist is ok.
     *
     * @param listener
     */
    public void removeListener(SmartListener listener) {
        listeners.remove(listener);
    }


    /**
     * Connect to the cloud server and start the asynchronous polling loop.
     * <br>
     * To stop it call {@link #disconnect()}.
     * <p>
     * The connector doesn't stop on most errors. There should probably be a {@link SmartListener} which can handle
     * errors and trigger a disconnect, if necessary.
     * </p>
     * <p>
     * This convenience method should be sufficient for most simple cases. If not, then you can extend the this
     * class and build your own connection handling using the protected do*-methods.
     * </p>
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
     * Will try to interrupt the polling thread too.
     */
    public void disconnect() {
        shallDisconnect = true;
        pollingThread.interrupt();
        try {
            pollingThread.join(THREAD_JOIN_GRACE_MILLIS); // One second should be more than enough to end the loop.
        } catch (InterruptedException ex) {
            throw new CotSdkException("Real time polling thread didn't finish properly when asked to disconnect.", ex);
        }
    }


    /**
     * Get the clientId that was assigned by the server during handshake.
     *
     * @return the clientId or null, if we are not currently connected.
     */
    public String getClientId() {
        return clientId;
    }


    /**
     * Whether there is currently a polling thread connected to the server.
     *
     * @return true = yes; false = no
     */
    public boolean isConnected() {
        return connected;
    }


    /**
     * The current read timeout.
     *
     * @return the timeout in milliseconds
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Set the read timeout for the polling connect request.
     * <br>
     * Can also be overwritten by advice messages sent from the server while the connector is connected.
     * <br>
     * Default is 60000.
     *
     * @param timeout the timeout in milliseconds
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * The current interval, which is the time that the polling thread waits before it reconnects, after receiving a response.
     *
     * @return the waiting interval in milliseconds
     */
    public int getInterval() {
        return interval;
    }

    /**
     * Set the time that the polling thread waits before it reconnects, after receiving a response.
     * <br>
     * Can also be overwritten by advice messages sent from the server while the connector is connected.
     * <br>
     * Default is 100.
     *
     * @param interval the waiting interval in milliseconds
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }


    /**
     * Don't call this method. It will be called by the system when the polling thread is started.
     * Use {@link #connect()} instead.
     */
    @Override
    public void run() {
        connected = true;
        try {
            postInitialSubscriptions();
            do {
                String response[] = doConnect();
                String activeXId = xId;
                int alternativeXIdCounter = 0;
                for (String line : response) {
                    String messageId = line.split(",", 2)[0];

                    // Handle new alternative X-Id:
                    if (MSG_REALTIME_XID.equals(messageId)) {
                        String[] messageParts = line.split(",");
                        alternativeXIdCounter = Integer.parseInt(messageParts[1]);
                        activeXId = messageParts[2];
                    }

                    SmartNotification notification = new SmartNotification(line, activeXId);

                    // System-messages (<100) are not passed on to any listeners:
                    if (notification.getMessageId() >= 100) {
                        for (SmartListener listener : listeners) {
                            listener.onNotification(notification);
                        }
                        // When we have processed all the announced alternative X-Id lines we fall back to the default:
                        if (--alternativeXIdCounter == 0) {
                            activeXId = xId;
                        }
                    }

                    // Defined error messages in the response are passed to our listeners as exceptions:
                    if (KNOWN_ERROR_MESSAGES.contains(notification.getMessageId())) {
                        CotSdkException exception = new CotSdkException("Smart real time response contained an error: "+line);
                        for (SmartListener listener : listeners) {
                            listener.onError(exception);
                        }
                    }

                    if (MSG_REALTIME_ADVICE.equals(notification.getMessageId()+"")) {
                        handleAdvice(notification);
                    }

                    // TODO: check for errors that should cause us to abort the loop or connection.
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


    /**
     * Post all the subscriptions that we have to the server. Necessary for reconnects and when there already were
     * subscriptions before we were connected.
     */
    protected void postInitialSubscriptions() {
        if (clientId == null) {
            throw new CotSdkException("Cannot subscribe to SmartREST notification because we don't have a clientId yet.");
        }

        // TODO: check whether all of them can be sent in one batch with one request
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
     * Override this method if You don't want the server advice to automatically change the interval between
     * response and reconnect.
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
