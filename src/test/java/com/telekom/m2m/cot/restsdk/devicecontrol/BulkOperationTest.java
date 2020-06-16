package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;
import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.*;

/**
 * Created by Patrick Steinert on 02.01.17.
 */
public class BulkOperationTest {

    private final String bulkOperationId = "bulkOperationId1";
    private final long creationRamp = System.currentTimeMillis();
    private final String failedBulkOperationId = "failedBulkOperationId3";
    private final String groupId = "groupId4";
    private final Operation operation = new Operation();
    private final Progress progress = new Progress();
    private final Date startDate = new Date() ;
    private final String status = "ACTIVE";

    @Test
    public void testBulkOperation() {
        // when
        final BulkOperation bulkOperation = new BulkOperation();

        // then
        assertNotNull(bulkOperation);
        assertNotNull(bulkOperation.getAttributes());
        assertEquals(bulkOperation.getAttributes().size(), 0);
        assertNull(bulkOperation.getId());
    }

    @Test
    public void testBulkOperationSetterAndGetter() {
        // when
        BulkOperation testBulkOperation = createTestBulkOperation();

        // then
        checkAssertions(testBulkOperation);
    }

    @Test
    public void testBulkOperationWrapper() {
        // given
        BulkOperation testBulkOperation = createTestBulkOperation();

        // when
        BulkOperation wrappedBulkOperation = new BulkOperation(testBulkOperation);

        // then
        assertNotNull(wrappedBulkOperation, "Wrapping into the BulkOperation failed!");
        checkAssertions(wrappedBulkOperation);
    }

    @Test
    public void testBulkOperationSerialization() {
        // given
        BulkOperation testBulkOperation = createTestBulkOperation();

        // when
        Gson gson = GsonUtils.createGson();
        String json = gson.toJson(testBulkOperation);

        // then
        assertNotNull(json, "Serialization of the BulkOperation failed!");
        assertTrue(json.contains(bulkOperationId));
        assertTrue(json.contains(groupId));
        assertTrue(json.contains(status));
        assertTrue(json.contains(failedBulkOperationId));
        assertTrue(json.contains(String.valueOf(creationRamp)));
        assertTrue(json.contains("operation"));
        assertTrue(json.contains("progress"));
        assertTrue(json.contains("startDate"));
    }

    @Test
    public void testBulkOperationDeserialization() {
        // given
        BulkOperation testBulkOperation = createTestBulkOperation();
        Gson gson = GsonUtils.createGson();
        String json = gson.toJson(testBulkOperation);

        // when
        ExtensibleObject deserializedBulkOperation = gson.fromJson(json, ExtensibleObject.class);

        // then
        assertNotNull(deserializedBulkOperation, "Deserialization of the BulkOperation failed!");
        BulkOperation bulkOperation = new BulkOperation(deserializedBulkOperation);
        checkAssertions(bulkOperation);
    }

    private BulkOperation createTestBulkOperation() {
        BulkOperation bulkOperation = new BulkOperation();
        bulkOperation.setId(bulkOperationId);
        bulkOperation.setCreationRamp(creationRamp);
        bulkOperation.setFailedBulkOperationId(failedBulkOperationId);
        bulkOperation.setGroupId(groupId);
        bulkOperation.setOperation(operation);
        bulkOperation.setProgress(progress);
        bulkOperation.setStartDate(startDate);
        bulkOperation.setStatus(status);

        return bulkOperation;
    }

    private void checkAssertions(BulkOperation testBulkOperation) {
        assertEquals(testBulkOperation.getId(), bulkOperationId);
        assertEquals(testBulkOperation.getGroupId(), groupId);
        assertEquals(testBulkOperation.getStatus(), status);
        assertEquals(testBulkOperation.getFailedBulkOperationId(), failedBulkOperationId);
        assertEquals(testBulkOperation.getCreationRamp().longValue(), creationRamp);
        assertTrue(testBulkOperation.getOperation() instanceof Operation);
        assertTrue(testBulkOperation.getProgress() instanceof Progress);
        assertEquals(testBulkOperation.getStartDate(), startDate);
    }


}
