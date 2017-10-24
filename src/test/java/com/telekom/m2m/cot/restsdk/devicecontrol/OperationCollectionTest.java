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
 * Created by Andreas Dyck on 26.07.17.
 */
public class OperationCollectionTest {

    private final String relativeApiUrl = "devicecontrol/operations/";
    private final Gson gson = GsonUtils.createGson();
    private final int pageSize = 5;

    @Test
    public void testGetOperations() {
        // given
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

        final OperationCollection operationCollectionSpy = Mockito.spy(new OperationCollection(
                cotRestClientMock,
                relativeApiUrl,
                gson,
                null,
                pageSize
        ));

        final JsonArray jsonArray = createJsonObjectArray(gson, 4);

        Mockito.doReturn(jsonArray).when(operationCollectionSpy).getJsonArray();

        // when
        final Operation[] operations = operationCollectionSpy.getOperations();

        // then
        assertNotNull(operations);
        assertEquals(operations.length, 4);
        for (int i = 0; i < operations.length; i++) {
            assertEquals(operations[i].getId(), String.valueOf(i + 1));
        }
    }

    @Test
    public void testGetOperationsIsEmpty() {
        // given
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

        final OperationCollection operationCollectionSpy = Mockito.spy(new OperationCollection(
                cotRestClientMock,
                relativeApiUrl,
                gson,
                null,
                pageSize
        ));

        Mockito.doReturn(null).when(operationCollectionSpy).getJsonArray();

        // when
        final Operation[] operations = operationCollectionSpy.getOperations();

        // then it should return an empty array:
        assertEquals(operations.length, 0);
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
