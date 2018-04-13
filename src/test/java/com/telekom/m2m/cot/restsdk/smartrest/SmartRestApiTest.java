package com.telekom.m2m.cot.restsdk.smartrest;

import static com.telekom.m2m.cot.restsdk.util.Filter.FilterBuilder;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import com.google.gson.JsonParser;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObjectCollection;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import org.mockito.ArgumentMatcher;
import org.testng.annotations.Test;

public class SmartRestApiTest {

    private final static ArgumentMatcher<FilterBuilder> filterBuilderArgumentMatcher =
            new ArgumentMatcher<FilterBuilder>() {
        @Override
        public boolean matches(final Object argument) {
            if (argument instanceof FilterBuilder) {
                FilterBuilder filterBuilder = (FilterBuilder) argument;
                return filterBuilder.buildFilter().equals("type=someXID");
            }
            return false;
        }
    };

    @Test
    public void testConstructor() {
        // given: mocks for dependencies
        CloudOfThingsRestClient cloudOfThingsRestClient = mock(CloudOfThingsRestClient.class);
        InventoryApi inventoryApi = mock(InventoryApi.class);

        // when: the constructor is called
        SmartRestApi smartRestApi = new SmartRestApi(cloudOfThingsRestClient, inventoryApi);

        // then: smartRestApi is constructed
        assertNotNull(smartRestApi);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullParameterA() {
        // given: mocks for dependencies
        CloudOfThingsRestClient cloudOfThingsRestClient = mock(CloudOfThingsRestClient.class);

        // when: the constructor is called with null parameter
        SmartRestApi smartRestApi = new SmartRestApi(cloudOfThingsRestClient, null);
        // then: exception is thrown
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullParameterB() {
        // given: mocks for dependencies
        InventoryApi inventoryApi = mock(InventoryApi.class);

        // when: the constructor is called with null parameter
        SmartRestApi smartRestApi = new SmartRestApi(null, inventoryApi);
        // then: exception is thrown
    }

    @Test
    public void getTemplatesByGId() {

        // given: a mock for cloudOfThingsRestClientMock (just to please the null check)
        CloudOfThingsRestClient cloudOfThingsRestClientMock = mock(CloudOfThingsRestClient.class);

        // and: a ManagedObjectCollection with one test object that has a request and a response template
        JsonParser jsonParser = new JsonParser();
        ManagedObject managedObject = new ManagedObject();
        managedObject.setType("someXID");
        managedObject.set("com_cumulocity_model_smartrest_SmartRestTemplate", jsonParser.parse(
                "{requestTemplates:[{templateString: 'REQ_TPL'}]," +
                        "responseTemplates:[{condition: 'RESP_TPL'}]}"));

        // and: a mock for the inventory API with a argument checker for the FilterBuilder
        InventoryApi inventoryApiMock = mock(InventoryApi.class);
        when(inventoryApiMock.get(eq("someGID"))).thenReturn(managedObject);

        // and: the test subject with necessary dependencies
        SmartRestApi testSubject = new SmartRestApi(cloudOfThingsRestClientMock, inventoryApiMock);

        // when: getTemplatesByXID() is called
        SmartTemplate[] result = testSubject.getTemplatesByGId("someGID");

        // then: the result contains the expected objects
        assertEquals(result.length, 2);
        assertEquals(((SmartRequestTemplate)result[0]).getTemplateString(), "REQ_TPL");
        assertEquals(((SmartResponseTemplate)result[1]).getCondition(), "RESP_TPL");
    }

    @Test
    public void getTemplatesByXId() {

        // given: a mock for cloudOfThingsRestClientMock (just to please the null check)
        CloudOfThingsRestClient cloudOfThingsRestClientMock = mock(CloudOfThingsRestClient.class);

        // and: a ManagedObjectCollection with one test object that has a request and a response template
        JsonParser jsonParser = new JsonParser();
        ManagedObject[] managedObjects = new ManagedObject[1];
        managedObjects[0] = new ManagedObject();
        managedObjects[0].setType("someXID");
        managedObjects[0].set("com_cumulocity_model_smartrest_SmartRestTemplate", jsonParser.parse(
                "{requestTemplates:[{templateString: 'REQ_TPL'}]," +
                     "responseTemplates:[{condition: 'RESP_TPL'}]}"));

        ManagedObjectCollection managedObjectCollectionMock = mock(ManagedObjectCollection.class);
        when(managedObjectCollectionMock.getManagedObjects()).thenReturn(managedObjects);

        // and: a mock for the inventory API with a argument checker for the FilterBuilder
        InventoryApi inventoryApiMock = mock(InventoryApi.class);

        when(inventoryApiMock.getManagedObjects(argThat(filterBuilderArgumentMatcher), eq(2))).
                thenReturn(managedObjectCollectionMock);

        // and: the test subject with necessary dependencies
        SmartRestApi testSubject = new SmartRestApi(cloudOfThingsRestClientMock, inventoryApiMock);

        // when: getTemplatesByXID() is called
        SmartTemplate[] result = testSubject.getTemplatesByXId("someXID");

        // then: the result contains the expected objects
        assertEquals(result.length, 2);
        assertEquals(((SmartRequestTemplate)result[0]).getTemplateString(), "REQ_TPL");
        assertEquals(((SmartResponseTemplate)result[1]).getCondition(), "RESP_TPL");
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void getTemplatesByXIdMoreThanOneResult() {

        // given: a mock for cloudOfThingsRestClientMock (just to please the null check)
        CloudOfThingsRestClient cloudOfThingsRestClientMock = mock(CloudOfThingsRestClient.class);

        // and: a ManagedObjectCollection with one test object that has a request and a response template
        JsonParser jsonParser = new JsonParser();
        ManagedObject[] managedObjects = new ManagedObject[2];
        managedObjects[0] = new ManagedObject();
        managedObjects[0].setType("sameXID");
        managedObjects[1] = new ManagedObject();
        managedObjects[1].setType("sameXID");

        ManagedObjectCollection managedObjectCollectionMock = mock(ManagedObjectCollection.class);
        when(managedObjectCollectionMock.getManagedObjects()).thenReturn(managedObjects);

        // and: a mock for the inventory API with a argument checker for the FilterBuilder
        InventoryApi inventoryApiMock = mock(InventoryApi.class);
        when(inventoryApiMock.getManagedObjects(argThat(filterBuilderArgumentMatcher), eq(2))).
                thenReturn(managedObjectCollectionMock);

        // and: the test subject with necessary dependencies
        SmartRestApi testSubject = new SmartRestApi(cloudOfThingsRestClientMock, inventoryApiMock);

        // when: getTemplatesByXID() is called
        testSubject.getTemplatesByXId("someXID");

        // then: exception is thrown

    }

}
