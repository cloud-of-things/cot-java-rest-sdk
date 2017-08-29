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
 *
 */
public class CepConnector implements Runnable {

    private CloudOfThingsRestClient cloudOfThingsRestClient;
    private final Gson gson = GsonUtils.createGson();

    private boolean connected = false;
    private boolean shallDisconnect = false;
    private String clientId;
    private Set<String> channels = new CopyOnWriteArraySet<>();;
    private Set<SubscriptionListener> listeners = new CopyOnWriteArraySet<>();

    public CepConnector() {
    }

    /**
     * Construct a new CepConnector with a default CloudOfThingsRestClient.
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
     *            to be subscribed to
     */
    public String subscribe(String channel) {

        if (channel == null) {
            throw new CotSdkException("Subscription must not have null as it's channel.");
        }

        channels.add(channel);

        if (clientId != null) {
            String CONTENT = "application/json";
            JsonArray body = new JsonArray();
            JsonObject obj = new JsonObject();
            obj.addProperty("channel", "/meta/subscribe");
            obj.addProperty("clientId", clientId);
            obj.addProperty("subscription", channel);
            body.add(obj);
            cloudOfThingsRestClient.doPostRequest(body.toString(), "/cep/realtime", CONTENT);
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
        channels.remove(channel);
        doUnsubscribe(channel);

    }

    private void doHandShake() {
        JsonArray body = new JsonArray();
        JsonObject obj = new JsonObject();
        obj.addProperty("channel", "/meta/handshake");
        obj.addProperty("version", "1");
        obj.addProperty("mininumVersion", "1.0beta");
        JsonArray inner = new JsonArray();
        inner.add("long-polling");
        inner.add("callback-polling");
        obj.add("supportedConnectionTypes", inner);
        body.add(obj);

        String CONTENT = "application/json";
        String result = cloudOfThingsRestClient.doPostRequest(body.toString(), "/cep/realtime", CONTENT);
        JsonArray r = new Gson().fromJson(result, JsonArray.class);
        clientId = r.get(0).getAsJsonObject().get("clientId").getAsString();

    }

    /**
     * The connection can be established only if there is(are) already
     * channel(s) to subscribe to. The following method does the post requrest
     * to subscribe these channels before the connection is established.
     * 
     */
    private void doInitialSubscribtions() {
        if (clientId == null) {
            throw new CotSdkException("Subscription failed because we don't have a clientId yet.");
        }
        for (String channel : channels) {

            String CONTENT = "application/json";
            JsonArray body = new JsonArray();
            JsonObject obj = new JsonObject();
            obj.addProperty("channel", "/meta/subscribe");
            obj.addProperty("clientId", clientId);
            obj.addProperty("subscription", channel);
            body.add(obj);
            cloudOfThingsRestClient.doPostRequest(body.toString(), "/cep/realtime", CONTENT);
        }
    }

    /**
     * The method that does the post request to unsubscribe from a channel.
     * 
     * @param channel
     *            to unsubscribe from.
     */
    public void doUnsubscribe(String channel) {

        String CONTENT = "application/json";
        JsonArray body = new JsonArray();
        JsonObject obj = new JsonObject();
        obj.addProperty("channel", "/meta/unsubscribe");
        obj.addProperty("clientId", clientId);
        obj.addProperty("subscription", channel);
        body.add(obj);
        cloudOfThingsRestClient.doPostRequest(body.toString(), "/cep/realtime", CONTENT);
        channels.remove(channel);

    }

    /**
     * The method that does the post request to establish a connection.
     * 
     * @return the response of the cloud as a string.
     */
    protected String doConnect() {

        String CONTENT = "application/json";
        JsonArray body = new JsonArray();
        JsonObject obj = new JsonObject();
        obj.addProperty("channel", "/meta/connect");
        obj.addProperty("clientId", clientId);
        obj.addProperty("connectionType", "long-polling");
        body.add(obj);
        String result = cloudOfThingsRestClient.doPostRequest(body.toString(), "/cep/realtime", CONTENT);
        return result;
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

    @Override
    public void run() {
        connected = true;
        try {
            doInitialSubscribtions();

            do {
                String response = doConnect();
                JsonArray r = new Gson().fromJson(response, JsonArray.class);

                for (JsonElement element : r) {

                    String notificationChannel = element.getAsJsonObject().get("channel").getAsString();

                    for (String channel : channels) {

                        if (channel.equals(notificationChannel)) {

                            // Now filter out the unnecessary fields from
                            // the JsonElement and pass the required
                            // information to the notification object:
                            JsonObject ObjectToPass = new JsonObject();
                            ObjectToPass.add("data", element.getAsJsonObject().get("data"));
                            ObjectToPass.add("channel", element.getAsJsonObject().get("channel"));

                            Notification notification = new Notification(ObjectToPass.toString());

                            for (SubscriptionListener listener : listeners) {
                                listener.onNotification(channel, notification);

                            }

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
