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

    private final CloudOfThingsRestClient cotRestClientMock = Mockito.mock(CloudOfThingsRestClient.class);

    private final String relativeApiUrl = "audit/auditRecords/";
    private final String contentType = "application/vnd.com.nsn.cumulocity.auditRecord+json;charset=UTF-8;ver=0.9";
    private final String elementName = "auditRecords";
    private final Gson gson = GsonUtils.createGson();
    private final String type = "com_telekom_audit_TestType";

    private final JsonArrayPagination jsonArrayPagination = new JsonArrayPagination(
            cotRestClientMock,
            relativeApiUrl,
            gson,
            contentType,
            elementName,
            null
    );

    @Test
    public void testGetJsonArray() throws Exception {
        // given
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

        Mockito.when(cotRestClientMock.getResponse(url, contentType)).thenReturn(json);

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
    public void testGetJsonArrayWithoutFilter() throws Exception {
        // given
        final String json = "{\"auditRecords\":[{\"id\":\"234\"}]}";
        final String url = relativeApiUrl + "?currentPage=1&pageSize=5";

        Mockito.when(cotRestClientMock.getResponse(url, contentType)).thenReturn(json);

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
    public void testGetJsonArrayWithoutResults() throws Exception {
        // given
        final String json = "{\"auditRecords\":[]}";
        final String url = relativeApiUrl + "?currentPage=1&pageSize=5";

        Mockito.when(cotRestClientMock.getResponse(url, contentType)).thenReturn(json);

        // when
        JsonArray jsonArray = jsonArrayPagination.getJsonArray();

        // then
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(jsonArray.size(), 0);
        Assert.assertFalse(jsonArrayPagination.hasPrevious());
    }

    @Test
    public void testGetJsonArrayWithoutCollection() throws Exception {
        // given
        final String json = "{}";
        final String url = relativeApiUrl + "?currentPage=1&pageSize=5";

        Mockito.when(cotRestClientMock.getResponse(url, contentType)).thenReturn(json);

        // when
        JsonArray jsonArray = jsonArrayPagination.getJsonArray();

        // then
        Assert.assertNull(jsonArray);
        Assert.assertFalse(jsonArrayPagination.hasPrevious());
    }

    @Test
    public void testPagination() throws Exception {
        // given
        final String jsonResultPageEmpty = "{\"auditRecords\":[]}";
        final String jsonResultPage1 = "{\"auditRecords\":[{\"id\":\"1\"}]}";
        final String jsonResultPage2 = "{\"auditRecords\":[{\"id\":\"2\"}],\"prev\":\"page1\"}";
        final String urlPage0 = relativeApiUrl + "?currentPage=0&pageSize=5";
        final String urlPage1 = relativeApiUrl + "?currentPage=1&pageSize=5";
        final String urlPage2 = relativeApiUrl + "?currentPage=2&pageSize=5";
        final String urlPage3 = relativeApiUrl + "?currentPage=3&pageSize=5";

        Mockito.when(cotRestClientMock.getResponse(urlPage0, contentType)).thenReturn(jsonResultPage1);
        Mockito.when(cotRestClientMock.getResponse(urlPage1, contentType)).thenReturn(jsonResultPage1);
        Mockito.when(cotRestClientMock.getResponse(urlPage2, contentType)).thenReturn(jsonResultPage2);
        Mockito.when(cotRestClientMock.getResponse(urlPage3, contentType)).thenReturn(jsonResultPageEmpty);

        // when you retrieve the current page of collection at first time
        JsonArray jsonArray = jsonArrayPagination.getJsonArray();

        // then you'll get the first page
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(jsonArray.size(), 1);
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
        Assert.assertTrue(jsonArray.get(0).getAsJsonObject().get("id").getAsString().equals("2"));
        Assert.assertTrue(jsonArrayPagination.hasPrevious());
        Assert.assertFalse(jsonArrayPagination.hasNext());

        // when you navigate back to the previous page
        jsonArrayPagination.previous();
        jsonArray = jsonArrayPagination.getJsonArray();

        // then you'll get the first page again
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(jsonArray.size(), 1);
        Assert.assertNotNull(jsonArray.get(0).getAsJsonObject());
        Assert.assertTrue(jsonArray.get(0).getAsJsonObject().get("id").getAsString().equals("1"));
        Assert.assertFalse(jsonArrayPagination.hasPrevious());
        Assert.assertTrue(jsonArrayPagination.hasNext());

        // when you navigate again to the previous page which does not exist
        jsonArrayPagination.previous();
        jsonArray = jsonArrayPagination.getJsonArray();

        // then you'll still stay at the first page
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(jsonArray.size(), 1);
        Assert.assertNotNull(jsonArray.get(0).getAsJsonObject());
        Assert.assertTrue(jsonArray.get(0).getAsJsonObject().get("id").getAsString().equals("1"));
        Assert.assertFalse(jsonArrayPagination.hasPrevious());
        Assert.assertTrue(jsonArrayPagination.hasNext());
    }

    @Test
    public void testSetPageSize() throws Exception {
        // given
        final String jsonResultPageSize1 = "{\"auditRecords\":[{\"id\":\"1\"}]}";
        final String jsonResultPageSize2 = "{\"auditRecords\":[{\"id\":\"1\"},{\"id\":\"2\"}]}";
        final String urlPageSize1 = relativeApiUrl + "?currentPage=1&pageSize=1";
        final String urlPageSize2 = relativeApiUrl + "?currentPage=1&pageSize=2";

        Mockito.when(cotRestClientMock.getResponse(urlPageSize1, contentType)).thenReturn(jsonResultPageSize1);
        Mockito.when(cotRestClientMock.getResponse(urlPageSize2, contentType)).thenReturn(jsonResultPageSize2);

        // when
        jsonArrayPagination.setPageSize(-1);
        JsonArray jsonArray = jsonArrayPagination.getJsonArray();

        // then
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(jsonArray.size(), 1);

        // when
        jsonArrayPagination.setPageSize(0);
        jsonArray = jsonArrayPagination.getJsonArray();

        // then
        Assert.assertNotNull(jsonArray);
        Assert.assertEquals(jsonArray.size(), 1);

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

    // TODO:
// test Exception handling in getJsonObject()
// with
//        response = cloudOfThingsRestClient.getResponse(url, contentType);
//        return gson.fromJson(response, JsonObject.class);

}
