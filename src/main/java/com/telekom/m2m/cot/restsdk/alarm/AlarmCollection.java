package com.telekom.m2m.cot.restsdk.alarm;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.JsonArrayPagination;

/**
 * Created by Patrick Steinert on 24.11.16.
 *
 * @author Patrick Steinert
 * @since 0.3.0
 */
public class AlarmCollection extends JsonArrayPagination {

    private static final String COLLECTION_CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.alarmCollection+json;charset=UTF-8;ver=0.9";
    private static final String COLLECTION_ELEMENT_NAME = "alarms";

    /**
     * Creates an AlarmCollection.
     * Use {@link AlarmApi} to get AlarmCollection.
     *
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     * @param relativeApiUrl          relative url of the REST API without leading slash.
     * @param gson                    the necessary json De-/serializer.
     * @param filterBuilder           the build criteria or null if all items should be retrieved.
     * @param pageSize                max number of retrieved elements per page.
     */
    AlarmCollection(final CloudOfThingsRestClient cloudOfThingsRestClient,
    final String relativeApiUrl,
    final Gson gson,
    final Filter.FilterBuilder filterBuilder,
    final int pageSize) {
        super(cloudOfThingsRestClient, relativeApiUrl, gson, COLLECTION_CONTENT_TYPE, COLLECTION_ELEMENT_NAME, filterBuilder, pageSize);
    }

    /**
     * Retrieves the current page.
     * <p>
     * Retrieves the Alarms influenced by filters set in construction.
     *
     * @return array of found Alarms
     */
    public Alarm[] getAlarms() {
        final JsonArray jsonAlarms = getJsonArray();

        if (jsonAlarms != null) {
            final Alarm[] arrayOfAlarms = new Alarm[jsonAlarms.size()];
            for (int i = 0; i < jsonAlarms.size(); i++) {
                JsonElement jsonAlarm = jsonAlarms.get(i).getAsJsonObject();
                final Alarm alarm = new Alarm(gson.fromJson(jsonAlarm, ExtensibleObject.class));
                arrayOfAlarms[i] = alarm;
            }
            return arrayOfAlarms;
        } else {
            return new Alarm[0];
        }
    }
}
