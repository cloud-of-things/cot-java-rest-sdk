package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.telekom.m2m.cot.restsdk.alarm.Alarm;
import com.telekom.m2m.cot.restsdk.audit.AuditRecord;
import com.telekom.m2m.cot.restsdk.devicecontrol.Operation;
import com.telekom.m2m.cot.restsdk.event.Event;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObjectReference;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObjectReferenceCollection;
import com.telekom.m2m.cot.restsdk.measurement.Measurement;
import com.telekom.m2m.cot.restsdk.users.CurrentUser;
import com.telekom.m2m.cot.restsdk.users.Group;
import com.telekom.m2m.cot.restsdk.users.Role;
import com.telekom.m2m.cot.restsdk.users.User;

/**
 * Created by Patrick Steinert on 31.01.16.
 */
public class GsonUtils {
	public static Gson createGson() {
		return new GsonBuilder().registerTypeAdapter(ManagedObject.class, new ManagedObjectSerializer())
				.registerTypeAdapter(Event.class, new ExtensibleObjectSerializer())
				.registerTypeAdapter(Alarm.class, new ExtensibleObjectSerializer())
				.registerTypeAdapter(AuditRecord.class, new ExtensibleObjectSerializer())
				.registerTypeAdapter(Operation.class, new ExtensibleObjectSerializer())
				.registerTypeAdapter(Measurement.class, new ExtensibleObjectSerializer())
				.registerTypeAdapter(ExtensibleObject.class, new ExtensibleObjectSerializer())
				.registerTypeAdapter(ManagedObjectReferenceCollection.class,
						new ManagedObjectReferenceCollectionSerializer())
				.registerTypeAdapter(ManagedObjectReference.class, new ManagedObjectReferenceSerializer())
				.registerTypeAdapter(User.class, new ExtensibleObjectSerializer())
				.registerTypeAdapter(Group.class, new ExtensibleObjectSerializer())
				.registerTypeAdapter(CurrentUser.class, new ExtensibleObjectSerializer())
				.registerTypeAdapter(Role.class, new ExtensibleObjectSerializer())

				.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").create();
	}
}
