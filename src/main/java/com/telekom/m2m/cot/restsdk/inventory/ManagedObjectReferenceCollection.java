package com.telekom.m2m.cot.restsdk.inventory;

import java.util.List;

/**
 * Created by breucking on 29.08.16.
 */
public class ManagedObjectReferenceCollection {

    private final List<ManagedObjectReference> mos;

    public ManagedObjectReferenceCollection(List<ManagedObjectReference> mos) {
        this.mos = mos;
    }

    public Iterable<ManagedObjectReference> get(int pageSize) {
        return mos;
    }

}
