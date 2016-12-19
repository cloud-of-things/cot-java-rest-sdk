package com.telekom.m2m.cot.restsdk.measurement;

import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;

import java.util.Date;

/**
 * Created by Patrick Steinert on 06.02.16.
 */
public class Measurement extends ExtensibleObject {

    public Measurement() {
        super();
    }

    public Measurement(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }

    public void setId(String id) {
        anyObject.put("id", id);
    }

    public void setTime(Date time) {
        anyObject.put("time", time);
    }

    public String getId() {
        return (String) anyObject.get("id");
    }

    public Date getTime() {
        return (Date) anyObject.get("time");
    }

    public void setType(String type) {
        anyObject.put("type", type);
    }

    public String getType() {
        return (String) anyObject.get("type");
    }


    public void setSource(ManagedObject managedObject) {
        anyObject.put("source", managedObject);
    }
}
