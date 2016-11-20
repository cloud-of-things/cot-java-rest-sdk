package com.telekom.m2m.cot.restsdk.alarm;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Use AlarmApi to work with Alarms.
 *
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


}
