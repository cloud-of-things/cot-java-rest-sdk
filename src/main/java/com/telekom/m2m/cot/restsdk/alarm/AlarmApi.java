package com.telekom.m2m.cot.restsdk.alarm;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Created by breucking on 22.09.16.
 */
public class AlarmApi {
    CloudOfThingsRestClient cloudOfThingsRestClient;
    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.alarm+json;charset=UTF-8;ver=0.9";
    private final Gson gson = GsonUtils.createGson();


    public AlarmApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }


    public Alarm getAlarm(String id) {
        String response = cloudOfThingsRestClient.getResponse(id, "alarm/alarms", CONTENT_TYPE);
        Alarm alarm = new Alarm(gson.fromJson(response, ExtensibleObject.class));
        return alarm;
    }

    public Alarm create(Alarm alarm) {
        String json = gson.toJson(alarm);
        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "alarm/alarms", CONTENT_TYPE);
        alarm.setId(id);
        return alarm;
    }


}
