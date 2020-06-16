package com.telekom.m2m.cot.restsdk.util;

import com.telekom.m2m.cot.restsdk.devicecontrol.OperationStatus;
import org.testng.annotations.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class FilterTest {

    @Test
    public void bySourceAddsValueToFilterString() {
        final String filter = Filter.build()
            .byAgentId("42")
            .buildFilter();

        assertTrue(filter.contains("42"));
    }

    @Test
    public void byTypeAddsValueToFilterString() {
        final String filter = Filter.build()
            .byType("mqtt")
            .buildFilter();

        assertTrue(filter.contains("mqtt"));
    }

    @Test
    public void byDateAddsValuesToFilterString() {
        final Instant start = LocalDate.of(2016, 1, 1)
            .atStartOfDay(ZoneId.of("Europe/Berlin"))
            .toInstant();
        final Instant end = LocalDate.of(2017, 1, 1)
            .atStartOfDay(ZoneId.of("Europe/Berlin"))
            .toInstant();

        final String filter = Filter.build()
            .byDate(Date.from(start), Date.from(end))
            .buildFilter();

        assertTrue(filter.contains("2016"));
        assertTrue(filter.contains("2017"));
    }

    @Test
    public void byFragmentTypeAddsValueToFilterString() {
        final String filter = Filter.build()
            .byFragmentType("test")
            .buildFilter();

        assertTrue(filter.contains("test"));
    }

    @Test
    public void byDeviceIdAddsValueToFilterString() {
        final String filter = Filter.build()
            .byDeviceId("42")
            .buildFilter();

        assertTrue(filter.contains("42"));
    }

    @Test
    public void byStatusAddsValueToFilterString() {
        final String filter = Filter.build()
            .byStatus("EXECUTING")
            .buildFilter();

        assertTrue(filter.contains("EXECUTING"));
    }

    @Test
    public void byStatusWithOperationParameterAddsValueToFilterString() {
        final String filter = Filter.build()
            .byStatus(OperationStatus.PENDING)
            .buildFilter();

        assertTrue(filter.contains(OperationStatus.PENDING.toString()));
    }

    @Test
    public void byTextAddsValueToFilterString() {
        final String filter = Filter.build()
            .byText("test")
            .buildFilter();

        assertTrue(filter.contains("test"));
    }

    @Test
    public void byListOfIdsAddsValueToFilterString() {
        final String filter = Filter.build()
            .byListOfIds("1,2,3")
            .buildFilter();

        assertTrue(filter.contains("1,2,3"));
    }

    @Test
    public void byAgentIdAddsValueToFilterString() {
        final String filter = Filter.build()
            .byAgentId("42")
            .buildFilter();

        assertTrue(filter.contains("42"));
    }

    @Test
    public void byUserAddsValueToFilterString() {
        final String filter = Filter.build()
            .byUser("matthias")
            .buildFilter();

        assertTrue(filter.contains("matthias"));
    }

    @Test
    public void byApplicationAddsValueToFilterString() {
        final String filter = Filter.build()
            .byApplication("unicorn")
            .buildFilter();

        assertTrue(filter.contains("unicorn"));
    }

    @Test
    public void multipleFiltersAreCombined() {
        final String filter = Filter.build()
            .byAgentId("42")
            .byUser("matthias")
            .buildFilter();

        assertTrue(filter.contains("42"));
        assertTrue(filter.contains("matthias"));
    }

    @Test
    public void settingSameFilterAgainOverwritesPreviousValue() {
        final String filter = Filter.build()
            .byUser("matthias")
            .byUser("harry")
            .buildFilter();

        assertFalse(filter.contains("matthias"));
        assertTrue(filter.contains("harry"));
    }

    @Test
    public void setFilterAddsProvidedFilter() {
        final String filter = Filter.build()
            .setFilter(FilterBy.BYTEXT, "test")
            .buildFilter();

        assertTrue(filter.contains("test"));
    }

    @Test
    public void setFiltersAddsAllProvidedFilters() {
        final HashMap<FilterBy, String> filters = new HashMap<>();
        filters.put(FilterBy.BYTEXT, "test");
        filters.put(FilterBy.BYUSER, "matthias");

        final String filter = Filter.build()
            .setFilters(filters)
            .buildFilter();

        assertTrue(filter.contains("test"));
        assertTrue(filter.contains("matthias"));
    }

    @Test
    public void usesCorrectFilterName() {
        final String filter = Filter.build()
            .byDeviceId("42")
            .buildFilter();

        assertTrue(filter.contains(FilterBy.BYDEVICEID.getFilterKey()));
    }

    @Test
    public void validateSupportedFiltersDoesNotThrowExceptionIfNullIsPassed() {
        final Filter.FilterBuilder builder = Filter.build()
            .byAgentId("42");

        // This should not throw an exception.
        builder.validateSupportedFilters(null);
    }

    @Test
    public void validateSupportedFiltersDoesNotThrowExceptionIfOnlyAcceptedFiltersHaveBeenAdded() {
        final Filter.FilterBuilder builder = Filter.build()
            .byAgentId("42");

        // This should not throw an exception.
        builder.validateSupportedFilters(Collections.singletonList(FilterBy.BYAGENTID));
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void validateSupportedFiltersThrowsExceptionIfNotAcceptedFilterHasBeenAdded() {
        final Filter.FilterBuilder builder = Filter.build()
            .byAgentId("42");

        builder.validateSupportedFilters(Collections.singletonList(FilterBy.BYDEVICEID));
    }
}
