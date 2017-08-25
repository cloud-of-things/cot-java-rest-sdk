package com.telekom.m2m.cot.restsdk;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import com.telekom.m2m.cot.restsdk.alarm.AlarmApi;
import com.telekom.m2m.cot.restsdk.audit.AuditApi;
import com.telekom.m2m.cot.restsdk.devicecontrol.CotCredentials;
import com.telekom.m2m.cot.restsdk.devicecontrol.DeviceControlApi;
import com.telekom.m2m.cot.restsdk.devicecontrol.DeviceCredentialsApi;
import com.telekom.m2m.cot.restsdk.event.EventApi;
import com.telekom.m2m.cot.restsdk.identity.IdentityApi;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;
import com.telekom.m2m.cot.restsdk.measurement.MeasurementApi;
import com.telekom.m2m.cot.restsdk.retentionrule.RetentionRuleApi;
import com.telekom.m2m.cot.restsdk.smartrest.SmartRestApi;
import com.telekom.m2m.cot.restsdk.users.UserApi;

import okhttp3.OkHttpClient;

/**
 * The CloudOfThingsPlatform is the starting point to access the Cloud of
 * Things.
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
     * Get a platform object to register new devices. This should be used for
     * retrieving the credentials.
     *
     * @param host
     *            URL to the host to connect to
     * @param cotCredentials
     *            a credentials object with the credentials.
     */
    public CloudOfThingsPlatform(String host, CotCredentials cotCredentials) {
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(1, TimeUnit.MINUTES).build();
        cloudOfThingsRestClient = new CloudOfThingsRestClient(client, host, cotCredentials.getUsername(),
                cotCredentials.getPassword());
    }

    /**
     * Get a platform object to register new devices. This should be used for
     * retrieving the credentials.
     *
     * @param host
     *            URL to the host to connect to.
     * @return a CloudOfThingsPlatform object with special connection
     *         properties.
     */
    public static CloudOfThingsPlatform getPlatformToRegisterDevice(String host) {
        return new CloudOfThingsPlatform(host,
                new String(Base64.getDecoder().decode(REGISTERDEVICE_TENANT)) + "/"
                        + new String(Base64.getDecoder().decode(REGISTERDEVICE_USERNAME)),
                new String(Base64.getDecoder().decode(REGISTERDEVICE_PASSWORD)));
    }
    
    /**
     * Get a platform object to register new devices through a proxy server.
     * This should be used for retrieving the credentials.
     *
     * @param host
     *            URL to the host to connect to.
     * @param proxyHost
     *            hostname of the HTTP proxy server
     * @param proxyPort
     *            port of the HTTP proxy server.
     * @return a CloudOfThingsPlatform object with special connection
     *         properties.
     */
    public static CloudOfThingsPlatform getPlatformToRegisterDevice(String host, String proxyHost, int proxyPort) {
        return new CloudOfThingsPlatform(host, new String(Base64.getDecoder().decode(REGISTERDEVICE_USERNAME)),
                new String(Base64.getDecoder().decode(REGISTERDEVICE_PASSWORD)), proxyHost, proxyPort);
    }

    /**
     * Creates a CloudOfThingsPlatform object, the start point to interfere with
     * the CoT.
     *
     * @param host
     *            URL to the host to connect to.
     * @param username
     *            the username of the platform user.
     * @param password
     *            the username of the platform user.
     */
    public CloudOfThingsPlatform(String host, String username, String password) {
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(1, TimeUnit.MINUTES).build();
        cloudOfThingsRestClient = new CloudOfThingsRestClient(client, host, username, password);
    }

    /**
     * Get a platform object to register new devices. This should be used for
     * retrieving the credentials.
     *
     * @param host
     *            URL to the host to connect to
     * @param cotCredentials
     *            a credentials object with the credentials.
     * @param proxyHost
     *            hostname of the HTTP proxy server
     * @param proxyPort
     *            port of the HTTP proxy server.
     */
    public CloudOfThingsPlatform(String host, CotCredentials cotCredentials, String proxyHost, int proxyPort) {
        OkHttpClient client = new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)))
                .readTimeout(1, TimeUnit.MINUTES).build();
        cloudOfThingsRestClient = new CloudOfThingsRestClient(client, host, cotCredentials.getUsername(),
                cotCredentials.getPassword());
    }

    /**
     * Creates a CloudOfThingsPlatform object, the start point to interfere with
     * the CoT over HTTP proxy server.
     *
     * @param host
     *            URL to the host to connect to.
     * @param username
     *            the username of the platform user.
     * @param password
     *            the username of the platform user.
     * @param proxyHost
     *            hostname of the HTTP proxy server
     * @param proxyPort
     *            port of the HTTP proxy server.
     */
    public CloudOfThingsPlatform(String host, String username, String password, String proxyHost, int proxyPort) {
        OkHttpClient client = new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)))
                .readTimeout(1, TimeUnit.MINUTES).build();
        cloudOfThingsRestClient = new CloudOfThingsRestClient(client, host, username, password);
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

    public EventApi getEventApi() {
        return new EventApi(cloudOfThingsRestClient);
    }

    public UserApi getUserApi() {
        return new UserApi(cloudOfThingsRestClient);
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

    /**
     * Returns the object to work with the audit API.
     *
     * @return ready to use AuditApi object.
     */
    public AuditApi getAuditApi() {
        return new AuditApi(cloudOfThingsRestClient);
    }

    /**
     * Returns the object to work with the retention rules API.
     *
     * @return ready to use RetentionRuleApi object.
     */
    public RetentionRuleApi getRetentionRuleApi() {
        return new RetentionRuleApi(cloudOfThingsRestClient);
    }


	public SmartRestApi getSmartRestApi() {
		return new SmartRestApi(cloudOfThingsRestClient);
	}

}
