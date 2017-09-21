package com.telekom.m2m.cot.restsdk.inventory;


import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

import java.util.Arrays;

/**
 * A binary (file) that can be stored in the CoT.
 */
public class Binary extends ManagedObject {


    public Binary(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }


    public Binary(String name, String type, byte[] data) {
        super();
        anyObject.put("name", name);
        anyObject.put("type", type);
        anyObject.put("data", Arrays.copyOf(data, data.length));
        anyObject.put("c8y_IsBinary", "");
    }

    public String getName() {
        return (String)anyObject.get("name");
    }

    public String getType() {
        return (String)anyObject.get("type");
    }

    public byte[] getData() {
        byte[] data = (byte[])anyObject.get("data");
        return Arrays.copyOf(data, data.length);
    }

    public int size() {
        byte[] data = (byte[])anyObject.get("data");
        return data.length;
    }

}
