package com.telekom.m2m.cot.restsdk.event;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Created by breucking on 04.02.16.
 */
public class EventApi {
    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.event+json;charset=UTF-8;ver=0.9";
    private final Gson gson = GsonUtils.createGson();


    public EventApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    public Event getEvent(String eventId) {
        String response = cloudOfThingsRestClient.getResponse(eventId, "event/events/", CONTENT_TYPE);
        Event event = new Event(gson.fromJson(response, ExtensibleObject.class));
        return event;
    }

    public Event create(Event event) {
        String json = gson.toJson(event);

        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "event/events/", CONTENT_TYPE);
        event.setId(id);

        return event;

    }
}
