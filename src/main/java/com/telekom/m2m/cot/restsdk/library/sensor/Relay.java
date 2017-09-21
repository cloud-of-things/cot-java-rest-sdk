package com.telekom.m2m.cot.restsdk.library.sensor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

public class Relay implements Fragment{

    
private String relayState;

public Relay (String relayState){
    
    this.relayState=relayState;
    
}

public String getRelayState(){
    return relayState;
}

    @Override
    public String getId() {
        return "c8y_Relay";
    }

    @Override
    public JsonElement getJson() {
        
        
        JsonObject relayObject = new JsonObject();
        relayObject.addProperty("relayState", relayState);
        
        return relayObject;


    }
}

