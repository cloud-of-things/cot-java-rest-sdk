package com.telekom.m2m.cot.restsdk.devicecontrol;

/**
 * Created by Patrick Steinert on 12.01.17.
 */
public class CotCredentials {
    private final String tenant;
    private final String username;
    private final String password;

    public CotCredentials(String tenant, String username, String password) {
        this.tenant = tenant;
        this.username = username;
        this.password = password;
    }


    public String getTenant() {
        return tenant;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
