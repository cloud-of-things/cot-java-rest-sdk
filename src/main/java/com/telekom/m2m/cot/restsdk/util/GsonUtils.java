package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.telekom.m2m.cot.restsdk.alarm.Alarm;
import com.telekom.m2m.cot.restsdk.audit.AuditRecord;
import com.telekom.m2m.cot.restsdk.devicecontrol.BulkOperation;
import com.telekom.m2m.cot.restsdk.devicecontrol.NewDeviceRequest;
import com.telekom.m2m.cot.restsdk.devicecontrol.Operation;
import com.telekom.m2m.cot.restsdk.devicecontrol.Progress;
import com.telekom.m2m.cot.restsdk.event.Event;
import com.telekom.m2m.cot.restsdk.inventory.Binary;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObjectReference;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObjectReferenceCollection;
import com.telekom.m2m.cot.restsdk.measurement.Measurement;
import com.telekom.m2m.cot.restsdk.retentionrule.RetentionRule;
import com.telekom.m2m.cot.restsdk.users.CurrentUser;
import com.telekom.m2m.cot.restsdk.users.Group;
import com.telekom.m2m.cot.restsdk.users.Role;
import com.telekom.m2m.cot.restsdk.users.User;

/**
 * Created by Patrick Steinert on 31.01.16.
 */
public class GsonUtils {

    public static Gson createGson() {
        return createGson(false);
    }

    public static Gson createGson(boolean pretty) {
        final ExtensibleObjectSerializer extensibleObjectSerializer = new ExtensibleObjectSerializer();
        GsonBuilder builder =
            new GsonBuilder()
                .registerTypeAdapter(ManagedObject.class, new ManagedObjectSerializer())
                .registerTypeAdapter(Event.class, extensibleObjectSerializer)
                .registerTypeAdapter(Alarm.class, extensibleObjectSerializer)
                .registerTypeAdapter(AuditRecord.class, extensibleObjectSerializer)
                .registerTypeAdapter(Operation.class, extensibleObjectSerializer)
                .registerTypeAdapter(NewDeviceRequest.class, extensibleObjectSerializer)
                .registerTypeAdapter(Measurement.class, extensibleObjectSerializer)
                .registerTypeAdapter(ExtensibleObject.class, extensibleObjectSerializer)
                .registerTypeAdapter(ManagedObjectReferenceCollection.class,
                                     new ManagedObjectReferenceCollectionSerializer())
                .registerTypeAdapter(ManagedObjectReference.class, new ManagedObjectReferenceSerializer())
                .registerTypeAdapter(User.class, extensibleObjectSerializer)
                .registerTypeAdapter(Group.class, extensibleObjectSerializer)
                .registerTypeAdapter(CurrentUser.class, extensibleObjectSerializer)
                .registerTypeAdapter(Role.class, extensibleObjectSerializer)
                .registerTypeAdapter(RetentionRule.class, extensibleObjectSerializer)
                .registerTypeAdapter(BulkOperation.class, extensibleObjectSerializer)
                .registerTypeAdapter(Progress.class, extensibleObjectSerializer)
                .registerTypeAdapter(Binary.class, new BinarySerializer())

                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");

        if (pretty) {
            builder.setPrettyPrinting();
        }

        return builder.create();
    }

}
