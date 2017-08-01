package com.telekom.m2m.cot.restsdk.users;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.JsonArrayPagination;

/**
 * Class that defines the methods of group collection. Created by Ozan Arslan on
 * 13.07.2017
 */

public class GroupCollection extends JsonArrayPagination {

	private static final String COLLECTION_CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.groupCollection+json;ver=0.9";
	private static final String COLLECTION_ELEMENT_NAME = "groups";

	/**
	 * Creates a GroupCollection. Use {@link UsersApi} to get GroupCollections.
	 *
	 * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
	 * @param relativeApiUrl          relative url of the REST API without leading slash.
	 * @param gson                    the necessary json De-/serializer.
	 * @param filterBuilder           the build criteria or null if all items should be retrieved.
	 */
	GroupCollection(final CloudOfThingsRestClient cloudOfThingsRestClient,
					final String relativeApiUrl,
					final Gson gson,
					final Filter.FilterBuilder filterBuilder) {
		super(cloudOfThingsRestClient, relativeApiUrl, gson, COLLECTION_CONTENT_TYPE, COLLECTION_ELEMENT_NAME, filterBuilder);
	}

	/**
	 * Retrieves the Groups influenced by filters set in construction.
	 *
	 * @return array of found Groups
	 */
	public Group[] getGroups() {
		final JsonArray jsonGroups = getJsonArray();

		if (jsonGroups != null) {
			final Group[] arrayOfGroups = new Group[jsonGroups.size()];
			for (int i = 0; i < jsonGroups.size(); i++) {
				JsonElement jsonGroup = jsonGroups.get(i).getAsJsonObject();
				final Group group = new Group(gson.fromJson(jsonGroup, ExtensibleObject.class));
				arrayOfGroups[i] = group;
			}
			return arrayOfGroups;
		} else {
			return null;
		}
	}
}
