package com.telekom.m2m.cot.restsdk.audit;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Created by Andreas Dyck on 24.07.17.
 */
public class AuditRecordCollection {

    private Filter.FilterBuilder criteria = null;
    private CloudOfThingsRestClient cloudOfThingsRestClient;
    private int pageCursor = 1;

    private Gson gson = GsonUtils.createGson();

    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.auditRecordCollection+json;charset=UTF-8;ver=0.9";
    private boolean nextAvailable = false;
    private boolean previousAvailable = false;
    private int pageSize = 5;

    /**
     * Creates a AuditRecordCollection.
     * Use {@link AuditApi} to get AuditRecordCollections.
     *
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     */
    AuditRecordCollection(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    /**
     * Creates a AuditRecordCollection with filters.
     * Use {@link AuditApi} to get AuditRecordCollections.
     *
     * @param filterBuilder           the build criteria.
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     */
    AuditRecordCollection(Filter.FilterBuilder filterBuilder, CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.criteria = filterBuilder;
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    /**
     * Retrieves the current page.
     * <p>
     * Retrieves the AuditRecords influenced by filters setted in construction.
     *
     * @return array of found AuditRecords
     */
    public AuditRecord[] getAuditRecords() {
        JsonObject object = getJsonObject(pageCursor);

        previousAvailable = object.has("prev");

        if (object.has("auditRecords")) {
            JsonArray jsonAuditRecords = object.get("auditRecords").getAsJsonArray();
            AuditRecord[] arrayOfAuditRecords = new AuditRecord[jsonAuditRecords.size()];
            for (int i = 0; i < jsonAuditRecords.size(); i++) {
                JsonElement jsonAuditRecord = jsonAuditRecords.get(i).getAsJsonObject();
                AuditRecord auditRecord = new AuditRecord(gson.fromJson(jsonAuditRecord, ExtensibleObject.class));
                arrayOfAuditRecords[i] = auditRecord;
            }
            return arrayOfAuditRecords;
        } else
            return null;
    }

    private JsonObject getJsonObject(int page) {
        String response;
        String url = "/audit/auditRecords?" +
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
     * @return true if next page has audit records, otherwise false.
     */
    public boolean hasNext() {
        JsonObject object = getJsonObject(pageCursor + 1);
        if (object.has("auditRecords")) {
            JsonArray jsonAuditRecords = object.get("auditRecords").getAsJsonArray();
            nextAvailable = jsonAuditRecords.size() > 0 ? true : false;
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
    public void setPageSize(int pageSize) {
        if (pageSize > 0) {
            this.pageSize = pageSize;
        } else {
            this.pageSize = 0;
        }
    }
}