package com.telekom.m2m.cot.restsdk.audit;

import com.google.gson.JsonObject;
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
 * Created by Andreas Dyck on 27.07.17.
 */
public class AuditRecordCollectionIT {

    private final CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

    private AuditApi auditApi;

    private ManagedObject testManagedObject;

    // given
    final String type1 = "com_telekom_audit_TestType1" + System.currentTimeMillis();
    final String user1 = "integration-tester1-" + System.currentTimeMillis();
    final String application1 = "test-app1-" + System.currentTimeMillis();

    final String type2 = "com_telekom_audit_TestType2" + System.currentTimeMillis();
    final String user2 = "integration-tester2-" + System.currentTimeMillis();
    final String application2 = "test-app2-" + System.currentTimeMillis();

    @BeforeClass
    public void setUp() {
        auditApi = cotPlat.getAuditApi();

        testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name");
        String text = "new audit record was created";
        Date timeOfAuditRecording = new Date();
        timeOfAuditRecording.setTime(System.currentTimeMillis() - 1000*60*60*24*2);
        String activity = "Create Audit Record";
        String severity = AuditRecord.SEVERITY_INFORMATION;

        // auditRecord1:user1:type1:application1
        AuditRecord auditRecord1 = new AuditRecord();
        auditRecord1.setType(type1);
        auditRecord1.setUser(user1);
        auditRecord1.setApplication(application1);
        auditRecord1.setText(text);
        auditRecord1.setTime(timeOfAuditRecording);
        auditRecord1.setSource(testManagedObject);
        auditRecord1.setActivity(activity);
        auditRecord1.setSeverity(severity);

        auditApi.createAuditRecord(auditRecord1);

        // auditRecord2:user1:type1:application2
        AuditRecord auditRecord2 = new AuditRecord();
        auditRecord2.setType(type1);
        auditRecord2.setUser(user1);
        auditRecord2.setApplication(application2);
        auditRecord2.setText(text);
        auditRecord2.setTime(timeOfAuditRecording);
        auditRecord2.setSource(testManagedObject);
        auditRecord2.setActivity(activity);
        auditRecord2.setSeverity(severity);

        auditApi.createAuditRecord(auditRecord2);

        // auditRecord3:user1:type2:application1
        AuditRecord auditRecord3 = new AuditRecord();
        auditRecord3.setType(type2);
        auditRecord3.setUser(user1);
        auditRecord3.setApplication(application1);
        auditRecord3.setText(text);
        auditRecord3.setTime(timeOfAuditRecording);
        auditRecord3.setSource(testManagedObject);
        auditRecord3.setActivity(activity);
        auditRecord3.setSeverity(severity);

        auditApi.createAuditRecord(auditRecord3);

        // auditRecord4:user2:type1:application1
        AuditRecord auditRecord4 = new AuditRecord();
        auditRecord4.setType(type1);
        auditRecord4.setUser(user2);
        auditRecord4.setApplication(application1);
        auditRecord4.setText(text);
        auditRecord4.setTime(timeOfAuditRecording);
        auditRecord4.setSource(testManagedObject);
        auditRecord4.setActivity(activity);
        auditRecord4.setSeverity(severity);

        auditApi.createAuditRecord(auditRecord4);
    }

    @AfterClass
    public void tearDown() {
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
    }

    @Test
    public void testAuditRecordCollection() throws Exception {
        // given: four created audit records in setUp()-method

        // when
        final AuditRecordCollection auditRecordCollection = auditApi.getAuditRecordCollection();

        // then
        Assert.assertNotNull(auditRecordCollection);

        final AuditRecord[] auditRecords = auditRecordCollection.getAuditRecords();

        Assert.assertTrue(auditRecords.length >= 4);
        Assert.assertEquals(auditRecords.length, auditRecordCollection.getJsonArray().size());

        final AuditRecord retrievedAuditRecord = auditRecords[0];
        final JsonObject jsonObject = auditRecordCollection.getJsonArray().get(0).getAsJsonObject();

        Assert.assertTrue(retrievedAuditRecord.getId() != null);
        Assert.assertFalse(retrievedAuditRecord.getId().isEmpty());
        Assert.assertTrue(retrievedAuditRecord.getId().equals(jsonObject.get("id").getAsString()));

        Assert.assertTrue(retrievedAuditRecord.getCreationTime() != null);
        Assert.assertTrue(retrievedAuditRecord.getCreationTime().compareTo(new Date()) < 0);

        Assert.assertTrue(retrievedAuditRecord.getType() != null);
        Assert.assertFalse(retrievedAuditRecord.getType().isEmpty());
        Assert.assertTrue(retrievedAuditRecord.getType().equals(jsonObject.get("type").getAsString()));
    }

    @Test
    public void testAuditRecordCollectionWithUserAndTypeAndApplicationFilter() throws Exception {
        // given: four created audit records in setUp()-method
        final Filter.FilterBuilder filterBuilder = Filter.build().byUser(user1).byType(type1).byApplication(application1);

        // when
        final AuditRecordCollection auditRecordCollection = auditApi.getAuditRecordCollection(filterBuilder);

        // then
        Assert.assertNotNull(auditRecordCollection);

        final AuditRecord[] auditRecords = auditRecordCollection.getAuditRecords();

        Assert.assertEquals(auditRecords.length, 1);
        Assert.assertEquals(auditRecords.length, auditRecordCollection.getJsonArray().size());

        final AuditRecord retrievedAuditRecord = auditRecords[0];

        Assert.assertEquals(retrievedAuditRecord.getUser(), user1);
        Assert.assertEquals(retrievedAuditRecord.getType(), type1);
        Assert.assertEquals(retrievedAuditRecord.getApplication(), application1);
    }

    @Test
    public void testAuditRecordCollectionWithUserFilter() throws Exception {
        // given: four created audit records in setUp()-method
        final Filter.FilterBuilder filterBuilder = Filter.build().byUser(user1);

        // when
        final AuditRecordCollection auditRecordCollection = auditApi.getAuditRecordCollection(filterBuilder);

        // then
        Assert.assertNotNull(auditRecordCollection);

        final AuditRecord[] auditRecords = auditRecordCollection.getAuditRecords();

        checkAssertions(auditRecords, 3, user1, null, null);
    }

    @Test
    public void testAuditRecordCollectionWithTypeFilter() throws Exception {
        // given: four created audit records in setUp()-method
        final Filter.FilterBuilder filterBuilder = Filter.build().byType(type1);

        // when
        final AuditRecordCollection auditRecordCollection = auditApi.getAuditRecordCollection(filterBuilder);

        // then
        Assert.assertNotNull(auditRecordCollection);

        final AuditRecord[] auditRecords = auditRecordCollection.getAuditRecords();

        checkAssertions(auditRecords, 3, null, type1, null);
    }

    @Test
    public void testAuditRecordCollectionWithApplicationFilter() throws Exception {
        // given: four created audit records in setUp()-method
        final Filter.FilterBuilder filterBuilder = Filter.build().byApplication(application1);

        // when
        final AuditRecordCollection auditRecordCollection = auditApi.getAuditRecordCollection(filterBuilder);

        // then
        Assert.assertNotNull(auditRecordCollection);

        final AuditRecord[] auditRecords = auditRecordCollection.getAuditRecords();

        checkAssertions(auditRecords, 3, null, null, application1);
    }

    @Test
    public void testAuditRecordCollectionWithUserAndTypeFilter() throws Exception {
        // given: four created audit records in setUp()-method
        final Filter.FilterBuilder filterBuilder = Filter.build().byUser(user1).byType(type1);

        // when
        final AuditRecordCollection auditRecordCollection = auditApi.getAuditRecordCollection(filterBuilder);

        // then
        Assert.assertNotNull(auditRecordCollection);

        final AuditRecord[] auditRecords = auditRecordCollection.getAuditRecords();

        checkAssertions(auditRecords, 2, user1, type1, null);
    }

    @Test
    public void testAuditRecordCollectionWithUserAndApplicationFilter() throws Exception {
        // given: four created audit records in setUp()-method
        final Filter.FilterBuilder filterBuilder = Filter.build().byUser(user1).byApplication(application1);

        // when
        final AuditRecordCollection auditRecordCollection = auditApi.getAuditRecordCollection(filterBuilder);

        // then
        Assert.assertNotNull(auditRecordCollection);

        final AuditRecord[] auditRecords = auditRecordCollection.getAuditRecords();

        checkAssertions(auditRecords, 2, user1, null, application1);
    }

    @Test
    public void testAuditRecordCollectionWithTypeAndApplicationFilter() throws Exception {
        // given: four created audit records in setUp()-method
        final Filter.FilterBuilder filterBuilder = Filter.build().byType(type1).byApplication(application1);

        // when
        final AuditRecordCollection auditRecordCollection = auditApi.getAuditRecordCollection(filterBuilder);

        // then
        Assert.assertNotNull(auditRecordCollection);

        final AuditRecord[] auditRecords = auditRecordCollection.getAuditRecords();

        checkAssertions(auditRecords, 2, null, type1, application1);
    }


    private void checkAssertions(AuditRecord[] auditRecords,
                                 int expectedNumOfEntries,
                                 String expectedUser,
                                 String expectedType,
                                 String expectedApplication) {
        Assert.assertEquals(auditRecords.length, expectedNumOfEntries);

        for (AuditRecord auditRecord : auditRecords) {
            if(expectedUser != null) {
                Assert.assertEquals(auditRecord.getUser(), expectedUser);
            }
            if (expectedType != null) {
                Assert.assertEquals(auditRecord.getType(), expectedType);
            }
            if(expectedApplication != null) {
                Assert.assertEquals(auditRecord.getApplication(), expectedApplication);
            }
        }
    }
}
