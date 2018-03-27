package com.telekom.m2m.cot.restsdk.alarm;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.*;

import java.util.Arrays;
import java.util.List;

/**
 * Use AlarmApi to work with Alarms.
 * <p>
 * Created by Patrick Steinert on 22.09.16.
 */
public class AlarmApi {
    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.alarm+json;charset=UTF-8;ver=0.9";
    private static final String ACCEPT = "application/vnd.com.nsn.cumulocity.alarm+json;charset=UTF-8;ver=0.9";
    private static final String RELATIVE_API_URL = "/alarm/alarms/";
    private static final List<FilterBy> acceptedFilters = Arrays.asList(
            FilterBy.BYSTATUS, FilterBy.BYSOURCE, FilterBy.BYDATEFROM, FilterBy.BYDATETO, FilterBy.BYTYPE);
    private final Gson gson = GsonUtils.createGson();
    CloudOfThingsRestClient cloudOfThingsRestClient;


    /**
     * Internal constructor.
     *
     * @param cloudOfThingsRestClient the configured rest client.
     */
    public AlarmApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    /**
     * Retrieve a specific Alarm from the CoT platform.
     *
     * @param alarmId the unique identifier of the desired Alarm.
     * @return the retrieved Alarm.
     */
    public Alarm getAlarm(String alarmId) {
        String response = cloudOfThingsRestClient.getResponse(alarmId, RELATIVE_API_URL, CONTENT_TYPE);
        Alarm alarm = new Alarm(gson.fromJson(response, ExtensibleObject.class));
        return alarm;
    }

    /**
     * Stores an alarm in the CoT platform.
     *
     * @param alarm the alarm to create.
     * @return the created alarm with the assigned unique identifier.
     */
    public Alarm create(Alarm alarm) {
        String json = gson.toJson(alarm);
        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, RELATIVE_API_URL, CONTENT_TYPE, ACCEPT);
        alarm.setId(id);
        return alarm;
    }

    /**
     * Updates an Alarm. It is just possible to update status and severity.
     * Any further attributes will be ignored.
     *
     * @param alarm the alarm to update.
     * @since 0.3.0
     */
    public void update(Alarm alarm) {
        ExtensibleObject extensibleObject = new ExtensibleObject();
        extensibleObject.set("status", alarm.getStatus());
        extensibleObject.set("severity", alarm.getSeverity());

        String json = gson.toJson(extensibleObject);
        cloudOfThingsRestClient.doPutRequest(json, RELATIVE_API_URL + alarm.getId(), CONTENT_TYPE);
    }

    /**
     * Get a pageable AlarmCollection to retrieve Alarms.
     *
     * @param resultSize size of the results (Max. 2000)
     * @return the found Alarms in a pageable collection.
     * @since 0.3.0
     */
    public AlarmCollection getAlarms(int resultSize) {
        return new AlarmCollection(
                cloudOfThingsRestClient,
                RELATIVE_API_URL,
                gson,
                null,
                resultSize);
    }

    /**
     * Get a pageable AlarmCollection to retrieve Alarms.
     *
     * @param filters    adds search criteria
     * @param resultSize size of the results (Max. 2000)
     * @return the found Alarms in a pageable collection.
     * @since 0.3.0
     */
    public AlarmCollection getAlarms(Filter.FilterBuilder filters, int resultSize) {
        if(filters != null)
            filters.validateSupportedFilters(acceptedFilters);
        return new AlarmCollection(
                cloudOfThingsRestClient,
                RELATIVE_API_URL,
                gson,
                filters,
                resultSize);
    }

    /**
     * Deletes a collection of Alarms by criteria.
     *
     * @param filters filters of Alarm attributes.
     */
    public void deleteAlarms(Filter.FilterBuilder filters) {
        if(filters != null)
            filters.validateSupportedFilters(acceptedFilters);
        cloudOfThingsRestClient.delete("", RELATIVE_API_URL+ "?" + filters.buildFilter() + "&x=");
    }
}
