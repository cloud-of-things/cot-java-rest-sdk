package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.realtime.CepConnector;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DeviceControl API is used to work with operations.
 *
 * @since 0.1.0
 * Created by Patrick Steinert on 31.01.16.
 */
public class DeviceControlApi {

    public static final String NOTIFICATION_REST_ENDPOINT = "devicecontrol/notifications";

    private static final String CONTENT_TYPE_NEW_DEVICE_REQUEST = "application/vnd.com.nsn.cumulocity.newDeviceRequest+json;charset=UTF-8;ver=0.9";
    private static final String CONTENT_TYPE_OPERATION = "application/vnd.com.nsn.cumulocity.operation+json;charset=UTF-8;ver=0.9";
    private static final String CONTENT_TYPE_BULK_OPERATION = "application/vnd.com.nsn.cumulocity.bulkoperation+json;charset=UTF-8;ver=0.9";

    private static final String RELATIVE_NEW_DEVICE_REQUEST_API_URL = "devicecontrol/newDeviceRequests/";
    private static final String RELATIVE_OPERATION_API_URL = "devicecontrol/operations/";
    private static final String RELATIVE_BULK_OPERATION_API_URL = "devicecontrol/bulkoperations/";

    private final CloudOfThingsRestClient cloudOfThingsRestClient;

    protected Gson gson = GsonUtils.createGson();

    /**
     * Internal used constructor.
     * <p>
     * Use {@link com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform}.getDeviceControlApi()
     * to create an instance.
     * </p>
     *
     * @param cloudOfThingsRestClient the configured rest client.
     * @since 0.1.0
     */
    public DeviceControlApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    /**
     * Creates a new Device Request to register new devices.
     *
     * @param operation {@link Operation} just with the deviceId to register.
     * @return the created operation.
     * @since 0.1.0
     */
    public Operation createNewDevice(Operation operation) {
        cloudOfThingsRestClient.doPostRequest(gson.toJson(operation), RELATIVE_NEW_DEVICE_REQUEST_API_URL, CONTENT_TYPE_NEW_DEVICE_REQUEST);
        return operation;
    }

    /**
     * Accepts a device after it requested the credentials for the first time.
     *
     * @param deviceId the Id of the device to accept.
     * @since 0.1.0
     */
    public void acceptDevice(String deviceId) {
        Operation operation = new Operation();
        operation.setStatus(OperationStatus.ACCEPTED);

        cloudOfThingsRestClient.doPutRequest(gson.toJson(operation), RELATIVE_NEW_DEVICE_REQUEST_API_URL + deviceId, CONTENT_TYPE_NEW_DEVICE_REQUEST);
    }

    /**
     * Retrieves a certain operation.
     *
     * @param operationId Id of the operation to retrieve.
     * @return the Operation as Object or null if not found.
     * @since 0.1.0
     */
    public Operation getOperation(String operationId) {
        String response = cloudOfThingsRestClient.getResponse(operationId, RELATIVE_OPERATION_API_URL, CONTENT_TYPE_OPERATION);
        final ExtensibleObject extensibleObject = gson.fromJson(response, ExtensibleObject.class);

        return extensibleObject != null ?
                new Operation(extensibleObject) : null;
    }

    /**
     * Creates an operation.
     *
     * @param operation Operation object with the necessary data (w/o Id).
     * @return the Operation with the created Id.
     * @since 0.1.0
     */
    public Operation create(Operation operation) {
        String json = gson.toJson(operation);

        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, RELATIVE_OPERATION_API_URL, CONTENT_TYPE_OPERATION);
        operation.setId(id);

        return operation;
    }

    /**
     * Updates an existing operation state.
     *
     * @param operation the operation with new operation state,
     *                  Id as existing, not changeable.
     * @return the Operation object.
     * @since 0.1.0
     */
    public Operation update(Operation operation) {
        String json = "{\"status\" : \"" + operation.getStatus() + "\"}";

        cloudOfThingsRestClient.doPutRequest(json, RELATIVE_OPERATION_API_URL + operation.getId(), CONTENT_TYPE_OPERATION);
        return operation;
    }

    /**
     * Retrieves a pageable Collection of Operations.
     *
     * @param resultSize size of the results (Max. 2000)
     * @return the first page of OperationCollection which can be used to navigate through the found Operations.
     */
    public OperationCollection getOperationCollection(int resultSize) {
        return new OperationCollection(
                cloudOfThingsRestClient,
                RELATIVE_OPERATION_API_URL,
                gson,
                null,
                resultSize);
    }

    /**
     * Retrieves a pageable Collection of Operations filtered by criteria.
     *
     * It provides filtering by Status, AgentId, DeviceId, DateFrom, DateTo
     *
     * @param filters    filters of Operation attributes.
     * @param resultSize size of the results (Max. 2000)
     * @return the first page of OperationCollection which can be used to navigate through the found Operations.
     */
    public OperationCollection getOperationCollection(Filter.FilterBuilder filters, int resultSize) {
        return new OperationCollection(
                cloudOfThingsRestClient,
                RELATIVE_OPERATION_API_URL,
                gson,
                filters,
                resultSize);
    }

    /**
     * Deletes a collection of Operations by criteria (Status, AgentId, DeviceId, DateFrom, DateTo).
     *
     * @param filters filters of Operation attributes.
     */
    public void deleteOperations(Filter.FilterBuilder filters) {
        cloudOfThingsRestClient.deleteBy(filters.buildFilter(), RELATIVE_OPERATION_API_URL);
    }


    /**
     * Creates a bulk operation.
     *
     * @param bulkOperation bulk operation object with the necessary data (w/o Id).
     * @return the bulk operation stored in CoT with the created Id.
     * @since 0.1.0
     */
    public BulkOperation create(BulkOperation bulkOperation) {
        String json = gson.toJson(bulkOperation);

        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, RELATIVE_BULK_OPERATION_API_URL, CONTENT_TYPE_BULK_OPERATION);
        bulkOperation.setId(id);

        return bulkOperation;
    }

    /**
     * Retrieves a certain bulk operation.
     *
     * @param id the unique identifier of the bulk operation to retrieve.
     * @return a BulkOperation object or null if not found.
     */
    public BulkOperation getBulkOperation(String id) {
        String response = cloudOfThingsRestClient.getResponse(id, RELATIVE_BULK_OPERATION_API_URL,
                CONTENT_TYPE_BULK_OPERATION);
        final ExtensibleObject extensibleObject = gson.fromJson(response, ExtensibleObject.class);

        return extensibleObject != null ?
                new BulkOperation(extensibleObject) : null;
    }


    /**
     * Updates an existing bulk operation.
     * Updatable fields: groupId, failedBulkOperationId, startDate, creationRamp, operation
     *
     * @param bulkOperation the bulk operation with new values for updatable fields.
     *                  Id, status, progress are not changeable.
     * @since 0.6.0
     */
    public void update(BulkOperation bulkOperation) {
        ExtensibleObject extensibleObject = new ExtensibleObject();

        if(bulkOperation.has("groupId")) {
            extensibleObject.set("groupId", bulkOperation.getGroupId());
        }

        if(bulkOperation.has("failedBulkOperationId")) {
            extensibleObject.set("failedBulkOperationId", bulkOperation.getFailedBulkOperationId());
        }

        if(bulkOperation.has("startDate")) {
            // curiously CoT platform accepts only a UTC time zone at updating the start date in a different way from creation of a bulk operation
            Instant instant = bulkOperation.getStartDate().toInstant();
            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant,
                    ZoneId.systemDefault());
            extensibleObject.set("startDate", zonedDateTime.format(DateTimeFormatter.ISO_INSTANT));
        }

        if(bulkOperation.has("creationRamp")) {
            extensibleObject.set("creationRamp", bulkOperation.getCreationRamp());
        }

        if(bulkOperation.has("operationPrototype")) {
            extensibleObject.set("operationPrototype", bulkOperation.getOperation());
        }

        String json = gson.toJson(extensibleObject);

        cloudOfThingsRestClient.doPutRequest(json, RELATIVE_BULK_OPERATION_API_URL + bulkOperation.getId(), CONTENT_TYPE_BULK_OPERATION);
    }

    /**
     * Retrieves a pageable Collection of BulkOperations.
     *
     * @param resultSize size of the results (Max. 2000)
     * @return the first page of BulkOperationCollection which can be used to navigate through the found BulkOperations.
     */
    public BulkOperationCollection getBulkOperationCollection(int resultSize) {
        return new BulkOperationCollection(
                cloudOfThingsRestClient,
                RELATIVE_BULK_OPERATION_API_URL,
                gson,
                null,
                resultSize);
    }

    /**
     * Deletes a bulk operation by provided id.
     * Note: it deletes only bulk operations with statuses ACTIVE or IN_PROGRESS
     *
     * @param bulkOperationId the unique identifier.
     */
    public void deleteBulkOperation(String bulkOperationId) {
        cloudOfThingsRestClient.delete(bulkOperationId, RELATIVE_BULK_OPERATION_API_URL);
    }


    /**
     * Returns the connector that establishes the real time communication with the notification service.
     *
     * @return CepConnector
     */
    public CepConnector getNotificationConnector() {
        return new CepConnector(cloudOfThingsRestClient, NOTIFICATION_REST_ENDPOINT);
    }

}
