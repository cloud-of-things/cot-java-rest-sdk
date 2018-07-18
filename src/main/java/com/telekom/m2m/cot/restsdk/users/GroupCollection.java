package com.telekom.m2m.cot.restsdk.users;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.IterableObjectPagination;

/**
 * Class that defines the methods of group collection. Group collections are
 * objects that hold several groups. They define methods on a collection of
 * groups.Created by Ozan Arslan on 13.07.2017
 */
public class GroupCollection extends IterableObjectPagination<Group> {

    private static final String COLLECTION_CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.groupCollection+json;ver=0.9";
    private static final String COLLECTION_ELEMENT_NAME = "groups";

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
    GroupCollection(
        final CloudOfThingsRestClient cloudOfThingsRestClient,
        final String relativeApiUrl,
        final Gson gson,
        final Filter.FilterBuilder filterBuilder
    ) {
        super(
            groupJson -> new Group(gson.fromJson(groupJson, ExtensibleObject.class)),
            cloudOfThingsRestClient,
            relativeApiUrl,
            gson,
            COLLECTION_CONTENT_TYPE,
            COLLECTION_ELEMENT_NAME,
            filterBuilder
        );
    }

    /**
     * Retrieves the Groups influenced by filters set in construction.
     *
     * @return array of found Groups, or null if there are no Groups.
     */
    public Group[] getGroups() {
        final JsonArray jsonGroups = getJsonArray();

        if (jsonGroups != null) {
            final Group[] arrayOfGroups = new Group[jsonGroups.size()];
            for (int i = 0; i < jsonGroups.size(); i++) {
                JsonElement jsonGroup = jsonGroups.get(i);
                final Group group = objectMapper.apply(jsonGroup);
                arrayOfGroups[i] = group;
            }
            return arrayOfGroups;
        } else {
            
            return new Group[0];
        }
    }
}
