package com.telekom.m2m.cot.restsdk.devicecontrol;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.mockito.Mockito;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Created by Andreas Dyck on 04.09.17.
 */
public class BulkOperationCollectionTest {

    private final String relativeApiUrl = "devicecontrol/bulkoperations/";
    private final Gson gson = GsonUtils.createGson();
    private final int pageSize = 5;

    @Test
    public void testGetOperations() {
        // given
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

        final BulkOperationCollection bulkOperationCollectionSpy = Mockito.spy(new BulkOperationCollection(
                cotRestClientMock,
                relativeApiUrl,
                gson,
                null,
                pageSize
        ));

        final JsonArray jsonArray = createJsonObjectArray(gson, 4);

        Mockito.doReturn(jsonArray).when(bulkOperationCollectionSpy).getJsonArray();

        // when
        final BulkOperation[] bulkOperations = bulkOperationCollectionSpy.getBulkOperations();

        // then
        assertNotNull(bulkOperations);
        assertEquals(bulkOperations.length, 4);
        for (int i = 0; i < bulkOperations.length; i++) {
            assertEquals(bulkOperations[i].getId(), String.valueOf(i + 1));
        }
    }

    @Test
    public void testGetOperationsIsEmpty() {
        // given
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

        final BulkOperationCollection bulkOperationCollectionSpy = Mockito.spy(new BulkOperationCollection(
                cotRestClientMock,
                relativeApiUrl,
                gson,
                null,
                pageSize
        ));

        Mockito.doReturn(null).when(bulkOperationCollectionSpy).getJsonArray();

        // when
        final BulkOperation[] bulkOperations = bulkOperationCollectionSpy.getBulkOperations();

        // then it should return an empty array:
        assertEquals(bulkOperations.length, 0);
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
