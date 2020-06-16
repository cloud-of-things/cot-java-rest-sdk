package com.telekom.m2m.cot.restsdk.library.sensor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.telekom.m2m.cot.restsdk.library.Fragment;

import java.util.Arrays;


public class RelayArray implements Fragment {

    private final Relay[] arrayOfRelays;

    
    public RelayArray(Relay[] arrayOfRelays) {
        
        this.arrayOfRelays = Arrays.copyOf(arrayOfRelays, arrayOfRelays.length);
    }
    
    public Relay[] getArrayOfRelays() {
        
        return Arrays.copyOf(arrayOfRelays, arrayOfRelays.length);
    }

    
    @Override
    public String getId() {
        return "c8y_RelayArray";
    }

    @Override
    public JsonElement getJson() {
        JsonArray array = new JsonArray();
        for (Relay r : arrayOfRelays) {
            array.add(r.getRelayState().name());
        }
        return array;
    }
}