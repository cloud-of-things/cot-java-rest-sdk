package com.telekom.m2m.cot.restsdk.devicecontrol;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Represents a non-pageable NewDeviceRequest collection.
 *
 * @since 0.3.0
 * Created by Patrick Steinert on 19.12.16.
 */
public class NewDeviceRequestCollection {

    private Filter.FilterBuilder criteria = null;
    private CloudOfThingsRestClient cloudOfThingsRestClient;
    private int pageCursor = 1;

    private Gson gson = GsonUtils.createGson();

    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.newDeviceRequestCollection+json;charset=UTF-8;ver=0.9";

    private boolean nextAvailable = false;
    private boolean previousAvailable = false;
    private int pageSize = 5;

    /**
     * Creates a NewDeviceRequest.
     * Use {@link DeviceControlApi} to get NewDeviceRequest.
     *
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     */
    NewDeviceRequestCollection(int resultSize, CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
        this.pageSize = resultSize;
    }

    /**
     * Creates a NewDeviceRequestCollection with filters.
     * Use {@link DeviceCredentialsApi} to get NewDeviceRequestCollection.
     *
     * @param filterBuilder           the build criteria.
     * @param resultSize              size of the results (Max. 2000)
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     */
    NewDeviceRequestCollection(Filter.FilterBuilder filterBuilder, int resultSize, CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.criteria = filterBuilder;
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
        this.pageSize = resultSize;
    }

    /**
     * Retrieves all NewDeviceRequest.
     *
     * @return array of found NewDeviceRequests.
     */
    public NewDeviceRequest[] getNewDeviceRequests() {

        JsonObject object = getJsonObject(pageCursor);

        previousAvailable = object.has("prev");

        if (object.has("newDeviceRequests")) {
            JsonArray jsonNewDeviceRequests = object.get("newDeviceRequests").getAsJsonArray();
            NewDeviceRequest[] arrayOfNewDeviceRequests = new NewDeviceRequest[jsonNewDeviceRequests.size()];
            for (int i = 0; i < jsonNewDeviceRequests.size(); i++) {
                JsonElement jsonOperation = jsonNewDeviceRequests.get(i).getAsJsonObject();
                NewDeviceRequest newDeviceRequests = new NewDeviceRequest(gson.fromJson(jsonOperation, ExtensibleObject.class));
                arrayOfNewDeviceRequests[i] = newDeviceRequests;
            }
            return arrayOfNewDeviceRequests;
        } else
            return null;
    }

    private JsonObject getJsonObject(int page) {
        String response;
        String url = "/devicecontrol/newDeviceRequests?" +
                "currentPage=" + page +
                "&pageSize=" + pageSize;
        if (criteria != null) {
            url += "&" + criteria.buildFilter();
        }
        response = cloudOfThingsRestClient.getResponse(url, CONTENT_TYPE);

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
            JsonArray jsonNewDeviceRequests = object.get("newDeviceRequests").getAsJsonArray();
            nextAvailable = jsonNewDeviceRequests.size() > 0 ? true : false;
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
