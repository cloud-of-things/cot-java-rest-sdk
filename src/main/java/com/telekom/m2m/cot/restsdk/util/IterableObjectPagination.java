package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
        @Nonnull final CloudOfThingsRestClient cloudOfThingsRestClient,
        @Nonnull final String relativeApiUrl,
        @Nonnull final Gson gson,
        @Nonnull final String contentType,
        @Nonnull final String collectionElementName,
        @Nullable final Filter.FilterBuilder filterBuilder
    ) {
        super(
            Objects.requireNonNull(cloudOfThingsRestClient),
            Objects.requireNonNull(relativeApiUrl),
            Objects.requireNonNull(gson),
            Objects.requireNonNull(contentType),
            Objects.requireNonNull(collectionElementName),
            filterBuilder
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
        @Nonnull final CloudOfThingsRestClient cloudOfThingsRestClient,
        @Nonnull final String relativeApiUrl,
        @Nonnull final Gson gson,
        @Nonnull final String contentType,
        @Nonnull final String collectionElementName,
        @Nullable final Filter.FilterBuilder filterBuilder,
        final int pageSize
    ) {
        super(
            Objects.requireNonNull(cloudOfThingsRestClient),
            Objects.requireNonNull(relativeApiUrl),
            Objects.requireNonNull(gson),
            Objects.requireNonNull(contentType),
            Objects.requireNonNull(collectionElementName),
            filterBuilder,
            pageSize
        );
    }
}
