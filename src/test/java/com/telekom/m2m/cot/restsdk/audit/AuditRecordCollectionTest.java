package com.telekom.m2m.cot.restsdk.audit;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by Andreas Dyck on 26.07.17.
 */
public class AuditRecordCollectionTest {

    private final String relativeApiUrl = "audit/auditRecords/";
    private final Gson gson = GsonUtils.createGson();

    @Test
    public void testGetAuditRecords() {
        // given
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

        final AuditRecordCollection auditRecordCollectionSpy = Mockito.spy(new AuditRecordCollection(
                cotRestClientMock,
                relativeApiUrl,
                gson,
                null
        ));

        final JsonArray jsonArray = createJsonObjectArray(gson, 4);

        Mockito.doReturn(jsonArray).when(auditRecordCollectionSpy).getJsonArray();

        // when
        final AuditRecord[] auditRecords = auditRecordCollectionSpy.getAuditRecords();

        // then
        Assert.assertNotNull(auditRecords);
        Assert.assertEquals(auditRecords.length, 4);
        for (int i = 0; i < auditRecords.length; i++) {
            Assert.assertEquals(auditRecords[i].getId(), String.valueOf(i + 1));
        }
    }

    @Test
    public void testGetAuditRecordsIsEmpty() {
        // given
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

        final AuditRecordCollection auditRecordCollectionSpy = Mockito.spy(new AuditRecordCollection(
                cotRestClientMock,
                relativeApiUrl,
                gson,
                null
        ));

        Mockito.doReturn(null).when(auditRecordCollectionSpy).getJsonArray();

        // when
        final AuditRecord[] auditRecords = auditRecordCollectionSpy.getAuditRecords();

        // then
        Assert.assertNull(auditRecords);
    }

    private JsonArray createJsonObjectArray(final Gson gson, final int numElements) {
        final JsonArray jsonArray = new JsonArray();

        for (int i = 0; i < numElements; i++) {
            final ExtensibleObject extensibleObject = new ExtensibleObject();
            extensibleObject.set("id", String.valueOf(i + 1));
            jsonArray.add(gson.toJsonTree(extensibleObject));
        }

        return jsonArray;
    }
}
