package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.telekom.m2m.cot.restsdk.inventory.Binary;

import java.lang.reflect.Type;

public class BinarySerializer extends ExtensibleObjectSerializer {

    public ExtensibleObject deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return new Binary(super.deserialize(jsonElement, type, jsonDeserializationContext));
    }

}
