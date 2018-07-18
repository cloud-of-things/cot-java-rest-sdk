package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Andreas Dyck on 26.07.17.
 */
public class JsonArrayPagination {

    public static final int DEFAULT_PAGE_SIZE = 5;

    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    private final String relativeApiUrl;
    @Deprecated
    private final String contentType;
    private final String collectionElementName;

    private int pageCursor = 1;
    private int pageSize = DEFAULT_PAGE_SIZE;

    private Filter.FilterBuilder criteria = null;

    protected final Gson gson;

    /**
     * The cached response of the current page.
     *
     * Null if the page has not been requested yet or after switching pages via {@link #next()} or {@link #previous()}.
     */
    @Nullable
    private JsonObject currentPageContent = null;

    /**
     * Creates a JsonArrayPagination.
     *
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     * @param relativeApiUrl          relative url of the REST API without leading slash.
     * @param gson                    the necessary json De-/serializer.
     * @param contentType             the Content-Type of the JSON Object.
     * @param collectionElementName   json element name which contains an array of JSON Objects.
     * @param filterBuilder           the build criteria or null if all items should be retrieved.
     */
    public JsonArrayPagination(
            final CloudOfThingsRestClient cloudOfThingsRestClient,
            final String relativeApiUrl,
            final Gson gson,
            final String contentType,
            final String collectionElementName,
            final Filter.FilterBuilder filterBuilder) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
        this.relativeApiUrl = relativeApiUrl;
        this.gson = gson;
        this.contentType = contentType;
        this.collectionElementName = collectionElementName;
        this.criteria = filterBuilder;
    }

    /**
     * Creates a JsonArrayPagination.
     *
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     * @param relativeApiUrl          relative url of the REST API without leading slash.
     * @param gson                    the necessary json De-/serializer.
     * @param contentType             the Content-Type of the JSON Object.
     * @param collectionElementName   json element name which contains an array of JSON Objects.
     * @param filterBuilder           the build criteria or null if all items should be retrieved.
     * @param pageSize                max number of retrieved elements per page.
     */
    public JsonArrayPagination(
            final CloudOfThingsRestClient cloudOfThingsRestClient,
            final String relativeApiUrl,
            final Gson gson,
            final String contentType,
            final String collectionElementName,
            final Filter.FilterBuilder filterBuilder,
            final int pageSize) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
        this.relativeApiUrl = relativeApiUrl;
        this.gson = gson;
        this.contentType = contentType;
        this.collectionElementName = collectionElementName;
        this.criteria = filterBuilder;
        setPageSize(pageSize);
    }

    /**
     * Retrieves the current page.
     * <p>
     * Retrieves the entries influenced by filters set in construction.
     *
     * @return JsonArray of found JsonElements
     */
    @Nullable
    public JsonArray getJsonArray() {
        final JsonObject currentPage = getCurrentPage();

        if (currentPage.has(collectionElementName)) {
            return currentPage.get(collectionElementName).getAsJsonArray();
        } else {
            return null;
        }
    }

    @Nonnull
    private JsonObject getJsonObject(final int page) {
        String url = relativeApiUrl +
                "?currentPage=" + page +
                "&pageSize=" + pageSize;
        //hint: it is possible to change the sort order by adding query parameter "revert=true"

        if (criteria != null) {
            url += "&" + criteria.buildFilter();
        }
        final String response = cloudOfThingsRestClient.getResponse(url);

        return gson.fromJson(response, JsonObject.class);
    }

    /**
     * Moves cursor to the next page.
     *
     * Please note: When calling next(), but there is no next page,
     * then the behavior is undefined.
     * Use {@link #hasNext()} to check page availability before
     * calling next().
     */
    public void next() {
        pageCursor += 1;
        clearPageCache();
    }

    /**
     * Moves cursor to the previous page.
     */
    public void previous() {
        if (pageCursor > 1) {
            pageCursor -= 1;
            clearPageCache();
        }
    }

    /**
     * Checks if the next page has elements.
     *
     * <b>Use with caution</b>: In worst case it does a separate HTTP request, so it is considered as slow
     *
     * @return true if next page has records, otherwise false.
     */
    public boolean hasNext() {
        final JsonObject page = getCurrentPage();
        if (!page.has("next")) {
            // No link to next page, there are no more results available.
            return false;
        }
        final JsonObject pageStats = page.get("statistics").getAsJsonObject();
        if (pageStats.has("totalPages") && !pageStats.get("totalPages").isJsonNull()) {
            // The whole number of pages is known. Check if there is a next page.
            return pageStats.get("currentPage").getAsInt() < pageStats.get("totalPages").getAsInt();
        }
        // The page might be filtered. When a filter is applied, the total number
        // of pages is unknown.
        final int numberOfItemsOnPage = page.has(collectionElementName) ? page.get(collectionElementName).getAsJsonArray().size() : 0;
        if (numberOfItemsOnPage < pageStats.get("pageSize").getAsInt()) {
            // There are less items on this page than allowed by the page size.
            // There will be no next page.
            return false;
        }
        // There *might* be a next page, we can't be sure. Fetch the following page to check.
        // This is an expensive operation, but it should not be necessary too often.
        final JsonObject nextPage = getJsonObject(pageCursor + 1);
        if (nextPage.has(collectionElementName)) {
            final JsonArray itemsOnNextPage = nextPage.get(collectionElementName).getAsJsonArray();
            return itemsOnNextPage.size() > 0;
        }
        return false;
    }

    /**
     * Checks if there is a previous page.
     *
     * @return true if there is a previous page.
     */
    public boolean hasPrevious() {
        return pageCursor > 1;
    }

    /**
     * Sets the page size for page queries.
     * The queries uses page size as a limit of elements to retrieve.
     * There is a maximum number of elements, currently 2,000 elements.
     * <i>Default is 5</i>
     *
     * @param pageSize the new page size as positive integer. If pageSize is  less than 1MO then it will be reset to it's default.
     */
    public void setPageSize(final int pageSize) {
        if (pageSize > 0) {
            this.pageSize = pageSize;
        } else {
            this.pageSize = DEFAULT_PAGE_SIZE;
        }
        clearPageCache();
    }

    /**
     * @return A copy of this pagination in its current state.
     */
    @Nonnull
    protected JsonArrayPagination copy() {
        final JsonArrayPagination pagination = new JsonArrayPagination(
            cloudOfThingsRestClient,
            relativeApiUrl,
            gson,
            contentType,
            collectionElementName,
            criteria,
            pageSize
        );
        pagination.pageCursor = pageCursor;
        return pagination;
    }

    /**
     * Loads the content of the current page. Returns it from cache, if possible.
     *
     * @return The current page content.
     */
    @Nonnull
    private JsonObject getCurrentPage() {
        if (currentPageContent == null) {
            currentPageContent = getJsonObject(pageCursor);
        }
        return currentPageContent;
    }

    /**
     * Clears the cache that contains the current page response.
     */
    private void clearPageCache() {
        currentPageContent = null;
    }
}