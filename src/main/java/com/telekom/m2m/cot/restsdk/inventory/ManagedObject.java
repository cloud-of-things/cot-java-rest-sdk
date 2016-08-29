package com.telekom.m2m.cot.restsdk.inventory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by breucking on 30.01.16.
 */
public class ManagedObject extends ExtensibleObject {

    public ManagedObject() {
        super();
    }

    public ManagedObject(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }

    public String getId() {
        return (String) anyObject.get("id");
    }

    public String getName() {
        return (String) anyObject.get("name");
    }

    public void setName(String name) {
        anyObject.put("name", name);
    }

    public void setId(String id) {
        anyObject.put("id", id);
    }


    public String getType() {
        return (String) anyObject.get("type");
    }

    public Date getLastUpdated() {
        return (Date) anyObject.get("lastUpdated");
    }

    public Set getChildDevices() {
        HashSet hs = new HashSet();
        JsonObject jso = (JsonObject) anyObject.get("childDevices");
        if (jso == null) {
            return hs;
        }
        if (!jso.has("references")) {
            return hs;
        }

        JsonArray refs = jso.getAsJsonArray("references");
        for (JsonElement element : refs) {
            hs.add("element");
        }

        return hs;
    }
}
