package com.telekom.m2m.cot.restsdk.realtime;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.JsonArrayPagination;

/**
 * A class that defines a ModuleReferenceCollection and its methods. A Reference type of object holds the reference (URL) of the original object, in this case a module.
 * A Collection type of object defines operations on a set of original object, in this case a ModuleReference. 
 * Therefore the ModuleReferenceCollection class defines operations on a set of References of Modules.
 * 
 * Created by Ozan Arslan on 14.08.2017.
 *
 */
public class ModuleReferenceCollection extends JsonArrayPagination{

    
    
    private static final String COLLECTION_CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.ModuleReferenceCollection+json;ver=0.9";
    private static final String COLLECTION_ELEMENT_NAME = "references";


    ModuleReferenceCollection(final CloudOfThingsRestClient cloudOfThingsRestClient, final String relativeApiUrl,
            final Gson gson, final Filter.FilterBuilder filterBuilder) {
        super(cloudOfThingsRestClient, relativeApiUrl, gson, COLLECTION_CONTENT_TYPE, COLLECTION_ELEMENT_NAME,
                filterBuilder);
    }
    
    
}
