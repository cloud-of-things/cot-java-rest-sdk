package com.telekom.m2m.cot.restsdk.audit;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by Andreas Dyck on 24.07.17.
 */
public class AuditApiIT {

    private final CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
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
    public void testAuditApi() {
        // given
        final String text = "new audit record created";
        final String type = "com_telekom_audit_TestType_" + System.currentTimeMillis();
        final Date timeOfAuditRecording = new Date();
        // reduce time by 10 second to get a difference between "time" set by client and "creationTime" set by platform
        // because sometimes the system time of the client is not equal to the system time of the platform
        timeOfAuditRecording.setTime(timeOfAuditRecording.getTime()-10000);
        final String user = "integration-tester";
        final String application = this.getClass().getSimpleName();
        final String activity = "Create Audit Record";

        final AuditRecord auditRecord = new AuditRecord();
        auditRecord.setText(text);
        auditRecord.setType(type);
        auditRecord.setTime(timeOfAuditRecording);
        auditRecord.setSource(testManagedObject);
        auditRecord.setUser(user);
        auditRecord.setApplication(application);
        auditRecord.setActivity(activity);

        final AuditApi auditApi = cotPlat.getAuditApi();

        // when
        final AuditRecord createdAuditRecord = auditApi.createAuditRecord(auditRecord);

        // then
        Assert.assertNotNull(auditRecord.getId(), "Should now have an Id!");
        Assert.assertEquals(auditRecord.getId(), createdAuditRecord.getId());

        // when
        final AuditRecord retrievedAuditRecord = auditApi.getAuditRecord(createdAuditRecord.getId());

        // then
        Assert.assertEquals(retrievedAuditRecord.getId(), createdAuditRecord.getId());
        Assert.assertEquals(retrievedAuditRecord.getText(), text);
        Assert.assertEquals(retrievedAuditRecord.getType(), type);
        Assert.assertEquals(retrievedAuditRecord.getTime().compareTo(timeOfAuditRecording), 0);
        Assert.assertEquals(retrievedAuditRecord.getSource().getId(), testManagedObject.getId());
        Assert.assertEquals(retrievedAuditRecord.getUser(), user);
        Assert.assertEquals(retrievedAuditRecord.getApplication(), application);
        Assert.assertEquals(retrievedAuditRecord.getActivity(), activity);
        Assert.assertNotNull(retrievedAuditRecord.getCreationTime());

        Assert.assertTrue(
                retrievedAuditRecord.getCreationTime().after(timeOfAuditRecording),
                String.format(
                        "retrievedAuditRecord.getCreationTime(): %s, timeOfAuditRecorded: %s",
                        retrievedAuditRecord.getCreationTime(),
                        timeOfAuditRecording
                )
        );

        // when
        AuditRecordCollection auditRecordCollection = auditApi.getAuditRecordCollection();

        // then
        Assert.assertNotNull(auditRecordCollection);
        Assert.assertNotNull(auditRecordCollection.getAuditRecords(), "auditRecordCollection should contain some audit records");
        Assert.assertTrue(auditRecordCollection.getAuditRecords().length > 0, "auditRecordCollection should contain some audit records");

        // when
        final Filter.FilterBuilder filterBuilder = Filter.build().byType(type);
        auditRecordCollection = auditApi.getAuditRecordCollection(filterBuilder);

        // then
        Assert.assertNotNull(auditRecordCollection);
        Assert.assertNotNull(auditRecordCollection.getAuditRecords());
        Assert.assertEquals(auditRecordCollection.getAuditRecords().length, 1, "auditRecordCollection should contain exact one audit record");

        // when
        auditApi.deleteAuditRecords(filterBuilder);
        auditRecordCollection = auditApi.getAuditRecordCollection(filterBuilder);

        // then
        Assert.assertNotNull(auditRecordCollection);
        Assert.assertNotNull(auditRecordCollection.getAuditRecords());
        Assert.assertEquals(auditRecordCollection.getAuditRecords().length, 0, "auditRecordCollection filtered by type may not contain audit records anymore");
    }


}
