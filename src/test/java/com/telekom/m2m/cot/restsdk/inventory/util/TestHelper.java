package com.telekom.m2m.cot.restsdk.inventory.util;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by breucking on 31.01.16.
 */
public class TestHelper {
    public static final String TEST_USERNAME = "patrick-restplay";
    public static final String TEST_PASSWORD = "Test1234";
    public static final String TEST_TENANT = "testing";

    private static final SecureRandom random = new SecureRandom();

    public static String getRandom(int length) {
        return new BigInteger(length*10, random).toString(length);
    }

    public static ManagedObject createManagedObject(String name) {
        ManagedObject mo = new ManagedObject();
        mo.setName(name);
        return mo;
    }

    public static ManagedObject createRandomManagedObjectInPlatform(CloudOfThingsPlatform cloudOfThingsPlatform, String name) {
        return cloudOfThingsPlatform.getInventoryApi().create(createManagedObject(name));
    }

    public static void deleteManagedObjectInPlatform(CloudOfThingsPlatform cloudOfThingsPlatform, ManagedObject managedObject) {
        cloudOfThingsPlatform.getInventoryApi().delete(managedObject.getId());
    }
}
