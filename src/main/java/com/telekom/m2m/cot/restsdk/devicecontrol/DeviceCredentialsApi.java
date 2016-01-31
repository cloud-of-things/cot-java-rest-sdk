package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

import java.io.IOException;

/**
 * Created by breucking on 31.01.16.
 */
public class DeviceCredentialsApi {
    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    protected Gson gson = GsonUtils.createGson();    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.deviceCredentials+json;charset=UTF-8;ver=0.9";


    private String deviceCredentialsApi = "devicecontrol/deviceCredentials";

    public DeviceCredentialsApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient= cloudOfThingsRestClient;
    }

    public DeviceCredentials getCredentials(String deviceId) throws IOException {
        DeviceCredentials deviceCredentials = new DeviceCredentials();
        deviceCredentials.setId(deviceId);
        String response = cloudOfThingsRestClient.doPostRequest(gson.toJson(deviceCredentials), deviceCredentialsApi, CONTENT_TYPE);

        DeviceCredentials retrievedDeviceCredentials = gson.fromJson(response, DeviceCredentials.class);
        return retrievedDeviceCredentials;
    }
}
