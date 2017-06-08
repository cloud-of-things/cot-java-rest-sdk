package com.telekom.m2m.cot.restsdk.devicecontrol;

/**
 * Created by Patrick Steinert on 31.01.16.
 */
public class DeviceCredentials {
    private String id;
    private String password;
    private String tenantId;
    private String username;

    public void setId(String id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "DeviceCredentials{" + "id='" + id + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
