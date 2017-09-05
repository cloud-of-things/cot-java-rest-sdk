package com.telekom.m2m.cot.restsdk.util;

import java.lang.reflect.Type;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.Map;
import java.time.ZonedDateTime;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;

/**
 * Created by Patrick Steinert on 31.01.16.
 */
public class ExtensibleObjectSerializer implements JsonSerializer<ExtensibleObject>, JsonDeserializer<ExtensibleObject> {

    private DateTimeFormatter oneLetterISO8601TimeZoneDTF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    private DateTimeFormatter twoLetterISO8601TimeZoneDTF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXX");
    private DateTimeFormatter threeLetterISO8601TimeZoneDTF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    @Override
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
            if ("source".equals(key) && (value instanceof ManagedObject)) {
                JsonPrimitive primitive = new JsonPrimitive(((ManagedObject) value).getId());
                JsonObject sourceObject = new JsonObject();
                sourceObject.add("id", primitive);
                object.add(key, sourceObject);
                continue;
            }
            object.add(key, context.serialize(value));
        }
        return object;

    }

    @Override
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
                        String tmpString = tmp.getAsString();
                        ZonedDateTime zonedDateTime = null;
                        // in the CoT plattform the stored date time objects has different formatted time zones
                        switch(tmpString.length()) {
                            case 24:
                                // e.g. 2017-09-05T17:19:32.601Z
                            case 26:
                                // e.g. 2017-09-05T17:19:32.601+02
                                zonedDateTime = ZonedDateTime.parse(tmp.getAsString(), oneLetterISO8601TimeZoneDTF);
                                break;
                            case 28:
                                // e.g. 2017-09-05T17:19:32.601+0200
                                zonedDateTime = ZonedDateTime.parse(tmp.getAsString(), twoLetterISO8601TimeZoneDTF);
                                break;
                            case 29:
                                // e.g. 2017-09-05T17:19:32.601+02:00
                                zonedDateTime = ZonedDateTime.parse(tmp.getAsString(), threeLetterISO8601TimeZoneDTF);
                                break;
                            default:
                                converted = tmp.getAsString();
                        }
                        if(zonedDateTime != null) {
                            converted = Date.from(zonedDateTime.toInstant());
                        }
                    } catch (DateTimeParseException e) {
                        converted = tmp.getAsString();
                    }

                } else if (tmp.isNumber()) {
                    converted = tmp.getAsNumber();
                }
                mo.set(element.getKey(), converted);
            } else if (element.getValue().isJsonObject()) {
                mo.set(element.getKey(), jsonDeserializationContext.deserialize(element.getValue(), type));
            }else if (element.getValue().isJsonArray()) {
                String key = element.getKey();
                // Some of the library fragments are arrays, but they don't need special treatment because all
                // fragments are stored as simple JsonElements in the ExtensibleObject, and not as themselves.
                // We just list them for documentation purposes, in case someone wants to change that in the future.
                switch (key) {
                    case "c8y_SoftwareList":
                    case "c8y_SupportedOperations":
                    default:
                        mo.set(key, element.getValue());
                }
            }
        }

        return mo;
    }
}
