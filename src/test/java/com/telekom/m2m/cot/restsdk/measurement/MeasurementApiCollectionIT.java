package com.telekom.m2m.cot.restsdk.measurement;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
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
    private final CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    private ManagedObject testManagedObject;
    private MeasurementApi measurementApi;

    @BeforeMethod
    public void setUp() {
        testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name");

        measurementApi = cotPlat.getMeasurementApi();
    }

    @AfterMethod
    public void tearDown() {
        measurementApi.deleteMeasurements(Filter.build().bySource(testManagedObject.getId()));
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
    }


    @Test
    public void testMultipleMeasurements() {
        final Measurement testMeasurement = new Measurement();
        testMeasurement.setSource(testManagedObject);
        testMeasurement.setTime(new Date());
        testMeasurement.setType("mytype");

        measurementApi.createMeasurement(testMeasurement);

        MeasurementCollection measurementCollection = measurementApi.getMeasurements(5);


        Measurement[] measurements = measurementCollection.getMeasurements();

        Assert.assertTrue(measurements.length > 0);

        Measurement measurement = measurements[0];

        Assert.assertNotNull(measurement.getId());
        Assert.assertTrue(measurement.getId().length() > 0);

        Assert.assertNotNull(measurement.getTime());
        Assert.assertTrue(measurement.getTime().compareTo(new Date()) < 0);

        Assert.assertNotNull(measurement.getType());
        Assert.assertTrue(measurement.getType().length() > 0);
    }

    @Test
    public void testMultipleMeasurementsWithPaging() {
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
    public void testDeleteMultipleMeasurementsBySource() {
        Measurement testMeasurement = new Measurement();
        testMeasurement.setSource(testManagedObject);
        testMeasurement.setTime(new Date());
        testMeasurement.setType("mytype");

        measurementApi.createMeasurement(testMeasurement);


        //measurements = measurementApi.getMeasurementsBySource(testManagedObject.getId());
        MeasurementCollection measurements = measurementApi.getMeasurements(Filter.build().bySource(testManagedObject.getId()), 5);
        Measurement[] ms = measurements.getMeasurements();
        Assert.assertEquals(ms.length, 1);

        measurementApi.deleteMeasurements(Filter.build().bySource(testManagedObject.getId()));
        measurements = measurementApi.getMeasurements(Filter.build().bySource(testManagedObject.getId()), 5);
        ms = measurements.getMeasurements();
        Assert.assertEquals(ms.length, 0);
    }

    @Test
    public void testMultipleMeasurementsBySource() {
        Measurement testMeasurement = new Measurement();
        testMeasurement.setSource(testManagedObject);
        testMeasurement.setTime(new Date());
        testMeasurement.setType("mytype");
        measurementApi.createMeasurement(testMeasurement);

        ManagedObject testManagedObject2 = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name");

        Measurement testMeasurement2 = new Measurement();
        testMeasurement2.setSource(testManagedObject2);
        testMeasurement2.setTime(new Date());
        testMeasurement2.setType("mytype");
        measurementApi.createMeasurement(testMeasurement2);

        MeasurementCollection measurements = measurementApi.getMeasurements(5);
        Measurement[] ms = measurements.getMeasurements();
        Assert.assertTrue(ms.length > 0);
        boolean allMeasurementsFromSource = true;
        for (Measurement m : ms) {
            ExtensibleObject source = (ExtensibleObject) m.get("source");
            if (!source.get("id").equals(testManagedObject.getId())) {
                allMeasurementsFromSource = false;
            }
        }
        Assert.assertFalse(allMeasurementsFromSource);

        measurements = measurementApi.getMeasurements(Filter.build().bySource(testManagedObject.getId()), 5);
        ms = measurements.getMeasurements();
        Assert.assertTrue(ms.length > 0);
        for (Measurement m : ms) {
            ExtensibleObject source = (ExtensibleObject) m.get("source");
            Assert.assertEquals(source.get("id"), testManagedObject.getId());
        }

        // cleanup
        measurementApi.deleteMeasurements(Filter.build().bySource(testManagedObject2.getId()));
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject2);
    }

    @Test
    public void testMultipleMeasurementsByType() {
        Measurement testMeasurement = new Measurement();
        testMeasurement.setSource(testManagedObject);
        testMeasurement.setTime(new Date());
        testMeasurement.setType("mysuperspecialtype");
        measurementApi.createMeasurement(testMeasurement);

        Measurement testMeasurement2 = new Measurement();
        testMeasurement2.setSource(testManagedObject);
        testMeasurement2.setTime(new Date());
        testMeasurement2.setType("mytype");
        measurementApi.createMeasurement(testMeasurement2);

        MeasurementCollection measurements = measurementApi.getMeasurements(5);
        Measurement[] ms = measurements.getMeasurements();
        Assert.assertTrue(ms.length > 0);
        boolean allMeasurementsFromSameType = true;
        for (Measurement m : ms) {
            if (!m.getType().equals(testManagedObject.getType())) {
                allMeasurementsFromSameType = false;
            }
        }
        Assert.assertFalse(allMeasurementsFromSameType);

        measurements = measurementApi.getMeasurements(Filter.build().byType(testMeasurement.getType()), 5);
        ms = measurements.getMeasurements();
        Assert.assertTrue(ms.length > 0);
        for (Measurement m : ms) {
            Assert.assertEquals(m.getType(), testMeasurement.getType());
        }
    }

    @Test
    public void testMultipleMeasurementsByDate() {
        Measurement testMeasurement = new Measurement();
        testMeasurement.setSource(testManagedObject);
        testMeasurement.setTime(new Date(new Date().getTime() - (1000 * 60)));
        testMeasurement.setType("mysuperspecialtype");
        measurementApi.createMeasurement(testMeasurement);

        Date yesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
        MeasurementCollection measurements = measurementApi.getMeasurements(Filter.build().byDate(yesterday, new Date()), 5);


        Measurement[] ms = measurements.getMeasurements();
        Assert.assertTrue(ms.length > 0);

        Date beforeYesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24) - 10);

        measurements = measurementApi.getMeasurements(Filter.build().byDate(beforeYesterday, yesterday), 5);
        ms = measurements.getMeasurements();
        Assert.assertEquals(ms.length, 0);
    }


    @Test
    public void testMultipleMeasurementsByDateAndBySource() {
        Measurement testMeasurement = new Measurement();
        testMeasurement.setSource(testManagedObject);
        testMeasurement.setTime(new Date(new Date().getTime() - (1000 * 60)));
        testMeasurement.setType("mysuperspecialtype");
        measurementApi.createMeasurement(testMeasurement);

        Date yesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24));
        MeasurementCollection measurements = measurementApi.getMeasurements(
                Filter.build()
                        .byDate(yesterday, new Date())
                        .bySource(testManagedObject.getId()), 5);


        Measurement[] ms = measurements.getMeasurements();
        Assert.assertEquals(ms.length, 1);

        Date beforeYesterday = new Date(new Date().getTime() - (1000 * 60 * 60 * 24) - 10);

        measurements = measurementApi.getMeasurements(
                Filter.build()
                        .byDate(beforeYesterday, yesterday)
                        .bySource(testManagedObject.getId()), 5);
        ms = measurements.getMeasurements();
        Assert.assertEquals(ms.length, 0);
    }


    @Test
    public void testMultipleMeasurementsByTypeAndBySource() {
        SampleTemperatureSensor sts = new SampleTemperatureSensor(100);
        Measurement testMeasurement = new Measurement();
        testMeasurement.setSource(testManagedObject);
        testMeasurement.setTime(new Date(new Date().getTime() - (1000 * 60)));
        testMeasurement.setType("mysuperspecialtype");
        testMeasurement.set(sts);
        measurementApi.createMeasurement(testMeasurement);

        MeasurementCollection measurements = measurementApi.getMeasurements(
                Filter.build()
                        .byType("mysuperspecialtype")
                        .bySource(testManagedObject.getId()), 5);


        Measurement[] ms = measurements.getMeasurements();
        Assert.assertEquals(ms.length, 1);


        measurements = measurementApi.getMeasurements(
                Filter.build()
                        .byType("NOT_USED")
                        .bySource(testManagedObject.getId()), 5);
        ms = measurements.getMeasurements();
        Assert.assertEquals(ms.length, 0);
    }


    @Test
    public void testMultipleMeasurementsByFragmentTypeAndBySource() {
        SampleTemperatureSensor sts = new SampleTemperatureSensor(100);
        Measurement testMeasurement = new Measurement();
        testMeasurement.setSource(testManagedObject);
        testMeasurement.setTime(new Date(new Date().getTime() - (1000 * 60)));
        testMeasurement.setType("mysuperspecialtype");
        testMeasurement.set(sts);
        measurementApi.createMeasurement(testMeasurement);

        MeasurementCollection measurements = measurementApi.getMeasurements(
                Filter.build()
                        .byFragmentType("com_telekom_m2m_cot_restsdk_util_SampleTemperatureSensor")
                        .bySource(testManagedObject.getId()), 5);


        Measurement[] ms = measurements.getMeasurements();
        Assert.assertEquals(ms.length, 1);


        measurements = measurementApi.getMeasurements(
                Filter.build()
                        .byFragmentType("com_telekom_m2m_cot_restsdk_util_SampleTemperatureSensor_not")
                        .bySource(testManagedObject.getId()), 5);
        ms = measurements.getMeasurements();
        Assert.assertEquals(ms.length, 0);
    }
}
