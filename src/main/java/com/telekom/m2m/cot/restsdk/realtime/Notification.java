package com.telekom.m2m.cot.restsdk.realtime;

/**
 * The Notification is the object that holds the data/message that a listener receives from the channel that it is subscribed to.
 */
public  class Notification {

    private String data;

    public Notification(String line) {
        data = line;
    }


    public String getData() {
        return data;
    }

}