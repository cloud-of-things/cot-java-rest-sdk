package com.telekom.m2m.cot.restsdk.users;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.JsonArrayPagination;


/**
 * The class that defines the operations on a collection of user references.
 * Created by Ozan Arslan on 27.07.2017
 */
public class UserReferenceCollection extends JsonArrayPagination {

    private static final String COLLECTION_CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.userReferenceCollection+json;ver=0.9";
    private static final String COLLECTION_ELEMENT_NAME = "references";

    /**
     * Creates a UserReferenceCollection. Use {@link UserApi} to get
     * UserCollections.
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
    UserReferenceCollection(final CloudOfThingsRestClient cloudOfThingsRestClient, final String relativeApiUrl,
            final Gson gson, final Filter.FilterBuilder filterBuilder) {
        super(cloudOfThingsRestClient, relativeApiUrl, gson, COLLECTION_CONTENT_TYPE, COLLECTION_ELEMENT_NAME,
                filterBuilder);
    }


    /**
     * Retrieves the Users influenced by filters set in construction.
     *
     * @return array of found Users
     */
    public UserReference[] getUserReferences() {
        final JsonArray jsonUserReferences = getJsonArray();

        if (jsonUserReferences != null) {
            final UserReference[] arrayOfUserReferences = new UserReference[jsonUserReferences.size()];
            for (int i = 0; i < jsonUserReferences.size(); i++) {
                JsonElement jsonGroup = jsonUserReferences.get(i).getAsJsonObject();
                final UserReference userreference = new UserReference(gson.fromJson(jsonGroup, ExtensibleObject.class));
                arrayOfUserReferences[i] = userreference;
            }
            return arrayOfUserReferences;
        } else {
            return new UserReference[0];
        }
    }
}
