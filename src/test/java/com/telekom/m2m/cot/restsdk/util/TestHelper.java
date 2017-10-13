package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Initializes the test configuration.
 *
 * The configuration must be passed via environment variables. Be sure to provide the required
 * values on the command line before starting the tests:
 *
 *     export COT_TEST_CONNECTION_HOST="my-host"
 *     export COT_TEST_CONNECTION_USER="my-user"
 *     export COT_TEST_CONNECTION_PASSWORD="my-password"
 *     export COT_TEST_CONNECTION_TENANT="my-tenant"
 */
public class TestHelper {
    public static String TEST_HOST = "";
    public static String TEST_USERNAME = "";
    public static String TEST_PASSWORD = "";
    public static String TEST_TENANT = "";

    private static final SecureRandom random = new SecureRandom();

    static {
        try {
            loadTestConfiguration();
        } catch (RuntimeException e) {
            System.out.println("Configuration exception: " + e);
            System.out.println("Have a look at " + TestHelper.class.getCanonicalName() + " to learn how to provide the test configuration.");
        }
    }

    private static void loadTestConfiguration() {
        TEST_HOST = readFromEnvironment("COT_TEST_CONNECTION_HOST");
        TEST_USERNAME = readFromEnvironment("COT_TEST_CONNECTION_USER");
        TEST_PASSWORD = readFromEnvironment("COT_TEST_CONNECTION_PASSWORD");
        TEST_TENANT = readFromEnvironment("COT_TEST_CONNECTION_TENANT");
    }

    public static String getRandom(int length) {
        return new BigInteger(length * 10, random).toString(length);
    }

    public static ManagedObject createManagedObject(String name) {
        return createManagedObject(name, true);
    }

    public static ManagedObject createManagedObject(String name, boolean isAgent) {
        ManagedObject mo = new ManagedObject();
        mo.setName(name);
        mo.set("c8y_IsDevice", new JsonObject());
        if (isAgent) {
            mo.set("com_cumulocity_model_Agent", new JsonObject());
        }
        return mo;
    }

    public static ManagedObject createRandomManagedObjectInPlatform(CloudOfThingsPlatform cloudOfThingsPlatform, ManagedObject managedObject) {
        return cloudOfThingsPlatform.getInventoryApi().create(managedObject);
    }

    public static ManagedObject createRandomManagedObjectInPlatform(CloudOfThingsPlatform cloudOfThingsPlatform, String name) {
        return cloudOfThingsPlatform.getInventoryApi().create(createManagedObject(name));
    }

    public static void deleteManagedObjectInPlatform(CloudOfThingsPlatform cloudOfThingsPlatform, ManagedObject managedObject) {
        cloudOfThingsPlatform.getInventoryApi().delete(managedObject.getId());
    }

    public static void registerAsChildDevice(CloudOfThingsPlatform cloudOfThingsPlatform, ManagedObject parentDevice, ManagedObject childDevice) {
        cloudOfThingsPlatform.getInventoryApi().registerAsChildDevice(
                parentDevice, childDevice
        );
    }

    /**
     * Accepts an environment variable name and returns its value.
     *
     * Fails if the requested variable is not available.
     *
     * Example:
     *
     *     String host = readFromEnvironment("COT_CONNECTION_HOST");
     *
     * @param name The name of the variable.
     * @return The variable value.
     */
    private static String readFromEnvironment(final String name)
    {
        String value = System.getenv(name);
        if (value == null) {
            throw new RuntimeException("Cannot read test configuration: No value found for environment variable '" + name + "'.");
        }
        return value;
    }
}
