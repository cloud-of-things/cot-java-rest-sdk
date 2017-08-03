package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;

/**
 * Created by Andreas Dyck on 26.07.17.
 */
public class JsonArrayPagination {

    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    private final String relativeApiUrl;
    protected final Gson gson;
    private final String contentType;
    private final String collectionElementName;

    private int pageCursor = 1;
    private boolean nextAvailable = false;
    private boolean previousAvailable = false;
    private int pageSize = 5;

    private Filter.FilterBuilder criteria = null;

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
        this.pageSize = pageSize;
    }

    /**
     * Retrieves the current page.
     * <p>
     * Retrieves the entries influenced by filters set in construction.
     *
     * @return JsonArray of found JsonElements
     */
    public JsonArray getJsonArray() {
        final JsonObject object = getJsonObject(pageCursor);

        previousAvailable = object.has("prev");

        if (object.has(collectionElementName)) {
            return object.get(collectionElementName).getAsJsonArray();
        } else {
            return null;
        }
    }

    private JsonObject getJsonObject(final int page) {
        final String response;
        String url = relativeApiUrl +
                "?currentPage=" + page +
                "&pageSize=" + pageSize;
        //hint: it is possible to change the sort order by adding query parameter "revert=true"

        if (criteria != null) {
            url += "&" + criteria.buildFilter();
        }
        response = cloudOfThingsRestClient.getResponse(url, contentType);

        return gson.fromJson(response, JsonObject.class);
    }

    /**
     * Moves cursor to the next page.
     */
    public void next() {
        pageCursor += 1;
    }

    /**
     * Moves cursor to the previous page.
     */
    public void previous() {
        if(pageCursor > 1) {
            pageCursor -= 1;
        }
    }

    /**
     * Checks if the next page has elements. <b>Use with caution, it does a seperate HTTP request, so it is considered as slow</b>
     *
     * @return true if next page has audit records, otherwise false.
     */
    public boolean hasNext() {
        final JsonObject object = getJsonObject(pageCursor + 1);
        if (object.has(collectionElementName)) {
            final JsonArray jsonArray = object.get(collectionElementName).getAsJsonArray();
            nextAvailable = (jsonArray.size() > 0);
        }
        return nextAvailable;
    }

    /**
     * Checks if there is a previous page.
     *
     * @return true if next page has audit records, otherwise false.
     */
    public boolean hasPrevious() {
        return previousAvailable;
    }

    /**
     * Sets the page size for page queries.
     * The queries uses page size as a limit of elements to retrieve.
     * There is a maximum number of elements, currently 2,000 elements.
     * <i>Default is 5</i>
     *
     * @param pageSize the new page size as positive integer.
     */
    public void setPageSize(final int pageSize) {
        if (pageSize > 0) {
            this.pageSize = pageSize;
        } else {
            this.pageSize = 1;
        }
    }
}