package com.telekom.m2m.cot.restsdk.inventory;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents the API to retrieve and manipulate ManagedObjects.
 * <p>
 * Created by Patrick Steinert on 30.01.16.
 */
public class InventoryApi {
    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    protected Gson gson = GsonUtils.createGson();

    private static final String CONTENT_TYPE_MANAGEDOBJECT = "application/vnd.com.nsn.cumulocity.managedObject+json;charset=UTF-8;ver=0.9";
    private static final String ACCEPT_MANAGEDOBJECT = "application/vnd.com.nsn.cumulocity.managedObject+json;charset=UTF-8;ver=0.9";
    private static final String CONTENT_TYPE_MANAGEDOBJECTREF = "application/vnd.com.nsn.cumulocity.managedObjectReference+json;charset=UTF-8;ver=0.9";
    private static final String ACCEPT_MANAGEDOBJECTREF = "application/vnd.com.nsn.cumulocity.managedObjectReference+json;charset=UTF-8;ver=0.9";
    private static final String RELATIVE_API_URL = "inventory/managedObjects/";
    private static final String SUPPORTED_MEASUREMENTS = "c8y_SupportedMeasurements";
    private static final List<FilterBy> acceptedFilters = Arrays.asList(FilterBy.BYTYPE, FilterBy.BYFRAGMENTTYPE, FilterBy.BYLISTOFIDs, FilterBy.BYTEXT, FilterBy.BYAGENTID);

    public InventoryApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }

    /**
     * Stores a ManagedObject in the platform.
     * ID should be empty, will be ignored if present.
     *
     * @param managedObject the managedObject to create.
     * @return the {@link ManagedObject} stored in the platform with the generated ID.
     */
    public ManagedObject create(ManagedObject managedObject) {
        String json = gson.toJson(managedObject);

        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, RELATIVE_API_URL, CONTENT_TYPE_MANAGEDOBJECT, ACCEPT_MANAGEDOBJECT);
        managedObject.setId(id);

        return managedObject;
    }

    /**
     * Retrieves a ManagedObject identified by ID from the platform.
     * <p>
     * Does not set withParents, so no parents will be loaded.
     *
     * @param id the identifier of the desired {@link ManagedObject}
     * @return the found {@link ManagedObject} (or null if not found)
     */
    public ManagedObject get(String id) {
        String response = cloudOfThingsRestClient.getResponse(id, RELATIVE_API_URL, CONTENT_TYPE_MANAGEDOBJECT);
        ExtensibleObject extensibleObject = gson.fromJson(response, ManagedObject.class);
        if (extensibleObject != null) {
            return new ManagedObject(extensibleObject);
        } else {
            return null;
        }
    }

    /**
     * Retrieves a ManagedObject identified by ID from the platform.
     *
     * @param id          the identifier of the desired {@link ManagedObject}
     * @param withParents set true to load with parents (parentDevices, parentAssets)
     * @return the found {@link ManagedObject} (or null if not found)
     */
    public ManagedObject get(String id, boolean withParents) {
        String response = cloudOfThingsRestClient.getResponse(id + "?withParents=" + Boolean.toString(withParents), RELATIVE_API_URL, CONTENT_TYPE_MANAGEDOBJECT);
        ExtensibleObject extensibleObject = gson.fromJson(response, ManagedObject.class);
        if (extensibleObject != null) {
            return new ManagedObject(extensibleObject);
        } else {
            return null;
        }
    }

    /**
     * Deletes the ManagedObject.
     *
     * @param id the identifier of the {@link ManagedObject} to delete.
     */
    public void delete(String id) {
        cloudOfThingsRestClient.delete(id, RELATIVE_API_URL);
    }

    /**
     * Updates the ManagedObject in the platform.
     *
     * @param managedObject object to update.
     */
    public void update(ManagedObject managedObject) {
        String json = gson.toJson(managedObject);
        cloudOfThingsRestClient.doPutRequest(json, RELATIVE_API_URL + "/" + managedObject.getId(), CONTENT_TYPE_MANAGEDOBJECT);
    }

    /**
     * Add a new child device {@link ManagedObject} to the {@link ManagedObject}.
     *
     * @param parentManagedObject           the parent ManagedObject, gets child.
     * @param managedObjectReferenceToChild the reference to the new child ManagedObject
     * @since 0.2.0
     */
    public void addChildDeviceToManagedObject(ManagedObject parentManagedObject, ManagedObjectReference managedObjectReferenceToChild) {
        String json = gson.toJson(managedObjectReferenceToChild);
        String selfRef = parentManagedObject.getChildDevices().getSelf();
        int idx = selfRef.lastIndexOf("inventory");

        cloudOfThingsRestClient.doPostRequest(json, selfRef.substring(idx), CONTENT_TYPE_MANAGEDOBJECTREF, ACCEPT_MANAGEDOBJECTREF);
    }


    /**
     * Deletes ManagedObjectReferences.
     * <p>
     * This method can be used to any of this reference collections:
     * <ul>
     * <li>childAssets</li>
     * <li>childDevices</li>
     * <li>parentAssets</li>
     * <li>parentDevices</li>
     * </ul>
     *
     * @param managedObjectReference the object to delete
     * @since 0.2.0
     */
    public void removeManagedObjectReference(ManagedObjectReference managedObjectReference) {
        cloudOfThingsRestClient.delete(managedObjectReference.getSelf());
    }

    /**
     * Add a new child asset {@link ManagedObject} to the {@link ManagedObject}.
     *
     * @param parentManagedObject           the parent ManagedObject, gets child.
     * @param managedObjectReferenceToChild the reference to the new child ManagedObject
     * @since 0.2.0
     */
    public void addChildAssetToManagedObject(ManagedObject parentManagedObject, ManagedObjectReference managedObjectReferenceToChild) {
        String json = gson.toJson(managedObjectReferenceToChild);
        String selfRef = parentManagedObject.getChildAssets().getSelf();
        int idx = selfRef.lastIndexOf("inventory");

        cloudOfThingsRestClient.doPostRequest(json, selfRef.substring(idx), CONTENT_TYPE_MANAGEDOBJECTREF, ACCEPT_MANAGEDOBJECTREF);
    }

    /**
     * Retrieves Managed Objects.
     *
     * @param pageSize size of the results (Max. 2000)
     * @return the found Managed Objects.
     * @since 0.3.0
     */
    public ManagedObjectCollection getManagedObjects(int pageSize) {
        return new ManagedObjectCollection(
                cloudOfThingsRestClient,
                RELATIVE_API_URL,
                gson,
                null,
                pageSize);
    }

    /**
     * Retrieves Measurements filtered by criteria.
     *
     * @param filters    filters of measurement attributes.
     * @param pageSize size of the results (Max. 2000)
     * @return the MeasurementsCollections to naviagte through the results.
     * @since 0.3.0
     */
    public ManagedObjectCollection getManagedObjects(Filter.FilterBuilder filters, int pageSize) {
        if(filters != null) {
            filters.validateSupportedFilters(acceptedFilters);
        }
        return new ManagedObjectCollection(
                cloudOfThingsRestClient,
                RELATIVE_API_URL,
                gson,
                filters,
                pageSize);
    }

    /**
     * Registers a device as child device of another device
     *
     * @param parentDevice device which should become the parent of the childDevice
     * @param childDevice  device which should be marked as child device of the parentDevice
     */
    public void registerAsChildDevice(ManagedObject parentDevice, ManagedObject childDevice) {

        final String json = String.format("{ \"managedObject\" : { \"id\" : \"%s\" } }", childDevice.getId());
        final String apiPattern = RELATIVE_API_URL + "%s/childDevices";
        final String api = String.format(
                apiPattern,
                parentDevice.getId()
        );

        cloudOfThingsRestClient.doPostRequest(json, api, CONTENT_TYPE_MANAGEDOBJECTREF, ACCEPT_MANAGEDOBJECTREF);
    }

    /**
     * Retrieve supported measurements of a managed object
     *
     * @param managedObjectId ID of the device for which supported measurements need to be retrieved
     */
    public ArrayList<String> getSupportedMeasurements(String managedObjectId) {
        Objects.requireNonNull(managedObjectId);
        String supportedMeasurementsApi = RELATIVE_API_URL + managedObjectId + "/supportedMeasurements";
        String response = cloudOfThingsRestClient.getResponse(supportedMeasurementsApi);
        JsonParser jsonParser = new JsonParser();
        JsonArray responseJsonArray = jsonParser.parse(response).getAsJsonObject().getAsJsonArray(SUPPORTED_MEASUREMENTS);
        return gson.fromJson(responseJsonArray, new TypeToken<List<String>>(){}.getType());
    }
}
