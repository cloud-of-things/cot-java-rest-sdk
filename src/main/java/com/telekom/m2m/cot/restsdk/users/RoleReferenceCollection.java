package com.telekom.m2m.cot.restsdk.users;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.IterableObjectPagination;

/**
 * The class that defines methods related to the role reference collections.
 * Role reference collections are a group of references to the roles. Created by
 * Ozan Arslan on 27.07.2017
 */
public class RoleReferenceCollection extends IterableObjectPagination<RoleReference> {

    private static final String COLLECTION_CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.roleReferenceCollection+json;ver=0.9";
    private static final String COLLECTION_ELEMENT_NAME = "references";

    /**
     * Creates a RoleCollection. Use {@link UserApi} to get RoleCollections.
     *
     * @param cloudOfThingsRestClient
     *            the necessary REST client to send requests to the CoT.
     * @param relativeApiUrl
     *            relative url of the REST API without leading slash.
     * @param gson
     *            the necessary json De-/serializer.
     * @param filterBuilder
     *            the build criteria or null if all items should be retrieved.
     */
    RoleReferenceCollection(
        final CloudOfThingsRestClient cloudOfThingsRestClient,
        final String relativeApiUrl,
        final Gson gson,
        final Filter.FilterBuilder filterBuilder
    ) {
        super(
            roleReferenceJson -> new RoleReference(gson.fromJson(roleReferenceJson, ExtensibleObject.class)),
            cloudOfThingsRestClient,
            relativeApiUrl,
            gson,
            COLLECTION_CONTENT_TYPE,
            COLLECTION_ELEMENT_NAME,
            filterBuilder
        );
    }

    /**
     * Retrieves the Roles influenced by filters set in construction.
     *
     * @return array of found Roles
     */
    public RoleReference[] getRoleReferences() {
        final JsonArray jsonRoleReferences = getJsonArray();

        if (jsonRoleReferences != null) {
            final RoleReference[] arrayOfRoleReferences = new RoleReference[jsonRoleReferences.size()];
            for (int i = 0; i < jsonRoleReferences.size(); i++) {
                JsonElement jsonRole = jsonRoleReferences.get(i).getAsJsonObject();
                final RoleReference rolereference = objectMapper.apply(jsonRole);
                arrayOfRoleReferences[i] = rolereference;
            }
            return arrayOfRoleReferences;
        } else {
            return new RoleReference[0];
        }
    }
}
