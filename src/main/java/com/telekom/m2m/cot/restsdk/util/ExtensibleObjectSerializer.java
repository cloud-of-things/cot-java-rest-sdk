package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.*;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Patrick Steinert on 31.01.16.
 */
public class ExtensibleObjectSerializer implements JsonSerializer<ExtensibleObject>, JsonDeserializer<ExtensibleObject> {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    public JsonElement serialize(ExtensibleObject src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        if (src == null)
            return null;
        else {
            JsonObject object = new JsonObject();
            Map<String, Object> attributes = src.getAttributes();
            Set<String> keys = attributes.keySet();
            for (String key : keys) {
                if (key.equals("source")) {
                    Object source = attributes.get(key);
                    if (source instanceof ManagedObject) {
                        JsonPrimitive primitive = new JsonPrimitive(((ManagedObject) source).getId());
                        JsonObject sourceObject = new JsonObject();
                        sourceObject.add("id", primitive);
                        object.add(key, sourceObject);
                        continue;
                    }
                }
                object.add(key, context.serialize(attributes.get(key)));
            }
            return object;
        }
    }

    public ExtensibleObject deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject object = jsonElement.getAsJsonObject();
        ExtensibleObject mo = new ExtensibleObject();

        Iterator<Map.Entry<String, JsonElement>> objectElementIterator = object.entrySet().iterator();
        while (objectElementIterator.hasNext()) {
            Map.Entry<String, JsonElement> element = objectElementIterator.next();

            try {
                Class foundClass = Class.forName(element.getKey().replace('_', '.'));
                if (foundClass != null) {
                    mo.set(element.getKey(), jsonDeserializationContext.deserialize(element.getValue(), foundClass));
                    continue;
                }
            } catch (ClassNotFoundException e) {
            }

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
            } else if (element.getValue().isJsonObject())
                mo.set(element.getKey(), jsonDeserializationContext.deserialize(element.getValue(), JsonObject.class));

        }

        return mo;
    }
}
