/**
 * 
 */
package com.telekom.m2m.cot.restsdk.event;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * @author chuhlich
 *
 */
public class EventCollection {

    private Filter.FilterBuilder criteria = null;
    private CloudOfThingsRestClient cloudOfThingsRestClient;
    private int pageCursor = 1;

    private Gson gson = GsonUtils.createGson();

    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.eventCollection+json;charset=UTF-8;ver=0.9";
    private boolean nextAvailable = false;
    private boolean previousAvailable = false;
    private int pageSize = 5;

    /**
     * Creates a EventCollection.
     * Use {@link EventApi} to get EventCollections.
     *
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     */
    EventCollection(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    /**
     * Creates a EventCollection with filters.
     * Use {@link EventApi} to get EventCollections.
     *
     * @param filterBuilder                the build criteria.
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     */
    EventCollection(Filter.FilterBuilder filterBuilder, CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.criteria = filterBuilder;
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    /**
     * Retrieves the current page.
     * <p>
     * Retrieves the Events influenced by filters setted in construction.
     *
     * @return array of found Events
     * @since 0.2.0
     */
    public Event[] getEvents() {
        JsonObject object = getJsonObject(pageCursor);

        previousAvailable = object.has("prev");

        if (object.has("events")) {
            JsonArray jsonEvents= object.get("events").getAsJsonArray();
            Event[] arrayOfEvents = new Event[jsonEvents.size()];
            for (int i = 0; i < jsonEvents.size(); i++) {
                JsonElement jsonEvent = jsonEvents.get(i).getAsJsonObject();
                Event event = new Event(gson.fromJson(jsonEvent, ExtensibleObject.class));
                arrayOfEvents[i] = event;
            }
            return arrayOfEvents;
        } else
            return null;
    }

    private JsonObject getJsonObject(int page) {
        String response;
        String url = "/event/events?" +
                "currentPage=" + page +
                "&pageSize=" + pageSize;
        if (criteria != null) {
            url += "&" + criteria.buildFilter();
        }
        response = cloudOfThingsRestClient.getResponse(url, CONTENT_TYPE);

        return gson.fromJson(response, JsonObject.class);
    }

    /**
     * Moves cursor to the next page.
     *
     * @since 0.2.0
     */
    public void next() {
        pageCursor += 1;
    }

    /**
     * Moves cursor to the previous page.
     *
     * @since 0.2.0
     */
    public void previous() {
        pageCursor -= 1;
    }

    /**
     * Checks if the next page has elements. <b>Use with caution, it does a seperate HTTP request, so it is considered as slow</b>
     *
     * @return true if next page has events, otherwise false.
     * @since 0.2.0
     */
    public boolean hasNext() {
        JsonObject object = getJsonObject(pageCursor + 1);
        if (object.has("events")) {
            JsonArray jsonEvents = object.get("events").getAsJsonArray();
            nextAvailable = jsonEvents.size() > 0 ? true : false;
        }
        return nextAvailable;
    }

    /**
     * Checks if there is a previous page.
     *
     * @return true if next page has events, otherwise false.
     * @since 0.2.0
     */
    public boolean hasPrevious() {
        return previousAvailable;
    }

    /**
     * Sets the page size for page queries.
     * The queries uses page size as a limit of elements to retrieve.
     * There is a maximum number of elements, currently 2,000 elements.
     * <i>Default is 5</i>
     *
     * @param pageSize the new page size as positive integer.
     */
    public void setPageSize(int pageSize) {
        if (pageSize > 0) {
            this.pageSize = pageSize;
        } else {
            this.pageSize = 0;
        }
    }


//    public Iterator<Event> iterator() {
//        return null;
//    }
//
//    public Iterable<Event> elements(int limit) {
//        return new EventCollectionIterable()
//    }
}