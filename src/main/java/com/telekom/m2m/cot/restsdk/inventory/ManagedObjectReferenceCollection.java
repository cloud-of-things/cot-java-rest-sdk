package com.telekom.m2m.cot.restsdk.inventory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Patrick Steinert on 29.08.16.
 */
public class ManagedObjectReferenceCollection {

    private final List<ManagedObjectReference> mos;
    private final String self;

    public ManagedObjectReferenceCollection(List<ManagedObjectReference> mos, String self) {
        this.mos = mos;
        this.self = self;
    }

    public Iterable<ManagedObjectReference> get() {
        return mos;
    }

    public String getSelf() {
        return self;
    }


    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ManagedObjectReferenceCollection[");
        builder.append(mos.stream().map(ManagedObjectReference::getSelf).collect(Collectors.joining(", ")));
        builder.append("]");
        return builder.toString();
    }

}
