package com.telekom.m2m.cot.restsdk;

import com.telekom.m2m.cot.restsdk.devicecontrol.DeviceControlApi;
import com.telekom.m2m.cot.restsdk.devicecontrol.DeviceCredentialsApi;
import com.telekom.m2m.cot.restsdk.identity.IdentityApi;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;

/**
 * Created by breucking on 30.01.16.
 */
public class CloudOfThingsPlatform {
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
