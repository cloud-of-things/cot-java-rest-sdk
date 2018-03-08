package com.telekom.m2m.cot.restsdk.retentionrule;

import java.util.Map;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;


/**
 * API to create, retrieve, update and delete retention rules.
 */
public class RetentionRuleApi {

    private static final String CONTENT_TYPE = "application/vnd.com.nsn.cumulocity.retentionRule+json;charset=UTF-8;ver=0.9";
    private static final String ACCEPT = "application/vnd.com.nsn.cumulocity.retentionRule+json;charset=UTF-8;ver=0.9";
    private static final String RELATIVE_API_URL = "retention/retentions/";

    private final Gson gson = GsonUtils.createGson();
    private final CloudOfThingsRestClient cloudOfThingsRestClient;


    /**
     * Internal Constructor.
     *
     * @param cloudOfThingsRestClient the configured rest client.
     */
    public RetentionRuleApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }


    /**
     * Retrieves a specific rule.
     *
     * @param ruleId the unique identifier of the desired rule.
     *
     * @return the RetentionRule (or null if not found).
     */
    public RetentionRule getRule(Long ruleId) {
        String response = cloudOfThingsRestClient.getResponse(ruleId.toString(), RELATIVE_API_URL, CONTENT_TYPE);
        RetentionRule rule = new RetentionRule(gson.fromJson(response, ExtensibleObject.class));
        if (rule.getAttributes().size() == 0) {
            return null;
        }
        return rule;
    }


    /**
     * Stores a rule.
     *
     * @param rule the RetentionRule to create. Will be modified but also returned.
     *
     * @return the updated RetentionRule with the assigned unique identifier.
     *
     * @throws CotSdkException if the rule is not semantically valid
     */
    public RetentionRule createRetentionRule(RetentionRule rule) {
        validateRule(rule);

        Map<String, Object> attributes = rule.getAttributes();
        attributes.remove("id"); // Because it feels wrong to ask the server to create an object with a predefined id. It is the server which assigns ids.
        attributes.remove("editable");

        ExtensibleObject eo = new ExtensibleObject();
        eo.setAttributes(attributes);
        RetentionRule filteredRule = new RetentionRule(eo);

        String json = gson.toJson(filteredRule);
        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, RELATIVE_API_URL, CONTENT_TYPE, ACCEPT);
        rule.setId(Long.parseLong(id));

        return rule;
    }


    /**
     * Deletes a rule.
     *
     * @param rule the RetentionRule to delete
     */
    public void deleteRetentionRule(RetentionRule rule) {
        cloudOfThingsRestClient.delete(rule.getId().toString(), RELATIVE_API_URL);
    }

    /**
     * Deletes a rule by id.
     *
     * @param id the id of the RetentionRule to delete
     */
    public void deleteRetentionRule(long id) {
        cloudOfThingsRestClient.delete(String.valueOf(id), RELATIVE_API_URL);
    }


    /**
     * Updates a rule.
     *
     * @param rule the rule to update.
     *
     * @throws CotSdkException if the rule is not semantically valid
     */
    public void update(RetentionRule rule) {
        validateRule(rule);

        Map<String, Object> attributes = rule.getAttributes();
        attributes.remove("id"); // It doesn't make sense to change the ID of a rule afterwards.
        attributes.remove("editable");

        ExtensibleObject extensibleObject = new ExtensibleObject();
        extensibleObject.setAttributes(attributes);

        String json = gson.toJson(extensibleObject);
        cloudOfThingsRestClient.doPutRequest(json, RELATIVE_API_URL + rule.getId(), CONTENT_TYPE);
    }


    /**
     * Retrieves RetentionRules.
     *
     * @return the found RetentionRules.
     */
    public RetentionRuleCollection getRetentionRules() {
        return new RetentionRuleCollection(
                cloudOfThingsRestClient,
                RELATIVE_API_URL,
                gson,
                null);
    }


    /**
     * Validate a rule and throw {@link CotSdkException} if it is invalid.
     * Some combinations of fields are not valid and it won't be possible to store them on the server.
     *
     * @param rule the rule to validate
     */
    public void validateRule(RetentionRule rule) {
        if (rule.getMaximumAge() <= 0) {
            // The server allows all numbers, but the web-frontend doesn't and they don't make much sense.
            throw new CotSdkException("RetentionRule must have a maximumAge > 0.");
        }

        switch (rule.getDataType()) {
            case "*":
                break;
            case RetentionRule.DATA_TYPE_EVENT :
                break;
            case RetentionRule.DATA_TYPE_MEASUREMENT :
                break;
            case RetentionRule.DATA_TYPE_ALARM :
                if ((rule.getFragmentType() != null) && (!"*".equals(rule.getFragmentType()))) {
                    throw new CotSdkException("RetentionRule of dataType " + RetentionRule.DATA_TYPE_ALARM + " cannot have a fragmentType.");
                }
                break;
             case RetentionRule.DATA_TYPE_AUDIT :
                if ((rule.getFragmentType() != null) && (!"*".equals(rule.getFragmentType()))) {
                    throw new CotSdkException("RetentionRule of dataType " + RetentionRule.DATA_TYPE_AUDIT + " cannot have a fragmentType.");
                }
                break;
            case RetentionRule.DATA_TYPE_OPERATION :
                if ((rule.getType() != null) && (!"*".equals(rule.getType()))) {
                    throw new CotSdkException("RetentionRule of dataType " + RetentionRule.DATA_TYPE_OPERATION + " cannot have a type.");
                }
                break;
            default:
                throw new CotSdkException("RetentionRule cannot have dataType " + rule.getDataType());
        }
    }

}
