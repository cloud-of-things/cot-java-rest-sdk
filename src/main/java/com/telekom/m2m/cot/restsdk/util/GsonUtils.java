package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.telekom.m2m.cot.restsdk.event.Event;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;

/**
 * Created by breucking on 31.01.16.
 */
public class GsonUtils {
    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(ManagedObject.class, new ManagedObjectSerializer())
                .registerTypeAdapter(Event.class, new ExtensibleObjectSerializer())
                .registerTypeAdapter(ExtensibleObject.class, new ExtensibleObjectSerializer())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
                .create();
    }
}
