package com.telekom.m2m.cot.restsdk.audit;

import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by Andreas Dyck on 27.07.17.
 */
public class AuditRecordCollectionIT {

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
    public void testAuditRecordCollection() throws Exception {
        // given at least one created audit record
        final String text = "new audit record created";
        final String type = "com_telekom_audit_TestType";
        final Date timeOfAuditRecording = new Date();
        final String activity = "Create Audit Record";

        final AuditRecord auditRecord = new AuditRecord();
        auditRecord.setText(text);
        auditRecord.setType(type);
        auditRecord.setTime(timeOfAuditRecording);
        auditRecord.setSource(testManagedObject);
        auditRecord.setActivity(activity);

        final AuditApi auditApi = cotPlat.getAuditApi();

        auditApi.createAuditRecord(auditRecord);

        // when
        final AuditRecordCollection auditRecordCollection = auditApi.getAuditRecords();

        // then
        Assert.assertNotNull(auditRecordCollection);

        final AuditRecord[] auditRecords = auditRecordCollection.getAuditRecords();

        Assert.assertTrue(auditRecords.length > 0);
        Assert.assertEquals(auditRecords.length, auditRecordCollection.getJsonArray().size());

        final AuditRecord retrievedAuditRecord = auditRecords[0];
        final JsonObject jsonObject = auditRecordCollection.getJsonArray().get(0).getAsJsonObject();

        Assert.assertTrue(retrievedAuditRecord.getId() != null);
        Assert.assertFalse(retrievedAuditRecord.getId().isEmpty());
        Assert.assertTrue(retrievedAuditRecord.getId().equals(jsonObject.get("id").getAsString()));

        Assert.assertTrue(retrievedAuditRecord.getTime() != null);
        Assert.assertTrue(retrievedAuditRecord.getTime().compareTo(new Date()) < 0);

        Assert.assertTrue(retrievedAuditRecord.getType() != null);
        Assert.assertFalse(retrievedAuditRecord.getType().isEmpty());
        Assert.assertTrue(retrievedAuditRecord.getType().equals(jsonObject.get("type").getAsString()));
    }

    @Test
    public void testAuditRecordCollectionWithFilter() throws Exception {
        // given
        final String text = "new audit record created";
        final String type = "com_telekom_audit_TestType";
        final Date timeOfAuditRecording = new Date();
        final String activity = "Create Audit Record";

        final AuditRecord auditRecord = new AuditRecord();
        auditRecord.setText(text);
        auditRecord.setType(type);
        auditRecord.setTime(timeOfAuditRecording);
        auditRecord.setSource(testManagedObject);
        auditRecord.setActivity(activity);
        final Filter.FilterBuilder filterBuilder = Filter.build().bySource(testManagedObject.getId());

        final AuditApi auditApi = cotPlat.getAuditApi();

        auditApi.createAuditRecord(auditRecord);

        // when
        final AuditRecordCollection auditRecordCollection = auditApi.getAuditRecords(filterBuilder);

        // then
        Assert.assertNotNull(auditRecordCollection);

        final AuditRecord[] auditRecords = auditRecordCollection.getAuditRecords();

        Assert.assertEquals(auditRecords.length, 1);
        Assert.assertEquals(auditRecords.length, auditRecordCollection.getJsonArray().size());

        final AuditRecord retrievedAuditRecord = auditRecords[0];
        final JsonObject jsonObject = auditRecordCollection.getJsonArray().get(0).getAsJsonObject();

        Assert.assertTrue(retrievedAuditRecord.getId() != null);
        Assert.assertFalse(retrievedAuditRecord.getId().isEmpty());
        Assert.assertTrue(retrievedAuditRecord.getId().equals(jsonObject.get("id").getAsString()));

        Assert.assertTrue(retrievedAuditRecord.getTime() != null);
        Assert.assertTrue(retrievedAuditRecord.getTime().compareTo(new Date()) < 0);

        Assert.assertTrue(retrievedAuditRecord.getType() != null);
        Assert.assertFalse(retrievedAuditRecord.getType().isEmpty());
        Assert.assertTrue(retrievedAuditRecord.getType().equals(jsonObject.get("type").getAsString()));
    }
}
