package com.telekom.m2m.cot.restsdk;

import okhttp3.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * Created by breucking on 30.01.16.
 */
public class CloudOfThingsRestClient {

    private final String encodedAuthString;
    private final String tenant;
    private final String user;
    private final String password;

    protected OkHttpClient client = new OkHttpClient();

    public CloudOfThingsRestClient(String tenant, String user, String password) throws UnsupportedEncodingException {
        this.user = user;
        this.password = password;
        this.tenant = tenant;
        encodedAuthString = Base64.getEncoder().encodeToString((user + ":" + password).getBytes("utf-8"));
    }

    public String doRequestWithIdResponse(String json, String api, String contentType) throws IOException {

        RequestBody body = RequestBody.create(MediaType.parse(contentType), json);
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .addHeader("Content-Type", contentType)
                .addHeader("Accept", contentType)
                .url("https://" + tenant + ".test-ram.m2m.telekom.com/" + api)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        String location = response.header("Location");
        String result = null;
        if (location != null) {
            String[] pathParts = location.split("\\/");
            result = pathParts[pathParts.length - 1];
        }
        return result;
    }

    public String doPostRequest(String json, String api, String contentType) throws IOException {

        RequestBody body = RequestBody.create(MediaType.parse(contentType), json);
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .addHeader("Content-Type", contentType)
                .addHeader("Accept", contentType)
                .url("https://" + tenant + ".test-ram.m2m.telekom.com/" + api)
                .post(body)
                .build();
        return client.newCall(request).execute().body().string();
    }

    public String getResponse(String s, String api, String contentType) throws IOException {


        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .url("https://testing.test-ram.m2m.telekom.com/" + api + s)
                .build();

        Response response = client.newCall(request).execute();
        String result = null;
        if (response.isSuccessful())
            result =  response.body().string();
        return result;
    }

    public void doPutRequest(String json, String api, String contentType) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse(contentType), json);
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .addHeader("Content-Type", contentType)
                .addHeader("Accept", contentType)
                .url("https://" + tenant + ".test-ram.m2m.telekom.com/" + api)
                .put(body)
                .build();
        Response response = client.newCall(request).execute();
    }
}
