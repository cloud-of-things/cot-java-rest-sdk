package com.telekom.m2m.cot.restsdk.users;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.IterableObjectPagination;

/**
 * Class that defines the methods of role collection. A role collection is a
 * group of roles. Created by Ozan Arslan on 13.07.2017
 */
public class RoleCollection extends IterableObjectPagination<Role> {

    private static final String COLLECTION_CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.roleCollection+json;ver=0.9";
    private static final String COLLECTION_ELEMENT_NAME = "roles";

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
    RoleCollection(
        final CloudOfThingsRestClient cloudOfThingsRestClient,
        final String relativeApiUrl,
        final Gson gson,
        final Filter.FilterBuilder filterBuilder
    ) {
        super(
            roleJson -> new Role(gson.fromJson(roleJson, ExtensibleObject.class)),
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
    public Role[] getRoles() {
        final JsonArray jsonRoles = getJsonArray();

        if (jsonRoles != null) {
            final Role[] arrayOfRoles = new Role[jsonRoles.size()];
            for (int i = 0; i < jsonRoles.size(); i++) {
                JsonElement jsonRole = jsonRoles.get(i).getAsJsonObject();
                final Role role = objectMapper.apply(jsonRole);
                arrayOfRoles[i] = role;
            }
            return arrayOfRoles;
        } else {
            return new Role[0];
        }
    }
}
