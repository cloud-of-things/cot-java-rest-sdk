package com.telekom.m2m.cot.restsdk.identity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Represents a pageable ExternalId collection.
 *
 * @since 0.3.0
 * Created by Patrick Steinert on 19.12.16.
 */
public class ExternalIdCollection {

    private String externalId;
    private String type;
    private Filter.FilterBuilder criteria = null;
    private CloudOfThingsRestClient cloudOfThingsRestClient;
    private int pageCursor = 1;

    private Gson gson = GsonUtils.createGson();

    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.externalIdCollection+json;charset=UTF-8;ver=0.9";

    private boolean nextAvailable = false;
    private boolean previousAvailable = false;
    private int pageSize = 5;

    /**
     * Internal contstructor to create a ExternalIdCollection.
     * Use {@link IdentityApi} to get ExternalIdCollection.
     *
     * @param externalId
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     */
    ExternalIdCollection(String externalId, int resultSize, CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.externalId = externalId;
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
        this.pageSize = resultSize;
    }

    /**
     * Retrieves all ExternalId.
     *
     * @return array of found ExternalIds.
     */
    public ExternalId[] getExternalIds() {

        JsonObject object = getJsonObject(pageCursor);

        previousAvailable = object.has("prev");

        if (object.has("externalIds")) {
            JsonArray jsonExternalIds = object.get("externalIds").getAsJsonArray();
            ExternalId[] arrayOfExternalIds = new ExternalId[jsonExternalIds.size()];
            for (int i = 0; i < jsonExternalIds.size(); i++) {
                JsonElement jsonExternalId = jsonExternalIds.get(i).getAsJsonObject();
                ExternalId newDeviceRequests = gson.fromJson(jsonExternalId, ExternalId.class);
                arrayOfExternalIds[i] = newDeviceRequests;
            }
            return arrayOfExternalIds;
        } else
            return null;
    }

    private JsonObject getJsonObject(int page) {
        String url = "identity/globalIds/" + externalId + "/externalIds?" +
                "currentPage=" + page +
                "&pageSize=" + pageSize;
        String response = cloudOfThingsRestClient.getResponse(url, CONTENT_TYPE);

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
        pageCursor -= 1;
    }

    /**
     * Checks if the next page has elements. <b>Use with caution, it does a seperate HTTP request, so it is considered as slow</b>
     *
     * @return true if next page has measurements, otherwise false.
     */
    public boolean hasNext() {
        JsonObject object = getJsonObject(pageCursor + 1);
        if (object.has("newDeviceRequests")) {
            JsonArray jsonExternalIds = object.get("newDeviceRequests").getAsJsonArray();
            nextAvailable = jsonExternalIds.size() > 0 ? true : false;
        }
        return nextAvailable;
    }

    /**
     * Checks if there is a previous page.
     *
     * @return true if next page has measurements, otherwise false.
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
    public void setPageSize(int pageSize) {
        if (pageSize > 0) {
            this.pageSize = pageSize;
        } else {
            this.pageSize = 0;
        }
    }

}
