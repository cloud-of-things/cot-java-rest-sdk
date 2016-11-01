package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.*;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObjectReference;

import java.lang.reflect.Type;

/**
 * Created by breucking on 03.09.16.
 */
public class ManagedObjectReferenceSerializer implements
        JsonSerializer<ManagedObjectReference>,
        JsonDeserializer<ManagedObjectReference> {

    public ManagedObjectReference deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement.isJsonObject()) {
            JsonElement jMO = jsonElement.getAsJsonObject().get("managedObject");
            JsonElement selfString = jsonElement.getAsJsonObject().get("self");
            ManagedObject mo = jsonDeserializationContext.deserialize(jMO, ManagedObject.class);
            return new ManagedObjectReference(mo, selfString.getAsString());
        }
        return new ManagedObjectReference();
    }

    public JsonElement serialize(ManagedObjectReference src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject reducedMo = new JsonObject();
        reducedMo.add("id", new JsonPrimitive(src.getManagedObject().getId()));
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("managedObject", reducedMo);
        return jsonObject;
    }
}
