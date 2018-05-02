package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;

import java.util.Objects;

public class IterableObjectPagination<T> extends JsonArrayPagination {
    /**
     * Creates a pagination with default page size.
     *
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     * @param relativeApiUrl          relative url of the REST API without leading slash.
     * @param gson                    the necessary json De-/serializer.
     * @param contentType             the Content-Type of the JSON Object.
     * @param collectionElementName   json element name which contains an array of JSON Objects.
     * @param filterBuilder           the build criteria or null if all items should be retrieved.
     */
    public IterableObjectPagination(
        final CloudOfThingsRestClient cloudOfThingsRestClient,
        final String relativeApiUrl,
        final Gson gson,
        final String contentType,
        final String collectionElementName,
        final Filter.FilterBuilder filterBuilder
    ) {
        super(
            Objects.requireNonNull(cloudOfThingsRestClient),
            Objects.requireNonNull(relativeApiUrl),
            Objects.requireNonNull(gson),
            Objects.requireNonNull(contentType),
            Objects.requireNonNull(collectionElementName),
            Objects.requireNonNull(filterBuilder)
        );
    }

    /**
     * Creates a pagination with custom page size.
     *
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     * @param relativeApiUrl          relative url of the REST API without leading slash.
     * @param gson                    the necessary json De-/serializer.
     * @param contentType             the Content-Type of the JSON Object.
     * @param collectionElementName   json element name which contains an array of JSON Objects.
     * @param filterBuilder           the build criteria or null if all items should be retrieved.
     * @param pageSize                max number of retrieved elements per page.
     */
    public IterableObjectPagination(
        final CloudOfThingsRestClient cloudOfThingsRestClient,
        final String relativeApiUrl,
        final Gson gson,
        final String contentType,
        final String collectionElementName,
        final Filter.FilterBuilder filterBuilder,
        final int pageSize
    ) {
        super(
            Objects.requireNonNull(cloudOfThingsRestClient),
            Objects.requireNonNull(relativeApiUrl),
            Objects.requireNonNull(gson),
            Objects.requireNonNull(contentType),
            Objects.requireNonNull(collectionElementName),
            Objects.requireNonNull(filterBuilder),
            pageSize
        );
    }
}
