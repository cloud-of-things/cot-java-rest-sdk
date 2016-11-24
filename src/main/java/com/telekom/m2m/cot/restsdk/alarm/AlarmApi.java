package com.telekom.m2m.cot.restsdk.alarm;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Use AlarmApi to work with Alarms.
 * <p>
 * Created by breucking on 22.09.16.
 */
public class AlarmApi {
    CloudOfThingsRestClient cloudOfThingsRestClient;
    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.alarm+json;charset=UTF-8;ver=0.9";
    private final Gson gson = GsonUtils.createGson();


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
     * @return
     */
    public Alarm getAlarm(String alarmId) {
        String response = cloudOfThingsRestClient.getResponse(alarmId, "alarm/alarms", CONTENT_TYPE);
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
        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "alarm/alarms", CONTENT_TYPE);
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
        cloudOfThingsRestClient.doPutRequest(json, "alarm/alarms/" + alarm.getId(), CONTENT_TYPE);
    }

    /**
     * Get a pageable AlarmCollection to retrieve Alarms.
     *
     * @return the found Alarms in a pageable collection.
     * @since 0.3.0
     */
    public AlarmCollection getAlarms() {
        return new AlarmCollection(cloudOfThingsRestClient);
    }

    /**
     * Get a pageable AlarmCollection to retrieve Alarms.
     *
     * @param filters adds search criteria
     * @return the found Alarms in a pageable collection.
     * @since 0.3.0
     */
    public AlarmCollection getAlarms(Filter.FilterBuilder filters) {
        return new AlarmCollection(filters, cloudOfThingsRestClient);
    }

    /**
     * Deletes a collection of Alarms by criteria.
     *
     * @param filters filters of Alarm attributes.
     */
    public void deleteAlarms(Filter.FilterBuilder filters) {
        cloudOfThingsRestClient.delete("", "alarm/alarms?" + filters.buildFilter() + "&x=");
    }
}
