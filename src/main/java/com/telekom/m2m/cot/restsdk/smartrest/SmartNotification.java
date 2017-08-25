package com.telekom.m2m.cot.restsdk.smartrest;


/**
 * A SmartNotification contains one line of the response data that was received from the server and the X-Id
 *
 * The data will be a CSV-String, not including the messageId. Everything else is up to the response
 * templates that were used to turn the servers' JSON responses into SmartREST lines.
 */
public class SmartNotification {

    private int messageId;
    private String data;
    private String xId;

    public SmartNotification(String line, String xId) {
        String[] parts = line.split(",", 2);
        messageId = Integer.parseInt(parts[0]);
        data = parts[1];
        this.xId = xId;
    }


    public int getMessageId() {
        return messageId;
    }

    public String getData() {
        return data;
    }

    public String getxId() {
        return xId;
    }

}
