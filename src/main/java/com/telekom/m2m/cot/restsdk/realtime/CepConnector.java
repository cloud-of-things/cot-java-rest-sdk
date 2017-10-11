package com.telekom.m2m.cot.restsdk.realtime;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;


/**
 * The class that establishes the connection to the notification services. It is
 * based on the SmartCepConnect class that is in the smartrest package. It
 * provides methods to perform operations such as handshake,
 * subscribe/unsubscribe and connect which are required to communicate with the
 * notification services.
 * 
 * Created by Ozan Arslan on 18.08.2017
 */
public class CepConnector implements Runnable {

    public static final String CONTENT_TYPE = "application/json";

    // TODO: find out what versions exist and which ones we can support:
    public static final String PROTOCOL_VERSION_REQUESTED = "1.0";
    public static final String PROTOCOL_VERSION_MINIMUM = "1.0";

    private static final int DEFAULT_READ_TIMEOUT_MILLIS = 60000;
    private static final int DEFAULT_RECONNECT_INTERVAL_MILLIS = 100;

    private static final int THREAD_JOIN_GRACE_MILLIS = 1000;

    private String notificationPath;
    private CloudOfThingsRestClient cloudOfThingsRestClient;

    private boolean connected = false;
    private boolean shallDisconnect = false;

    private String clientId;

    // Read timeout in milliseconds for the connect request:
    private int timeout = DEFAULT_READ_TIMEOUT_MILLIS;

    // Interval in milliseconds between connect requests:
    private int interval = DEFAULT_RECONNECT_INTERVAL_MILLIS;

    private Set<String> channels = new CopyOnWriteArraySet<>();
    private Set<SubscriptionListener> listeners = new CopyOnWriteArraySet<>();

    private Gson gson = GsonUtils.createGson();

    private Thread pollingThread;


    /**
     * Construct a new CepConnector.
     *
     * @param cloudOfThingsRestClient
     *            the client to use for connection to the cloud
     *
     * @param notificationPath a String with REST endpoint path for notification requests (handshake, subscribe, connect...)
     *                       without host, leading and trailing slashes e.g. cep/realtime
     */
    public CepConnector(CloudOfThingsRestClient cloudOfThingsRestClient, String notificationPath) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
        this.notificationPath = notificationPath;
    }


    /**
     * The method that is used to subscribe to a given channel.
     * 
     * @param channel
     *            to be subscribed to. Can include * as a wildcard (e.g. "/alarms/*").
     */
    public void subscribe(String channel) {
        if (channel == null) {
            throw new CotSdkException("Subscription must not have null as its channel.");
        }

        channels.add(channel);

        if (clientId != null) {
            JsonArray body = new JsonArray();
            JsonObject obj = new JsonObject();
            obj.addProperty("channel", "/meta/subscribe");
            obj.addProperty("clientId", clientId);
            obj.addProperty("subscription", channel);
            body.add(obj);
            cloudOfThingsRestClient.doPostRequest(body.toString(), notificationPath, CONTENT_TYPE);
        }
    }

    /**
     * The method used to unsubscribe from a channel that was previously
     * subscribed to.
     * 
     * @param channel
     *            to unsubscribe from
     */
    public void unsubscribe(String channel) {
        if (channels.remove(channel)) {
            JsonArray body = new JsonArray();
            JsonObject obj = new JsonObject();
            obj.addProperty("channel", "/meta/unsubscribe");
            obj.addProperty("clientId", clientId);
            obj.addProperty("subscription", channel);
            body.add(obj);
            cloudOfThingsRestClient.doPostRequest(body.toString(), notificationPath, CONTENT_TYPE);
        }
    }


    /**
     * The method to initiate the connection. It checks the pre-requisite
     * conditions to establish a connection. When the conditions are satisfied
     * it starts the run cycle.
     */
    public void connect() {
        shallDisconnect = false;

        if (connected) {
            throw new CotSdkException("Already connected. Please disconnect first.");
        }

        if (clientId == null) {
            doHandShake();
        }

        if (clientId == null) {
            throw new CotSdkException("Handshake failed, could not get clientId.");
        }

        pollingThread = new Thread(this);
        pollingThread.setName("CepConnector.pollingThread");
        pollingThread.start();
    }


    public void disconnect() {
        shallDisconnect = true;
        if (pollingThread != null) {
            pollingThread.interrupt();
            try {
                pollingThread.join(THREAD_JOIN_GRACE_MILLIS); // One second should be more than enough to end the loop.
            } catch (InterruptedException ex) {
                throw new CotSdkException("Real time polling thread didn't finish properly when asked to disconnect.", ex);
            }
        }
    }

    public void addListener(SubscriptionListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SubscriptionListener listener) {
        listeners.remove(listener);
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
     * Default is {@value DEFAULT_READ_TIMEOUT_MILLIS}.
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
     * Default is {@value DEFAULT_RECONNECT_INTERVAL_MILLIS}.
     *
     * @param interval the waiting interval in milliseconds
     */
    public void setInterval(int interval) {
        this.interval = interval;
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
     * The method that does the post request to establish a connection.
     *
     * @return the response of the cloud as a string.
     */
    protected String doConnect() {
        JsonArray body = new JsonArray();
        JsonObject obj = new JsonObject();
        obj.addProperty("channel", "/meta/connect");
        obj.addProperty("clientId", clientId);
        obj.addProperty("connectionType", "long-polling");
        body.add(obj);
        String result = cloudOfThingsRestClient.doRealTimePollingRequest(body.toString(), notificationPath, CONTENT_TYPE, timeout);
        return result;
    }


    protected void doHandShake() {
        JsonObject obj = new JsonObject();
        obj.addProperty("channel", "/meta/handshake");
        obj.addProperty("version", PROTOCOL_VERSION_REQUESTED);
        obj.addProperty("minimumVersion", PROTOCOL_VERSION_MINIMUM);

        JsonArray supportedConnectionTypes = new JsonArray();
        supportedConnectionTypes.add("long-polling");
        obj.add("supportedConnectionTypes", supportedConnectionTypes);

        // TODO: find out what this advice even does...
        JsonObject advice = new JsonObject();
        advice.addProperty("timeout", timeout);
        advice.addProperty("interval", interval);
        obj.add("advice", advice);

        JsonArray body = new JsonArray();
        body.add(obj);
        String result = cloudOfThingsRestClient.doPostRequest(body.toString(), notificationPath, CONTENT_TYPE);
        JsonArray r = gson.fromJson(result, JsonArray.class);
        clientId = r.get(0).getAsJsonObject().get("clientId").getAsString();
    }


    /**
     * Post the subscriptions for all channels that were added before we were connected.
     */
    protected void doInitialSubscriptions() {
        if (clientId == null) {
            throw new CotSdkException("Subscription failed because we don't have a clientId yet.");
        }
        if (channels.isEmpty()) {
            return;
        }

        JsonArray body = new JsonArray();
        // We can request multiple subscriptions in one go.
        // (unlike with SmartREST, where we need one request for each channel).
        for (String channel : channels) {
            JsonObject obj = new JsonObject();
            obj.addProperty("channel", "/meta/subscribe");
            obj.addProperty("clientId", clientId);
            obj.addProperty("subscription", channel);
            body.add(obj);
        }
        cloudOfThingsRestClient.doPostRequest(body.toString(), notificationPath, CONTENT_TYPE);
    }

    /**
     * Starts the connector in a separate thread. Not meant to be called directly,
     * please use {@link #connect()} to start the connector.
     */
    @Override
    public void run() {
        connected = true;
        try {
            doInitialSubscriptions();

            do {
                String responseString = doConnect();
                if (responseString != null) {
                    JsonArray response = gson.fromJson(responseString, JsonArray.class);

                    for (JsonElement element : response) {
                        // TODO: evaluate advice?
                        // TODO: pass errors to our listeners?

                        JsonObject jsonObject = element.getAsJsonObject();

                        String notificationChannel = jsonObject.get("channel").getAsString();

                        // We don't pass on failures and meta data to the listeners.
                        if (!notificationChannel.startsWith("/meta/")) {

                            for (SubscriptionListener listener : listeners) {
                                // Now filter out the unnecessary fields from
                                // the JsonElement and pass the required
                                // information to the notification object:
                                JsonObject jsonNotification = new JsonObject();
                                jsonNotification.add("data", jsonObject.get("data"));
                                jsonNotification.add("channel", jsonObject.get("channel"));
                                listener.onNotification(notificationChannel, new Notification(jsonNotification));
                            }
                        }
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
            clientId = null;
            connected = false;
        }
    }

}