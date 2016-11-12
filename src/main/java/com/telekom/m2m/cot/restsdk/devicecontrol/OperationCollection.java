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
 * Represents a pageable Measurement collection.
 * <p>
 * Collection can be scrolled with next() and prev().
 * <p>
 *
 * @since 0.1.0
 * Created by breucking on 14.02.16.
 */
public class OperationCollection {

    private Filter.FilterBuilder criteria = null;
    private CloudOfThingsRestClient cloudOfThingsRestClient;
    private int pageCursor = 1;

    private Gson gson = GsonUtils.createGson();

    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.operationCollection+json;charset=UTF-8;ver=0.9";
    private boolean nextAvailable = false;
    private boolean previousAvailable = false;
    private int pageSize = 5;

    /**
     * Creates a OperationCollection.
     * Use {@link DeviceControlApi} to get OperationCollection.
     *
     * @param resultSize              size of the results (Max. 2000)
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     */
    OperationCollection(int resultSize, CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
        this.pageSize = resultSize;
    }

    /**
     * Creates a OperationCollection with filters.
     * Use {@link DeviceControlApi} to get OperationCollection.
     *
     * @param filterBuilder           the build criteria.
     * @param resultSize              size of the results (Max. 2000)
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     */
    OperationCollection(Filter.FilterBuilder filterBuilder, int resultSize, CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.criteria = filterBuilder;
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
        this.pageSize = resultSize;
    }

    /**
     * Creates a OperationCollection with filters.
     * Use {@link DeviceControlApi} to get OperationCollection.
     *
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     */
    public OperationCollection(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    /**
     * Retrieves the current page.
     *
     * @return array of found Operations.
     * @since 0.1.0
     */
    public Operation[] getOperations() {
        JsonObject object = getJsonObject(pageCursor);

        previousAvailable = object.has("prev");

        if (object.has("operations")) {
            JsonArray jsonOperations = object.get("operations").getAsJsonArray();
            Operation[] arrayOfOperations = new Operation[jsonOperations.size()];
            for (int i = 0; i < jsonOperations.size(); i++) {
                JsonElement jsonOperation = jsonOperations.get(i).getAsJsonObject();
                Operation operation = new Operation(gson.fromJson(jsonOperation, ExtensibleObject.class));
                arrayOfOperations[i] = operation;
            }
            return arrayOfOperations;
        } else
            return null;
    }

    private JsonObject getJsonObject(int page) {
        String response;
        String url = "/devicecontrol/operations?" +
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
     *
     * @since 0.2.0
     */
    public void next() {
        pageCursor += 1;
    }

    /**
     * Moves cursor to the previous page.
     *
     * @since 0.2.0
     */
    public void previous() {
        pageCursor -= 1;
    }

    /**
     * Checks if the next page has elements. <b>Use with caution, it does a seperate HTTP request, so it is considered as slow</b>
     *
     * @return true if next page has measurements, otherwise false.
     * @since 0.2.0
     */
    public boolean hasNext() {
        JsonObject object = getJsonObject(pageCursor + 1);
        if (object.has("operations")) {
            JsonArray jsonOperations = object.get("operations").getAsJsonArray();
            nextAvailable = jsonOperations.size() > 0 ? true : false;
        }
        return nextAvailable;
    }

    /**
     * Checks if there is a previous page.
     *
     * @return true if next page has measurements, otherwise false.
     * @since 0.2.0
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


//    public Iterator<Measurement> iterator() {
//        return null;
//    }
//
//    public Iterable<Measurement> elements(int limit) {
//        return new MeasurementCollectionIterable()
//    }
}
