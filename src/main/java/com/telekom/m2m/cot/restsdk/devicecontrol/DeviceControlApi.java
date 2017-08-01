package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * DeviceControl API is used to work with operations.
 *
 * @since 0.1.0
 * Created by Patrick Steinert on 31.01.16.
 */
public class DeviceControlApi {
    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    protected Gson gson = GsonUtils.createGson();
    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.newDeviceRequest+json;charset=UTF-8;ver=0.9";
    private static final String CONTENT_TYPE_OPERATION = "application/vnd.com.nsn.cumulocity.operation+json;charset=UTF-8;ver=0.9";
    private static final String CONTENT_TYPE_BULK_OPERATION = "application/vnd.com.nsn.cumulocity.bulkoperation+json;charset=UTF-8;ver=0.9";

    private static final String RELATIVE_NEW_DEVICE_REQUEST_API_URL = "devicecontrol/newDeviceRequests/";
    private static final String RELATIVE_OPERATION_API_URL = "devicecontrol/operations/";

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
        cloudOfThingsRestClient.doPostRequest(gson.toJson(operation), RELATIVE_NEW_DEVICE_REQUEST_API_URL, CONTENT_TYPE);
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

        cloudOfThingsRestClient.doPutRequest(gson.toJson(operation), RELATIVE_NEW_DEVICE_REQUEST_API_URL + deviceId, CONTENT_TYPE);
    }

    /**
     * Retrieves a certain operation.
     *
     * @param operationId Id of the operation to retrieve.
     * @return the Operation as Object.
     * @since 0.1.0
     */
    public Operation getOperation(String operationId) {
        String response = cloudOfThingsRestClient.getResponse(operationId, RELATIVE_OPERATION_API_URL, CONTENT_TYPE_OPERATION);
        Operation operation = new Operation(gson.fromJson(response, ExtensibleObject.class));
        return operation;
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
     * Retrieve Operations.
     *
     * @param resultSize size of the results (Max. 2000)
     * @return an OperationCollection.
     */
    public OperationCollection getOperations(int resultSize) {
        return new OperationCollection(
                cloudOfThingsRestClient,
                RELATIVE_OPERATION_API_URL,
                gson,
                null,
                resultSize);
    }

    /**
     * Retrieve Operations by criteria.
     *
     * @param filters    filters of measurement attributes.
     * @param resultSize size of the results (Max. 2000)
     * @return an OperationCollection.
     */
    public OperationCollection getOperations(Filter.FilterBuilder filters, int resultSize) {
        return new OperationCollection(
                cloudOfThingsRestClient,
                RELATIVE_OPERATION_API_URL,
                gson,
                filters,
                resultSize);
    }

    /**
     * Deletes a collection of Operations by criteria.
     *
     * @param filters filters of Operation attributes.
     */
    public void deleteOperations(Filter.FilterBuilder filters) {
        cloudOfThingsRestClient.delete("", RELATIVE_OPERATION_API_URL + "?" + filters.buildFilter() + "&x=");
    }

    /**
     * Retrieves a certain bulk operation.
     *
     * @param id the unique identifier of the bulk operation of retrieve.
     * @return a BulkOperation object or null if not found.
     */
    public BulkOperation getBulkOperation(String id) {
        String response = cloudOfThingsRestClient.getResponse(id, "/devicecontrol/bulkoperations/",
                CONTENT_TYPE_BULK_OPERATION);
        BulkOperation bulkOperation = new BulkOperation(gson.fromJson(response, ExtensibleObject.class));
        return bulkOperation;
    }
}
