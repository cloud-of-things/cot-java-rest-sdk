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

    /**
     * Get the realtime action out of the json data.
     * The realtime action is
     * e.g. CREATE, UPDATE or DELETE for an action, happened in the cot.
     *
     * @return enum value for realtime action
     */
    public Enum<RealtimeAction> getRealtimeAction() {
        try {
            String realtimeAction = data.get("realtimeAction").getAsString();
            return RealtimeAction.valueOf(realtimeAction);
        } catch (NullPointerException e) {
            throw new CotSdkException("No member realtimeAction in Json Object", e);
        }

    }

    /**
     *
     * @return the payload of the json data
     */
    public String getPayload() {
        try {
            return data.get("data").getAsString();
        } catch (NullPointerException e) {
            throw new CotSdkException("No member data in Json Object", e);
        }
    }


    public JsonObject getData() {
        return data;
    }

}