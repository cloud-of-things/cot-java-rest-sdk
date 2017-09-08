package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.Filter;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.util.Date;

import static org.mockito.Matchers.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by Patrick Steinert on 03.02.16.
 */
public class DeviceControlApiTest {

    @Test
    public void testDeviceControlApi() {
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

        final DeviceControlApi deviceControlApi = new DeviceControlApi(cotRestClientMock);

        assertNotNull(deviceControlApi);
    }

    @Test
    public void testCreateOperation() {
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

        final DeviceControlApi deviceControlApi = new DeviceControlApi(cotRestClientMock);

        final String operationId = "234";
        final String deviceId = "dev123";

        final Operation testOperation = new Operation();
        testOperation.setDeviceId(deviceId);

        Mockito.when(cotRestClientMock.doRequestWithIdResponse(any(String.class), any(String.class), any(String.class))).thenReturn(operationId);

        Operation createdOperation = deviceControlApi.create(testOperation);

        assertNotNull(createdOperation);
        assertEquals(createdOperation.getId(), operationId);
        assertEquals(createdOperation.getDeviceId(), deviceId);
    }

    @Test
    public void testUpdateOperation() {
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

        final DeviceControlApi deviceControlApi = new DeviceControlApi(cotRestClientMock);

        final String operationId = "234";
        final OperationStatus operationStatus = OperationStatus.EXECUTING;

        final Operation testOperation = new Operation();
        testOperation.setId(operationId);
        testOperation.setStatus(operationStatus);

        Operation updatedOperation = deviceControlApi.update(testOperation);

        Mockito.verify(cotRestClientMock, Mockito.times(1)).
                doPutRequest(contains(operationStatus.toString()) , contains(operationId), any(String.class));

        assertNotNull(updatedOperation);
        assertEquals(updatedOperation.getId(), operationId);
        assertEquals(updatedOperation.getStatus(), operationStatus);
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testAcceptDeviceWithFailure() throws Exception {
        // given
        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        DeviceControlApi deviceControlApi = new DeviceControlApi(rc);

        Mockito.doThrow(CotSdkException.class).when(rc).doPutRequest(contains(OperationStatus.ACCEPTED.toString()), contains("foo"), any(String.class));

        // when
        deviceControlApi.acceptDevice("foo");
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testGetOperationWithFailure() throws Exception {
        // given
        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        DeviceControlApi deviceControlApi = new DeviceControlApi(rc);

        Mockito.doThrow(CotSdkException.class).when(rc).getResponse(eq("foo"), any(String.class), any(String.class));

        // when
        deviceControlApi.getOperation("foo");
    }

    @Test
    public void testGetOperation() throws Exception {
        // given
        String operationId = "123";

        String eventJsonExample = "{\n" +
                "  \"id\" : \"" + operationId + "\",\n" +
                "  \"self\" : \"<<This Operation URL>>\",\n" +
                "  \"deviceId\" : \"1243\",\n" +
                "  \"status\" : \"PENDING\",\n" +
                "  \"creationTime\" : \"2011-09-06T12:03:27.000+02:00\",\n" +
                "  \"com_telekom_model_WebCamDevice\" : {\n" +
                "    \"name\" : \"take picture\",\n" +
                "    \"parameters\" : {\n" +
                "      \"duration\" : \"5s\",\n" +
                "      \"quality\" : \"HD\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        DeviceControlApi deviceControlApi = new DeviceControlApi(rc);

        Mockito.when(rc.getResponse(eq(operationId), any(String.class), any(String.class))).thenReturn(eventJsonExample);

        // when
        Operation operation = deviceControlApi.getOperation(operationId);

        // then
        assertEquals(operation.getId(), operationId);
        assertEquals(operation.getCreationTime().compareTo(new Date(1315303407000L)), 0);
        assertEquals(operation.getStatus().toString(), "PENDING");
        assertEquals(operation.getDeviceId(), "1243");

        Object obj = operation.get("com_telekom_model_WebCamDevice");
        assertNotNull(obj);
    }

    @Test
    public void testGetOperationCollection() {
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

        final DeviceControlApi deviceControlApi = new DeviceControlApi(cotRestClientMock);

        OperationCollection operationCollection = deviceControlApi.getOperationCollection(5);

        assertNotNull(operationCollection);
    }

    @Test
    public void testGetOperationCollectionWithFilter() {
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

        final DeviceControlApi deviceControlApi = new DeviceControlApi(cotRestClientMock);
        final String deviceId = "dev123";
        final Filter.FilterBuilder filterBuilder = Filter.build().byDeviceId(deviceId);

        OperationCollection operationCollection = deviceControlApi.getOperationCollection(filterBuilder, 5);

        assertNotNull(operationCollection);
    }

    @Test
    public void testDeleteOperations() throws Exception {
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

        final DeviceControlApi deviceControlApi = new DeviceControlApi(cotRestClientMock);
        final String deviceId = "dev123";
        final Filter.FilterBuilder filterBuilder = Filter.build().byDeviceId(deviceId);

        deviceControlApi.deleteOperations(filterBuilder);

        Mockito.verify(cotRestClientMock, Mockito.times(1)).deleteBy(eq("deviceId="+deviceId), any(String.class));
    }

    @Test
    public void testCreateNewDevice() throws Exception {
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

        final DeviceControlApi deviceControlApi = new DeviceControlApi(cotRestClientMock);

        final String deviceId = "dev123";

        final Operation testOperation = new Operation();
        testOperation.setDeviceId(deviceId);

        deviceControlApi.createNewDevice(testOperation);

        Mockito.verify(cotRestClientMock, Mockito.times(1)).doPostRequest(contains(deviceId), any(String.class), any(String.class));
    }

    @Test
    public void testGetBulkOperation() throws Exception {

        // given
        String bulkOperationId = "123";

        String bulkOperationJsonExample = "{\n" +
                "  \"id\" : \"" + bulkOperationId + "\",\n" +
                "  \"self\" : \"<<This BulkOperation URL>>\",\n" +
                "  \"groupId\" : \"124301\",\n" +
                "  \"status\" : \"ACTIVE\",\n" +
                "  \"startDate\" : \"2011-09-06T12:03:27\",\n" +
                "  \"operationPrototype\":{\"test\"=>\"TEST1\"},\n" +
                "  \"creationRamp\":15,\n" +
                "  \"progress\":\n" +
                "    {\n" +
                "     \"pending\":0, \"failed\":0, \"executing\":0, \"successful\":0, \"all\":2\n" +
                "    }\n" +
                "}";

        CloudOfThingsRestClient rc = Mockito.mock(CloudOfThingsRestClient.class);
        DeviceControlApi deviceControlApi = new DeviceControlApi(rc);
        Mockito.when(rc.getResponse(eq(bulkOperationId), any(String.class), any(String.class))).thenReturn(
                bulkOperationJsonExample);

        // when
        BulkOperation bulkOperation = deviceControlApi.getBulkOperation(bulkOperationId);

        // then
        assertEquals(bulkOperation.getId(), bulkOperationId);
        assertEquals(bulkOperation.getGroupId(), "124301");
        assertEquals(bulkOperation.getStatus(), "ACTIVE");
        assertEquals(bulkOperation.getCreationRamp().intValue(), 15);
    }

    @Test
    public void testGetBulkOperationCollection() {
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

        final DeviceControlApi deviceControlApi = new DeviceControlApi(cotRestClientMock);

        BulkOperationCollection bulkOperationCollection = deviceControlApi.getBulkOperationCollection(7);

        assertNotNull(bulkOperationCollection);
    }

    @Test
    public void testUpdateBulkOperation() {
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

        final DeviceControlApi deviceControlApi = new DeviceControlApi(cotRestClientMock);

        BulkOperation bulkOperation = new BulkOperation();
        bulkOperation.setId("id234");
        bulkOperation.setStatus("id234");
        Progress progress = new Progress();
        progress.setNumberOfDevices(1);
        bulkOperation.setProgress(progress);
        // it make sense to set either groupId or failedBulkOperationId, not both, but for test purposes I set both
        bulkOperation.setGroupId("group123");
        bulkOperation.setFailedBulkOperationId("failedBulkOperationId321");
        bulkOperation.setStartDate(new Date(1504528150614L));

        Operation operation = new Operation();
        operation.set("description", "Restart device");
        operation.set("c8y_Restart", new JsonObject());
        bulkOperation.setOperation(operation);
        bulkOperation.setCreationRamp(9);

        String expectedBulkOperationJson = "{\"failedBulkOperationId\":\"failedBulkOperationId321\",\"operationPrototype\":{\"description\":\"Restart device\",\"c8y_Restart\":{}},\"creationRamp\":9,\"startDate\":\"2017-09-04T12:29:10.614Z\",\"groupId\":\"group123\"}";

        deviceControlApi.update(bulkOperation);

        Mockito.verify(cotRestClientMock, Mockito.times(1)).doPutRequest(contains(expectedBulkOperationJson), contains("id234"), any(String.class));
    }

}
