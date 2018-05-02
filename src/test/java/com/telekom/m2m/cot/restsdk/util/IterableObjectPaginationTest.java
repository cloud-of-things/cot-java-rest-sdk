package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.annotation.Nonnull;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;
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
        cloudOfThingsRestClient = createRestClient();
        pagination = new IterableObjectPagination<ExtensibleObject>(
            cloudOfThingsRestClient,
            "test/url",
            GsonUtils.createGson(),
            "application/json",
            "operations",
            null,
            PAGE_SIZE_IN_TESTS
        ) {
            @Nonnull
            @Override
            protected ExtensibleObject convertJsonToObject(@Nonnull final JsonElement element) {
                return gson.fromJson(element, ExtensibleObject.class);
            }
        };
    }

    @Test
    public void streamReadsObjectsUntilLastPageIsReached() {
        simulateNumberOfObjectsOnPages(10);

        final List<ExtensibleObject> objects = pagination.stream().collect(Collectors.toList());

        assertEquals(objects.size(), 10);
    }

    @Test
    public void streamReturnsDifferentObjectsOnEachCall() {
        simulateNumberOfObjectsOnPages(10);

        try (
            final Stream<ExtensibleObject> first = pagination.stream();
            final Stream<ExtensibleObject> second = pagination.stream()
        ) {
            assertNotSame(first, second);
        }
    }

    @Test
    public void multipleStreamCallsReturnSameNumberOfObjects() {
        simulateNumberOfObjectsOnPages(10);

        final List<ExtensibleObject> first = pagination.stream().collect(Collectors.toList());
        final List<ExtensibleObject> second = pagination.stream().collect(Collectors.toList());

        assertEquals(first.size(), second.size());
    }

    @Test
    public void streamReadsObjectsIfThereIsOnlyOnePage() {
        simulateNumberOfObjectsOnPages(2);

        final List<ExtensibleObject> objects = pagination.stream().collect(Collectors.toList());

        assertEquals(objects.size(), 2);
    }

    @Test
    public void loadsOnlyNecessaryPagesIfNotAllObjectsInStreamAreConsumed() {
        simulateNumberOfObjectsOnPages(10);

        final List<ExtensibleObject> objects = pagination.stream().limit(5).collect(Collectors.toList());

        assertEquals(objects.size(), 5);
        assertPageNotRequested(4);
        assertPageNotRequested(3);
    }

    @Test
    public void doesNotLoadAnyPageIfStreamDoesNotReadAnyObject() {
        simulateNumberOfObjectsOnPages(10);

        final List<ExtensibleObject> objects = pagination.stream().limit(0).collect(Collectors.toList());

        assertEquals(objects.size(), 0);
        assertPageNotRequested(4);
        assertPageNotRequested(3);
        assertPageNotRequested(2);
        assertPageNotRequested(1);
    }

    @Test
    public void returnsNoObjectsIfPageIsEmpty() {
        simulateNumberOfObjectsOnPages(0);

        final List<ExtensibleObject> objects = pagination.stream().collect(Collectors.toList());

        assertEquals(objects.size(), 0);
    }

    private void simulateNumberOfObjectsOnPages(final int numberOfObjects) {
        if (numberOfObjects == 0) {
            simulatePageResponse(1, readTemplate("pages/no-filter/empty-page.json"));
            return;
        }
        final String pageTemplate = readTemplate("pages/no-filter/page.json.template");
        final String itemTemplate = readTemplate("pages/no-filter/item.json.template");
        final int totalPages = ceilDiv(numberOfObjects, PAGE_SIZE_IN_TESTS);
        for (int page = 1; page <= totalPages; page++) {
            final int itemsOnPage = (page == totalPages) ? numberOfObjects % PAGE_SIZE_IN_TESTS : PAGE_SIZE_IN_TESTS;
            final String itemsJson = IntStream.range(1, itemsOnPage + 1)
                .mapToObj(id -> itemTemplate.replace("%%id%%", String.valueOf(id)))
                .collect(Collectors.joining(","));
            final String body = pageTemplate
                .replace("%%totalPages%%", String.valueOf(totalPages))
                .replace("%%pageSize%%", String.valueOf(PAGE_SIZE_IN_TESTS))
                .replace("%%currentPage%%", String.valueOf(page))
                .replace("%%previousPage%%", String.valueOf(page - 1))
                .replace("%%nextPage%%", String.valueOf(page + 1))
                .replace("%%items%%", itemsJson);
            final JsonObject document = GsonUtils.createGson(true).fromJson(body, JsonElement.class).getAsJsonObject();
            if (page == 1) {
                document.remove("prev");
            }
            if (page == totalPages) {
                document.remove("next");
            }
            simulatePageResponse(page, document.toString());
        }
    }

    /**
     * Asserts that the given page was *not* requested via HTTP.
     *
     * @param pageNumber The 1-indexed page number.
     */
    private void assertPageNotRequested(final int pageNumber) {
        assertTrue(
            pageNumber > 0,
            "Invalid page number value '" + pageNumber + "', page numbers *must* start at one."
        );
        verify(cloudOfThingsRestClient, never()).getResponse(eq(pageUrl(pageNumber)));
    }

    /**
     * Simulates a HTTP response body for the given page.
     *
     * @param pageNumber The number of the simulated page.
     * @param body The returned content.
     */
    private void simulatePageResponse(final int pageNumber, @Nonnull final String body) {
        doReturn(body)
            .when(cloudOfThingsRestClient)
            .getResponse(eq(pageUrl(pageNumber)));
    }

    /**
     * @param pageNumber The 1-indexed page number.
     * @return The API URL to the page.
     */
    @Nonnull
    private String pageUrl(final int pageNumber) {
        return "test/url?currentPage=" + pageNumber + "&pageSize=" + PAGE_SIZE_IN_TESTS;
    }

    /**
     * Reads the content of a test data file.
     *
     * @param fileResourcePath Path to the resource, relative the test package.
     * @return The file content.
     */
    @Nonnull
    private String readTemplate(final String fileResourcePath) {
        final InputStream input = getClass().getResourceAsStream(fileResourcePath);
        try (final Scanner scanner = new Scanner(input, StandardCharsets.UTF_8.name())) {
            return scanner.useDelimiter("\\A").next();
        }
    }

    private int ceilDiv(final int dividend, final int divisor){
        return -Math.floorDiv(-dividend,divisor);
    }

    /**
     * Creates a mocked test client.
     *
     * The client returns empty responses per default.
     *
     * @return The mocked test client.
     */
    @Nonnull
    private CloudOfThingsRestClient createRestClient() {
        final CloudOfThingsRestClient client = mock(CloudOfThingsRestClient.class);
        doAnswer(invocation -> readTemplate("pages/no-filter/empty-page.json"))
            .when(client)
            .getResponse(anyString());
        return client;
    }
}
