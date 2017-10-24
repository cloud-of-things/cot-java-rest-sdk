package com.telekom.m2m.cot.restsdk.audit;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Use the AuditApi to work with audit records.
 * <p>
 * Created by Andreas Dyck on 24.07.17.
 */
public class AuditApi {
    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.auditRecord+json;charset=UTF-8;ver=0.9";
    private static final String RELATIVE_API_URL = "audit/auditRecords/";

    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    private final Gson gson = GsonUtils.createGson();

    /**
     * Internal Constructor.
     *
     * @param cloudOfThingsRestClient the configured rest client.
     */
    public AuditApi(final CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    /**
     * Retrieves a specific AuditRecord by requested id.
     *
     * @param auditRecordId the unique identifier of the desired AuditRecord.
     * @return the AuditRecord (or null if not found).
     */
    public AuditRecord getAuditRecord(final String auditRecordId) {
        final String response = cloudOfThingsRestClient.getResponse(auditRecordId, RELATIVE_API_URL, CONTENT_TYPE);
        return new AuditRecord(gson.fromJson(response, ExtensibleObject.class));
    }

    /**
     * Stores an AuditRecord.
     *
     * It can be used to create and store e.g. login attempts
     * or some modifications of e.g. alarm, operation, user/group permissions, smart rule, event Processing module
     *
     * @param auditRecord the auditRecord to store.
     * @return the stored auditRecord with the assigned unique identifier.
     */
    public AuditRecord createAuditRecord(final AuditRecord auditRecord) {
        final String json = gson.toJson(auditRecord);

        final String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, RELATIVE_API_URL, CONTENT_TYPE);
        auditRecord.setId(id);

        return auditRecord;
    }

    /**
     * Retrieves a pageable Collection of AuditRecords.
     *
     * @return the first page of AuditRecordCollection which can be used to navigate through the found AuditRecords.
     */
    public AuditRecordCollection getAuditRecordCollection() {
        return new AuditRecordCollection(
                cloudOfThingsRestClient,
                RELATIVE_API_URL,
                gson,
                null
        );
    }

    /**
     * Retrieves a pageable Collection of AuditRecords filtered by criteria.
     *
     * It provides filtering by User, Type, Application, DateFrom, DateTo
     *
     * @param filters filters of audit record attributes.
     * @return the first page of AuditRecordCollection which can be used to navigate through the found AuditRecords.
     */
    public AuditRecordCollection getAuditRecordCollection(final Filter.FilterBuilder filters) {
        return new AuditRecordCollection(
                cloudOfThingsRestClient,
                RELATIVE_API_URL,
                gson,
                filters);
    }

    /**
     * Deletes a collection of AuditRecord by criteria (User, Type, Application, DateFrom, DateTo).
     *
     * @param filters filters of audit record attributes.
     */
    public void deleteAuditRecords(Filter.FilterBuilder filters) {
        cloudOfThingsRestClient.deleteBy(filters.buildFilter(), RELATIVE_API_URL);
    }
}
