package com.telekom.m2m.cot.restsdk.event;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Use the Event to work with Events.
 *
 * Created by Patrick Steinert on 04.02.16.
 */
public class EventApi {
    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.event+json;charset=UTF-8;ver=0.9";
    private static final String RELATIVE_API_URL = "event/events/";

    private final Gson gson = GsonUtils.createGson();
    private final CloudOfThingsRestClient cloudOfThingsRestClient;

    /**
     * Internal Constructor.
     *
     * @param cloudOfThingsRestClient the configured rest client.
     */
    public EventApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    /**
     * Retrieves a specific Event.
     *
     * @param eventId the unique identifier of the desired Event.
     * @return the Event (or null if not found). TODO: it does _not_ return null. But maybe it should.
     */
    public Event getEvent(String eventId) {
        String response = cloudOfThingsRestClient.getResponse(eventId, RELATIVE_API_URL, CONTENT_TYPE);
        return new Event(gson.fromJson(response, ExtensibleObject.class));
    }

    /**
     * Stores an Event.
     *
     * @param event the event to create.
     * @return the created event with the assigned unique identifier.
     */
    public Event createEvent(Event event) {
        String json = gson.toJson(event);

        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, RELATIVE_API_URL, CONTENT_TYPE);
        event.setId(id);

        return event;
    }

    /**
     * Deletes an Event.
     *
     * @param event the Event to delete
     */
    public void deleteEvent(Event event) {
        cloudOfThingsRestClient.delete(event.getId(), RELATIVE_API_URL);
    }

    /**
     * Retrieves Events.
     *
     * @return the found Events.
     */
    public EventCollection getEvents() {
        return new EventCollection(
                cloudOfThingsRestClient,
                RELATIVE_API_URL,
                gson,
                null);
    }

    /**
     * Retrieves Events filtered by criteria.
     *
     * @param filters filters of event attributes.
     * @return the EventCollections to navigate through the results.
     * @since 0.2.0
     */
    public EventCollection getEvents(Filter.FilterBuilder filters) {
        return new EventCollection(
                cloudOfThingsRestClient,
                RELATIVE_API_URL,
                gson,
                filters);
    }

    /**
     * Deletes a collection of Events by criteria.
     *
     * @param filters filters of Event attributes.
     */
    public void deleteEvents(Filter.FilterBuilder filters) {
        cloudOfThingsRestClient.delete("", RELATIVE_API_URL + "?" + filters.buildFilter() + "&x=");
    }
}
