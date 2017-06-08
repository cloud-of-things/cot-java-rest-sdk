package com.telekom.m2m.cot.restsdk.measurement;

import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.SampleTemperatureSensor;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by Patrick Steinert on 14.10.16.
 *
 * @author Patrick Steinert
 */
public class MeasurementApiCollectionIT {
    private CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
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
    public void testMultipleMeasurements() throws Exception {
        // Expects a tenant with already multiple measurements

        MeasurementApi measurementApi = cotPlat.getMeasurementApi();

        MeasurementCollection measurementCollection = measurementApi.getMeasurements(5);


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

        MeasurementCollection measurementCollection = measurementApi.getMeasurements(Filter.build().bySource(testManagedObject.getId()), 5);


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
    public void testDeleteMultipleMeasurementsBySource() throws Exception {
        MeasurementApi mApi = cotPlat.getMeasurementApi();

        Measurement testMeasurement = new Measurement();
        testMeasurement.setSource(testManagedObject);
        testMeasurement.setTime(new Date());
        testMeasurement.setType("mytype");

        mApi.createMeasurement(testMeasurement);


        //measurements = mApi.getMeasurementsBySource(testManagedObject.getId());
        MeasurementCollection measurements = mApi.getMeasurements(Filter.build().bySource(testManagedObject.getId()), 5);
        Measurement[] ms = measurements.getMeasurements();
        Assert.assertEquals(ms.length, 1);

        mApi.deleteMeasurements(Filter.build().bySource(testManagedObject.getId()));
        measurements = mApi.getMeasurements(Filter.build().bySource(testManagedObject.getId()), 5);
        ms = measurements.getMeasurements();
        Assert.assertEquals(ms.length, 0);
    }

    @Test
    public void testMultipleMeasurementsBySource() throws Exception {
        MeasurementApi mApi = cotPlat.getMeasurementApi();

        Measurement testMeasurement = new Measurement();
        testMeasurement.setSource(testManagedObject);
        testMeasurement.setTime(new Date());
        testMeasurement.setType("mytype");
        mApi.createMeasurement(testMeasurement);

        MeasurementCollection measurements = mApi.getMeasurements(5);
        Measurement[] ms = measurements.getMeasurements();
        Assert.assertTrue(ms.length > 0);
        boolean allMeasuremntsFromSource = true;
        for (Measurement m : ms) {
            JsonObject source = (JsonObject) m.get("source");
            if (!source.get("id").getAsString().equals(testManagedObject.getId())) {
                allMeasuremntsFromSource = false;
            }
        }
        Assert.assertFalse(allMeasuremntsFromSource);

        //measurements = mApi.getMeasurementsBySource(testManagedObject.getId());
        measurements = mApi.getMeasurements(Filter.build().bySource(testManagedObject.getId()), 5);
        ms = measurements.getMeasurements();
        allMeasuremntsFromSource = true;
        Assert.assertTrue(ms.length > 0);
        for (Measurement m : ms) {
            JsonObject source = (JsonObject) m.get("source");
            if (!source.get("id").getAsString().equals(testManagedObject.getId())) {
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

        MeasurementCollection measurements = mApi.getMeasurements(5);
        Measurement[] ms = measurements.getMeasurements();
        Assert.assertTrue(ms.length > 0);
        boolean allMeasuremntsFromSameType = true;
        for (Measurement m : ms) {
            if (!m.getType().equals(testManagedObject.getType())) {
                allMeasuremntsFromSameType = false;
            }
        }
        Assert.assertFalse(allMeasuremntsFromSameType);

        measurements = mApi.getMeasurements(Filter.build().byType(testMeasurement.getType()), 5);
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

    @Test
    public void testMultipleMeasurementsByDate() throws Exception {
        MeasurementApi mApi = cotPlat.getMeasurementApi();

        Measurement testMeasurement = new Measurement();
        testMeasurement.setSource(testManagedObject);
        testMeasurement.setTime(new Date(new Date().getTime() - (1000 * 60)));
        testMeasurement.setType("mysuperspecialtype");
        mApi.createMeasurement(testMeasurement);

        Date yesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
        MeasurementCollection measurements = mApi.getMeasurements(Filter.build().byDate(yesterday, new Date()), 5);


        Measurement[] ms = measurements.getMeasurements();
        Assert.assertTrue(ms.length > 0);

        Date beforeYesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24) - 10);

        measurements = mApi.getMeasurements(Filter.build().byDate(beforeYesterday, yesterday), 5);
        ms = measurements.getMeasurements();
        Assert.assertEquals(ms.length, 0);
    }


    @Test
    public void testMultipleMeasurementsByDateAndBySource() throws Exception {
        MeasurementApi mApi = cotPlat.getMeasurementApi();

        Measurement testMeasurement = new Measurement();
        testMeasurement.setSource(testManagedObject);
        testMeasurement.setTime(new Date(new Date().getTime() - (1000 * 60)));
        testMeasurement.setType("mysuperspecialtype");
        mApi.createMeasurement(testMeasurement);

        Date yesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
        MeasurementCollection measurements = mApi.getMeasurements(
                Filter.build()
                        .byDate(yesterday, new Date())
                        .bySource(testManagedObject.getId()), 5);


        Measurement[] ms = measurements.getMeasurements();
        Assert.assertEquals(ms.length, 1);

        Date beforeYesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24) - 10);

        measurements = mApi.getMeasurements(
                Filter.build()
                        .byDate(beforeYesterday, yesterday)
                        .bySource(testManagedObject.getId()), 5);
        ms = measurements.getMeasurements();
        Assert.assertEquals(ms.length, 0);
    }


    @Test
    public void testMultipleMeasurementsByTypeAndBySource() throws Exception {
        MeasurementApi mApi = cotPlat.getMeasurementApi();

        SampleTemperatureSensor sts = new SampleTemperatureSensor();
        sts.setTemperature(100);
        Measurement testMeasurement = new Measurement();
        testMeasurement.setSource(testManagedObject);
        testMeasurement.setTime(new Date(new Date().getTime() - (1000 * 60)));
        testMeasurement.setType("mysuperspecialtype");
        testMeasurement.set(sts);
        mApi.createMeasurement(testMeasurement);

        MeasurementCollection measurements = mApi.getMeasurements(
                Filter.build()
                        .byType("mysuperspecialtype")
                        .bySource(testManagedObject.getId()), 5);


        Measurement[] ms = measurements.getMeasurements();
        Assert.assertEquals(ms.length, 1);


        measurements = mApi.getMeasurements(
                Filter.build()
                        .byType("NOT_USED")
                        .bySource(testManagedObject.getId()), 5);
        ms = measurements.getMeasurements();
        Assert.assertEquals(ms.length, 0);
    }


    @Test
    public void testMultipleMeasurementsByFragmentTypeAndBySource() throws Exception {
        MeasurementApi mApi = cotPlat.getMeasurementApi();

        SampleTemperatureSensor sts = new SampleTemperatureSensor();
        sts.setTemperature(100);
        Measurement testMeasurement = new Measurement();
        testMeasurement.setSource(testManagedObject);
        testMeasurement.setTime(new Date(new Date().getTime() - (1000 * 60)));
        testMeasurement.setType("mysuperspecialtype");
        testMeasurement.set(sts);
        mApi.createMeasurement(testMeasurement);

        MeasurementCollection measurements = mApi.getMeasurements(
                Filter.build()
                        .byFragmentType("com_telekom_m2m_cot_restsdk_util_SampleTemperatureSensor")
                        .bySource(testManagedObject.getId()), 5);


        Measurement[] ms = measurements.getMeasurements();
        Assert.assertEquals(ms.length, 1);


        measurements = mApi.getMeasurements(
                Filter.build()
                        .byFragmentType("com_telekom_m2m_cot_restsdk_util_SampleTemperatureSensor_not")
                        .bySource(testManagedObject.getId()), 5);
        ms = measurements.getMeasurements();
        Assert.assertEquals(ms.length, 0);
    }
}
