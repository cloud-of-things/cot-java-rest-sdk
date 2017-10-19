package com.telekom.m2m.cot.restsdk.users;

import com.telekom.m2m.cot.restsdk.util.CotSdkException;

/**
 * DevicePermission represents a device permission structure in the CoT which is used to be set for a user.
 * It is possible to set separate device permissions for each device and each fragment assigned to this device.
 *
 * Structure expected in CoT:
 *     [API:fragment_name:permission]
 * where:
 *
 * [API] is one of the following values: "OPERATION", "ALARM", "AUDIT", "EVENT", "MANAGED_OBJECT", "MEASUREMENT"
 *     or "*" if all APIs needs to get the same permission.
 *
 * [fragment_name] is any fragment name, e.g. "c8y_Restart"
 *     or "*" if a device does not have any fragments
 *
 * [permission] is
 *     "ADMIN" allows POST, PUT and DELETE http methods,
 *     "READ" allows only GET http method,
 *     "*" if both permissions "ADMIN" and "READ" should be allowed
 *
 */
public class DevicePermission {

    /**
     * enum for existing APIs
     */
    public enum Api {
        OPERATION("OPERATION"),
        ALARM("ALARM"),
        AUDIT("AUDIT"),
        EVENT("EVENT"),
        MANAGED_OBJECT("MANAGED_OBJECT"),
        MEASUREMENT("MEASUREMENT"),
        ALL("*");

        private String value;

        Api(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Api getEnum(String value) {
            for(Api v : values()) {
                if(v.getValue().equalsIgnoreCase(value)) {
                    return v;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    /**
     * Enum for allowed permissions
     */
    public enum Permission {
        ADMIN("ADMIN"),
        READ("READ"),
        ALL("*");

        private String value;

        Permission(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Permission getEnum(String value) {
            for(Permission v : values()) {
                if(v.getValue().equalsIgnoreCase(value)) {
                    return v;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    // separator for device permission structure
    private static final String SEPARATOR = ":";
    private static final String ALL_FRAGMENTS = "*";

    private Api api;
    private String fragmentName;
    private Permission permission;

    /**
     * Constructor to set all values provided by the CoT device permission structure.
     *
     * @param api an Api enum like ALARM, EVENT ....
     * @param fragmentName a String with fragment name like "c8y_Restart" or null if the device does not have any fragment.
     * @param permission a Permission enum: ADMIN, READ or ALL
     */
    public DevicePermission(Api api, String fragmentName, Permission permission) {
        this.api = api;
        this.fragmentName = fragmentName;
        this.permission = permission;
    }

    /**
     * Constructor to set all values converting the CoT device permission structure.
     *
     * @param cotDevicePermissionStructure a String with device permission structure: [API:fragment_name:permission].
     */
    public DevicePermission(String cotDevicePermissionStructure) {
        if(cotDevicePermissionStructure == null || cotDevicePermissionStructure.isEmpty()) {
            throw new CotSdkException("CoT device permission structure is empty!");
        }

        String[] params = cotDevicePermissionStructure.split(":");

        // we are always expecting three parameters
        if(params.length != 3) {
            throw new CotSdkException("CoT device permission structure is not conform to [API:fragment_name:permission]");
        }

        // the first parameter should be the Api like ALARM, EVENT...
        try {
            this.api = Api.getEnum(params[0]);
        } catch(Exception e) {
            throw new CotSdkException("Couldn't convert Api in the CoT device permission structure: " + cotDevicePermissionStructure);
        }

        // second parameter should be fragment name like "c8y_Restart"
        if(params[1] == null || params[1].isEmpty()) {
            throw new CotSdkException("Fragment name is empty! in the CoT device permission structure: " + cotDevicePermissionStructure);
        }

        this.fragmentName = params[1];

        // the last parameter should be the permission
        try {
            this.permission = Permission.getEnum(params[2]);
        } catch(Exception e) {
            throw new CotSdkException("Couldn't convert Permission in the CoT device permission structure: " + cotDevicePermissionStructure);
        }
    }

    /**
     * Get the Api of the device permission.
     *
     * @return Api enum of the device permission like ALARM, EVENT ...
     */
    public Api getApi() {
        return api;
    }

    /**
     * Set the Api of the device permission.
     *
     * @param api an Api enum of the device permission like ALARM, EVENT ...
     */
    public void setApi(Api api) {
        this.api = api;
    }

    /**
     * Get fragment name of the device permission.
     *
     * @return String with the fragment name of the device permission like e.g. "c8y_Restart"
     *  or null or "*" if the device does not have any fragment.
     */
    public String getFragmentName() {
        return fragmentName;
    }

    /**
     * Set fragment name of the device permission.
     *
     * @param fragmentName a String with the fragment name of the device permission like e.g. "c8y_Restart"
     * or null or "*" if the device does not have any fragment.
     */
    public void setFragmentName(String fragmentName) {
        this.fragmentName = fragmentName;
    }

    /**
     * Get the permission for an Api and a fragment.
     *
     * @return a Permission enum for an Api and a fragment. Possible values: ADMIN, READ or ALL.
     */
    public Permission getPermission() {
        return permission;
    }

    /**
     * Set the permission for an Api and a fragment.
     *
     * @param permission a Permission enum for an Api and a fragment. Possible values: ADMIN, READ or ALL.
     */
    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    /**
     * Overrides the method to get device permission structure which is expected in CoT.
     *
     * @return a String with the device permission structure for the CoT:
     * [API:fragment_name:permission]
     */
    @Override
    public String toString() {
        return api.getValue() + SEPARATOR + (fragmentName==null?ALL_FRAGMENTS:fragmentName) + SEPARATOR + permission.getValue();
    }
}
