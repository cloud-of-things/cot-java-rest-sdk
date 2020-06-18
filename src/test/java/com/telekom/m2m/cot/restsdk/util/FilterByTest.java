package com.telekom.m2m.cot.restsdk.util;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class FilterByTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void getFilterByThrowsExceptionIfFilterDoesNotExist() {
        FilterBy.getFilterBy("this-is-missing");
    }

    @Test
    public void getFilterByReturnsRequestedFilter() {
        final FilterBy filter = FilterBy.getFilterBy(FilterBy.BYUSER.getFilterKey());

        assertEquals(FilterBy.BYUSER, filter);
    }
}
