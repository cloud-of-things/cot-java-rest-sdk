package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.*;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObjectReference;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObjectReferenceCollection;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Patrick Steinert on 03.09.16.
 */
public class ManagedObjectReferenceCollectionSerializer implements
        JsonSerializer<ManagedObjectReferenceCollection>,
        JsonDeserializer<ManagedObjectReferenceCollection> {

    public ManagedObjectReferenceCollection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        ArrayList<ManagedObjectReference> mos = new ArrayList<ManagedObjectReference>();
        String selfRef = null;
        /*
         * To Check:
         * 1. Is Object?
         * 2. Contains references
         */
        if (json.isJsonObject()) {
            JsonObject jObject = json.getAsJsonObject();
            if (jObject.has("references") && jObject.get("references").isJsonArray()) {
                JsonArray references = jObject.get("references").getAsJsonArray();
                for (JsonElement jReference : references) {
                    ManagedObjectReference exO = jsonDeserializationContext.deserialize(jReference, ManagedObjectReference.class);
                    mos.add(exO);
                }
            }
            if (jObject.has("self") && jObject.get("self").isJsonPrimitive()) {
                JsonPrimitive jsonSelf = jObject.get("self").getAsJsonPrimitive();
                selfRef = jsonSelf.getAsString();
            }
        }

        ManagedObjectReferenceCollection morc = new ManagedObjectReferenceCollection(mos, selfRef);

        return morc;
    }

    public JsonElement serialize(ManagedObjectReferenceCollection src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null)
            return null;
        else {
            // TODO: This needs more work!
            JsonArray references = new JsonArray();

            Iterable<ManagedObjectReference> mors = src.get(1);
            for (ManagedObjectReference mor : mors) {
                references.add(context.serialize(mor));
            }
            return references;
        }
    }
}
