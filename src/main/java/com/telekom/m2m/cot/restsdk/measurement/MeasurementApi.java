package com.telekom.m2m.cot.restsdk.measurement;

import com.google.gson.*;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.realtime.CepConnector;
import com.telekom.m2m.cot.restsdk.realtime.Notification;
import com.telekom.m2m.cot.restsdk.realtime.SubscriptionListener;
import com.telekom.m2m.cot.restsdk.util.*;

import javax.annotation.Nullable;
import java.util.*;

/**
 * The API object to operate with Measurements in the platform.
 * <p>
 * Created by Patrick Steinert on 07.02.16.
 *
 * @since 0.1.0
 */
public class MeasurementApi {

    private static final String CONTENT_TYPE_MEASUREMENT = "application/vnd.com.nsn.cumulocity.measurement+json;charset=UTF-8;ver=0.9";
    private static final String ACCEPT_MEASUREMENT = "application/vnd.com.nsn.cumulocity.measurement+json;charset=UTF-8;ver=0.9";
    private static final String CONTENT_TYPE_MEASUREMENT_COLLECTION = "application/vnd.com.nsn.cumulocity.measurementCollection+json;charset=UTF-8;ver=0.9";
    private static final String ACCEPT_MEASUREMENT_COLLECTION = "application/vnd.com.nsn.cumulocity.measurementCollection+json;charset=UTF-8;ver=0.9";
    private static final List<FilterBy> acceptedFilters = Arrays.asList(FilterBy.BYSOURCE, FilterBy.BYDATEFROM, FilterBy.BYDATETO, FilterBy.BYFRAGMENTTYPE, FilterBy.BYTYPE);

    private static final String MEASUREMENTS_API = "measurement/measurements/";

    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    private Gson gson = GsonUtils.createGson();
    private final CepConnector cepConnector;
    private HashMap<String, List<String>> notifications = new HashMap<>();

    /**
     * Internal Constructor.
     *
     * @param cloudOfThingsRestClient the configured rest client.
     */
    public MeasurementApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
        cepConnector = new CepConnector(cloudOfThingsRestClient, "cep/realtime");
    }


    /**
     * Retrives a specific Measurement.
     *
     * @param id of the desired Measurement.
     * @return the Measurement (or null if not found).
     */
    public Measurement getMeasurement(String id) {
        String response = cloudOfThingsRestClient.getResponse(id, MEASUREMENTS_API, CONTENT_TYPE_MEASUREMENT);
        //assuming: when response is null, then http status code is 404
        if (response == null) {
            throw new CotSdkException(404, "Measurement not found (id='" + id + "')");
        }
        return new Measurement(gson.fromJson(response, ExtensibleObject.class));
    }

    /**
     * Stores a Measurement.
     *
     * @param measurement the measurement to create.
     * @return the created measurement with the ID.
     */
    public Measurement createMeasurement(Measurement measurement) {
        String json = gson.toJson(measurement);
        String id = cloudOfThingsRestClient.doRequestWithIdResponse(json, MEASUREMENTS_API, CONTENT_TYPE_MEASUREMENT, ACCEPT_MEASUREMENT);
        measurement.setId(id);
        return measurement;
    }

    /**
     * Stores a list of Measurements.
     *
     * @param measurements the measurements to create.
     * @return the created measurement with the ID.
     */
    public List<Measurement> createMeasurements(final List<Measurement> measurements) {

        if (measurements.isEmpty()) {
            return Collections.emptyList();
        }

        final String json = gson.toJson(createJsonObject(measurements));

        final String response = cloudOfThingsRestClient.doPostRequest(json, MEASUREMENTS_API, CONTENT_TYPE_MEASUREMENT_COLLECTION, ACCEPT_MEASUREMENT_COLLECTION);

        return gson.fromJson(response, MeasurementsHolder.class)
                .getMeasurements();
    }

    /**
     * Deletes a Measurement.
     *
     * @param measurement the Measurement to delete
     */
    public void delete(Measurement measurement) {
        cloudOfThingsRestClient.delete(measurement.getId(), MEASUREMENTS_API);
    }

    /**
     * Retrieves Measurements.
     *
     * @param resultSize size of the results (Max. 2000)
     * @return the found Measurements.
     */
    public MeasurementCollection getMeasurements(int resultSize) {
        return new MeasurementCollection(
                cloudOfThingsRestClient,
                MEASUREMENTS_API,
                gson,
                null,
                resultSize);
    }

    /**
     * Retrieves Measurements filtered by criteria.
     *
     * @param filters    filters of measurement attributes.
     * @param resultSize size of the results (Max. 2000)
     * @return the MeasurementsCollections to navigate through the results.
     * @since 0.2.0
     */
    public MeasurementCollection getMeasurements(Filter.FilterBuilder filters, int resultSize) {
        if(filters != null) {
            filters.validateSupportedFilters(acceptedFilters);
        }
        return new MeasurementCollection(
                cloudOfThingsRestClient,
                MEASUREMENTS_API,
                gson,
                filters,
                resultSize);
    }

    /**
     * Deletes a collection of Measurements by criteria.
     *
     * @param filters filters of measurement attributes.
     */
    public void deleteMeasurements(@Nullable final Filter.FilterBuilder filters) {
        if(filters != null) {
            filters.validateSupportedFilters(acceptedFilters);
        }
        final String filterParams = Optional.ofNullable(filters)
            .map(filterBuilder -> filterBuilder.buildFilter() + "&")
            .orElse("");
        // The x query parameter is a workaround. Without, it seems as if there are cases where deletion does not work.
        cloudOfThingsRestClient.delete("", MEASUREMENTS_API + "?" + filterParams + "x=");
    }

    private JsonObject createJsonObject(final List<Measurement> measurements) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.add("measurements", listToJsonArray(measurements));
        return jsonObject;
    }

    private JsonArray listToJsonArray(final List<Measurement> measurements) {
        final JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(gson.toJson(measurements)).getAsJsonArray();
    }

    public void subscribeToMeasurementsNotifications(String deviceManagedObjectId) {
        if (managedObjectIdIsValid(deviceManagedObjectId)) {
            NotificationListener listener = new NotificationListener();
            cepConnector.addListener(listener);
            cepConnector.subscribe("/measurements/" + deviceManagedObjectId);
            if (!cepConnector.isConnected()) {
                cepConnector.connect();
            }
        }
    }

    public void unsubscribeFromMeasurementsNotifications(String deviceManagedObjectId) {
        if (cepConnector.isConnected()) {
            cepConnector.unsubscribe("/measurements/" + deviceManagedObjectId);
        }
    }

    // execution of this method is restricted to be performed by only one thread
    public synchronized List<String> pullNotifications(String managedObjectId) {
        List<String> notificationsForManagedObject = notifications.get(managedObjectId);
        notifications.remove(managedObjectId);
        return notificationsForManagedObject;
    }

    private boolean managedObjectIdIsValid(String managedObjectId) {
        if (managedObjectId == null || managedObjectId.contains("*")) {
            return false;
        } else {
            return true;
        }
    }


    private class NotificationListener implements SubscriptionListener {

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();

        @Override
        public void onNotification(String channel, Notification notification) {
            String notificationData = gson.toJson(notification.getData());
            System.out.println("New notification on channel " + channel + ":\n");
            System.out.println(notificationData);
            addNotificationToHashMap(channel, notificationData);
        }

        @Override
        public void onError(String channel, Throwable error) {
            System.out.println("There was an error on channel " + channel + ": " + error);
        }
    }

    private synchronized void addNotificationToHashMap(String channel, String notificationData) {
        String managedObjectId = channel.replace("/measurements/", "");
        List<String> notificationsList = notifications.get(managedObjectId);

        if(notificationsList == null) {
            notificationsList = new ArrayList<>();
            notificationsList.add(notificationData);
            notifications.put(managedObjectId, notificationsList);
        } else {
            notifications.get(managedObjectId).add(notificationData);
        }
    }
}
