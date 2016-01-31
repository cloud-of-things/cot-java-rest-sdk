package com.telekom.m2m.cot.restsdk.inventory;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

/**
 * Created by breucking on 30.01.16.
 */
public class ManagedObject extends ExtensibleObject {

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


}
