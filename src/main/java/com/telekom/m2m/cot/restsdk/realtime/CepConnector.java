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
 * TODO: allow changing of timeout/interval
 */
public class CepConnector implements Runnable {

    public static final String CONTENT_TYPE = "application/json";

    public static final String REST_ENDPOINT = "/cep/realtime";

    // TODO: find out what versions exist and which ones we can support:
    public static final String PROTOCOL_VERSION_REQUESTED = "1.0";
    public static final String PROTOCOL_VERSION_MINIMUM = "1.0";


    private CloudOfThingsRestClient cloudOfThingsRestClient;

    private boolean connected = false;
    private boolean shallDisconnect = false;

    private String clientId;

    private Set<String> channels = new CopyOnWriteArraySet<>();
    private Set<SubscriptionListener> listeners = new CopyOnWriteArraySet<>();

    private Gson gson = GsonUtils.createGson();



    /**
     * Construct a new CepConnector.
     *
     * @param cloudOfThingsRestClient
     *            the client to use for connection to the cloud
     */
    public CepConnector(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }


    /**
     * The method that is used to subscribe to a given channel.
     * 
     * @param channel
     *            to be subscribed to. Can include * as a wildcard (e.g. "/alarms/*").
     */
    public String subscribe(String channel) {
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
            cloudOfThingsRestClient.doPostRequest(body.toString(), REST_ENDPOINT, CONTENT_TYPE);
            // TODO: check response
        }
        return channel;
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
            cloudOfThingsRestClient.doPostRequest(body.toString(), REST_ENDPOINT, CONTENT_TYPE);
        }
    }


    /**
     * The method to initiate the connection. It checks the pre-requisite
     * conditions to establish a connection. When the conditions are satisfied
     * it starts the run cycle.
     */
    public void connect() {
        if (connected) {
            throw new CotSdkException("Already connected. Please disconnect first.");
        }

        if (channels.size() == 0) {
            throw new CotSdkException("Create at least one subscription before connecting.");
        }
        if (clientId == null) {
            doHandShake();
        }

        if (clientId == null) {
            throw new CotSdkException("Handshake failed, could not get clientId.");
        }

        new Thread(this).start();
    }

    public void disconnect() {
        shallDisconnect = true;
    }

    public void addListener(SubscriptionListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SubscriptionListener listener) {
        listeners.remove(listener);
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
        // TODO: use real-time-client
        String result = cloudOfThingsRestClient.doPostRequest(body.toString(), REST_ENDPOINT, CONTENT_TYPE);
        return result;
    }


    protected void doHandShake() {
        JsonArray body = new JsonArray();
        JsonObject obj = new JsonObject();
        obj.addProperty("channel", "/meta/handshake");
        obj.addProperty("version", PROTOCOL_VERSION_REQUESTED);
        obj.addProperty("mininumVersion", PROTOCOL_VERSION_MINIMUM);
        JsonArray supportedConnectionTypes = new JsonArray();
        supportedConnectionTypes.add("long-polling");
        obj.add("supportedConnectionTypes", supportedConnectionTypes);
        body.add(obj);

        String result = cloudOfThingsRestClient.doPostRequest(body.toString(), REST_ENDPOINT, CONTENT_TYPE);
        // TODO: check result for errors
        JsonArray r = gson.fromJson(result, JsonArray.class);
        clientId = r.get(0).getAsJsonObject().get("clientId").getAsString();
    }


    /**
     * The connection can be established only if there is(are) already
     * channel(s) to subscribe to. The following method does the post request
     * to subscribe these channels before the connection is established.
     * TODO: verify if that is really true. Maybe it is possible to subscibe later (seems to work with SmartREST).
     */
    protected void doInitialSubscriptions() {
        if (clientId == null) {
            throw new CotSdkException("Subscription failed because we don't have a clientId yet.");
        }
        for (String channel : channels) {
            JsonArray body = new JsonArray();
            JsonObject obj = new JsonObject();
            obj.addProperty("channel", "/meta/subscribe");
            obj.addProperty("clientId", clientId);
            obj.addProperty("subscription", channel);
            body.add(obj);
            // TODO: check if we can do multiple subscriptions in one request
            cloudOfThingsRestClient.doPostRequest(body.toString(), REST_ENDPOINT, CONTENT_TYPE);
        }
    }


    @Override
    public void run() {
        connected = true;
        try {
            doInitialSubscriptions();

            do {
                String responseString = doConnect();
                JsonArray response = gson.fromJson(responseString, JsonArray.class);

                for (JsonElement element : response) {
                    // TODO: evaluate advice

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

                            listener.onNotification(notificationChannel, new Notification(jsonObject));
                        }
                    }
                }
            } while (!shallDisconnect);
        } finally {
            clientId = null;
            connected = false;
        }
    }

}