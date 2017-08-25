package com.telekom.m2m.cot.restsdk.identity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Identity API is used to retrieve and manipulate Identity.
 * <p>
 * Created by Patrick Steinert on 31.01.16.
 */
public class IdentityApi {
    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.externalId+json;charset=UTF-8;ver=0.9";
    protected Gson gson = GsonUtils.createGson();

    private final CloudOfThingsRestClient cloudOfThingsRestClient;

    /**
     * Internal used constructor.
     * <p>
     * Use {@link com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform}.getIdentityApi()
     * to create an instance.
     * </p>
     *
     * @param cloudOfThingsRestClient the REST client to access CoT.
     */
    public IdentityApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    /**
     * Store an ExternalId in the platform.
     *
     * @param externalId the ExternalId object with the data to store.
     * @return the created operation with the generated unique identifier.
     */
    public ExternalId create(ExternalId externalId) {
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
     */
    public ExternalId getExternalId(ExternalId externalId) {
        String response = cloudOfThingsRestClient.getResponse(externalId.getExternalId(), "/identity/externalIds/" + externalId.getType() + "/", CONTENT_TYPE);
        return gson.fromJson(response, ExternalId.class);
    }

    /**
     * Deleted a certain ExternalId in the platform.
     *
     * @param externalId the {@link ExternalId} to delete.
     */
    public void delete(ExternalId externalId) {
        // The request to create an external ID is different, so we need a custom object.
        cloudOfThingsRestClient.delete(externalId.getExternalId(), "/identity/externalIds/" + externalId.getType());
    }

    /**
     * Get a pageable {@link ExternalIdCollection} to retrieve ExternalIds.
     *
     * @param externalId value of the external ID
     * @param resultSize size of the results (Max. 2000)  @return the found Alarms in a pageable collection.
     * @return a collection of found ExternalIds.
     * @since 0.3.0
     */
    public ExternalIdCollection getGlobalIds(String externalId, int resultSize) {
        return new ExternalIdCollection(
                cloudOfThingsRestClient,
                "/identity/globalIds/" + externalId + "/externalIds",
                gson,
                null,
                resultSize);
    }
}
