package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Properties;

/**
 * Created by breucking on 31.01.16.
 */
public class TestHelper {
    public static String TEST_HOST = "";
    public static String TEST_USERNAME = "";
    public static String TEST_PASSWORD = "";
    public static String TEST_TENANT = "";

    private static final SecureRandom random = new SecureRandom();

    static {
        try {
            getPropValues();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void getPropValues() throws IOException {
        InputStream inputStream;

        try {
            Properties prop = new Properties();
            String propFileName = "test.properties";

            inputStream = TestHelper.class.getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }


            // get the property value and print it out
            TEST_HOST = prop.getProperty("cot.connection.host");
            TEST_USERNAME = prop.getProperty("cot.connection.user");
            TEST_PASSWORD = prop.getProperty("cot.connection.password");
            TEST_TENANT = prop.getProperty("cot.connecion.tenant");

            inputStream.close();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

    public static String getRandom(int length) {
        return new BigInteger(length * 10, random).toString(length);
    }

    public static ManagedObject createManagedObject(String name) {
        ManagedObject mo = new ManagedObject();
        mo.setName(name);
        mo.set("c8y_IsDevice", new JsonObject());
        mo.set("com_cumulocity_model_Agent", new JsonObject());
        return mo;
    }

    public static ManagedObject createRandomManagedObjectInPlatform(CloudOfThingsPlatform cloudOfThingsPlatform, String name) {
        return cloudOfThingsPlatform.getInventoryApi().create(createManagedObject(name));
    }

    public static void deleteManagedObjectInPlatform(CloudOfThingsPlatform cloudOfThingsPlatform, ManagedObject managedObject) {
        cloudOfThingsPlatform.getInventoryApi().delete(managedObject.getId());
    }
}
