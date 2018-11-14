package com.telekom.m2m.cot.restsdk.util;

import com.telekom.m2m.cot.restsdk.devicecontrol.OperationStatus;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Filter to build as criteria for collection queries.
 *
 * @author Patrick Steinert
 * @since 0.2.0
 */
public class Filter {

    @Nonnull
    private final HashMap<String, String> arguments = new HashMap<>();

    private Filter() {
    }

    /**
     * Use to create a FilterBuilder.
     *
     * @return FilterBuilder.
     */
    @Nonnull
    public static FilterBuilder build() {
        return new FilterBuilder();
    }


    /**
     * Filter Builder for build collection queries.
     * <p><b>Usage:</b></p>
     * <pre>
     * {@code
     * measurementApi.getMeasurements(
     *     Filter.build()
     *         .byFragmentType("com_telekom_example_SampleTemperatureSensor")
     *         .bySource("1122334455")
     *     );
     * }
     * </pre>
     */
    public static class FilterBuilder {
        private Filter instance = new Filter();

        /**
         * Creates a parameter string.
         *
         * @return a string in pattern <code>arg1=val1&amp;arg2=val2</code>
         */
        public String buildFilter() {
            String qs = "";
            Set<Map.Entry<String, String>> set = instance.arguments.entrySet();

            for (Map.Entry<String, String> entry : set) {
                qs += entry.getKey() + "=" + entry.getValue() + "&";
            }
            return qs.substring(0, qs.length() - 1);
        }

        /**
         * Adds a build for source id.
         *
         * @param id ID of the source ({@link com.telekom.m2m.cot.restsdk.inventory.ManagedObject}) to build for.
         * @return an appropriate build Object.
         */
        @Deprecated
        public FilterBuilder bySource(String id) {
            instance.arguments.put("source", id);
            return this;
        }

        /**
         * Adds a build for type.
         *
         * @param type type to build for.
         * @return an appropriate build Object.
         */
        @Deprecated
        public FilterBuilder byType(String type) {
            //instance.type = type;
            instance.arguments.put("type", type);
            return this;
        }

        /**
         * Adds a build for a time range.
         *
         * @param from start of the date range (more in the history).
         * @param to   end of the date range (more in the future).
         * @return an appropriate build Object.
         */
        @Deprecated
        public FilterBuilder byDate(Date from, Date to) {
            //instance.dateFrom = from;
            //instance.dateTo = to;
            instance.arguments.put("dateFrom", CotUtils.convertDateToTimestring(from));
            instance.arguments.put("dateTo", CotUtils.convertDateToTimestring(to));
            return this;
        }

        /**
         * Adds a build for a fragment.
         *
         * @param fragmentType to build for.
         * @return an appropriate build Object.
         */
        @Deprecated
        public FilterBuilder byFragmentType(String fragmentType) {
            instance.arguments.put("fragmentType", fragmentType);
            return this;
        }

        /**
         * Adds a build for a deviceId.
         *
         * @param deviceId to build for.
         * @return an appropriate build Object.
         */
        @Deprecated
        public FilterBuilder byDeviceId(String deviceId) {
            instance.arguments.put("deviceId", deviceId);
            return this;
        }

        /**
         * Adds a build for a status.
         *
         * @param operationStatus to build for.
         * @return an appropriate build Object.
         */
        @Deprecated
        public FilterBuilder byStatus(OperationStatus operationStatus) {
            instance.arguments.put("status", operationStatus.toString());
            return this;
        }

        /**
         * Adds a build for a text.
         *
         * @param text to build for.
         * @return an appropriate build Object.
         */
        @Deprecated
        public FilterBuilder byText(String text) {
            instance.arguments.put("text", text);
            return this;
        }

        /**
         * Adds a build for a list of comma separated Ids.
         *
         * @param listOfIds to build for (comma separated).
         * @return an appropriate build Object.
         */
        @Deprecated
        public FilterBuilder byListOfIds(String listOfIds) {
            instance.arguments.put("ids", listOfIds);
            return this;
        }

        /**
         * Adds a build for a Alarm status.
         *
         * @param status to build for.
         * @return an appropriate build Object.
         * @since 0.3.0
         */
        @Deprecated
        public FilterBuilder byStatus(String status) {
            instance.arguments.put("status", status);
            return this;
        }

        /**
         * Adds a build for an agentId.
         *
         * @param agentId to build for.
         * @return an appropriate build Object.
         * @since 0.3.1
         */
        @Deprecated
        public FilterBuilder byAgentId(String agentId) {
            instance.arguments.put("agentId", agentId);
            return this;
        }

        /**
         * Adds a build for a user.
         *
         * @param user to build for.
         * @return an appropriate build Object.
         * @since 0.6.0
         */
        @Deprecated
        public FilterBuilder byUser(String user) {
            instance.arguments.put("user", user);
            return this;
        }
        
        /**
         * Adds a build for an application.
         *
         * @param application to build for.
         * @return an appropriate build Object.
         * @since 0.6.0
         */
        @Deprecated
        public FilterBuilder byApplication(String application) {
            instance.arguments.put("application", application);
            return this;
        }

        /**
         * adds a build for a filter
         *
         * @param filterBy enum value, which filter should to be added
         * @param value value for filter, which should be added
         * @return an appropriate build Object
         */
        public FilterBuilder setFilter(FilterBy filterBy, String value) {
           instance.arguments.put(filterBy.getFilterKey(), value);
           return this;
        }

        /**
         * adds a build for a hashmap of filters
         *
         * @param hashmap contains enum values and vaslues for filter builds
         * @return an appropriate build Object
         */
        public FilterBuilder setFilters(HashMap<FilterBy, String> hashmap){
            for (Map.Entry e : hashmap.entrySet()){
                instance.arguments.put(((FilterBy)e.getKey()).getFilterKey(), (String) e.getValue());
            }
            return this;
        }

        /**
         * validate all set filters allowed by the api
         *
         * @param filters list of filters, which have to be checked
         */
        public void validateSupportedFilters(List filters) {
            //do nothing, when filters is null
            if (filters != null) {
                for (Map.Entry e : instance.arguments.entrySet()) {
                    if (!filters.contains(FilterBy.getFilterBy((String) e.getKey()))) {
                        throw new CotSdkException(String.format("This filter is not avaible in used api [%s]", e.getKey()));
                    }
                }
            }
        }

    }

}
