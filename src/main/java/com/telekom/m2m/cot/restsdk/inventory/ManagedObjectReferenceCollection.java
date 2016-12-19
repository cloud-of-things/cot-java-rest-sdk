package com.telekom.m2m.cot.restsdk.inventory;

import java.util.List;

/**
 * Created by Patrick Steinert on 29.08.16.
 */
public class ManagedObjectReferenceCollection {

    private final List<ManagedObjectReference> mos;
    private String self;

    public ManagedObjectReferenceCollection(List<ManagedObjectReference> mos, String self) {
        this.mos = mos;
        this.self = self;
    }

    public Iterable<ManagedObjectReference> get(int pageSize) {
        return mos;
    }

    public String getSelf() {
        return self;
    }
}
