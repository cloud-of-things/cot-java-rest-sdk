/**
 * 
 */
package com.telekom.m2m.cot.restsdk.event;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.IterableObjectPagination;

/**
 * @author chuhlich
 *
 */
public class EventCollection extends IterableObjectPagination<Event> {

    private static final String COLLECTION_CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.eventCollection+json;charset=UTF-8;ver=0.9";
    private static final String COLLECTION_ELEMENT_NAME = "events";

    /**
     * Creates an EventCollection.
     * Use {@link EventApi} to get EventCollection.
     *
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     * @param relativeApiUrl          relative url of the REST API without leading slash.
     * @param gson                    the necessary json De-/serializer.
     * @param filterBuilder           the build criteria or null if all items should be retrieved.
     */
    EventCollection(
        final CloudOfThingsRestClient cloudOfThingsRestClient,
        final String relativeApiUrl,
        final Gson gson,
        final Filter.FilterBuilder filterBuilder
    ) {
        super(
            eventJson -> new Event(gson.fromJson(eventJson, ExtensibleObject.class)),
            cloudOfThingsRestClient,
            relativeApiUrl,
            gson,
            COLLECTION_CONTENT_TYPE,
            COLLECTION_ELEMENT_NAME,
            filterBuilder
        );
    }

    /**
     * Retrieves the current page.
     * <p>
     * Retrieves the Events influenced by filters set in construction.
     *
     * @return array of found Events
     */
    public Event[] getEvents() {
        final JsonArray jsonEvents = getJsonArray();

        if (jsonEvents != null) {
            final Event[] arrayOfEvents = new Event[jsonEvents.size()];
            for (int i = 0; i < jsonEvents.size(); i++) {
                JsonElement jsonEvent = jsonEvents.get(i).getAsJsonObject();
                final Event event = objectMapper.apply(jsonEvent);
                arrayOfEvents[i] = event;
            }
            return arrayOfEvents;
        } else {
            return new Event[0];
        }
    }
}