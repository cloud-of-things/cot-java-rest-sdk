package com.telekom.m2m.cot.restsdk.devicecontrol;

/**
 * Enumeration for operation status values.
 * <p>
 * Created by Patrick Steinert on 31.01.16.
 */
public enum OperationStatus {
    /**
     * Operation is retrieved by device, accepted and now executing. Next status can be SUCCESSFULL or FAILED.
     */
    EXECUTING,

    /**
     * Operation execution failed or device denied execution.
     */
    FAILED,

    /**
     * Operation is new and awaits retrieval from device.
     */
    PENDING,

    /**
     * Operation is executed successfully.
     */
    SUCCESSFUL,

    /**
     * Just used in device registry and indicates an accepted device.
     */
    ACCEPTED
}
