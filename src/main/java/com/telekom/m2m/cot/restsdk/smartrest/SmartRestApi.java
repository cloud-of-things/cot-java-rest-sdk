package com.telekom.m2m.cot.restsdk.smartrest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObjectCollection;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.FilterBy;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * The SmartRestApi is used to register and query SmartREST-templates and to execute requests against them.
 *
 * <p>
 * Note that templates for an X-Id can only be registered all in one go and cannot be modified.
 * </p>
 *
 * <p>
 * For examples see SmartRestApiIT.
 * </p>
 */
public class SmartRestApi {

    public static final String MSG_TEMPLATES_NOT_FOUND     = "40";
    public static final String MSG_TEMPLATE_CREATION_ERROR = "41";
    public static final String MSG_TEMPLATES_OK            = "20";

    public static final String MSG_REALTIME_HANDSHAKE   = "80";
    public static final String MSG_REALTIME_SUBSCRIBE   = "81";
    public static final String MSG_REALTIME_UNSUBSCRIBE = "82";
    public static final String MSG_REALTIME_CONNECT     = "83";
    public static final String MSG_REALTIME_ADVICE      = "86";
    public static final String MSG_REALTIME_XID         = "87";

    // It seems that not all responses from the server use the same style of line breaks.
    public static final String LINE_BREAK_PATTERN = "\\r\\n|\\n";

    private static final String JSON_TEMPLATE_ATTRIBUTE = "com_cumulocity_model_smartrest_SmartRestTemplate";

    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    private final InventoryApi inventoryApi;

    private static final Gson gson = GsonUtils.createGson();

    public SmartRestApi(CloudOfThingsRestClient cloudOfThingsRestClient, InventoryApi inventoryApi) {
        this.cloudOfThingsRestClient = Objects.requireNonNull(cloudOfThingsRestClient);
        this.inventoryApi = Objects.requireNonNull(inventoryApi);
    }


    /**
     * Query the server for the existence of smart templates for a given X-Id.
     *
     * @param xId the X-Id for which to ask for templates
     *
     * @return the GId of the templates, if they exist, null otherwise.
     */
    public String checkTemplateExistence(String xId) {
        SmartRequest smartRequest = new SmartRequest(xId, SmartRequest.ProcessingMode.PERSISTENT, "");

        SmartResponse response = cloudOfThingsRestClient.doSmartRequest(smartRequest);
        String[] responseLines = response.getLines();
        if (responseLines.length == 1) {
            String responseMsg = responseLines[0].split(",")[0];
            switch (responseMsg) {
                case MSG_TEMPLATES_OK:
                    return responseLines[0].split(",")[1];
                case MSG_TEMPLATES_NOT_FOUND:
                    return null;
                default:
                    throw new CotSdkException("Invalid response to smart template check: " + responseLines[0]);
            }
        }

        throw new CotSdkException("Invalid response to smart template check: " + response);
    }


    /**
     * Do a SmartREST-request.
     *
     * @param smartRequest the request with meta information and lines to send to platform.
     *
     * @return a SmartResponse object with the response body as a String with newline-separated lines.
     */
    public SmartResponse execute(SmartRequest smartRequest) {
        SmartResponse response = cloudOfThingsRestClient.doSmartRequest(smartRequest);

        return response;
    }


    /**
     * Get all the templates that belong to this GId.
     * The GId is the one returned by {@link #checkTemplateExistence(String)}.
     *
     * @param gId the ID of the managedObject that contains the templates.
     *
     * @return Array containing instances of {@link SmartRequestTemplate} and/or {@link SmartResponseTemplate}.
     *         Can be empty if no templates for this GId are found.
     */
    public SmartTemplate[] getTemplatesByGId(String gId) {
        ManagedObject mo = inventoryApi.get(gId);
        return getSmartTemplatesFromManagedObject(mo);
    }

    /**
     * Get all the templates that belong to this X-ID.
     *
     * @param xId the X-ID of the managedObject that contains the templates.
     *
     * @return Array containing instances of {@link SmartRequestTemplate} and/or {@link SmartResponseTemplate}.
     *         Can be empty if no templates for this X-ID are found.
     *
     * @throws CotSdkException if more than one managed object with the given xId was found
     */
    public SmartTemplate[] getTemplatesByXId(String xId) {

        Filter.FilterBuilder filterBuilder = new Filter.FilterBuilder();
        filterBuilder.setFilter(FilterBy.BYTYPE, xId);
        ManagedObjectCollection managedObjectCollection = inventoryApi.getManagedObjects(filterBuilder, 2);

        if (managedObjectCollection.getManagedObjects().length > 1) {
            throw new CotSdkException("Got more than one managed object with xId: " + xId);
        }

        ArrayList<SmartTemplate> templateList = new ArrayList<>();

        for(ManagedObject managedObject : managedObjectCollection.getManagedObjects()) {
            SmartTemplate[] templates = getSmartTemplatesFromManagedObject(managedObject);
            templateList.addAll(Arrays.asList(templates));
        }
        return templateList.toArray(new SmartTemplate[0]);
    }

    private SmartTemplate[] getSmartTemplatesFromManagedObject(final ManagedObject managedObject) {
        if ((managedObject != null) && (managedObject.has(JSON_TEMPLATE_ATTRIBUTE))) {
            List<SmartTemplate> templates = new ArrayList<>();
            JsonObject templateJson = (JsonObject) managedObject.getAttributes().get(JSON_TEMPLATE_ATTRIBUTE);
            if (templateJson.has("requestTemplates")) {
                SmartRequestTemplate[] requests = gson.fromJson(templateJson.get("requestTemplates"), SmartRequestTemplate[].class);
                templates.addAll(Arrays.asList(requests));
            }
            if (templateJson.has("responseTemplates")) {
                SmartResponseTemplate[] responses = gson.fromJson(templateJson.get("responseTemplates"), SmartResponseTemplate[].class);
                templates.addAll(Arrays.asList(responses));
            }
            return templates.toArray(new SmartTemplate[0]);
        }
        return new SmartTemplate[0];
    }

    /**
     * Send SmartTemplates to the server.
     * RequestTemplates and ResponseTemplates have to be sent together, because there is no way
     * to update or add more templates later.
     *
     * @param xId the xId which owns the templates
     * @param requestTemplates all the SmartRequestTemplates
     * @param responseTemplates all the SmartResponseTemplate
     *
     * @return the GId of the new templates, if they could be stored.
     * @throws CotSdkException if there already are templates or the new templates have errors.
     */
    public String storeTemplates(String xId,
                                 SmartRequestTemplate[] requestTemplates,
                                 SmartResponseTemplate[] responseTemplates) {
        StringBuilder lines = new StringBuilder();
        for (SmartRequestTemplate t : requestTemplates) {
            lines.append("10,").append(t).append("\n");
        }
        for (SmartResponseTemplate t : responseTemplates) {
            lines.append("11,").append(t).append("\n");
        }

        SmartRequest smartRequest = new SmartRequest(xId, SmartRequest.ProcessingMode.PERSISTENT, lines.toString());
        SmartResponse response = cloudOfThingsRestClient.doSmartRequest(smartRequest);

        String[] responseLines = response.getLines();

        if (responseLines.length == 0) {
            throw new CotSdkException("Received empty response when trying to store SmartREST-templates.");
        }

        String[] responseParts = responseLines[0].split(",");
        switch (responseParts[0]) {
            case MSG_TEMPLATES_OK:
                return responseParts[1].trim();
            case MSG_TEMPLATE_CREATION_ERROR:
            default:
                int lineNumber = 0;
                String line = "";
                try {
                    lineNumber = Integer.parseInt(responseParts[1]);
                    if (lineNumber <= requestTemplates.length) {
                        line = requestTemplates[lineNumber - 1].toString();
                    } else {
                        line = responseTemplates[lineNumber - 1 - requestTemplates.length].toString();
                    }
                } catch (NumberFormatException e) {
                    // Ok, because line number is optional.
                }
                throw new CotSdkException("Template creation error in line "+((lineNumber > 0) ? lineNumber : "")+" ("+line+"): "+responseParts[2]);
        }
    }


    /**
     * Delete the templates that were registered for this GId.
     *
     * @param gId the GId to delete.
     */
    public void deleteByGId(String gId) {
        inventoryApi.delete(gId);
    }


    /**
     * Get the {@link SmartCepConnector} which can be used to subscribe to channels for real time notifications.
     *
     * @param xId the base X-Id to use for the connection.
     * @return the new connector
     */
    public SmartCepConnector getNotificationsConnector(String xId) {
        return new SmartCepConnector(cloudOfThingsRestClient, xId);
    }

}
