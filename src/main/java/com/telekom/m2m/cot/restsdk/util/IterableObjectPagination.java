package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Simplifies iteration over paged result by providing access to paged objects
 * via {@link Stream}.
 *
 * The object stream can be accessed via {@link #stream()}. It will read pages
 * *only* if necessary and forward until the end of result (the last page) is
 * reached.
 *
 * @param <T> The type of objects on the pages.
 */
abstract public class IterableObjectPagination<T> extends JsonArrayPagination {
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

    @Nonnull
    public Stream<T> stream() {
        return null;
    }

    /**
     * Converts the given JSON data into an object.
     *
     * @param element The element.
     * @return An object created from the JSON data.
     */
    @Nonnull
    abstract protected T convertJsonToObject(@Nonnull final JsonElement element);
}
