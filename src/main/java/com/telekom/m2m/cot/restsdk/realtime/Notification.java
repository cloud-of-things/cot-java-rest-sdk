package com.telekom.m2m.cot.restsdk.realtime;

import com.google.gson.JsonObject;

/**
 * The Notification is the object that holds the data/message that a listener receives from the channel that it is subscribed to.
 */
public  class Notification {

    private JsonObject data;

    public Notification(JsonObject data) {
        this.data = data;
    }


    public JsonObject getData() {
        return data;
    }

}