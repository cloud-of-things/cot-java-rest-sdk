package com.telekom.m2m.cot.restsdk;

import com.telekom.m2m.cot.restsdk.devicecontrol.DeviceControlApi;
import com.telekom.m2m.cot.restsdk.devicecontrol.DeviceCredentialsApi;
import com.telekom.m2m.cot.restsdk.identity.IdentityApi;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;

import java.util.Base64;

/**
 * The CloudOfThingsPlatform is the starting point to interfere the Cloud of Things.
 *
 * Created by breucking on 30.01.16.
 * @since 1.4
 */
public class CloudOfThingsPlatform {

    private static final String REGISTERDEVICE_USERNAME = "ZGV2aWNlYm9vdHN0cmFw";
    private static final String REGISTERDEVICE_PASSWORD = "RmhkdDFiYjFm";
    private static final String REGISTERDEVICE_TENANT = "bWFuYWdlbWVudA==";

    private CloudOfThingsRestClient cloudOfThingsRestClient;

    /**
     * Get a platform object to register new devices. This should be used for retrieving the credentials.
     * @return a CloudOfThingsPlatform object with special connection properties.
     */
    public static CloudOfThingsPlatform getPlatformToRegisterDevice() {
        try {
            return new CloudOfThingsPlatform(new String(Base64.getDecoder().decode(REGISTERDEVICE_TENANT)),
                    new String(Base64.getDecoder().decode(REGISTERDEVICE_USERNAME)),
                    new String(Base64.getDecoder().decode(REGISTERDEVICE_PASSWORD)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a CloudOfThingsPlatform object, the start point to interfere with the CoT.
     * @param tenant the tenant of the platform.
     * @param username the username of the platform user.
     * @param password the username of the platform user.
     * @throws Exception if initialization failed
     */
    public CloudOfThingsPlatform(String tenant, String username, String password) throws Exception{
        cloudOfThingsRestClient = new CloudOfThingsRestClient(tenant, username, password);
    }

    /**
     * Returns the object to work with the inventory API.
     * @return ready to use InventoryApi object.
     */
    public InventoryApi getInventoryApi() {
        return new InventoryApi(cloudOfThingsRestClient);
    }

    /**
     * Returns the object to work with the device control API.
     * @return ready to use DeviceControlApi object.
     */
    public DeviceControlApi getDeviceControlApi() {
        return new DeviceControlApi(cloudOfThingsRestClient);
    }

    /**
     * Returns the object to work with the device credentials API.
     * @return ready to use DeviceCredentialsApi object.
     */
    public DeviceCredentialsApi getDeviceCredentialsApi() {
        return new DeviceCredentialsApi(cloudOfThingsRestClient);
    }

    /**
     * Returns the object to work with the identity API.
     * @return ready to use IdentityApi object.
     */
    public IdentityApi getIdentityApi() {
        return new IdentityApi(cloudOfThingsRestClient);
    }

    public void getManagementApi() {
    }
}
