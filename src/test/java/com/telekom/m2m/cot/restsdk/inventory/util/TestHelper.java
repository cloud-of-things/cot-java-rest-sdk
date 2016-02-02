package com.telekom.m2m.cot.restsdk.inventory.util;

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
}
