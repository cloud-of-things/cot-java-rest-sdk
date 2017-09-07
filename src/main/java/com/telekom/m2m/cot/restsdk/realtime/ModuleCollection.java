package com.telekom.m2m.cot.restsdk.realtime;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.JsonArrayPagination;

/**
 * This class represents a collection of {@link Module}s.
 *
 * Created by Ozan Arslan on 14.08.2017.
 *
 */
public class ModuleCollection extends JsonArrayPagination {

    //TODO below CONTENT_TYPE might not work. In the cumulocity guide (realtime statements), the CONTENT_TYPE is not fully given.
    private static final String COLLECTION_CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.cepModuleCollection+json;ver=0.9";

    private static final String COLLECTION_ELEMENT_NAME = "modules";


    /**
     * Creates a ModuleCollection. Use {@link CepApi} to get ModuleCollections.
     *
     * @param cloudOfThingsRestClient the necessary REST client to send requests to the CoT.
     * @param relativeApiUrl          relative url of the REST API without leading slash.
     * @param gson                    the necessary json De-/serializer.
     * @param filterBuilder           the build criteria or null if all items should be retrieved.
     */
    ModuleCollection(final CloudOfThingsRestClient cloudOfThingsRestClient,
                     final String relativeApiUrl,
                     final Gson gson,
                     final Filter.FilterBuilder filterBuilder) {
        super(cloudOfThingsRestClient, relativeApiUrl, gson, COLLECTION_CONTENT_TYPE, COLLECTION_ELEMENT_NAME, filterBuilder);
    }

    /**
     * Retrieves the Modules influenced by filters set in construction.
     *
     * @return array of found Modules
     */
    public Module[] getModules() {
        final JsonArray jsonModules = getJsonArray();

        if (jsonModules != null) {
            final Module[] arrayOfModules = new Module[jsonModules.size()];
            for (int i = 0; i < arrayOfModules.length; i++) {
                arrayOfModules[i] = gson.fromJson(jsonModules.get(i), Module.class);
            }
            return arrayOfModules;
        } else {
            return null;
        }
    }

}
