package com.telekom.m2m.cot.restsdk.inventory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.JsonArrayPagination;


/**
 * A Collection of binaries, or, rather, their metadata.
 */
public class BinariesCollection extends JsonArrayPagination {

    private static final String COLLECTION_CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.applicationCollection+json;vcharset=UTF-8;ver=0.9";
    private static final String COLLECTION_ELEMENT_NAME = "managedObjects";



    
    public BinariesCollection(Filter.FilterBuilder filters,CloudOfThingsRestClient cloudOfThingsRestClient,
            String relativeApiUrl,
            Gson gson,
            Integer pageSize) {
super(cloudOfThingsRestClient, relativeApiUrl, gson, COLLECTION_CONTENT_TYPE, COLLECTION_ELEMENT_NAME, filters);
if (pageSize != null) {
setPageSize(pageSize);
}
}
    
    
    /**
     * Retrieves the current page.
     * <p>
     * Retrieves the Binaries influenced by filters set in construction.
     *
     * @return array of found Binaries
     */
    public Binary[] getBinaries() {
        final JsonArray jsonBinaries = getJsonArray();
        Binary[] binaries = gson.fromJson(jsonBinaries, Binary[].class);
        return binaries;
    }

}
