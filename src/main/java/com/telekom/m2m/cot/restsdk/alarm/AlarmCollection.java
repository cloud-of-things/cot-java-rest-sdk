package com.telekom.m2m.cot.restsdk.alarm;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Created by Patrick Steinert on 24.11.16.
 *
 * @author Patrick Steinert
 * @since 0.3.0
 */
public class AlarmCollection {
    private Filter.FilterBuilder criteria = null;
    private CloudOfThingsRestClient cloudOfThingsRestClient;
    private int pageCursor = 1;

    private Gson gson = GsonUtils.createGson();

    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.alarmCollection+json;charset=UTF-8;ver=0.9";
    private boolean nextAvailable = false;
    private boolean previousAvailable = false;
    private int pageSize = 5;

    /**
     * Internal constructor to create an AlarmCollection.
     *
     * @param cloudOfThingsRestClient
     */
    AlarmCollection(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    /**
     * Internal constructor to create an AlarmCollection.
     *
     * @param filters
     * @param cloudOfThingsRestClient
     */
    AlarmCollection(Filter.FilterBuilder filters, CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
        this.criteria = filters;
    }

    /**
     * Retrieves the current page.
     * <p>
     * Retrieves the Alarms influenced by filters setted in construction.
     *
     * @return array of found Alarms
     */
    public Alarm[] getAlarms() {
        JsonObject object = getJsonObject(pageCursor);

        previousAvailable = object.has("prev");

        if (object.has("alarms")) {
            JsonArray jsonAlarms = object.get("alarms").getAsJsonArray();
            Alarm[] arrayOfAlarms = new Alarm[jsonAlarms.size()];
            for (int i = 0; i < jsonAlarms.size(); i++) {
                JsonElement jsonAlarm = jsonAlarms.get(i).getAsJsonObject();
                Alarm alarm = new Alarm(gson.fromJson(jsonAlarm, ExtensibleObject.class));
                arrayOfAlarms[i] = alarm;
            }
            return arrayOfAlarms;
        } else
            return null;
    }

    private JsonObject getJsonObject(int page) {
        String response;
        String url = "/alarm/alarms?" +
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
     */
    public void next() {
        pageCursor += 1;
    }

    /**
     * Moves cursor to the previous page.
     */
    public void previous() {
        pageCursor -= 1;
    }

    /**
     * Checks if the next page has elements. <b>Use with caution, it does a seperate HTTP request, so it is considered as slow</b>
     *
     * @return true if next page has events, otherwise false.
     */
    public boolean hasNext() {
        JsonObject object = getJsonObject(pageCursor + 1);
        if (object.has("alarms")) {
            JsonArray jsonEvents = object.get("alarms").getAsJsonArray();
            nextAvailable = jsonEvents.size() > 0 ? true : false;
        }
        return nextAvailable;
    }

    /**
     * Checks if there is a previous page.
     *
     * @return true if next page has events, otherwise false.
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

}
