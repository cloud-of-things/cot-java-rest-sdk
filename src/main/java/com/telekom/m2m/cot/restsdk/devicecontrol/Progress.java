package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

/**
 * It represents the progress and contains number of processed bulk operations.
 * <p>
 * Created by Andreas Dyck on 31.08.17.
 */
public class Progress extends ExtensibleObject {

    /**
     * Constructor to create new progress.
     */
    public Progress() {
        super();
    }

    /**
     * Internal constructor to create a progress object by base class.
     *
     * @param extensibleObject the base class object.
     */
    public Progress(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }

    /**
     * Set the number of all devices on which bulk operation should be executed.
     *
     * @param numberOfDevices Number of all devices on which bulk operation should be executed.
     */
    public void setNumberOfDevices(final Number numberOfDevices) {
        anyObject.put("all", numberOfDevices);
    }

    /**
     * Get the number of all devices on which bulk operation should be executed.
     *
     * @return the number of all devices on which bulk operation should be executed.
     */
    public Number getNumberOfDevices() {
        return (Number) anyObject.get("all");
    }

    /**
     * Set the number of all devices on which the operation status is pending.
     *
     * @param numberOfPendingDevices Number of all devices on which the operation status is pending.
     */
    public void setNumberOfPendingDevices(final Number numberOfPendingDevices) {
        anyObject.put("pending", numberOfPendingDevices);
    }

    /**
     * Get the number of all devices on which the operation status is pending.
     *
     * @return the number of all devices on which the operation status is pending.
     */
    public Number getNumberOfPendingDevices() {
        return (Number) anyObject.get("pending");
    }

    /**
     * Set the number of all devices on which the operation status is failed.
     *
     * @param numberOfFailedDevices Number of all devices on which the operation status is failed.
     */
    public void setNumberOfFailedDevices(final Number numberOfFailedDevices) {
        anyObject.put("failed", numberOfFailedDevices);
    }

    /**
     * Get the number of all devices on which the operation status is failed.
     *
     * @return the number of all devices on which the operation status is failed.
     */
    public Number getNumberOfFailedDevices() {
        return (Number) anyObject.get("failed");
    }

    /**
     * Set the number of all devices on which the operation status is executing.
     *
     * @param numberOfExecutingDevices Number of all devices on which the operation status is executing.
     */
    public void setNumberOfExecutingDevices(final Number numberOfExecutingDevices) {
        anyObject.put("executing", numberOfExecutingDevices);
    }

    /**
     * Get the number of all devices on which the operation status is executing.
     *
     * @return the number of all devices on which the operation status is executing.
     */
    public Number getNumberOfExecutingDevices() {
        return (Number) anyObject.get("executing");
    }

    /**
     * Set the number of all devices on which the operation status is successful.
     *
     * @param numberOfSuccessfulDevices Number of all devices on which the operation status is successful.
     */
    public void setNumberOfSuccessfulDevices(final Number numberOfSuccessfulDevices) {
        anyObject.put("successful", numberOfSuccessfulDevices);
    }

    /**
     * Get the number of all devices on which the operation status is successful.
     *
     * @return the number of all devices on which the operation status is successful.
     */
    public Number getNumberOfSuccessfulDevices() {
        return (Number) anyObject.get("successful");
    }

}
