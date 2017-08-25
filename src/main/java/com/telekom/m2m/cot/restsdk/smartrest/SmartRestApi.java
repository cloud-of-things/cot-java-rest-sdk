package com.telekom.m2m.cot.restsdk.smartrest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.inventory.InventoryApi;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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

    private static final String JSON_TEMPLATE_ATTRIBUTE = "com_cumulocity_model_smartrest_SmartRestTemplate";


    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    private final InventoryApi inventoryApi;



    public SmartRestApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
        // TODO: should we get this from the CloudOfThingsPlatform or is it ok to instantiate our own?
        this.inventoryApi = new InventoryApi(cloudOfThingsRestClient);
    }


    /**
     * Query the server for the existence of smart templates for a given X-Id.
     *
     * @param xId the X-Id for which to ask for templates
     *
     * @return the GId of the templates, if they exist, null otherwise.
     */
    public String checkTemplateExistence(String xId) {
        String[] response = cloudOfThingsRestClient.doSmartRequest(xId, "", false);
        if (response.length == 1) {
            String responseMsg = response[0].split(",")[0];
            switch (responseMsg) {
                case MSG_TEMPLATES_OK:
                    return response[0].split(",")[1];
                case MSG_TEMPLATES_NOT_FOUND:
                    return null;
                default:
                    throw new CotSdkException("Invalid response to smart template check: " + response[0]);
            }
        }

        throw new CotSdkException("Invalid response to smart template check: " + String.join("\n", (CharSequence[])response));
    }


    /**
     * Do a SmartREST-request.
     *
     * @param xId the X-Id for which this request shall be made.
     *            Can be null, omitting the X-Id header, to allow for multiple X-Id ("15,myxid").
     * @param lines a String with newline-separated lines for the request body
     * @param transientMode whether to use "X-Cumulocity-Processing-Mode: TRANSIENT" (false: PERSISTENT).
     *
     * @return the response body as an array of individual lines
     */
    public String[] execute(String xId, String lines, boolean transientMode) {
        return cloudOfThingsRestClient.doSmartRequest(xId, lines, transientMode);
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

        if ((mo != null) && (mo.has(JSON_TEMPLATE_ATTRIBUTE))) {
            List<SmartTemplate> templates = new ArrayList<>();
            JsonObject templateJson = (JsonObject) mo.getAttributes().get(JSON_TEMPLATE_ATTRIBUTE);
            if (templateJson.has("requestTemplates")) {
                SmartRequestTemplate[] requests = new Gson().fromJson(templateJson.get("requestTemplates"), SmartRequestTemplate[].class);
                templates.addAll(Arrays.asList(requests));
            }
            if (templateJson.has("responseTemplates")) {
                SmartResponseTemplate[] responses = new Gson().fromJson(templateJson.get("responseTemplates"), SmartResponseTemplate[].class);
                templates.addAll(Arrays.asList(responses));
            }
            return templates.toArray(new SmartTemplate[templates.size()]);

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

        String[] response = cloudOfThingsRestClient.doSmartRequest(xId, lines.toString(), false);

        if ((response == null) || (response.length == 0)) {
            throw new CotSdkException("Received empty response when trying to store SmartREST-templates.");
        }

        String[] responseParts = response[0].split(",");
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



    public SmartCepConnector getNotificationsConnector(String xId) {
        return new SmartCepConnector(cloudOfThingsRestClient, xId);
    }

}
