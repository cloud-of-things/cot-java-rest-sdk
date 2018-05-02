package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.JsonElement;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.annotation.Nonnull;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class IterableObjectPaginationTest {

    private static final int PAGE_SIZE_IN_TESTS = 3;

    /**
     * System under test.
     */
    private IterableObjectPagination<ExtensibleObject> pagination;

    /**
     * A mocked REST client.
     */
    private CloudOfThingsRestClient cloudOfThingsRestClient;

    @BeforeTest
    public void setup() {
        cloudOfThingsRestClient = mock(CloudOfThingsRestClient.class);
        pagination = new IterableObjectPagination<ExtensibleObject>(
            cloudOfThingsRestClient,
            "test/url",
            GsonUtils.createGson(),
            "application/json",
            "test-objects",
            null,
            PAGE_SIZE_IN_TESTS
        ) {
            @Nonnull
            @Override
            protected ExtensibleObject convertJsonToObject(@Nonnull JsonElement element) {
                return gson.fromJson(element, ExtensibleObject.class);
            }
        };
    }

    @Test
    public void streamReadsObjectsUntilLastPageIsReached() {
        simulateNumberOfObjectsOnPages(10);

        final List<ExtensibleObject> objects = pagination.stream().collect(Collectors.toList());

        assertEquals(10, objects.size());
    }

    @Test
    public void streamReadsObjectsIfThereIsOnlyOnePage() {
        simulateNumberOfObjectsOnPages(2);

        final List<ExtensibleObject> objects = pagination.stream().collect(Collectors.toList());

        assertEquals(2, objects.size());
    }

    @Test
    public void loadsOnlyNecessaryPagesIfNotAllObjectsInStreamAreConsumed() {
        simulateNumberOfObjectsOnPages(10);

        final List<ExtensibleObject> objects = pagination.stream().limit(5).collect(Collectors.toList());

        assertEquals(5, objects.size());
        assertPageNotRequested(3);
    }

    @Test
    public void doesNotLoadAnyPageIfStreamDoesNotReadAnyObject() {
        simulateNumberOfObjectsOnPages(10);

        final List<ExtensibleObject> objects = pagination.stream().limit(0).collect(Collectors.toList());

        assertEquals(0, objects.size());
        assertPageNotRequested(1);
        assertPageNotRequested(2);
        assertPageNotRequested(3);
    }

    private void simulateNumberOfObjectsOnPages(final int numberOfObjects) {

    }

    /**
     * Asserts that the given page was *not* requested via HTTP.
     *
     * @param pageNumber The 1-indexed page number.
     */
    private void assertPageNotRequested(int pageNumber) {
        assertTrue(
            pageNumber > 0,
            "Invalid page number value '" + pageNumber + "', page numbers *must* start at one."
        );
    }
}
