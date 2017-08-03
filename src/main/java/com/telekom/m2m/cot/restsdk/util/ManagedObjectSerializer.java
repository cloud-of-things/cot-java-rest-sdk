package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.*;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObjectReferenceCollection;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Patrick Steinert on 31.01.16.
 */
public class ManagedObjectSerializer implements JsonSerializer<ExtensibleObject>, JsonDeserializer<ExtensibleObject> {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    private static ArrayList<String> blacklist = new ArrayList<String>() {{
        add("id");
        add("self");
        add("lastUpdated");
        add("childDevices");
        add("childAssets");
        add("deviceParents");
        add("assetParents");
    }};

    public JsonElement serialize(ExtensibleObject src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        if (src == null) {
            return null;
        }

        JsonObject object = new JsonObject();
        Map<String, Object> attributes = src.getAttributes();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            // Omit blacklisted keys
            if (blacklist.contains(key)) {
                continue;
            }

            object.add(key, context.serialize(value));
        }
        return object;
    }

    public ExtensibleObject deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject object = jsonElement.getAsJsonObject();
        ManagedObject mo = new ManagedObject();

        Iterator<Map.Entry<String, JsonElement>> objectElementIterator = object.entrySet().iterator();
        while (objectElementIterator.hasNext()) {
            Map.Entry<String, JsonElement> element = objectElementIterator.next();

            JsonPrimitive tmp;
            if (element.getValue().isJsonPrimitive()) {
                tmp = (JsonPrimitive) element.getValue();
                Object converted = null;
                if (tmp.isBoolean()) {
                    converted = tmp.getAsBoolean();
                } else if (tmp.isString()) {
                    try {
                        converted = sdf.parse(tmp.getAsString());
                    } catch (ParseException e) {
                        converted = tmp.getAsString();
                    }

                } else if (tmp.isNumber()) {
                    converted = tmp.getAsNumber();
                }
                mo.set(element.getKey(), converted);
            } else if (element.getValue().isJsonObject()) {
                String key = element.getKey();
                switch (key) {
                    case "childDevices" :
                    case "childAssets" :
                    case "deviceParents" :
                    case "assetParents" :
                        mo.set(key, jsonDeserializationContext.deserialize(element.getValue(), ManagedObjectReferenceCollection.class));
                        break;
                    default :
                        mo.set(element.getKey(), jsonDeserializationContext.deserialize(element.getValue(), JsonObject.class));
                }
            }

        }

        return mo;
    }
}
