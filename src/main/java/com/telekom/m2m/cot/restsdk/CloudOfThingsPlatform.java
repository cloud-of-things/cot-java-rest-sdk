package com.telekom.m2m.cot.restsdk;

import com.telekom.m2m.cot.restsdk.devicecontrol.DeviceControlApi;
import com.telekom.m2m.cot.restsdk.devicecontrol.DeviceCredentialsApi;
import com.telekom.m2m.cot.restsdk.identity.IdentityApi;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;

import java.util.Base64;

/**
 * Created by breucking on 30.01.16.
 */
public class CloudOfThingsPlatform {

    private static final String REGISTERDEVICE_USERNAME = "ZGV2aWNlYm9vdHN0cmFw";
    private static final String REGISTERDEVICE_PASSWORD = "RmhkdDFiYjFm";
    private static final String REGISTERDEVICE_TENANT = "bWFuYWdlbWVudA==";

    public static CloudOfThingsPlatform getPlatformToRegisterDevice() throws Exception {
        return new CloudOfThingsPlatform(new String(Base64.getDecoder().decode(REGISTERDEVICE_TENANT)),
                new String(Base64.getDecoder().decode(REGISTERDEVICE_USERNAME)),
                new String(Base64.getDecoder().decode(REGISTERDEVICE_PASSWORD)));
    }


    CloudOfThingsRestClient cloudOfThingsRestClient;

    public CloudOfThingsPlatform(String tenant, String username, String password) throws Exception {
        cloudOfThingsRestClient = new CloudOfThingsRestClient(tenant, username, password);
    }

    public void getManagementApi() {

    }

    public InventoryApi getInventoryApi() {
        return new InventoryApi(cloudOfThingsRestClient);
    }

    public DeviceControlApi getDeviceControlApi() {
        return new DeviceControlApi(cloudOfThingsRestClient);
    }

    public DeviceCredentialsApi getDeviceCredentialsApi() {
        return new DeviceCredentialsApi(cloudOfThingsRestClient);
    }

    public IdentityApi getIdentityApi() {
        return new IdentityApi(cloudOfThingsRestClient);
    }
}
