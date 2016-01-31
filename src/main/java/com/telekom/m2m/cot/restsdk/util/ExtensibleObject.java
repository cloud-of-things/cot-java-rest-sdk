package com.telekom.m2m.cot.restsdk.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by breucking on 31.01.16.
 */
public abstract class ExtensibleObject {
    protected final Map<String, Object> anyObject = new HashMap<String, Object>();

    public void set(String property, Object object) {
        anyObject.put(property, object);
    }

    public Map<String, Object> getAttributes() {
        return anyObject;
    }

    public Object get(String property) {
        return anyObject.get(property);
    }

    public boolean has(String property) {
        return anyObject.containsKey(property);
    }
}
