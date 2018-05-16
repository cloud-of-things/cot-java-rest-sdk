package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
public class IterableObjectPagination<T> extends JsonArrayPagination {

    /**
     * Converts JSON objects into the object that are provided during iteration.
     */
    @Nonnull
    protected final Function<JsonElement, T> objectMapper;

    /**
     * Creates a pagination with default page size.
     *
     * @param objectMapper            maps page items into objects that this class iterates over.
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     * @param relativeApiUrl          relative url of the REST API without leading slash.
     * @param gson                    the necessary json De-/serializer.
     * @param contentType             the Content-Type of the JSON Object.
     * @param collectionElementName   json element name which contains an array of JSON Objects.
     * @param filterBuilder           the build criteria or null if all items should be retrieved.
     */
    public IterableObjectPagination(
        @Nonnull final Function<JsonElement, T> objectMapper,
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
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    /**
     * Creates a pagination with custom page size.
     *
     * @param objectMapper            maps page items into objects that this class iterates over.
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     * @param relativeApiUrl          relative url of the REST API without leading slash.
     * @param gson                    the necessary json De-/serializer.
     * @param contentType             the Content-Type of the JSON Object.
     * @param collectionElementName   json element name which contains an array of JSON Objects.
     * @param filterBuilder           the build criteria or null if all items should be retrieved.
     * @param pageSize                max number of retrieved elements per page.
     */
    public IterableObjectPagination(
        @Nonnull final Function<JsonElement, T> objectMapper,
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
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    /**
     * Returns a {@link Stream} that reads objects until the last page is reached.
     *
     * The stream can be used only *once* for iteration.
     *
     * @return Object stream.
     */
    @Nonnull
    public Stream<T> stream() {
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(
                createPageIterator(),
                // Iteration attributes:
                // DISTINCT: There are no duplicates in the returned items.
                // NONNULL: There are *only* item objects in the list.
                Spliterator.DISTINCT | Spliterator.NONNULL
            ),
            false
        )
            .peek(page -> {
                int x = 1;
            })
            .flatMap(jsonArray -> StreamSupport.stream(jsonArray.spliterator(), false))
            .map(this.objectMapper)
            .peek(item -> {
                int x = 1;
            });
    }

    @Nonnull
    private Iterator<JsonArray> createPageIterator() {
        final JsonArrayPagination pagination = this.copy();
        return new Iterator<JsonArray>() {
            /**
             * Indicates if another item page is available.
             *
             * Initialized as `true` as we always want to present the first page and
             * {@link JsonArrayPagination#hasNext()} would return `false` if there
             * is no following page.
             */
            private boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            @Nonnull
            public JsonArray next() {
                final JsonArray itemsOnPage = pagination.getJsonArray();
                hasNext = pagination.hasNext();
                if (hasNext) {
                    pagination.next();
                }
                return Optional.ofNullable(itemsOnPage).orElseGet(JsonArray::new);
            }
        };
    }
}
