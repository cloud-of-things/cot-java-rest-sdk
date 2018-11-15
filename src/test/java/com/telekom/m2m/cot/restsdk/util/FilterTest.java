package com.telekom.m2m.cot.restsdk.util;

import com.telekom.m2m.cot.restsdk.devicecontrol.OperationStatus;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

public class FilterTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void bySourceAddsValueToFilterString() {
        final String filter = Filter.build()
            .byAgentId("42")
            .buildFilter();

        assertThat(filter, containsString("42"));
    }

    @Test
    public void byTypeAddsValueToFilterString() {
        final String filter = Filter.build()
            .byType("mqtt")
            .buildFilter();

        assertThat(filter, containsString("mqtt"));
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

        assertThat(filter, containsString("2016"));
        assertThat(filter, containsString("2017"));
    }

    @Test
    public void byFragmentTypeAddsValueToFilterString() {
        final String filter = Filter.build()
            .byFragmentType("test")
            .buildFilter();

        assertThat(filter, containsString("test"));
    }

    @Test
    public void byDeviceIdAddsValueToFilterString() {
        final String filter = Filter.build()
            .byDeviceId("42")
            .buildFilter();

        assertThat(filter, containsString("42"));
    }

    @Test
    public void byStatusAddsValueToFilterString() {
        final String filter = Filter.build()
            .byStatus("EXECUTING")
            .buildFilter();

        assertThat(filter, containsString("EXECUTING"));
    }

    @Test
    public void byStatusWithOperationParameterAddsValueToFilterString() {
        final String filter = Filter.build()
            .byStatus(OperationStatus.PENDING)
            .buildFilter();

        assertThat(filter, containsString(OperationStatus.PENDING.toString()));
    }

    @Test
    public void byTextAddsValueToFilterString() {
        final String filter = Filter.build()
            .byText("test")
            .buildFilter();

        assertThat(filter, containsString("test"));
    }

    @Test
    public void byListOfIdsAddsValueToFilterString() {
        final String filter = Filter.build()
            .byListOfIds("1,2,3")
            .buildFilter();

        assertThat(filter, containsString("1,2,3"));
    }

    @Test
    public void byAgentIdAddsValueToFilterString() {
        final String filter = Filter.build()
            .byAgentId("42")
            .buildFilter();

        assertThat(filter, containsString("42"));
    }

    @Test
    public void byUserAddsValueToFilterString() {
        final String filter = Filter.build()
            .byUser("matthias")
            .buildFilter();

        assertThat(filter, containsString("matthias"));
    }

    @Test
    public void byApplicationAddsValueToFilterString() {
        final String filter = Filter.build()
            .byApplication("unicorn")
            .buildFilter();

        assertThat(filter, containsString("unicorn"));
    }

    @Test
    public void multipleFiltersAreCombined() {
        final String filter = Filter.build()
            .byAgentId("42")
            .byUser("matthias")
            .buildFilter();

        assertThat(filter, containsString("42"));
        assertThat(filter, containsString("matthias"));
    }

    @Test
    public void settingSameFilterAgainOverwritesPreviousValue() {
        final String filter = Filter.build()
            .byUser("matthias")
            .byUser("harry")
            .buildFilter();

        assertThat(filter, not(containsString("matthias")));
        assertThat(filter, containsString("harry"));
    }

    @Test
    public void setFilterAddsProvidedFilter() {
        final String filter = Filter.build()
            .setFilter(FilterBy.BYTEXT, "test")
            .buildFilter();

        assertThat(filter, containsString("test"));
    }

    @Test
    public void setFiltersAddsAllProvidedFilters() {
        final HashMap<FilterBy, String> filters = new HashMap<>();
        filters.put(FilterBy.BYTEXT, "test");
        filters.put(FilterBy.BYUSER, "matthias");

        final String filter = Filter.build()
            .setFilters(filters)
            .buildFilter();

        assertThat(filter, containsString("test"));
        assertThat(filter, containsString("matthias"));
    }

    @Test
    public void usesCorrectFilterName() {
        final String filter = Filter.build()
            .byDeviceId("42")
            .buildFilter();

        assertThat(filter, containsString(FilterBy.BYDEVICEID.getFilterKey()));
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

    @Test
    public void validateSupportedFiltersThrowsExceptionIfNotAcceptedFilterHasBeenAdded() {
        final Filter.FilterBuilder builder = Filter.build()
            .byAgentId("42");

        exception.expect(CotSdkException.class);
        builder.validateSupportedFilters(Collections.singletonList(FilterBy.BYDEVICEID));
    }
}
