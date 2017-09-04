package com.telekom.m2m.cot.restsdk.library;

import com.google.gson.JsonElement;

/**
 * A Fragment is a named JsonElement that can be part of a {@link com.telekom.m2m.cot.restsdk.inventory.ManagedObject}
 * or an Operation.
 */
public interface Fragment {

    String getId();

    JsonElement getJson();

}
