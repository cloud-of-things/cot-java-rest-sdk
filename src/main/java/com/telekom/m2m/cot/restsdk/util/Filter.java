package com.telekom.m2m.cot.restsdk.util;

import com.telekom.m2m.cot.restsdk.devicecontrol.OperationStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Filter to build as criteria for collection queries.
 *
 * @author Patrick Steinert
 * @since 0.2.0
 */
public class Filter {

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

        @Nonnull
        private final HashMap<FilterBy, String> filters = new HashMap<>();

        /**
         * Creates a parameter string.
         *
         * @return a string in pattern <code>arg1=val1&amp;arg2=val2</code>
         */
        @Nonnull
        public String buildFilter() {
            return filters.entrySet().stream()
                .map(filter -> filter.getKey().getFilterKey() + "=" + filter.getValue())
                .collect(Collectors.joining("&"));
        }

        /**
         * Adds a build for source id.
         *
         * @param id ID of the source ({@link com.telekom.m2m.cot.restsdk.inventory.ManagedObject}) to build for.
         * @return an appropriate build Object.
         */
        @Nonnull
        public FilterBuilder bySource(String id) {
            filters.put(FilterBy.BYSOURCE, id);
            return this;
        }

        /**
         * Adds a build for type.
         *
         * @param type type to build for.
         * @return an appropriate build Object.
         */
        @Nonnull
        public FilterBuilder byType(String type) {
            filters.put(FilterBy.BYTYPE, type);
            return this;
        }

        /**
         * Adds a build for a time range.
         *
         * @param from start of the date range (more in the history).
         * @param to   end of the date range (more in the future).
         * @return an appropriate build Object.
         */
        @Nonnull
        public FilterBuilder byDate(Date from, Date to) {
            filters.put(FilterBy.BYDATEFROM, CotUtils.convertDateToTimestring(from));
            filters.put(FilterBy.BYDATETO, CotUtils.convertDateToTimestring(to));
            return this;
        }

        /**
         * Adds a build for a fragment.
         *
         * @param fragmentType to build for.
         * @return an appropriate build Object.
         */
        @Nonnull
        public FilterBuilder byFragmentType(String fragmentType) {
            filters.put(FilterBy.BYFRAGMENTTYPE, fragmentType);
            return this;
        }

        /**
         * Adds a build for a deviceId.
         *
         * @param deviceId to build for.
         * @return an appropriate build Object.
         */
        @Nonnull
        public FilterBuilder byDeviceId(String deviceId) {
            filters.put(FilterBy.BYDEVICEID, deviceId);
            return this;
        }

        /**
         * Adds a build for a status.
         *
         * @param operationStatus to build for.
         * @return an appropriate build Object.
         */
        @Nonnull
        public FilterBuilder byStatus(OperationStatus operationStatus) {
            filters.put(FilterBy.BYSTATUS, operationStatus.toString());
            return this;
        }

        /**
         * Adds a build for a text.
         *
         * @param text to build for.
         * @return an appropriate build Object.
         */
        @Nonnull
        public FilterBuilder byText(String text) {
            filters.put(FilterBy.BYTEXT, text);
            return this;
        }

        /**
         * Adds a build for a list of comma separated Ids.
         *
         * @param listOfIds to build for (comma separated).
         * @return an appropriate build Object.
         */
        @Nonnull
        public FilterBuilder byListOfIds(String listOfIds) {
            filters.put(FilterBy.BYLISTOFIDs, listOfIds);
            return this;
        }

        /**
         * Adds a build for a Alarm status.
         *
         * @param status to build for.
         * @return an appropriate build Object.
         * @since 0.3.0
         */
        @Nonnull
        public FilterBuilder byStatus(String status) {
            filters.put(FilterBy.BYSTATUS, status);
            return this;
        }

        /**
         * Adds a build for an agentId.
         *
         * @param agentId to build for.
         * @return an appropriate build Object.
         * @since 0.3.1
         */
        @Nonnull
        public FilterBuilder byAgentId(String agentId) {
            filters.put(FilterBy.BYAGENTID, agentId);
            return this;
        }

        /**
         * Adds a build for a user.
         *
         * @param user to build for.
         * @return an appropriate build Object.
         * @since 0.6.0
         */
        @Nonnull
        public FilterBuilder byUser(String user) {
            filters.put(FilterBy.BYUSER, user);
            return this;
        }
        
        /**
         * Adds a build for an application.
         *
         * @param application to build for.
         * @return an appropriate build Object.
         * @since 0.6.0
         */
        @Nonnull
        public FilterBuilder byApplication(String application) {
            filters.put(FilterBy.BYAPPLICATION, application);
            return this;
        }

        /**
         * adds a build for a filter
         *
         * @param filterBy enum value, which filter should to be added
         * @param value value for filter, which should be added
         * @return an appropriate build Object
         */
        @Nonnull
        public FilterBuilder setFilter(@Nonnull final FilterBy filterBy, @Nonnull final String value) {
           filters.put(filterBy, value);
           return this;
        }

        /**
         * adds a build for a map of filters
         *
         * @param filtersToAdd contains enum values and values for filter builds
         * @return an appropriate build Object
         */
        @Nonnull
        public FilterBuilder setFilters(@Nonnull final Map<FilterBy, String> filtersToAdd){
            filters.putAll(filtersToAdd);
            return this;
        }

        /**
         * validate all set filters allowed by the api
         *
         * @param acceptedFilters list of filters, which are allowed. Pass null if all filters are allowed.
         * @throws CotSdkException If a filter that is not allowed is detected.
         */
        public void validateSupportedFilters(@Nullable final List<FilterBy> acceptedFilters) {
            // Do nothing, when all filters are accepted.
            if (acceptedFilters != null) {
                for (final Map.Entry<FilterBy, String> definedFilter : filters.entrySet()) {
                    if (!acceptedFilters.contains(definedFilter.getKey())) {
                        throw new CotSdkException(String.format("This filter is not available in used api [%s]", definedFilter.getKey()));
                    }
                }
            }
        }
    }

}
