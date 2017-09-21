package com.telekom.m2m.cot.restsdk.library.sensor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

/**
 * Currently, barely more than a place holder.
 * 
 * @author ozanarslan
 *
 */
public class RelayArray implements Fragment{

    

    @Override
    public String getId() {
        return "c8y_RelayArray";
    }

    @Override
    public JsonElement getJson() {
        return new JsonObject();
    }
}