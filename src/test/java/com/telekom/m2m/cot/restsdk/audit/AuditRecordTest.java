package com.telekom.m2m.cot.restsdk.audit;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by Andreas Dyck on 24.07.17.
 */
public class AuditRecordTest {

    private final String auditRecordId = "234";
    private final String text = "new audit record created";
    private final String type = "com_telekom_audit_TestType";
    private final Date timeOfAuditRecording = new Date();
    private final String user = "integration-tester";
    private final String application = this.getClass().getSimpleName();
    private final String activity = "Create Audit Record";
    private final String severity = AuditRecord.SEVERITY_MINOR;
    private final String managedObjectId = "123";
    private final String managedObjectName = "device123";

    @Test
    public void testAuditRecordSetterAndGetter() {
        // when
        AuditRecord testAuditRecord = createTestAuditRecord();

        // then
        checkAssertions(testAuditRecord);
    }

    @Test
    public void testAuditRecordWrapper() {
        // given
        AuditRecord testAuditRecord = createTestAuditRecord();

        // when
        AuditRecord wrappedAuditRecord = new AuditRecord(testAuditRecord);

        // then
        Assert.assertNotNull(wrappedAuditRecord, "Wrapping into the AuditRecord failed!");
        checkAssertions(wrappedAuditRecord);
    }

    @Test
    public void testAuditRecordSerialization() {
        // given
        AuditRecord testAuditRecord = createTestAuditRecord();

        // when
        Gson gson = GsonUtils.createGson();
        String json = gson.toJson(testAuditRecord);

        // then
        Assert.assertNotNull(json, "Serialization of the AuditRecord failed!");
        Assert.assertTrue(json.contains(auditRecordId));
        Assert.assertTrue(json.contains(text));
        Assert.assertTrue(json.contains(type));
        Assert.assertTrue(json.contains(user));
        Assert.assertTrue(json.contains(application));
        Assert.assertTrue(json.contains(activity));
        Assert.assertTrue(json.contains(severity));
        Assert.assertTrue(json.contains(managedObjectId));
    }

    @Test
    public void testAuditRecordDeserialization() {
        // given
        AuditRecord testAuditRecord = createTestAuditRecord();
        Gson gson = GsonUtils.createGson();
        String json = gson.toJson(testAuditRecord);

        // when
        ExtensibleObject deserializedAuditRecord = gson.fromJson(json, ExtensibleObject.class);

        // then
        Assert.assertNotNull(deserializedAuditRecord, "Deserialization of the AuditRecord failed!");
        AuditRecord auditRecord = new AuditRecord(deserializedAuditRecord);
        checkAssertions(auditRecord);
    }

    private AuditRecord createTestAuditRecord() {
        ManagedObject managedObject = new ManagedObject();
        managedObject.setId(managedObjectId);
        managedObject.setName(managedObjectName);

        AuditRecord auditRecord = new AuditRecord();
        auditRecord.setId(auditRecordId);
        auditRecord.setText(text);
        auditRecord.setType(type);
        auditRecord.setTime(timeOfAuditRecording);
        auditRecord.setSource(managedObject);
        auditRecord.setUser(user);
        auditRecord.setApplication(application);
        auditRecord.setActivity(activity);
        auditRecord.setSeverity(severity);

        return auditRecord;
    }

    private void checkAssertions(AuditRecord testAuditRecord) {
        Assert.assertEquals(testAuditRecord.getId(), auditRecordId);
        Assert.assertEquals(testAuditRecord.getText(), text);
        Assert.assertEquals(testAuditRecord.getType(), type);
        Assert.assertEquals(testAuditRecord.getTime().compareTo(timeOfAuditRecording), 0);
        Assert.assertEquals(testAuditRecord.getSource().getId(), managedObjectId);
        Assert.assertEquals(testAuditRecord.getUser(), user);
        Assert.assertEquals(testAuditRecord.getApplication(), application);
        Assert.assertEquals(testAuditRecord.getActivity(), activity);
        Assert.assertEquals(testAuditRecord.getSeverity(), severity);
        Assert.assertNull(testAuditRecord.getCreationTime());
    }
}
