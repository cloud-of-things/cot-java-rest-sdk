package com.telekom.m2m.cot.restsdk.audit;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Created by Andreas Dyck on 26.07.17.
 */
public class AuditApiTest {

    @Test
    public void testAuditApi() {
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

        AuditApi auditApi = new AuditApi(cotRestClientMock);

        Assert.assertNotNull(auditApi);
    }

    @Test
    public void testGetAuditRecord() {
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

        final AuditApi auditApi = new AuditApi(cotRestClientMock);

        final String auditRecordId = "234";
        final String text = "new audit record created";

        final AuditRecord testAuditRecord = new AuditRecord();
        testAuditRecord.setId(auditRecordId);
        testAuditRecord.setText(text);

        final Gson gson = GsonUtils.createGson();
        final String json = gson.toJson(testAuditRecord);

        Mockito.when(cotRestClientMock.getResponse(eq(auditRecordId), any(String.class), any(String.class))).thenReturn(json);

        AuditRecord retrievedAuditRecord = auditApi.getAuditRecord(auditRecordId);

        Assert.assertNotNull(retrievedAuditRecord);
        Assert.assertEquals(retrievedAuditRecord.getId(), auditRecordId);
        Assert.assertEquals(retrievedAuditRecord.getText(), text);
    }

    @Test
    public void testCreateAuditRecord() {
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

        final AuditApi auditApi = new AuditApi(cotRestClientMock);

        final String auditRecordId = "234";
        final String text = "new audit record created";

        final AuditRecord testAuditRecord = new AuditRecord();
        testAuditRecord.setText(text);

        Mockito.when(cotRestClientMock.doRequestWithIdResponse(any(String.class), any(String.class), any(String.class), any(String.class))).thenReturn(auditRecordId);

        AuditRecord createdAuditRecord = auditApi.createAuditRecord(testAuditRecord);

        Assert.assertNotNull(createdAuditRecord);
        Assert.assertEquals(createdAuditRecord.getId(), auditRecordId);
        Assert.assertEquals(createdAuditRecord.getText(), text);
    }

    @Test
    public void testGetAuditRecordCollection() {
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

        final AuditApi auditApi = new AuditApi(cotRestClientMock);

        AuditRecordCollection auditRecordCollection = auditApi.getAuditRecordCollection();

        Assert.assertNotNull(auditRecordCollection);
    }

    @Test
    public void testGetAuditRecordCollectionWithFilter() {
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

        final AuditApi auditApi = new AuditApi(cotRestClientMock);
        final String type = "com_telekom_audit_TestType";
        final Filter.FilterBuilder filterBuilder = Filter.build().byType(type);

        AuditRecordCollection auditRecordCollection = auditApi.getAuditRecordCollection(filterBuilder);

        Assert.assertNotNull(auditRecordCollection);
    }

    @Test
    public void testDeleteAuditRecords() {
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

        final AuditApi auditApi = new AuditApi(cotRestClientMock);
        final String type = "com_telekom_audit_TestType";
        final Filter.FilterBuilder filterBuilder = Filter.build().byType(type);

        auditApi.deleteAuditRecords(filterBuilder);

        Mockito.verify(cotRestClientMock, Mockito.times(1)).deleteBy("type="+type, "audit/auditRecords/");
    }
}
