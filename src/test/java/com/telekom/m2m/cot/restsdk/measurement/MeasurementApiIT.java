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

/**
 * Created by Patrick Steinert on 30.01.16.
 */
public class MeasurementApiIT {

    private CloudOfThingsPlatform cloudOfThingsPlatform = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
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
    public void testCreateMeasurement() throws Exception {
        Measurement measurement = new Measurement();
        measurement.setTime(new Date());
        measurement.setType("com_telekom_TestType");
        measurement.setSource(testManagedObject);

        MeasurementApi measurementApi = cloudOfThingsPlatform.getMeasurementApi();

        Measurement createdMeasurements = measurementApi.createMeasurement(measurement);

        assertNotNull("Should now have an Id", createdMeasurements.getId());
    }

    @Test
    public void testCreateAndRead() throws Exception {
        Date timeOfEventHappening = new Date();

        Measurement measurement = new Measurement();
        measurement.setTime(timeOfEventHappening);
        measurement.setType("com_telekom_TestType");
        measurement.setSource(testManagedObject);

        MeasurementApi measurementApi = cloudOfThingsPlatform.getMeasurementApi();

        Measurement createdMeasurements = measurementApi.createMeasurement(measurement);

        Measurement retrievedMeasurement = measurementApi.getMeasurement(createdMeasurements.getId());

        Assert.assertEquals(retrievedMeasurement.getId(), createdMeasurements.getId());
        Assert.assertEquals(retrievedMeasurement.getType(), "com_telekom_TestType");
        Assert.assertEquals(retrievedMeasurement.getTime().compareTo(timeOfEventHappening), 0);
    }

    @Test(expectedExceptions = {CotSdkException.class})
    public void testCreateAndReadAndDelete() throws Exception {
        Date timeOfEventHappening = new Date();

        Measurement measurement = new Measurement();
        measurement.setTime(timeOfEventHappening);
        measurement.setType("com_telekom_TestType");
        measurement.setSource(testManagedObject);

        MeasurementApi measurementApi = cloudOfThingsPlatform.getMeasurementApi();

        Measurement createdMeasurements = measurementApi.createMeasurement(measurement);

        Measurement retrievedMeasurement = measurementApi.getMeasurement(createdMeasurements.getId());

        Assert.assertEquals(retrievedMeasurement.getId(), createdMeasurements.getId());

        measurementApi.delete(retrievedMeasurement);

        Measurement deletedMeasurement = measurementApi.getMeasurement(createdMeasurements.getId());
    }

    @Test
    public void createMeasurements() {

        // given
        final Measurement measurement = createMeasurement(testManagedObject);
        final Measurement measurement2 = createMeasurement(testManagedObject);

        final List<Measurement> measurements = Arrays.asList(
                measurement,
                measurement2
        );

        final MeasurementApi measurementApi = cloudOfThingsPlatform.getMeasurementApi();

        // when
        final List<Measurement> createdMeasurements = measurementApi.createMeasurements(
                measurements
        );

        // then
        assertNotNull(createdMeasurements);
        assertEquals(measurements.size(), createdMeasurements.size());

    }

    private static Measurement createMeasurement(final ManagedObject source) {
        final Measurement measurement = new Measurement();
        measurement.setTime(new Date());
        measurement.setType("com_telekom_TestType");
        measurement.setSource(source);
        return measurement;
    }
}
