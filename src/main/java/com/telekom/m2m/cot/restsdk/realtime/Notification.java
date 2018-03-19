package com.telekom.m2m.cot.restsdk.realtime;

import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;

/**
 * The Notification is the object that holds the data/message that a listener receives from the channel that it is subscribed to.
 */
public  class Notification {

    private JsonObject data;

    public Notification(JsonObject data) {
        this.data = data;
    }

    public Enum<RealtimeAction> getRealtimeAction() {
        try {
            String realtimeAction = data.get("realtimeAction").getAsString();
            return RealtimeAction.valueOf(realtimeAction);
        } catch (NullPointerException e) {
            throw new CotSdkException("No member realtimeAction in Json Object");
        }

    }

    public String getDataPart() {
        try {
            return data.get("data").getAsString();
        } catch (NullPointerException e) {
            throw new CotSdkException("No member data in Json Object");
        }
    }


    public JsonObject getData() {
        return data;
    }

}