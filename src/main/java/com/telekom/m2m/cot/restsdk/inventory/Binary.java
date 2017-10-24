package com.telekom.m2m.cot.restsdk.inventory;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

import java.util.Arrays;


/**
 * A binary (file) that can be stored in the CoT.
 */
public class Binary extends ManagedObject {


    public Binary(String id) {
        super();
        setId(id);
        anyObject.put("c8y_IsBinary", "");
    }

    /**
     * Normal constructor.
     *
     * @param name filename of this binary
     * @param type the content type of this binary
     * @param data the bytes of this binary
     */
    public Binary(String name, String type, byte[] data) {
        super();
        anyObject.put("name", name);
        anyObject.put("type", type);
        anyObject.put("data", Arrays.copyOf(data, data.length));
        anyObject.put("c8y_IsBinary", "");
    }

    /**
     * Constructor for deserialization.
     *
     * @param extensibleObject the ExtensibleObject created by deserialization.
     */
    public Binary(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }


    public String getName() {
        return (String)anyObject.get("name");
    }


    public String getType() {
        return (String)anyObject.get("type");
    }


    /**
     * Get a copy of the binary data.
     *
     * @return a copy of the binary data.
     */
    public byte[] getData() {
        byte[] data = (byte[])anyObject.get("data");
        return Arrays.copyOf(data, data.length);
    }

    /**
     * Set the binary data.
     *
     * @param data byte[] which will be copied.
     */
    public void setData(byte[] data) {
        anyObject.put("data", Arrays.copyOf(data, data.length));
    }


    /**
     * The real size of this binary, in bytes.
     * @return size in bytes
     */
    public int size() {
        byte[] data = (byte[])anyObject.get("data");
        return data.length;
    }

}
