package com.telekom.m2m.cot.restsdk.audit;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Use the AuditApi to work with audit records.
 *
 * Created by Andreas Dyck on 24.07.17.
 */
public class AuditApi {
    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.auditRecord+json;charset=UTF-8;ver=0.9";
    private final Gson gson = GsonUtils.createGson();

    /**
     * Internal Constructor.
     *
     * @param cloudOfThingsRestClient the configured rest client.
     */
    public AuditApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    /**
     * Retrieves a specific AuditRecord.
     *
     * @param auditRecordId the unique identifier of the desired AuditRecord.
     * @return the AuditRecord (or null if not found).
     */
    public AuditRecord getAuditRecord(String auditRecordId) {
        String response = cloudOfThingsRestClient.getResponse(auditRecordId, "audit/auditRecords/", CONTENT_TYPE);
        AuditRecord auditRecord = new AuditRecord(gson.fromJson(response, ExtensibleObject.class));
        return auditRecord;
    }

    /**
     * Stores an AuditRecord.
     *
     * @param auditRecord the auditRecord to create.
     * @return the created auditRecord with the assigned unique identifier.
     */
    public AuditRecord createAuditRecord(AuditRecord auditRecord) {
        String json = gson.toJson(auditRecord);

        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, "audit/auditRecords/", CONTENT_TYPE);
        auditRecord.setId(id);

        return auditRecord;
    }

    /**
     * Retrieves AuditRecords.
     *
     * @return the found AuditRecords.
     */
    public AuditRecordCollection getAuditRecords() {
        return new AuditRecordCollection(cloudOfThingsRestClient);
    }

    /**
     * Retrieves AuditRecords filtered by criteria.
     *
     * @param filters filters of audit record attributes.
     * @return the AuditRecordCollections to navigate through the results.
     */
    public AuditRecordCollection getAuditRecords(Filter.FilterBuilder filters) {
        return new AuditRecordCollection(filters, cloudOfThingsRestClient);
    }
}
