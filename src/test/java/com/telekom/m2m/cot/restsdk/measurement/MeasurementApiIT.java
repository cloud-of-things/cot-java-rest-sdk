package com.telekom.m2m.cot.restsdk.measurement;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Created by Patrick Steinert on 30.01.16.
 */
public class MeasurementApiIT {

    private static final String MEASUREMENT_TYPE = "com_telekom_TestType";

    private CloudOfThingsPlatform cloudOfThingsPlatform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    private MeasurementApi measurementApi = cloudOfThingsPlatform.getMeasurementApi();
    private ManagedObject testManagedObject;

    @BeforeClass
    public void setUp() {
        testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cloudOfThingsPlatform, "fake_name");
    }

    @AfterClass
    public void tearDown() {
        TestHelper.deleteManagedObjectInPlatform(cloudOfThingsPlatform, testManagedObject);
    }

    @Test
    public void testCreateMeasurement() {
        Measurement measurement = createMeasurement();
        Measurement createdMeasurement = measurementApi.createMeasurement(measurement);

        assertNotNull(createdMeasurement.getId(), "Measurement created in the cloud should have an ID.");
    }

    @Test
    public void testCreateAndRead() {
        Measurement measurement = createMeasurement();
        Measurement createdMeasurement = measurementApi.createMeasurement(measurement);
        Measurement retrievedMeasurement = measurementApi.getMeasurement(createdMeasurement.getId());

        Assert.assertEquals(retrievedMeasurement.getId(), createdMeasurement.getId(), "ID of measurement created in the cloud should be equal to returned measurement ID.");
        Assert.assertEquals(retrievedMeasurement.getType(), createdMeasurement.getType(), "Type of measurement created in the cloud should be equal to returned measurement type.");
        Assert.assertEquals(retrievedMeasurement.getTime(), createdMeasurement.getTime(), "Time of measurement created in the cloud should be equal to returned measurement time.");
    }

    @Test(expectedExceptions = {CotSdkException.class})
    public void testCreateAndReadAndDelete() {
        Measurement measurement = createMeasurement();
        Measurement createdMeasurement = measurementApi.createMeasurement(measurement);
        Measurement retrievedMeasurement = measurementApi.getMeasurement(createdMeasurement.getId());

        Assert.assertEquals(retrievedMeasurement.getId(), createdMeasurement.getId());

        measurementApi.delete(retrievedMeasurement);

        Measurement deletedMeasurement = measurementApi.getMeasurement(createdMeasurement.getId());
    }

    @Test
    public void createMeasurements() {
        final Measurement measurement1 = createMeasurement();
        final Measurement measurement2 = createMeasurement();

        final List<Measurement> measurements = Arrays.asList(
                measurement1,
                measurement2
        );

        final List<Measurement> createdMeasurements = measurementApi.createMeasurements(
                measurements
        );

        assertNotNull(createdMeasurements);
        assertEquals(measurements.size(), createdMeasurements.size());
    }

    @Test
    public void testMeasurementsNotifications() throws InterruptedException {
        measurementApi.subscribeToMeasurementsNotifications(testManagedObject.getId());

        Thread.sleep(1000);

        Measurement measurement = createMeasurement();
        measurementApi.createMeasurement(measurement);

        Thread.sleep(1000);

        List<String> notifications = measurementApi.getNotifications(testManagedObject.getId());
        assertNotNull(notifications);
        assertEquals(notifications.size(), 1, "It should be exactly one notification returned");

        assertTrue(notifications.get(0).contains("\"realtimeAction\": \"CREATE\""));
        assertTrue(notifications.get(0).contains(MEASUREMENT_TYPE));

        measurementApi.unsubscribeFromMeasurementsNotifications(testManagedObject.getId());
    }

    private Measurement createMeasurement() {
        final Measurement measurement = new Measurement();
        measurement.setTime(new Date());
        measurement.setType(MEASUREMENT_TYPE);
        measurement.setSource(testManagedObject);
        return measurement;
    }
}
