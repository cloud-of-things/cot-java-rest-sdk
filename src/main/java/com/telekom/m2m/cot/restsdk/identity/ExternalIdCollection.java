package com.telekom.m2m.cot.restsdk.identity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.JsonArrayPagination;

/**
 * Represents a pageable ExternalId collection.
 *
 * @since 0.3.0
 * Created by Patrick Steinert on 19.12.16.
 */
public class ExternalIdCollection extends JsonArrayPagination {

    private static final String COLLECTION_CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.externalIdCollection+json;charset=UTF-8;ver=0.9";
    private static final String COLLECTION_ELEMENT_NAME = "externalIds";

    /**
     * Internal contstructor to create an ExternalIdCollection.
     * Use {@link IdentityApi} to get ExternalIdCollection.
     *
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     * @param relativeApiUrl          relative url of the REST API without leading slash.
     * @param gson                    the necessary json De-/serializer.
     * @param filterBuilder           the build criteria or null if all items should be retrieved.
     * @param pageSize                max number of retrieved elements per page.
     */
    ExternalIdCollection(final CloudOfThingsRestClient cloudOfThingsRestClient,
                    final String relativeApiUrl,
                    final Gson gson,
                    final Filter.FilterBuilder filterBuilder,
                    final int pageSize) {
        super(cloudOfThingsRestClient, relativeApiUrl, gson, COLLECTION_CONTENT_TYPE, COLLECTION_ELEMENT_NAME, filterBuilder, pageSize);
    }

    /**
     * Retrieves the current page.
     * <p>
     * Retrieves the ExternalIds influenced by filters set in construction.
     *
     * @return array of found ExternalIds
     */
    public ExternalId[] getExternalIds() {
        final JsonArray jsonExternalIds = getJsonArray();

        if (jsonExternalIds != null) {
            final ExternalId[] arrayOfExternalIds = new ExternalId[jsonExternalIds.size()];
            for (int i = 0; i < jsonExternalIds.size(); i++) {
                JsonElement jsonExternalId = jsonExternalIds.get(i).getAsJsonObject();
                final ExternalId externalId = gson.fromJson(jsonExternalId, ExternalId.class);
                arrayOfExternalIds[i] = externalId;
            }
            return arrayOfExternalIds;
        } else {
            return new ExternalId[0];
        }
    }
}
