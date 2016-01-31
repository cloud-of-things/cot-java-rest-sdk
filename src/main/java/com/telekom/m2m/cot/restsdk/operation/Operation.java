package com.telekom.m2m.cot.restsdk.operation;

import java.util.Date;
import java.util.Map;

/**
 * Created by breucking on 31.01.16.
 */
public class Operation {

    private String id;
    private OperationStatus status;

    public Operation() {

    }

    public Operation(String id) {
        this.id = id;
    }

    public Operation(String id, Date creationTime, OperationStatus status, String failureReason, String deviceId, String agentId) {

    }

    public Operation(String id, Date creationTime, OperationStatus status, String failureReason, String deviceId, String agentId, Long bulkOperationId, Map<String,Object> fragments) {

    }
    public Operation(String id, Date creationTime, OperationStatus status, String failureReason, String deviceId, String agentId, Map<String,Object> fragments) {

    }

    public void setStatus(OperationStatus status) {
        this.status = status;
    }
}
