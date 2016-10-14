package com.telekom.m2m.cot.restsdk.measurement;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by breucking on 30.01.16.
 */
public class MeasurementApiIT {

    CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    private ManagedObject testManagedObject;

    @BeforeMethod
    public void setUp() {
        testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name");
    }

    @AfterMethod
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

    @Test
    public void testMultipleMeasurementsWithPaging() throws Exception {
        // Expects a tenant with already multiple measurements

        // !!! Important !!!
        // Test assumes pageSize default is 5.

        MeasurementApi measurementApi = cotPlat.getMeasurementApi();

        for (int i = 0; i < 6; i++) {
            Measurement testMeasurement = new Measurement();
            testMeasurement.setSource(testManagedObject);
            testMeasurement.setTime(new Date(new Date().getTime() - (i * 5000)));
            testMeasurement.setType("mytype-" + i);
            measurementApi.createMeasurement(testMeasurement);
        }

        MeasurementCollection measurementCollection = measurementApi.getMeasurements(Filter.filter().bySource(testManagedObject.getId()));


        Measurement[] measurements = measurementCollection.getMeasurements();

        Assert.assertEquals(measurements.length, 5);
        Assert.assertTrue(measurementCollection.hasNext());
        Assert.assertFalse(measurementCollection.hasPrevious());

        measurementCollection.next();

        measurements = measurementCollection.getMeasurements();
        Assert.assertEquals(measurements.length, 1);

        Assert.assertFalse(measurementCollection.hasNext());
        Assert.assertTrue(measurementCollection.hasPrevious());

        measurementCollection.previous();
        measurements = measurementCollection.getMeasurements();

        Assert.assertEquals(measurements.length, 5);

        Assert.assertTrue(measurementCollection.hasNext());
        Assert.assertFalse(measurementCollection.hasPrevious());

        measurementCollection.setPageSize(10);
        measurements = measurementCollection.getMeasurements();

        Assert.assertEquals(measurements.length, 6);
        Assert.assertFalse(measurementCollection.hasNext());
        Assert.assertFalse(measurementCollection.hasPrevious());

    }

    @Test
    public void testMultipleMeasurementsBySource() throws Exception {
        MeasurementApi mApi = cotPlat.getMeasurementApi();

        Measurement testMeasurement = new Measurement();
        testMeasurement.setSource(testManagedObject);
        testMeasurement.setTime(new Date());
        testMeasurement.setType("mytype");
        mApi.createMeasurement(testMeasurement);

        MeasurementCollection measurements = mApi.getMeasurements();
        Measurement[] ms = measurements.getMeasurements();
        Assert.assertTrue(ms.length > 0);
        boolean allMeasuremntsFromSource = true;
        for (Measurement m : ms) {
            if (!m.getId().equals(testManagedObject.getId())) {
                allMeasuremntsFromSource = false;
            }
        }
        Assert.assertFalse(allMeasuremntsFromSource);

        //measurements = mApi.getMeasurementsBySource(testManagedObject.getId());
        measurements = mApi.getMeasurements(Filter.filter().bySource(testManagedObject.getId()));
        ms = measurements.getMeasurements();
        allMeasuremntsFromSource = true;
        Assert.assertTrue(ms.length > 0);
        for (Measurement m : ms) {
            if (m.getId().equals(testManagedObject.getId())) {
                allMeasuremntsFromSource = false;
            }
        }
        Assert.assertTrue(allMeasuremntsFromSource);
    }

    @Test
    public void testMultipleMeasurementsByType() throws Exception {
        MeasurementApi mApi = cotPlat.getMeasurementApi();

        Measurement testMeasurement = new Measurement();
        testMeasurement.setSource(testManagedObject);
        testMeasurement.setTime(new Date());
        testMeasurement.setType("mysuperspecialtype");
        mApi.createMeasurement(testMeasurement);

        MeasurementCollection measurements = mApi.getMeasurements();
        Measurement[] ms = measurements.getMeasurements();
        Assert.assertTrue(ms.length > 0);
        boolean allMeasuremntsFromSameType = true;
        for (Measurement m : ms) {
            if (!m.getType().equals(testManagedObject.getType())) {
                allMeasuremntsFromSameType = false;
            }
        }
        Assert.assertFalse(allMeasuremntsFromSameType);

        measurements = mApi.getMeasurements(Filter.filter().byType(testMeasurement.getType()));
        ms = measurements.getMeasurements();
        allMeasuremntsFromSameType = true;
        Assert.assertTrue(ms.length > 0);
        for (Measurement m : ms) {
            if (!m.getType().equals(testMeasurement.getType())) {
                allMeasuremntsFromSameType = false;
            }
        }
        Assert.assertTrue(allMeasuremntsFromSameType);
    }

}
