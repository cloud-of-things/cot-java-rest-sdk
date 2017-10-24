package com.telekom.m2m.cot.restsdk.library.sensor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.library.Fragment;

public class Relay implements Fragment {

    public enum State {
        OPEN, CLOSED
    }

    private State relayState;


    public Relay(State relayState) {

        this.relayState = relayState;
    }


    public State getRelayState() {
        return relayState;
    }

    @Override
    public String getId() {
        return "c8y_Relay";
    }

    @Override
    public JsonElement getJson() {

        JsonObject relayObject = new JsonObject();
        relayObject.addProperty("relayState", relayState.toString());

        return relayObject;
    }

}
