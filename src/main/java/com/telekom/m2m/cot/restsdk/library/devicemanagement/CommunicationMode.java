package com.telekom.m2m.cot.restsdk.library.devicemanagement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

/**
 * The device managed object should contain this fragment to send operations via SMS.
 * Alternative, the operation can contain the property 'deliveryType'.
 */
public class CommunicationMode implements Fragment {

    // e.g. 'SMS'
    private String mode;

    /**
     * Constructor with communication mode as argument
     *
     * @param mode String communication mode e.g. "SMS"
     */
    public CommunicationMode(String mode) {
        this.mode = mode;
    }

    /**
     * Getter for communication mode value
     *
     * @return String communication mode value, e.g. "SMS"
     */
    public String getCommunicationMode() {
        return mode;
    }


    @Override
    public String getId() {
        return "c8y_CommunicationMode";
    }

    @Override
    public JsonElement getJson() {
        JsonObject communicationMode = new JsonObject();
        communicationMode.addProperty("mode", mode);

        return communicationMode;
    }

}
