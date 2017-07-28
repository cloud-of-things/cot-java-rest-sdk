package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.audit.AuditApi;
import com.telekom.m2m.cot.restsdk.audit.AuditRecord;
import com.telekom.m2m.cot.restsdk.audit.AuditRecordCollection;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by Andreas Dyck on 28.07.17.
 */
public class JsonArrayPaginationIT {

    private final CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
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
    public void testMultipleAuditRecords() {
        // given
        final String type = "com_telekom_audit_TestType";

        final AuditRecord testAuditRecord = new AuditRecord();
        testAuditRecord.setType(type);
        testAuditRecord.setTime(new Date());
        testAuditRecord.setText("new audit test record created");
        testAuditRecord.setActivity("Create Test Audit Record");
        testAuditRecord.setSource(testManagedObject);

        final AuditApi auditApi = cotPlat.getAuditApi();
        auditApi.createAuditRecord(testAuditRecord);

        // when
        final AuditRecordCollection auditRecordCollection = auditApi.getAuditRecords();

        final AuditRecord[] auditRecords = auditRecordCollection.getAuditRecords();

        // then
        Assert.assertTrue(auditRecords.length > 0);

        AuditRecord retrievedAuditRecord = auditRecords[0];

        Assert.assertTrue(retrievedAuditRecord.getId() != null);
        Assert.assertTrue(retrievedAuditRecord.getId().length() > 0);

        Assert.assertTrue(retrievedAuditRecord.getTime() != null);
        Assert.assertTrue(retrievedAuditRecord.getTime().compareTo(new Date()) < 0);

        Assert.assertTrue(retrievedAuditRecord.getType() != null);
        Assert.assertTrue(retrievedAuditRecord.getType().length() > 0);
    }

    @Test
    public void testMultipleAuditRecordsWithPaging() {
        // given
        final AuditApi auditApi = cotPlat.getAuditApi();

        for (int i = 0; i < 3; i++) {
            final AuditRecord testAuditRecord = new AuditRecord();
            testAuditRecord.setType("mytype-" + i);
            testAuditRecord.setTime(new Date(new Date().getTime() - (i * 5000)));
            testAuditRecord.setSource(testManagedObject);
            testAuditRecord.setText("new audit test record created");
            testAuditRecord.setActivity("Create Test Audit Record");

            auditApi.createAuditRecord(testAuditRecord);
        }

        // when you retrieve the created audit records filtered by id of the testManagedObject
        final AuditRecordCollection auditRecordCollection = auditApi.getAuditRecords(Filter.build().bySource(testManagedObject.getId()));
        auditRecordCollection.setPageSize(2);

        AuditRecord[] auditRecords = auditRecordCollection.getAuditRecords();

        // then
        Assert.assertEquals(auditRecords.length, 2);
        Assert.assertTrue(auditRecordCollection.hasNext());
        Assert.assertFalse(auditRecordCollection.hasPrevious());

        // when
        auditRecordCollection.next();
        auditRecords = auditRecordCollection.getAuditRecords();

        // then
        Assert.assertEquals(auditRecords.length, 1);

        Assert.assertFalse(auditRecordCollection.hasNext());
        Assert.assertTrue(auditRecordCollection.hasPrevious());

        // when
        auditRecordCollection.previous();
        auditRecords = auditRecordCollection.getAuditRecords();

        // then
        Assert.assertEquals(auditRecords.length, 2);

        Assert.assertTrue(auditRecordCollection.hasNext());
        Assert.assertFalse(auditRecordCollection.hasPrevious());

        // when
        auditRecordCollection.setPageSize(5);
        auditRecords = auditRecordCollection.getAuditRecords();

        // then
        Assert.assertEquals(auditRecords.length, 3);
        Assert.assertFalse(auditRecordCollection.hasNext());
        Assert.assertFalse(auditRecordCollection.hasPrevious());
    }

    @Test
    public void testMultipleAuditRecordsBySource() {
        // given
        final AuditApi auditApi = cotPlat.getAuditApi();

        final AuditRecord testAuditRecord = new AuditRecord();
        testAuditRecord.setSource(testManagedObject);
        testAuditRecord.setTime(new Date());
        testAuditRecord.setType("mytype");
        testAuditRecord.setText("new audit test record created");
        testAuditRecord.setActivity("Create Test Audit Record");

        auditApi.createAuditRecord(testAuditRecord);

        // when you retrieve the first page of the audit records
        AuditRecordCollection auditRecordCollection = auditApi.getAuditRecords();
        AuditRecord[] auditRecords = auditRecordCollection.getAuditRecords();

        // then there should be at least one audit record with a source.id which does not match to the source.id of the just created testAuditRecord
        Assert.assertTrue(auditRecords.length > 0);
        boolean allAuditRecordsFromSource = true;
        for (AuditRecord auditRecord : auditRecords) {
            final JsonObject source = (JsonObject) auditRecord.get("source");
            if (!source.get("id").getAsString().equals(testManagedObject.getId())) {
                allAuditRecordsFromSource = false;
            }
        }
        Assert.assertFalse(allAuditRecordsFromSource);

        // when you search by id of the testManagedObject for created audit record
        auditRecordCollection = auditApi.getAuditRecords(Filter.build().bySource(testManagedObject.getId()));
        auditRecords = auditRecordCollection.getAuditRecords();

        // then there should be delivered only audit records with the requested source.id
        allAuditRecordsFromSource = true;
        Assert.assertTrue(auditRecords.length > 0);
        for (AuditRecord auditRecord : auditRecords) {
            final JsonObject source = (JsonObject) auditRecord.get("source");
            if (!source.get("id").getAsString().equals(testManagedObject.getId())) {
                allAuditRecordsFromSource = false;
            }
        }
        Assert.assertTrue(allAuditRecordsFromSource);
    }

    @Test
    public void testMultipleAuditRecordsByType() {
        //given
        final AuditApi auditApi = cotPlat.getAuditApi();

        AuditRecord testAuditRecord = new AuditRecord();
        testAuditRecord.setSource(testManagedObject);
        testAuditRecord.setTime(new Date());
        testAuditRecord.setType("mysuperspecialtype" + System.currentTimeMillis());
        testAuditRecord.setText("new audit test record created");
        testAuditRecord.setActivity("Create Test Audit Record");

        auditApi.createAuditRecord(testAuditRecord);

        // when you retrieve the first page of the audit records
        AuditRecordCollection auditRecordCollection = auditApi.getAuditRecords();
        AuditRecord[] auditRecords = auditRecordCollection.getAuditRecords();

        // then there should be at least one audit record with a type which does not match to the type of the just created testAuditRecord
        Assert.assertTrue(auditRecords.length > 0);
        boolean allAuditRecordsFromSameType = true;
        for (AuditRecord auditRecord : auditRecords) {
            if (!auditRecord.getType().equals(testManagedObject.getType())) {
                allAuditRecordsFromSameType = false;
            }
        }
        Assert.assertFalse(allAuditRecordsFromSameType);

        // when you search by type of the testManagedObject for created audit record
        auditRecordCollection = auditApi.getAuditRecords(Filter.build().byType(testAuditRecord.getType()));
        auditRecords = auditRecordCollection.getAuditRecords();

        // then there should be delivered only audit records with the requested type
        allAuditRecordsFromSameType = true;
        Assert.assertTrue(auditRecords.length > 0);
        for (AuditRecord auditRecord : auditRecords) {
            if (!auditRecord.getType().equals(testAuditRecord.getType())) {
                allAuditRecordsFromSameType = false;
            }
        }
        Assert.assertTrue(allAuditRecordsFromSameType);
    }

    @Test
    public void testMultipleAuditRecordsByDate() {
        // given
        final AuditApi auditApi = cotPlat.getAuditApi();

        AuditRecord testAuditRecord = new AuditRecord();
        testAuditRecord.setSource(testManagedObject);
        testAuditRecord.setTime(new Date(new Date().getTime() - (1000 * 60)));
        testAuditRecord.setType("mysuperspecialtype");
        testAuditRecord.setText("new audit test record created");
        testAuditRecord.setActivity("Create Test Audit Record");

        auditApi.createAuditRecord(testAuditRecord);

        // when you are searching for audit records created in the last five minutes
        Date sinceAboutFiveMinutes = new Date(new Date().getTime() - (1000 * 60 * 5));
        AuditRecordCollection auditRecordCollection = auditApi.getAuditRecords(Filter.build().byDate(sinceAboutFiveMinutes, new Date()));
        AuditRecord[] auditRecords = auditRecordCollection.getAuditRecords();

        // then there should be found at least one record
        Assert.assertTrue(auditRecords.length > 0);

        // when you are searching for audit records in the future
        Date inOneMinute = new Date(new Date().getTime() + (1000 * 60 * 1));
        Date inTwoMinutes = new Date(new Date().getTime() + (1000 * 60 * 2));
        auditRecordCollection = auditApi.getAuditRecords(Filter.build().byDate(inOneMinute, inTwoMinutes));
        auditRecords = auditRecordCollection.getAuditRecords();

        // then there should be found no records
        Assert.assertEquals(auditRecords.length, 0);
    }

    @Test
    public void testMultipleAuditRecordsByDateAndBySource() {
        // given
        final AuditApi auditApi = cotPlat.getAuditApi();

        final AuditRecord testAuditRecord = new AuditRecord();
        testAuditRecord.setSource(testManagedObject);
        testAuditRecord.setTime(new Date(new Date().getTime() - (1000 * 60)));
        testAuditRecord.setType("mysuperspecialtype");
        testAuditRecord.setText("new audit test record created");
        testAuditRecord.setActivity("Create Test Audit Record");

        auditApi.createAuditRecord(testAuditRecord);

        final Date sinceAboutFiveMinutes = new Date(new Date().getTime() - (1000 * 60 * 5));

        // when you are searching for records with a specific source.id created in last five minutes
        AuditRecordCollection auditRecordCollection = auditApi.getAuditRecords(
                Filter.build()
                        .byDate(sinceAboutFiveMinutes, new Date())
                        .bySource(testManagedObject.getId()));

        AuditRecord[] auditRecords = auditRecordCollection.getAuditRecords();

        // then there can only be found one record
        Assert.assertEquals(auditRecords.length, 1);

        // when you are searching for records with a specific source.id created in last ten to five minutes
        final Date sinceAboutTenMinutes = new Date(new Date().getTime() - (1000 * 60 * 10));

        auditRecordCollection = auditApi.getAuditRecords(
                Filter.build()
                        .byDate(sinceAboutTenMinutes, sinceAboutFiveMinutes)
                        .bySource(testManagedObject.getId()));
        auditRecords = auditRecordCollection.getAuditRecords();

        // then there should be found no records
        Assert.assertEquals(auditRecords.length, 0);
    }

    @Test
    public void testMultipleAuditRecordsByTypeAndBySource() {
        // given
        final AuditApi auditApi = cotPlat.getAuditApi();

        AuditRecord testAuditRecord = new AuditRecord();

        SampleTemperatureSensor sts = new SampleTemperatureSensor();
        sts.setTemperature(100);
        testAuditRecord.set(sts);

        testAuditRecord.setSource(testManagedObject);
        testAuditRecord.setTime(new Date(new Date().getTime() - (1000 * 60)));
        testAuditRecord.setType("mysuperspecialtype" + System.currentTimeMillis());
        testAuditRecord.setText("new audit test record created");
        testAuditRecord.setActivity("Create Test Audit Record");

        auditApi.createAuditRecord(testAuditRecord);

        // when you are searching for records with a specific source.id and type
        AuditRecordCollection auditRecordCollection = auditApi.getAuditRecords(
                Filter.build()
                        .byType(testAuditRecord.getType())
                        .bySource(testManagedObject.getId()));

        AuditRecord[] auditRecords = auditRecordCollection.getAuditRecords();

        // then there can only be found one record
        Assert.assertEquals(auditRecords.length, 1);

        // when you are searching for records with a type which does not exist
        auditRecordCollection = auditApi.getAuditRecords(
                Filter.build()
                        .byType("NOT_USED" + System.currentTimeMillis())
                        .bySource(testManagedObject.getId()));
        auditRecords = auditRecordCollection.getAuditRecords();

        // then there should be found no records
        Assert.assertEquals(auditRecords.length, 0);
    }
}
