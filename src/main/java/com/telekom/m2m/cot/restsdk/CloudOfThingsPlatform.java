package com.telekom.m2m.cot.restsdk;

import com.telekom.m2m.cot.restsdk.alarm.AlarmApi;
import com.telekom.m2m.cot.restsdk.devicecontrol.DeviceControlApi;
import com.telekom.m2m.cot.restsdk.devicecontrol.DeviceCredentialsApi;
import com.telekom.m2m.cot.restsdk.event.EventApi;
import com.telekom.m2m.cot.restsdk.identity.IdentityApi;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;
import com.telekom.m2m.cot.restsdk.measurement.MeasurementApi;
import okhttp3.OkHttpClient;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * The CloudOfThingsPlatform is the starting point to interfere the Cloud of Things.
 * <p>
 * Created by Patrick Steinert on 30.01.16.
 *
 * @since 0.1.0
 */
public class CloudOfThingsPlatform {

    private static final String REGISTERDEVICE_USERNAME = "ZGV2aWNlYm9vdHN0cmFw";
    private static final String REGISTERDEVICE_PASSWORD = "RmhkdDFiYjFm";
    private static final String REGISTERDEVICE_TENANT = "bWFuYWdlbWVudA==";

    private CloudOfThingsRestClient cloudOfThingsRestClient;

    /**
     * Get a platform object to register new devices. This should be used for retrieving the credentials.
     *
     * @param host URL to the host to connect to.
     * @return a CloudOfThingsPlatform object with special connection properties.
     */
    public static CloudOfThingsPlatform getPlatformToRegisterDevice(String host) {
        return new CloudOfThingsPlatform(host,
                new String(Base64.getDecoder().decode(REGISTERDEVICE_TENANT)),
                new String(Base64.getDecoder().decode(REGISTERDEVICE_USERNAME)),
                new String(Base64.getDecoder().decode(REGISTERDEVICE_PASSWORD)));
    }

    /**
     * Creates a CloudOfThingsPlatform object, the start point to interfere with the CoT.
     *
     * @param host     URL to the host to connect to.
     * @param tenant   the tenant of the platform.
     * @param username the username of the platform user.
     * @param password the username of the platform user.
     */
    public CloudOfThingsPlatform(String host, String tenant, String username, String password) {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.MINUTES)
                .build();
        cloudOfThingsRestClient = new CloudOfThingsRestClient(client, host, tenant, username, password);
    }

    /**
     * Creates a CloudOfThingsPlatform object, the start point to interfere
     * with the CoT over HTTP proxy server.
     *
     * @param host      URL to the host to connect to.
     * @param tenant    the tenant of the platform.
     * @param username  the username of the platform user.
     * @param password  the username of the platform user.
     * @param proxyHost hostname of the HTTP proxy server
     * @param proxyPort port of the HTTP proxy server.
     */
    public CloudOfThingsPlatform(String host, String tenant, String username, String password, String proxyHost, int proxyPort) {
        OkHttpClient client = new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)))
                .readTimeout(1, TimeUnit.MINUTES)
                .build();
        cloudOfThingsRestClient = new CloudOfThingsRestClient(client, host, tenant, username, password);
    }

    /**
     * Returns the object to work with the inventory API.
     *
     * @return ready to use InventoryApi object.
     */
    public InventoryApi getInventoryApi() {
        return new InventoryApi(cloudOfThingsRestClient);
    }

    /**
     * Returns the object to work with the device control API.
     *
     * @return ready to use DeviceControlApi object.
     */
    public DeviceControlApi getDeviceControlApi() {
        return new DeviceControlApi(cloudOfThingsRestClient);
    }

    /**
     * Returns the object to work with the device credentials API.
     *
     * @return ready to use DeviceCredentialsApi object.
     */
    public DeviceCredentialsApi getDeviceCredentialsApi() {
        return new DeviceCredentialsApi(cloudOfThingsRestClient);
    }

    /**
     * Returns the object to work with the identity API.
     *
     * @return ready to use IdentityApi object.
     */
    public IdentityApi getIdentityApi() {
        return new IdentityApi(cloudOfThingsRestClient);
    }

    public void getManagementApi() {

    }

    public EventApi getEventApi() {
        return new EventApi(cloudOfThingsRestClient);
    }

    /**
     * Returns the object to work with the measurements API.
     *
     * @return ready to use MeasurementApi object.
     */
    public MeasurementApi getMeasurementApi() {
        return new MeasurementApi(cloudOfThingsRestClient);
    }

    /**
     * Returns the object to work with the alarm API.
     *
     * @return ready to use AlarmApi object.
     */
    public AlarmApi getAlarmApi() {
        return new AlarmApi(cloudOfThingsRestClient);
    }
}
