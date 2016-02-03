package com.telekom.m2m.cot.restsdk.inventory;


import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.inventory.util.TestHelper;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import okhttp3.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Matchers.any;

/**
 * Created by breucking on 30.01.16.
 */
@PrepareForTest({OkHttpClient.class, CloudOfThingsRestClient.class, Response.class})
public class CloudOfThingsRestClientTest extends PowerMockTestCase {

    @Test(expectedExceptions = CotSdkException.class)
    public void testDoRequetsWithIdResponseWithException() throws Exception {
        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);

        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenThrow(new RuntimeException());

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

        cloudOfThingsRestClient.doRequestWithIdResponse("", "", "");
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testDoPutRequestWithException() throws Exception {
        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);

        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenThrow(new RuntimeException());

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

        cloudOfThingsRestClient.doPutRequest("", "", "");
    }

    @Test
    public void testConnection() throws Exception {
        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);
        CloudOfThingsRestClient cotRestClient = new CloudOfThingsRestClient(clientMock, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testGetResponseWithException() throws Exception {
        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);

        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenThrow(new RuntimeException());

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

        cloudOfThingsRestClient.getResponse("", "", "");
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void testDeleteWithException() throws Exception {
        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);

        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenThrow(new RuntimeException());

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

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

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

        cloudOfThingsRestClient.delete("", "");
    }

    @Test(expectedExceptions = CotSdkException.class)
    public void test() throws Exception {

        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);
        Call call = PowerMockito.mock(Call.class);

        Response response = PowerMockito.mock(Response.class);


        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenReturn(call);
        PowerMockito.when(call.execute()).thenThrow(new IOException());

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

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

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

        cloudOfThingsRestClient.delete("", "");
    }


}
