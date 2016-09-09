package com.telekom.m2m.cot.restsdk.measurement;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by breucking on 30.01.16.
 */
public class MeasurementApiIT {

    CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    private ManagedObject testManagedObject;

    @BeforeClass
    public void setUp() {
        testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name");
    }

    @AfterClass
    public void tearDown() {
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
    }


    @Test
    public void testCreateEvent() throws Exception {
        Measurement measurement = new Measurement();
        measurement.setTime(new Date());
        measurement.setType("com_telekom_TestType");
        measurement.setSource(testManagedObject);

        MeasurementApi measurementApi = cotPlat.getMeasurementApi();

        Measurement createdMeasurements = measurementApi.createMeasurement(measurement);

        Assert.assertNotNull("Should now have an Id", createdMeasurements.getId());
    }

    @Test
    public void testCreateAndRead() throws Exception {
        Date timeOfEventHappening = new Date();

        Measurement measurement = new Measurement();
        measurement.setTime(timeOfEventHappening);
        measurement.setType("com_telekom_TestType");
        measurement.setSource(testManagedObject);

        MeasurementApi measurementApi = cotPlat.getMeasurementApi();

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

        MeasurementApi measurementApi = cotPlat.getMeasurementApi();

        Measurement createdMeasurements = measurementApi.createMeasurement(measurement);

        Measurement retrievedMeasurement = measurementApi.getMeasurement(createdMeasurements.getId());

        Assert.assertEquals(retrievedMeasurement.getId(), createdMeasurements.getId());

        measurementApi.delete(retrievedMeasurement);

        Measurement deletedMeasurement = measurementApi.getMeasurement(createdMeasurements.getId());
    }

    @Test
    public void testMultipleMeasurements() throws Exception {
        // Expects a tenant with already multiple measurements

        MeasurementApi measurementApi = cotPlat.getMeasurementApi();

        MeasurementCollection measurementCollection = measurementApi.getMeasurements();


        Measurement[] measurements = measurementCollection.getMeasurements();

        Assert.assertTrue(measurements.length > 0);

        Measurement measurement = measurements[0];

        Assert.assertTrue(measurement.getId() != null);
        Assert.assertTrue(measurement.getId().length() > 0);

        Assert.assertTrue(measurement.getTime() != null);
        Assert.assertTrue(measurement.getTime().compareTo(new Date()) < 0);

        Assert.assertTrue(measurement.getType() != null);
        Assert.assertTrue(measurement.getType().length() > 0);


    }
}
