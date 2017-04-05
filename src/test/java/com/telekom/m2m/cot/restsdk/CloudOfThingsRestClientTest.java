package com.telekom.m2m.cot.restsdk;


import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Created by Patrick Steinert on 30.01.16.
 */
@PrepareForTest({OkHttpClient.class, CloudOfThingsRestClient.class, Response.class})
public class CloudOfThingsRestClientTest extends PowerMockTestCase {

    @Test(expectedExceptions = CotSdkException.class)
    public void testDoRequetsWithIdResponseWithException() throws Exception {
        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);

        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenThrow(new RuntimeException());

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

        cloudOfThingsRestClient.doRequestWithIdResponse("", "", "");
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testDoRequetsWithIdResponseWithBadResponse() throws Exception {
        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);
        Call call = PowerMockito.mock(Call.class);

        Response response = PowerMockito.mock(Response.class);

        PowerMockito.when(response.isSuccessful()).thenReturn(false);
        PowerMockito.when(response.code()).thenReturn(422);
        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenReturn(call);
        PowerMockito.when(call.execute()).thenReturn(response);

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

        cloudOfThingsRestClient.doRequestWithIdResponse("", "", "");
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testDoPutRequestWithException() throws Exception {
        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);

        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenThrow(new RuntimeException());

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

        cloudOfThingsRestClient.doPutRequest("", "", "");
    }

    @Test
    public void testDoPutRequestWithNonSuccessStatusCode() throws Exception {

        final OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);

        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);

        final Response response = PowerMockito.mock(Response.class);
        PowerMockito.when(response.isSuccessful())
                .thenReturn(false);
        final int httpStatusCode = 404;
        PowerMockito.when(response.code())
                .thenReturn(httpStatusCode);
        final ResponseBody responseBody = PowerMockito.mock(ResponseBody.class);
        PowerMockito.when(response.body())
                .thenReturn(responseBody);

        final Call call = PowerMockito.mock(Call.class);
        PowerMockito.when(clientMock.newCall(any(Request.class)))
                .thenReturn(call);
        PowerMockito.when(call.execute())
                .thenReturn(response);

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

        try {
            cloudOfThingsRestClient.doPutRequest("", "", "");
            fail("an exception should have occured");
        } catch (CotSdkException e) {
            assertEquals(httpStatusCode, e.getHttpStatus());
        }

    }

    @Test
    public void testConnection() throws Exception {
        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);
        new CloudOfThingsRestClient(clientMock, TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testGetResponseWithException() throws Exception {
        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);

        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenThrow(new RuntimeException());

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

        cloudOfThingsRestClient.getResponse("", "", "");
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testDeleteWithException() throws Exception {
        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);

        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenThrow(new RuntimeException());

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

        cloudOfThingsRestClient.delete("", "");
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testDeleteWithIOException() throws Exception {

        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);
        Call call = PowerMockito.mock(Call.class);

        Response response = PowerMockito.mock(Response.class);

        PowerMockito.when(response.isSuccessful()).thenReturn(false);
        PowerMockito.when(response.code()).thenReturn(404);

        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenReturn(call);
        PowerMockito.when(call.execute()).thenThrow(new IOException());

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

        cloudOfThingsRestClient.delete("", "");
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testPostWithError() throws Exception {

        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);
        Call call = PowerMockito.mock(Call.class);

        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenReturn(call);
        PowerMockito.when(call.execute()).thenThrow(new IOException());

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

        cloudOfThingsRestClient.doPostRequest("", "", "");
    }

    @Test
    public void testPostWithNonSuccessStatusCode() throws Exception {

        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);
        Call call = PowerMockito.mock(Call.class);

        Response response = PowerMockito.mock(Response.class);

        final int httpStatusCode = 403;
        PowerMockito.when(response.code())
                .thenReturn(httpStatusCode);
        ResponseBody responseBody = PowerMockito.mock(ResponseBody.class);
        PowerMockito.when(response.body())
                .thenReturn(responseBody);

        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenReturn(call);
        PowerMockito.when(call.execute()).thenReturn(response);

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(
                clientMock, TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD
        );

        try {
            cloudOfThingsRestClient.doPutRequest("", "", "");
            fail("an exception should have occured");
        } catch (CotSdkException e) {
            assertEquals(httpStatusCode, e.getHttpStatus());
        }

    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testDeleteWithReturningError() throws Exception {

        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);
        Call call = PowerMockito.mock(Call.class);

        Response response = PowerMockito.mock(Response.class);

        PowerMockito.when(response.isSuccessful()).thenReturn(false);
        PowerMockito.when(response.code()).thenReturn(404);

        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenReturn(call);
        PowerMockito.when(call.execute()).thenReturn(response);

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TestHelper.TEST_HOST, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

        cloudOfThingsRestClient.delete("", "");
    }


}
