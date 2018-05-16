package com.telekom.m2m.cot.restsdk.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by Andreas Dyck on 27.07.17.
 */
public class JsonArrayPaginationTest {

    private final String relativeApiUrl = "audit/auditRecords/";
    private final String contentType = "application/vnd.com.nsn.cumulocity.auditRecord+json;charset=UTF-8;ver=0.9";
    private final String elementName = "auditRecords";
    private final Gson gson = GsonUtils.createGson();
    private final String type = "com_telekom_audit_TestType";

    private JsonArrayPagination createJsonArrayPagination(CloudOfThingsRestClient cloudOfThingsRestClient) {
        return new JsonArrayPagination(
                cloudOfThingsRestClient,
                relativeApiUrl,
                gson,
                contentType,
                elementName,
                null
        );
    }

    @Test
    public void testGetJsonArray() {
        // given
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);
        final Filter.FilterBuilder filterBuilder = Filter.build().byType(type);
        final JsonArrayPagination jsonArrayPaginationWithFilter = new JsonArrayPagination(
                cotRestClientMock,
                relativeApiUrl,
                gson,
                contentType,
                elementName,
                filterBuilder
        );

        final String json = "{\"auditRecords\":[{\"id\":\"234\"}]}";
        final String url = relativeApiUrl + "?currentPage=1&pageSize=5&type=" + type;

        Mockito.when(cotRestClientMock.getResponse(url)).thenReturn(json);

        // when
        final JsonArray jsonArray = jsonArrayPaginationWithFilter.getJsonArray();

        // then
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(jsonArray.size(), 1);
        Assert.assertNotNull(jsonArray.get(0).getAsJsonObject());
        Assert.assertTrue(jsonArray.get(0).getAsJsonObject().has("id"));
        Assert.assertTrue(jsonArray.get(0).getAsJsonObject().get("id").getAsString().equals("234"));
        Assert.assertFalse(jsonArrayPaginationWithFilter.hasPrevious());
    }

    @Test
    public void testGetJsonArrayWithoutFilter() {
        // given
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);
        final JsonArrayPagination jsonArrayPagination = createJsonArrayPagination(cotRestClientMock);

        final String json = "{\"auditRecords\":[{\"id\":\"234\"}]}";
        final String url = relativeApiUrl + "?currentPage=1&pageSize=5";

        Mockito.when(cotRestClientMock.getResponse(url)).thenReturn(json);

        // when
        final JsonArray jsonArray = jsonArrayPagination.getJsonArray();

        // then
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(jsonArray.size(), 1);
        Assert.assertNotNull(jsonArray.get(0).getAsJsonObject());
        Assert.assertTrue(jsonArray.get(0).getAsJsonObject().has("id"));
        Assert.assertTrue(jsonArray.get(0).getAsJsonObject().get("id").getAsString().equals("234"));
        Assert.assertFalse(jsonArrayPagination.hasPrevious());
    }

    @Test
    public void testGetJsonArrayWithoutResults() {
        // given
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);
        final JsonArrayPagination jsonArrayPagination = createJsonArrayPagination(cotRestClientMock);

        final String json = "{\"auditRecords\":[]}";
        final String url = relativeApiUrl + "?currentPage=1&pageSize=5";

        Mockito.when(cotRestClientMock.getResponse(url)).thenReturn(json);

        // when
        JsonArray jsonArray = jsonArrayPagination.getJsonArray();

        // then
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(jsonArray.size(), 0);
        Assert.assertFalse(jsonArrayPagination.hasPrevious());
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testGetJsonArrayWithException() {
        // given
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);
        final JsonArrayPagination jsonArrayPagination = createJsonArrayPagination(cotRestClientMock);

        final String url = relativeApiUrl + "?currentPage=1&pageSize=5";

        Mockito.when(cotRestClientMock.getResponse(url)).thenThrow(new CotSdkException("exception was thrown"));

        // when
        jsonArrayPagination.getJsonArray();

        // then an exception should be thrown
    }

    @Test
    public void testGetJsonArrayWithoutCollection() {
        // given
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);
        final JsonArrayPagination jsonArrayPagination = createJsonArrayPagination(cotRestClientMock);

        final String json = "{}";
        final String url = relativeApiUrl + "?currentPage=1&pageSize=5";

        Mockito.when(cotRestClientMock.getResponse(url)).thenReturn(json);

        // when
        JsonArray jsonArray = jsonArrayPagination.getJsonArray();

        // then
        Assert.assertNull(jsonArray);
        Assert.assertFalse(jsonArrayPagination.hasPrevious());
    }

    @Test
    public void testPagination() {
        // given
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);
        final JsonArrayPagination jsonArrayPagination = createJsonArrayPagination(cotRestClientMock);
        jsonArrayPagination.setPageSize(2);

        final String jsonResultPageEmpty = "{\"auditRecords\":[], \"statistics\": {\"currentPage\": 4, \"pageSize\": 2}}";
        final String jsonResultPage1 = "{\"auditRecords\":[{\"id\":\"1\"}, {\"id\":\"2\"}], \"next\": \"page2\", \"statistics\": {\"currentPage\": 1, \"pageSize\": 2}}";
        final String jsonResultPage2 = "{\"auditRecords\":[{\"id\":\"3\"}],\"prev\":\"page1\", \"next\": \"page3\", \"statistics\": {\"currentPage\": 2, \"pageSize\": 2}}";
        final String urlPage1 = relativeApiUrl + "?currentPage=1&pageSize=2";
        final String urlPage2 = relativeApiUrl + "?currentPage=2&pageSize=2";
        final String urlPage3 = relativeApiUrl + "?currentPage=3&pageSize=2";

        Mockito.when(cotRestClientMock.getResponse(urlPage1)).thenReturn(jsonResultPage1);
        Mockito.when(cotRestClientMock.getResponse(urlPage2)).thenReturn(jsonResultPage2);
        Mockito.when(cotRestClientMock.getResponse(urlPage3)).thenReturn(jsonResultPageEmpty);

        // when you retrieve the current page of collection at first time
        JsonArray jsonArray = jsonArrayPagination.getJsonArray();

        // then you'll get the first page
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(jsonArray.size(), 2);
        Assert.assertNotNull(jsonArray.get(0).getAsJsonObject());
        Assert.assertTrue(jsonArray.get(0).getAsJsonObject().get("id").getAsString().equals("1"));
        Assert.assertFalse(jsonArrayPagination.hasPrevious());
        Assert.assertTrue(jsonArrayPagination.hasNext());

        // when you navigate to the next page
        jsonArrayPagination.next();
        jsonArray = jsonArrayPagination.getJsonArray();

        // then you'll get the second page
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(jsonArray.size(), 1);
        Assert.assertNotNull(jsonArray.get(0).getAsJsonObject());
        Assert.assertTrue(jsonArray.get(0).getAsJsonObject().get("id").getAsString().equals("3"));
        Assert.assertTrue(jsonArrayPagination.hasPrevious());
        Assert.assertFalse(jsonArrayPagination.hasNext());

        // when you navigate back to the previous page
        jsonArrayPagination.previous();
        jsonArray = jsonArrayPagination.getJsonArray();

        // then you'll get the first page again
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(jsonArray.size(), 2);
        Assert.assertNotNull(jsonArray.get(0).getAsJsonObject());
        Assert.assertTrue(jsonArray.get(0).getAsJsonObject().get("id").getAsString().equals("1"));
        Assert.assertFalse(jsonArrayPagination.hasPrevious());
        Assert.assertTrue(jsonArrayPagination.hasNext());

        // when you navigate again to the previous page which does not exist
        jsonArrayPagination.previous();
        jsonArray = jsonArrayPagination.getJsonArray();

        // then you'll still stay at the first page
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(jsonArray.size(), 2);
        Assert.assertNotNull(jsonArray.get(0).getAsJsonObject());
        Assert.assertTrue(jsonArray.get(0).getAsJsonObject().get("id").getAsString().equals("1"));
        Assert.assertFalse(jsonArrayPagination.hasPrevious());
        Assert.assertTrue(jsonArrayPagination.hasNext());
    }

    @Test
    public void testSetPageSize() {
        // given
        final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);
        final JsonArrayPagination jsonArrayPagination = createJsonArrayPagination(cotRestClientMock);

        final String jsonResultPageSize1 = "{\"auditRecords\":[{\"id\":\"1\"}]}";
        final String jsonResultPageSize2 = "{\"auditRecords\":[{\"id\":\"1\"},{\"id\":\"2\"}]}";
        final String urlPageSize1 = relativeApiUrl + "?currentPage=1&pageSize=1";
        final String urlPageSize2 = relativeApiUrl + "?currentPage=1&pageSize=2";
        final String urlPageSize5 = relativeApiUrl + "?currentPage=1&pageSize=5";

        Mockito.when(cotRestClientMock.getResponse(urlPageSize1)).thenReturn(jsonResultPageSize1);
        Mockito.when(cotRestClientMock.getResponse(urlPageSize2)).thenReturn(jsonResultPageSize2);
        Mockito.when(cotRestClientMock.getResponse(urlPageSize5)).thenReturn(jsonResultPageSize2);

        // when
        jsonArrayPagination.setPageSize(-1);
        JsonArray jsonArray = jsonArrayPagination.getJsonArray();

        // then
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(jsonArray.size(), 2);

        // when
        jsonArrayPagination.setPageSize(0);
        jsonArray = jsonArrayPagination.getJsonArray();

        // then
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(jsonArray.size(), 2);

        // when
        jsonArrayPagination.setPageSize(1);
        jsonArray = jsonArrayPagination.getJsonArray();

        // then
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(jsonArray.size(), 1);

        // when
        jsonArrayPagination.setPageSize(2);
        jsonArray = jsonArrayPagination.getJsonArray();

        // then
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(jsonArray.size(), 2);
    }
}
