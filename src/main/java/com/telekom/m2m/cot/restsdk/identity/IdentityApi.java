package com.telekom.m2m.cot.restsdk.identity;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by breucking on 31.01.16.
 */
public class IdentityApi {
    String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.externalId+json;charset=UTF-8;ver=0.9";
    protected Gson gson = GsonUtils.createGson();

    private final CloudOfThingsRestClient cloudOfThingsRestClient;

    public IdentityApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    public ExternalId create(ExternalId externalId) throws Exception {
        throw new NotImplementedException();
    }

    public ExternalId getExternalId(String extId) throws Exception {
        String response = cloudOfThingsRestClient.getResponse(extId, "/identity/externalIds/c8y_Serial/", CONTENT_TYPE);
        return gson.fromJson(response, ExternalId.class);
    }


}
