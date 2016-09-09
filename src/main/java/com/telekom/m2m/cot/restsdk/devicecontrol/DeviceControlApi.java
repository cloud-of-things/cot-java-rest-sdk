package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.operation.Operation;
import com.telekom.m2m.cot.restsdk.operation.OperationStatus;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

import java.io.IOException;

/**
 * Created by breucking on 31.01.16.
 */
public class DeviceControlApi {
    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    protected Gson gson = GsonUtils.createGson();
    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.newDeviceRequest+json;charset=UTF-8;ver=0.9";
    private static final String CONTENT_TYPE_OPERATION = "application/vnd.com.nsn.cumulocity.operation+json;charset=UTF-8;ver=0.9";

    public DeviceControlApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    public Operation createNewDevice(Operation operation) throws IOException {
        cloudOfThingsRestClient.doPostRequest(gson.toJson(operation), "devicecontrol/newDeviceRequests", CONTENT_TYPE);
        return operation;
    }

    public void acceptDevice(String deviceId) throws IOException {
        Operation operation = new Operation();
        operation.setStatus(OperationStatus.ACCEPTED);

        cloudOfThingsRestClient.doPutRequest(gson.toJson(operation), "devicecontrol/newDeviceRequests/" + deviceId, CONTENT_TYPE);
    }

    public Operation getOperation(String operationId) {
        String response = cloudOfThingsRestClient.getResponse(operationId, "devicecontrol/operations/", CONTENT_TYPE_OPERATION);
        Operation operation = new Operation(gson.fromJson(response, ExtensibleObject.class));
        return operation;
    }

    public Operation create(Operation operation) {
        String json = gson.toJson(operation);

        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "devicecontrol/operations/", CONTENT_TYPE_OPERATION);
        operation.setId(id);

        return operation;
    }

    public Operation update(Operation operation) {
        String json = "{\"status\" : \"" + operation.getStatus() + "\"}";

        cloudOfThingsRestClient.doPutRequest(json, "devicecontrol/operations/" + operation.getId(), CONTENT_TYPE_OPERATION);
        return operation;
    }
}
