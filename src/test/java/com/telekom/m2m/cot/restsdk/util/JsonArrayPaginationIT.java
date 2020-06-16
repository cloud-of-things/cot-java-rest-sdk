package com.telekom.m2m.cot.restsdk.util;

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

    private AuditApi auditApi;

    private ManagedObject testManagedObject;

    // given
    final String user1 = "integration-tester1-" + System.currentTimeMillis();
    final String type1 = "com_telekom_audit_TestType1" + System.currentTimeMillis();
    final String type2 = "com_telekom_audit_TestType2" + System.currentTimeMillis();
    final String type3 = "com_telekom_audit_TestType3" + System.currentTimeMillis();

    @BeforeMethod
    public void setUp() {
        auditApi = cotPlat.getAuditApi();

        testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name");

        String text = "new audit record was created";
        String activity = "Create Audit Record";
        String severity = AuditRecord.SEVERITY_INFORMATION;

        // auditRecord1:user1:type1
        AuditRecord auditRecord1 = new AuditRecord();
        auditRecord1.setUser(user1);
        auditRecord1.setType(type1);
        auditRecord1.setText(text);
        auditRecord1.setTime(new Date());
        auditRecord1.setSource(testManagedObject);
        auditRecord1.setActivity(activity);
        auditRecord1.setSeverity(severity);

        auditApi.createAuditRecord(auditRecord1);

        // auditRecord2:user1:type2
        AuditRecord auditRecord2 = new AuditRecord();
        auditRecord2.setUser(user1);
        auditRecord2.setType(type2);
        auditRecord2.setText(text);
        auditRecord2.setTime(new Date());
        auditRecord2.setSource(testManagedObject);
        auditRecord2.setActivity(activity);
        auditRecord2.setSeverity(severity);

        auditApi.createAuditRecord(auditRecord2);

        // auditRecord3:user1:type3
        AuditRecord auditRecord3 = new AuditRecord();
        auditRecord3.setUser(user1);
        auditRecord3.setType(type3);
        auditRecord3.setText(text);
        auditRecord3.setTime(new Date());
        auditRecord3.setSource(testManagedObject);
        auditRecord3.setActivity(activity);
        auditRecord3.setSeverity(severity);

        auditApi.createAuditRecord(auditRecord3);
    }

    @AfterMethod
    public void tearDown() {
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);

        Filter.FilterBuilder filterBuilder = Filter.build().byUser(user1);
        auditApi.deleteAuditRecords(filterBuilder);
    }

    @Test
    public void testMultipleAuditRecords() {
        // given: three audit recods created in setUp

        // when
        final AuditRecordCollection auditRecordCollection = auditApi.getAuditRecordCollection();

        final AuditRecord[] auditRecords = auditRecordCollection.getAuditRecords();

        // then
        Assert.assertTrue(auditRecords.length >= 3);  // It's >= because we don't know what other data might already be there.

        AuditRecord retrievedAuditRecord = auditRecords[0];

        Assert.assertNotNull(retrievedAuditRecord.getId());
        Assert.assertTrue(retrievedAuditRecord.getId().length() > 0);

        Assert.assertNotNull(retrievedAuditRecord.getTime());
        Assert.assertTrue(retrievedAuditRecord.getTime().compareTo(new Date()) < 0);

        Assert.assertNotNull(retrievedAuditRecord.getType());
        Assert.assertTrue(retrievedAuditRecord.getType().length() > 0);
    }

    @Test
    public void testMultipleAuditRecordsWithPaging() {
        // given: three audit recods created in setUp

        // when you retrieve the created audit records filtered by user1
        final AuditRecordCollection auditRecordCollection = auditApi.getAuditRecordCollection(Filter.build().byUser(user1));
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
    public void testMultipleAuditRecordsByType() {
        // given: three audit recods created in setUp

        // when you retrieve the first page of the audit records
        AuditRecordCollection auditRecordCollection = auditApi.getAuditRecordCollection();
        AuditRecord[] auditRecords = auditRecordCollection.getAuditRecords();

        // then there should be at least one audit record with a type which does not match to the type1
        Assert.assertTrue(auditRecords.length >= 3);
        boolean type1AuditRecords = true;
        for (AuditRecord auditRecord : auditRecords) {
            if (!auditRecord.getType().equals(type1)) {
                type1AuditRecords = false;
            }
        }
        Assert.assertFalse(type1AuditRecords);

        // when you retrieve the created audit records filtered by type1
        auditRecordCollection = auditApi.getAuditRecordCollection(Filter.build().byType(type1));

        auditRecords = auditRecordCollection.getAuditRecords();

        // then there should be delivered only audit records with the requested type
        Assert.assertTrue(auditRecords.length > 0);
        for (AuditRecord auditRecord : auditRecords) {
            Assert.assertEquals(auditRecord.getType(), type1);
        }
    }
}
