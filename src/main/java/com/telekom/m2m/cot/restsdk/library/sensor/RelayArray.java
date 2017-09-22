package com.telekom.m2m.cot.restsdk.library.sensor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.telekom.m2m.cot.restsdk.library.Fragment;

/**
 * Currently, barely more than a place holder.
 * 
 * @author ozanarslan
 *
 */
public class RelayArray implements Fragment{

    private Relay arrayOfRelays[];

    
    public RelayArray(Relay arrayOfRelays[]){
        
        this.arrayOfRelays=arrayOfRelays;
    }
    
    public Relay[] getArrayOfRelays(){
        
        return arrayOfRelays;
    }
    
    @Override
    public String getId() {
        return "c8y_RelayArray";
    }

    @Override
    public JsonElement getJson() {
        JsonArray array = new JsonArray();
        for (Relay r:arrayOfRelays){
        array.add(r.getRelayState().toString());
        }
        return array;
    }
}