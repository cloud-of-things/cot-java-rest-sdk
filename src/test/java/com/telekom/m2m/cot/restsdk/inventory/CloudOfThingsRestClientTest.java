package com.telekom.m2m.cot.restsdk.inventory;


import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.inventory.util.TestHelper;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;

/**
 * Created by breucking on 30.01.16.
 */
@PrepareForTest({OkHttpClient.class, CloudOfThingsRestClient.class})
public class CloudOfThingsRestClientTest extends PowerMockTestCase {

    @Test(expectedExceptions = CotSdkException.class)
    public void testTestMe() throws Exception {

        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);

        PowerMockito.whenNew(OkHttpClient.class).withAnyArguments().thenReturn(clientMock);
        PowerMockito.when(clientMock.newCall(any(Request.class))).thenThrow(new RuntimeException());

        CloudOfThingsRestClient cloudOfThingsRestClient = new CloudOfThingsRestClient(clientMock, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);

        RequestBody body = RequestBody.create(MediaType.parse(""), "");
        Request request = new Request.Builder()
                .post(body)
                .url("http://foo.bar")
                .build();
        cloudOfThingsRestClient.doRequestWithIdResponse("", "", "");
    }

    @Test
    public void testConnection() throws Exception {
        OkHttpClient clientMock = PowerMockito.mock(OkHttpClient.class);
        CloudOfThingsRestClient cotRestClient = new CloudOfThingsRestClient(clientMock, TestHelper.TEST_TENANT, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    }

    @Test
    public void testGetResponseWithError() throws Exception {
    }
}
