package com.telekom.m2m.cot.restsdk.users;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.measurement.Measurement;
import com.telekom.m2m.cot.restsdk.measurement.MeasurementApi;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.SampleTemperatureSensor;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.testng.Assert.*;

public class DevicePermissionIT {
    private static final String USERNAME = "testUsername";
    private static final String FIRST_NAME = "testFirstName";
    private static final String LAST_NAME = "testLastName";
    private static final String PASSWORD = "testPassword";
    private static final String EMAIL = "mail@mail77.com";
    private static final String DEVICE_ID = "fake_device_10111";
    private static final String DEVICE_TYPE = "fake_device_type_" + System.currentTimeMillis();

    private final CloudOfThingsPlatform ownOfThingsPlatform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

    // A second instance of CloudOfThingsPlatform is needed because it represents the session of the other user accessing own managed object
    private final CloudOfThingsPlatform othersCloudOfThingsPlatform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, USERNAME, PASSWORD);

    private final UserApi userApi = ownOfThingsPlatform.getUserApi();

    // This has to be a tenant, for which the account from TestHelper has the necessary permissions!
    // Be careful when using deleting functionality in order to avoid a deletion of the "main" user configured in TestHelper.TEST_USERNAME
    private final String tenant = TestHelper.TEST_TENANT;

    private List<Group> groupsToDelete = new ArrayList<>();
    private List<User> usersToDelete = new ArrayList<>();
    private List<ManagedObject> managedObjectsToDelete = new ArrayList<>();

    @AfterMethod
    public void tearDown() {
        cleanup();
    }

    @Test
    public void testUserCanReadMeasurements() {
        // create own managed object having some measurements and attempt to read those for further comparision
        ManagedObject testManagedObject = createManagedObjectWithMeasurements();
        List<Measurement> measurementCollection1 = readAndAssertMeasurements(ownOfThingsPlatform.getMeasurementApi(), 1);

        // create other user with permissions to read own managed objects measurements
        createUserWithPermissions(testManagedObject, "MEASUREMENT:*:READ");

        // attempt by other user to read own managed objects measurements
        readAndAssertMeasurements(othersCloudOfThingsPlatform.getMeasurementApi(), measurementCollection1.size(), measurementCollection1.get(0));
    }

    @Test
    public void testUserMustNotReadMeasurements() {
        // create own managed object having some measurements
        ManagedObject testManagedObject = createManagedObjectWithMeasurements();

        // create other user without any permissions
        createUser();

        final CloudOfThingsPlatform usersCloudOfThingsPlatform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, USERNAME, PASSWORD);

        // attempt by other user to read own managed objects measurements, expected to fail
        try {
            readAndAssertMeasurements(usersCloudOfThingsPlatform.getMeasurementApi(), -1);
            fail("must not succeed to read measurements w/o access right");
        } catch (CotSdkException ex) {
            assertEquals(403, ex.getHttpStatus());
        }
    }

    @Test
    public void testUserCanCreateMeasurements() {
        // create own managed object having some measurements and attempt to read those for further comparision
        ManagedObject testManagedObject = createManagedObjectWithMeasurements();
        List<Measurement> measurementCollection1 = readAndAssertMeasurements(ownOfThingsPlatform.getMeasurementApi(), 1);

        // create other user with permissions to write own managed objects measurements
        createUserWithPermissions(testManagedObject, "MEASUREMENT:*:ADMIN");

        // attempt by other user to write own managed objects measurements
        Measurement measurement = createMeasurement(testManagedObject, othersCloudOfThingsPlatform.getMeasurementApi());

        // as of today (running againt Cot9) get measurements succeeds but returns empty result if permission READ is not given, so we use tenant to check if creation succeeded
        readAndAssertMeasurements(ownOfThingsPlatform.getMeasurementApi(), 2, measurement, measurementCollection1.get(0));
    }

    @Test
    public void testUserCanCreateAndReadMeasurements() {
        // create own managed object having some measurements and attempt to read those for further comparision
        ManagedObject testManagedObject = createManagedObjectWithMeasurements();
        List<Measurement> measurementCollection1 = readAndAssertMeasurements(ownOfThingsPlatform.getMeasurementApi(), 1);

        // create other user with permissions to read and write own managed objects measurements
        createUserWithPermissions(testManagedObject, "MEASUREMENT:*:ADMIN", "MEASUREMENT:*:READ");

        // attempt by other user to write own managed objects measurements
        Measurement measurement = createMeasurement(testManagedObject, othersCloudOfThingsPlatform.getMeasurementApi());

        // attempt by other user to read own managed objects measurements
        readAndAssertMeasurements(othersCloudOfThingsPlatform.getMeasurementApi(), 2, measurement, measurementCollection1.get(0));
    }

    @Test
    public void testUserMustNotCreateMeasurements() {
        // create own managed object having some measurements
        ManagedObject testManagedObject = createManagedObjectWithMeasurements();

        // create other user with permissions only to read own managed objects measurements
        createUserWithPermissions(testManagedObject,"MEASUREMENT:*:READ");

        // attempt by other user to write own managed objects measurements, expected to fail
        try {
            createMeasurement(testManagedObject, othersCloudOfThingsPlatform.getMeasurementApi());
            fail("must not succeed to create measurements w/o access right");
        } catch (CotSdkException ex) {
            assertEquals(403, ex.getHttpStatus());
        }
    }

    private ManagedObject createManagedObjectWithMeasurements() {
        ManagedObject testManagedObject = TestHelper.createRandomManagedObjectInPlatform(ownOfThingsPlatform, DEVICE_ID);
        managedObjectsToDelete.add(testManagedObject);

        createMeasurement(testManagedObject, ownOfThingsPlatform.getMeasurementApi());

        return testManagedObject;
    }

    private User createUser() {
        return createUserWithPermissions(null);
    }

    private User createUserWithPermissions(ManagedObject managedObject, String... permissions) {
        User user = new User();
        usersToDelete.add(user);

        user.setUserName(USERNAME);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setPassword(PASSWORD);
        user.setEmail(EMAIL);
        user.setDevicePermissions(managedObject != null ? prepareDevicePermissions(managedObject, permissions) : Collections.EMPTY_MAP);

        return userApi.createUser(user, tenant);
    }

    private Map<String, List<DevicePermission>> prepareDevicePermissions(ManagedObject managedObject, String... permissions) {
        List<DevicePermission> devicePermissionsList1 = Stream.of(permissions).map(DevicePermission::new).collect(Collectors.toList());

        Map<String, List<DevicePermission>> devicePermissionsMap = new LinkedHashMap<>();
        devicePermissionsMap.put(managedObject.getId(), devicePermissionsList1);

        return devicePermissionsMap;
    }

    private Measurement createMeasurement(ManagedObject managedObject, MeasurementApi measurementApi) {
        SampleTemperatureSensor sts = new SampleTemperatureSensor(100);
        Measurement testMeasurement = new Measurement();
        testMeasurement.setSource(managedObject);
        testMeasurement.setTime(new Date());
        testMeasurement.setType(DEVICE_TYPE);
        testMeasurement.set(sts);
        return measurementApi.createMeasurement(testMeasurement);
    }

    private List<Measurement> readAndAssertMeasurements(MeasurementApi measurementApi, int expectedSize, Measurement... expectedMeasurementsToContain) {
        List<Measurement> measurements = Arrays.asList(measurementApi.getMeasurements(Filter.build().byType(DEVICE_TYPE), Integer.MAX_VALUE).getMeasurements());

        assertEquals(measurements.size(), expectedSize);
        Stream.of(expectedMeasurementsToContain).forEach(expectedMeasurementToContain -> assertTrue(measurements.stream().anyMatch(measurement -> measurement.getId().equals(expectedMeasurementToContain.getId()))));

        return measurements;
    }

    private void cleanup() {
        for (User user : usersToDelete) {
            try {
                userApi.deleteUser(user, tenant);
            } catch (CotSdkException ex) {
                // This exception is ok, because then the test method managed to delete its own user (should be the norm):
                assertEquals(ex.getHttpStatus(), 404);
            }
        }
        usersToDelete.clear();

        for (Group group : groupsToDelete) {
            try {
                if (group.getId() != null) {
                    userApi.deleteGroup(group, tenant);
                }
            } catch (CotSdkException ex) {
                // This exception is ok, because then the test method managed to delete its own group (should be the norm):
                assertEquals(ex.getHttpStatus(), 404);
            }
        }
        groupsToDelete.clear();

        for (ManagedObject managedObject : managedObjectsToDelete) {
            try {
                if (managedObject.getId() != null) {
                    ownOfThingsPlatform.getInventoryApi().delete(managedObject.getId());
                }
            } catch (CotSdkException ex) {
                // This exception is ok, because then the test method managed to delete its own group (should be the norm):
                assertEquals(ex.getHttpStatus(), 404);
            }
        }
        managedObjectsToDelete.clear();
    }
}