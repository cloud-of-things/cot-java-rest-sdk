package com.telekom.m2m.cot.restsdk.util;

import com.telekom.m2m.cot.restsdk.devicecontrol.OperationStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Filter to build as criteria for collection queries.
 *
 *
 * @since 0.2.0
 * @author Patrick Steinert
 */
public class Filter {

    private HashMap<String, String> arguments = new HashMap<String, String>();

    private Filter() {
    }

    /**
     * Use to create a FilterBuilder.
     *
     * @return FilterBuilder.
     */
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
         * Adds a build for source id.
         *
         * @param id ID of the source ({@link com.telekom.m2m.cot.restsdk.inventory.ManagedObject}) to build for.
         * @return an approprieate build Object.
         */
        public FilterBuilder bySource(String id) {
            instance.arguments.put("source", id);
            return this;
        }

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
         * Adds a build for type.
         *
         * @param type type to build for.
         * @return an approprieate build Object.
         */
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
         * @return an approprieate build Object.
         */
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
         * @return an approprieate build Object.
         */
        public FilterBuilder byFragmentType(String fragmentType) {
            instance.arguments.put("fragmentType", fragmentType);
            return this;
        }

        /**
         * Adds a build for a deviceId.
         *
         * @param deviceId to build for.
         * @return an approprieate build Object.
         */
        public FilterBuilder byDeviceId(String deviceId) {
            instance.arguments.put("deviceId", deviceId);
            return this;
        }

        /**
         * Adds a build for a status.
         *
         * @param operationStatus to build for.
         * @return an approprieate build Object.
         */
        public FilterBuilder byStatus(OperationStatus operationStatus) {
            instance.arguments.put("status", operationStatus.toString());
            return this;
        }

        /**
         * Adds a build for a text.
         *
         * @param text to build for.
         * @return an approprieate build Object.
         */
        public FilterBuilder byText(String text) {
            instance.arguments.put("text", text);
            return this;
        }

        /**
         * Adds a build for a list of comma separated Ids.
         *
         * @param listOfIds to build for (comma separated).
         * @return an approprieate build Object.
         */
        public FilterBuilder byListOfIds(String listOfIds) {
            instance.arguments.put("ids", listOfIds);
            return this;
        }
    }

}
