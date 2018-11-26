package com.telekom.m2m.cot.restsdk.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class FilterByTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void getFilterByThrowsExceptionIfFilterDoesNotExist() {
        exception.expect(IllegalArgumentException.class);
        FilterBy.getFilterBy("this-is-missing");
    }

    @Test
    public void getFilterByReturnsRequestedFilter() {
        final FilterBy filter = FilterBy.getFilterBy(FilterBy.BYUSER.getFilterKey());

        assertEquals(FilterBy.BYUSER, filter);
    }
}
