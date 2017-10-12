package com.telekom.m2m.cot.restsdk;


import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Matchers.any;

/**
 * Created by Patrick Steinert on 30.01.16.
 */
@PrepareForTest({OkHttpClient.class, CloudOfThingsRestClient.class, Response.class})
public class CloudOfThingsRestClientTest extends PowerMockTestCase {

    private final static String TEST_HOST = "https://test.m2m.telekom.com";
    private final static String TEST_USERNAME = "tester";
    private final static String TEST_PASSWORD = "anything-goes";

    @Test(expectedExceptions = CotSdkException.class)
    public void testDoRequetsWithIdResponseWithException() throws Exception {
        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);

        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenThrow(new RuntimeException());

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TEST_HOST, TEST_USERNAME, TEST_PASSWORD);

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

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TEST_HOST, TEST_USERNAME, TEST_PASSWORD);

        cloudOfThingsRestClient.doRequestWithIdResponse("", "", "");
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testDoPutRequestWithException() throws Exception {
        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);

        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenThrow(new RuntimeException());

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TEST_HOST, TEST_USERNAME, TEST_PASSWORD);

        cloudOfThingsRestClient.doPutRequest("", "", "");
    }

    @Test
    public void testConnection() throws Exception {
        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);
        CloudOfThingsRestClient cotRestClient = new CloudOfThingsRestClient(clientMock, TEST_HOST, TEST_USERNAME, TEST_PASSWORD);
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testGetResponseWithException() throws Exception {
        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);

        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenThrow(new RuntimeException());

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TEST_HOST, TEST_USERNAME, TEST_PASSWORD);

        cloudOfThingsRestClient.getResponse("", "", "");
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testDeleteWithException() throws Exception {
        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);

        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenThrow(new RuntimeException());

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TEST_HOST, TEST_USERNAME, TEST_PASSWORD);

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

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TEST_HOST, TEST_USERNAME, TEST_PASSWORD);

        cloudOfThingsRestClient.delete("", "");
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testPostWithError() throws Exception {

        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);
        Call call = PowerMockito.mock(Call.class);

        Response response = PowerMockito.mock(Response.class);


        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenReturn(call);
        PowerMockito.when(call.execute()).thenThrow(new IOException());

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TEST_HOST, TEST_USERNAME, TEST_PASSWORD);

        cloudOfThingsRestClient.doPostRequest("", "", "");
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

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TEST_HOST, TEST_USERNAME, TEST_PASSWORD);

        cloudOfThingsRestClient.delete("", "");
    }

    @Test(expectedExceptions = { CotSdkException.class }, expectedExceptionsMessageRegExp = "Error in request:.*")
    public void testDeleteByWithException() throws Exception {
        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);

        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenThrow(new RuntimeException());

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TEST_HOST, TEST_USERNAME, TEST_PASSWORD);

        cloudOfThingsRestClient.deleteBy("", "");
    }

    @Test(expectedExceptions = { CotSdkException.class }, expectedExceptionsMessageRegExp = "Error in delete by criteria.*")
    public void testDeleteByWithReturningError() throws Exception {

        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);
        Call call = PowerMockito.mock(Call.class);

        Response response = PowerMockito.mock(Response.class);

        PowerMockito.when(response.isSuccessful()).thenReturn(false);
        PowerMockito.when(response.code()).thenReturn(404);

        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenReturn(call);
        PowerMockito.when(call.execute()).thenReturn(response);

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TEST_HOST, TEST_USERNAME, TEST_PASSWORD);

        cloudOfThingsRestClient.deleteBy("", "");
    }

    @Test
    public void testDeleteBy() throws Exception {

        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);
        Call call = PowerMockito.mock(Call.class);

        Response response = PowerMockito.mock(Response.class);

        PowerMockito.when(response.isSuccessful()).thenReturn(true);
        PowerMockito.when(response.code()).thenReturn(204); //204: no content

        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenReturn(call);
        PowerMockito.when(call.execute()).thenReturn(response);

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TEST_HOST, TEST_USERNAME, TEST_PASSWORD);

        cloudOfThingsRestClient.deleteBy("", "");
    }
}
