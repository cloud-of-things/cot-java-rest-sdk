package com.telekom.m2m.cot.restsdk.smartrest;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

import static com.telekom.m2m.cot.restsdk.smartrest.SmartRestApi.LINE_BREAK_PATTERN;

/**
 * Represents a response received via SmartREST API.
 */
public class SmartResponse extends ExtensibleObject {

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


    /**
     * Get the response body as an Array of Strings, one for each line.
     *
     * @return the Array, which will be empty if the body was empty.
     */
    public String[] getLines() {
        String body = (String) anyObject.get("body");
        if (body == null || body.isEmpty()) {
            return new String[0];
        } else {
            return body.split(LINE_BREAK_PATTERN);
        }
    }

}
