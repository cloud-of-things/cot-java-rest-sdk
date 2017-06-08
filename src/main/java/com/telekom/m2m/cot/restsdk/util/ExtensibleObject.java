package com.telekom.m2m.cot.restsdk.util;


import java.util.HashMap;
import java.util.Map;

/**
 * Extensible object is a base class of all domain objects. It handles the (de-)serialization parts.
 * <p>
 * Created by Patrick Steinert on 31.01.16.
 */
public class ExtensibleObject {
    protected final HashMap<String, Object> anyObject = new HashMap<String, Object>();

    /**
     * Constructur for use in sub classes.
     *
     * @param extensibleObject
     */
    protected ExtensibleObject(ExtensibleObject extensibleObject) {
        if (extensibleObject != null)
            anyObject.putAll(extensibleObject.anyObject);
    }

    /**
     * Default constructor.
     */
    public ExtensibleObject() {
    }

    /**
     * Set a custom attribute of the object. Setting the same property again will override the old value.
     *
     * @param attributeId the unique id of the property as String
     * @param value       the value of the property.
     */
    public void set(String attributeId, Object value) {
        anyObject.put(attributeId, value);
    }

    /**
     * Getting all attributes.
     *
     * @return a map with all attributes.
     */
    public Map<String, Object> getAttributes() {
        return (Map<String, Object>) anyObject.clone();
    }

    /**
     * Get a custom attribute of the object.
     *
     * @param attributeId the unique identifier of the attribute.
     * @return the value of the attribute.
     */
    public Object get(String attributeId) {
        return anyObject.get(attributeId);
    }

    /**
     * Checks if a custom attribute is set.
     *
     * @param attributeId the unique identifier of the attribute.
     * @return true if attribute is set, even if null.
     */
    public boolean has(String attributeId) {
        return anyObject.containsKey(attributeId);
    }

    /**
     * Set a custom attribute with its name derived from class package and name.
     * <p>
     * E.g. a class com.telekom.SpecialObject will get the identifiert com_telekom_SpecialObject.
     *
     * @param attribute the value of the custom attribute.
     */
    public void set(Object attribute) {
        anyObject.put(attribute.getClass().getCanonicalName().replace('.', '_'), attribute);
    }

    /**
     * Adds all attributes from argument to this object. Attributes with the existing attribute identifiers will be
     * overridden.
     *
     * @param attributes the attributes to add, can't be null.
     */
    public void setAttributes(Map<String, Object> attributes) {
        if (attributes != null) {
            anyObject.putAll(attributes);
        }
    }

    @Override
    public String toString() {
        return "ExtensibleObject{" + "anyObject=" + anyObject +
                '}';
    }
}
