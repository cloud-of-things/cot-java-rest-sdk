package com.telekom.m2m.cot.restsdk.identity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

import java.io.IOException;

/**
 * Created by Patrick Steinert on 31.01.16.
 */
public class IdentityApi {
    String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.externalId+json;charset=UTF-8;ver=0.9";
    protected Gson gson = GsonUtils.createGson();

    private final CloudOfThingsRestClient cloudOfThingsRestClient;

    public IdentityApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    public ExternalId create(ExternalId externalId) throws Exception {
        // The request to create an external ID is different, so we need a custom object.

        JsonObject externalIdObject = new JsonObject();
        externalIdObject.add("type", new JsonPrimitive(externalId.getType()));
        externalIdObject.add("externalId", new JsonPrimitive(externalId.getExternalId()));

        String response = cloudOfThingsRestClient.doPostRequest(externalIdObject.toString(), "/identity/globalIds/" + externalId.getManagedObject().getId() + "/externalIds", CONTENT_TYPE);
        return gson.fromJson(response, ExternalId.class);

    }

    /**
     * Retrieves External Identity objects from the CoT.
     *
     * @param externalId a constructed ExternalId object with the externalId set.
     * @return the desired ExternalId object or null if not found
     * @throws Exception if somethings went wrong, expecially in server communication.
     */
    public ExternalId getExternalId(ExternalId externalId) throws Exception {
        String response = cloudOfThingsRestClient.getResponse(externalId.getExternalId(), "/identity/externalIds/" + externalId.getType() + "/", CONTENT_TYPE);
        return gson.fromJson(response, ExternalId.class);
    }


    public void delete(ExternalId externalId) throws IOException {
        // The request to create an external ID is different, so we need a custom object.
        cloudOfThingsRestClient.delete(externalId.getExternalId(), "/identity/externalIds/" + externalId.getType());

    }
}
