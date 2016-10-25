package com.telekom.m2m.cot.restsdk.event;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.measurement.Measurement;
import com.telekom.m2m.cot.restsdk.measurement.MeasurementCollection;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Created by breucking on 04.02.16.
 */
public class EventApi {
    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.event+json;charset=UTF-8;ver=0.9";
    private final Gson gson = GsonUtils.createGson();

    /**
     * Internal Constructor.
     *
     * @param cloudOfThingsRestClient the configured rest client.
     */
    public EventApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    /**
     * Retrives a specific Event.
     * @param id of the desired Event.
     * @return the Event (or null if not found).
     */
    public Event getEvent(String eventId) {
        String response = cloudOfThingsRestClient.getResponse(eventId, "event/events/", CONTENT_TYPE);
        Event event = new Event(gson.fromJson(response, ExtensibleObject.class));
        return event;
    }

    /**
     * Stores a Event.
     * @param event the event to create.
     * @return the created event with the ID.
     */
    public Event createEvent(Event event) {
        String json = gson.toJson(event);

        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "event/events/", CONTENT_TYPE);
        event.setId(id);

        return event;
    }

    /**
     * Deletes a Event.
     * @param measurement the Measurement to delete
     */
    public void deleteEvent(Event event) {
        cloudOfThingsRestClient.delete(event.getId(), "event/events/");
    }

    /**
     * Retrieves Events.
     *
     * @return the found Events.
     */
    public EventCollection getEvents() {
        return new EventCollection(cloudOfThingsRestClient);
    }

    /**
     * Retrieves Events filtered by criteria.
     *
     * @param filters filters of event attributes.
     * @return the EventCollections to navigate through the results.
     * @since 0.2.0
     */
    public EventCollection getEvents(Filter.FilterBuilder filters) {
        return new EventCollection(filters, cloudOfThingsRestClient);
    }

    /**
     * Deletes a collection of Measurements by criteria.
     *
     * @param filters filters of measurement attributes.
     */
    public void deleteEvents(Filter.FilterBuilder filters) {
        cloudOfThingsRestClient.delete("", "event/events?" + filters.buildFilter() + "&x=");
    }
}
