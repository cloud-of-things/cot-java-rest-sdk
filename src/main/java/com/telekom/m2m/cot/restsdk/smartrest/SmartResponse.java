package com.telekom.m2m.cot.restsdk.smartrest;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

/**
 * Represents a response received via SmartREST API.
 */
public class SmartResponse extends ExtensibleObject {

    /**
     * Default constructor to create a new response received from smartREST API.
     */
    public SmartResponse() {
        super();
    }

    /**
     * Constructor to create a new response with received body from smartREST API.
     *
     * @param body a String with the body of the SmartREST Response.
     */
    public SmartResponse(String body) {
        super();
        setBody(body);
    }

    /**
     * Get the body of the smartREST Response.
     *
     * @return a String with the body of the smartREST Response.
     */
    public String getBody() {
        return (String) anyObject.get("body");
    }

    /**
     * Setting the body of the smartREST Response.
     *
     * @param body a String with the body of the SmartREST Response.
     */
    public void setBody(String body) {
        anyObject.put("body", body);
    }
}
