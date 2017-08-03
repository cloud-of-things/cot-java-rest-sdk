package com.telekom.m2m.cot.restsdk.retentionrule;

import com.telekom.m2m.cot.restsdk.util.ExtensibleObject;


/**
 * RetentionRules govern after what time old data is to be removed from the cloud.
 * Each type of document can have it's own rule, so that some types can be kept longer,
 * while others get cleaned up faster.
 */
public class RetentionRule extends ExtensibleObject {


    /**
     * Default constructor to create a new rule.
     */
    public RetentionRule() {
        super();
    }

    /**
     * Internal constructor to create rules from base class.
     *
     * @param extensibleObject object from base class.
     */
    RetentionRule(ExtensibleObject extensibleObject) {
        super(extensibleObject);
    }


    /**
     * Get the unique identifier of the rule.
     * If the rule was retrieved from the platform, it has an ID. If just
     * created, there is no ID.
     * TODO: verify whether/why this is a number and not a String as in other entities.
     *
     * @return Long the unique identifier of the rule or null if not available.
     */
    public Long getId() {
        Object id = anyObject.get("id");
        try {
            return (id instanceof Number) ? ((Number) id).longValue() : (Long) id;
        } catch (ClassCastException | NullPointerException ex) {
            // TODO: is there really no better way? The untyped anyObject-Map makes it hard to know what
            //       we can expect here and it wouldn't be nice to explicitly rely on gson here.
            return null;
        }
    }

    /**
     * Set the unique identifier of the rule.
     * Just used internally.
     *
     * @param id the new identifier.
     */
    void setId(Long id) {
        anyObject.put("id", id);
    }


    /**
     * Get the type of the rule.
     * The type categorizes the rule.
     *
     * @return a String with the type or null if not available.
     */
    public String getType() {
        return (String) anyObject.get("type");
    }

    /**
     * Setting the rule type.
     *
     * @param type a String with the rule type. Use cot_abc_xyz style.
     */
    public void setType(String type) {
        anyObject.put("type", type);
    }


    /**
     * Get the dataType of the rule.
     * The type categorizes the rule.
     *
     * @return a String with the dataType or null if not available.
     */
    public String getDataType() {
        return (String) anyObject.get("dataType");
    }

    /**
     * Setting the rule dataType.
     *
     * @param dataType a String with the rule dataType. Use cot_abc_xyz style.
     */
    public void setDataType(String dataType) {
        anyObject.put("dataType", dataType);
    }


    /**
     * Get the fragmentType of the rule.
     * The type categorizes the rule.
     *
     * @return a String with the fragmentType or null if not available.
     */
    public String getFragmentType() {
        return (String) anyObject.get("fragmentType");
    }

    /**
     * Setting the rule fragmentType.
     *
     * @param fragmentType a String with the rule fragmentType. Use cot_abc_xyz style.
     */
    public void setFragmentType(String fragmentType) {
        anyObject.put("fragmentType", fragmentType);
    }


    /**
     * Get the source of the rule.
     *
     * TODO: verify whether/why this is a String and not a ManagedObject as in other entities.
     *
     * @return a String with the source or null if not available.
     */
    public String getSource() {
        return (String) anyObject.get("source");
    }

    /**
     * Setting the rule fragmentType.
     *
     * @param source a String with the rule source.
     */
    public void setSource(String source) {
        anyObject.put("source", source);
    }


    /**
     * Get the maximumAge of the rule.
     *
     * @return an int with the maximumAge
     */
    public int getMaximumAge() {
        Object maximumAge = anyObject.get("maximumAge");
        if (maximumAge instanceof Number) {
            return ((Number) maximumAge).intValue();
        }
        if (maximumAge instanceof String) {
            return Integer.parseInt((String) maximumAge);
        }
        try {
            return (int) maximumAge;
        } catch (ClassCastException | NullPointerException ex) {
            // TODO: is there really no better way? The untyped anyObject-Map makes it hard to know what
            //       we can expect here and it wouldn't be nice to explicitly rely on gson here.
        }
        return 0;
    }

    /**
     * Setting the rule maximumAge.
     *
     * @param maximumAge an int with the rule maximumAge.
     */
    public void setMaximumAge(int maximumAge) {
        anyObject.put("maximumAge", maximumAge);
    }


    /**
     * Get the editable-flag of the rule.
     *
     * @return the boolean editable-flag
     */
    public boolean isEditable() {
        Object editable = anyObject.get("editable");
        return (editable != null) && (boolean)editable;
    }

    /**
     * Setting the  editable-flag of the rule.
     *
     * @param editable flag
     */
    public void setEditable(boolean editable) {
        anyObject.put("editable", editable);
    }

}

