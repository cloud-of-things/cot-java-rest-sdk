package com.telekom.m2m.cot.restsdk.users;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.JsonArrayPagination;

/**
 * The class that defines the methods on a collection of group references.
 * Created by Ozan Arslan on 27.07.2017
 */
public class GroupReferenceCollection extends JsonArrayPagination {

    private static final String COLLECTION_CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.groupReferenceCollection+json;ver=0.9";
    private static final String COLLECTION_ELEMENT_NAME = "references";

    /**
     * Creates a GroupCollection. Use {@link UserApi} to get GroupCollections.
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
    GroupReferenceCollection(final CloudOfThingsRestClient cloudOfThingsRestClient, final String relativeApiUrl,
            final Gson gson, final Filter.FilterBuilder filterBuilder) {
        super(cloudOfThingsRestClient, relativeApiUrl, gson, COLLECTION_CONTENT_TYPE, COLLECTION_ELEMENT_NAME,
                filterBuilder);
    }

    /**
     * Retrieves the Groups influenced by filters set in construction.
     *
     * @return array of found Group references.
     */
    public GroupReference[] getGroupReferences() {
        final JsonArray jsonGroupReferences = getJsonArray();

        if (jsonGroupReferences != null) {
            final GroupReference[] arrayOfGroupReferences = new GroupReference[jsonGroupReferences.size()];
            for (int i = 0; i < jsonGroupReferences.size(); i++) {
                JsonElement jsonGroup = jsonGroupReferences.get(i).getAsJsonObject();
                final GroupReference groupReference = new GroupReference(
                        gson.fromJson(jsonGroup, ExtensibleObject.class));
                arrayOfGroupReferences[i] = groupReference;
            }
            return arrayOfGroupReferences;
        } else {
            return new GroupReference[0];
        }
    }
}
