package com.telekom.m2m.cot.restsdk.audit;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.JsonArrayPagination;

/**
 * Created by Andreas Dyck on 27.07.17.
 */
public class AuditRecordCollection extends JsonArrayPagination {

    /**
     * Creates an AuditRecordCollection.
     * Use {@link AuditApi} to get AuditRecordCollections.
     *
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     * @param relativeApiUrl          relative url of the REST API without leading slash.
     * @param gson                    the necessary json De-/serializer.
     * @param contentType             the Content-Type of the JSON Object.
     * @param collectionElementName   json element name which contains an array of JSON Objects.
     * @param filterBuilder           the build criteria or null if all items should be retrieved.
     */
    AuditRecordCollection(final CloudOfThingsRestClient cloudOfThingsRestClient,
                          final String relativeApiUrl,
                          final Gson gson,
                          final String contentType,
                          final String collectionElementName,
                          final Filter.FilterBuilder filterBuilder) {
        super(cloudOfThingsRestClient, relativeApiUrl, gson, contentType, collectionElementName, filterBuilder);
    }

    /**
     * Retrieves the current page.
     * <p>
     * Retrieves the AuditRecords influenced by filters set in construction.
     *
     * @return array of found AuditRecords
     */
    public AuditRecord[] getAuditRecords() {
        final JsonArray jsonAuditRecords = getJsonArray();

        if (jsonAuditRecords != null) {
            final AuditRecord[] arrayOfAuditRecords = new AuditRecord[jsonAuditRecords.size()];
            for (int i = 0; i < jsonAuditRecords.size(); i++) {
                JsonElement jsonAuditRecord = jsonAuditRecords.get(i).getAsJsonObject();
                final AuditRecord auditRecord = new AuditRecord(super.gson.fromJson(jsonAuditRecord, ExtensibleObject.class));
                arrayOfAuditRecords[i] = auditRecord;
            }
            return arrayOfAuditRecords;
        } else {
            return null;
        }
    }
}