package com.telekom.m2m.cot.restsdk.smartrest;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

/**
 * Represents a request to send via SmartREST API.
 */
public class SmartRequest extends ExtensibleObject {

    public enum ProcessingMode {
        TRANSIENT, PERSISTENT
    }


    /**
     * Constructor to create a new request for SmartREST API.
     * It will have the processing mode PERSISTENT.
     *
     * @param xId the X-Id for which this request shall be made.
     *            Can be null, omitting the X-Id header, to allow for multiple X-Id ("15,myxid").
     * @param body a String with newline-separated lines for the request body
     */
    public SmartRequest(String xId, String body) {
        super();
        setXId(xId);
        setProcessingMode(ProcessingMode.PERSISTENT);
        setBody(body);
    }


    /**
     * Constructor to create a new request for SmartREST API.
     *
     * @param xId the X-Id for which this request shall be made.
     *            Can be null, omitting the X-Id header, to allow for multiple X-Id ("15,myxid").
     * @param body a String with newline-separated lines for the request body
     * @param processingMode can be {@link ProcessingMode#TRANSIENT} if you just want to pass data for real time
     *                       processing, which need not be stored in the CoT database.
     */
    public SmartRequest(String xId, ProcessingMode processingMode, String body) {
        super();
        setXId(xId);
        setProcessingMode(processingMode);
        setBody(body);
    }


    /**
     * Get the X-Id of the template to use for this request.
     * Can be null if message-ID 15 contained in body for multiple X-Id.
     *
     * @return a String with X-Id which specifies the SmartREST template to use for this request.
     */
    public String getXId() {
        return (String) anyObject.get("x-id");
    }

    /**
     * Setting the X-Id of the template to use for this request.
     * Can be left empty if message-ID 15 contained in body for multiple X-Id.
     *
     * @param xId a String with X-Id which specifies the SmartREST template to use for this request.
     */
    public void setXId(String xId) {
        anyObject.put("x-id", xId);
    }

    /**
     * Get the processing mode to use for this request.
     * One of two values are possible: TRANSIENT and PERSISTENT.
     *
     * @return the {@link ProcessingMode}
     */
    public ProcessingMode getProcessingMode() {
        return (ProcessingMode) anyObject.get("processing-mode");
    }

    /**
     * Setting processing mode to use for this request.
     * Only two values will be accepted: TRANSIENT and PERSISTENT.
     * When left empty, PERSISTENT processing mode will be assumed and used as default.
     *
     * @param processingMode the {@link ProcessingMode}
     */
    public void setProcessingMode(ProcessingMode processingMode) {
        anyObject.put("processing-mode", processingMode);
    }

    /**
     * Get the body of the smartREST Request: newline-separated lines for the request body.
     *
     * @return a String with the body of the smartREST Request.
     */
    public String getBody() {
        return (String) anyObject.get("body");
    }

    /**
     * Setting the body of the smartREST Request:
     * a String with newline-separated lines for the request body.
     *
     * @param body a String with the body of the smartREST Request.
     */
    public void setBody(String body) {
        anyObject.put("body", body);
    }

}
