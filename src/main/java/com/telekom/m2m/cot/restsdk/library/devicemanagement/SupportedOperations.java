package com.telekom.m2m.cot.restsdk.library.devicemanagement;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

import java.util.Arrays;

/**
 * c8y_Firmware
 * c8y_SoftwareList
 * c8y_Configuration
 * c8y_SendConfiguration
 * c8y_Restart
 * c8y_Command
 * c8y_LogfileRequest
 */
public class SupportedOperations implements Fragment {

    private String[] operations;


    public SupportedOperations(String... operations) {
        this.operations = operations.clone();
    }


    public String[] getOperations() {
        return Arrays.copyOf(operations, operations.length);
    }


    @Override
    public String getId() {
        return "c8y_SupportedOperations";
    }

    @Override
    public JsonElement getJson() {
        JsonArray array = new JsonArray();
        for (String operation : operations) {
            array.add(operation);
        }

        JsonObject object = new JsonObject();
        object.add(getId(), array);
        return object;
    }

}
