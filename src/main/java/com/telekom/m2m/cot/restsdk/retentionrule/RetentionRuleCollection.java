package com.telekom.m2m.cot.restsdk.retentionrule;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.JsonArrayPagination;
import java.util.stream.StreamSupport;


public class RetentionRuleCollection extends JsonArrayPagination {

    private static final String COLLECTION_CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.retentionRuleCollection+json;charset=UTF-8;ver=0.9";
    private static final String COLLECTION_ELEMENT_NAME = "retentionRules";

    /**
     * Creates a RetentionRuleCollection.
     *
     * Use {@link RetentionRuleApi} to get RetentionRuleCollection.
     *
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     * @param relativeApiUrl          relative url of the REST API without leading slash.
     * @param gson                    the necessary json De-/serializer.
     * @param filterBuilder           the build criteria or null if all items should be retrieved.
     */
    RetentionRuleCollection(final CloudOfThingsRestClient cloudOfThingsRestClient,
                            final String relativeApiUrl,
                            final Gson gson,
                            final Filter.FilterBuilder filterBuilder) {
        super(cloudOfThingsRestClient, relativeApiUrl, gson, COLLECTION_CONTENT_TYPE, COLLECTION_ELEMENT_NAME, filterBuilder);
    }


    /**
     * Retrieves the current page.
     * <p>
     * Retrieves the rules influenced by filters set in construction.
     *
     * @return array of found rules
     */
    public RetentionRule[] getRetentionRules() {
        final JsonArray jsonRules = getJsonArray();
        return (jsonRules == null) ? null : StreamSupport.stream(getJsonArray().spliterator(), false).
                map(rule -> new RetentionRule(gson.fromJson(rule.getAsJsonObject(), ExtensibleObject.class))).
                toArray(RetentionRule[]::new);
    }

}
