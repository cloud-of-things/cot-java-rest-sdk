package com.telekom.m2m.cot.restsdk;

import com.telekom.m2m.cot.restsdk.util.CotSdkException;
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

    protected OkHttpClient client;

    public CloudOfThingsRestClient(OkHttpClient okHttpClient, String tenant, String user, String password) {
        this.user = user;
        this.password = password;
        this.tenant = tenant;

        try {
            encodedAuthString = Base64.getEncoder().encodeToString((user + ":" + password).getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new CotSdkException("Error generating auth string.", e);
        }
        client = okHttpClient;
    }

    public String doRequestWithIdResponse(String json, String api, String contentType)  {
        try {

        RequestBody body = RequestBody.create(MediaType.parse(contentType), json);
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .addHeader("Content-Type", contentType)
                .addHeader("Accept", contentType)
                .url("https://" + tenant + ".test-ram.m2m.telekom.com/" + api)
                .post(body)
                .build();
        Response response = null;
            response = client.newCall(request).execute();
        String location = response.header("Location");
        String result = null;
        if (location != null) {
            String[] pathParts = location.split("\\/");
            result = pathParts[pathParts.length - 1];
        }
        return result;
        } catch (Exception e) {
           throw new CotSdkException("Problem", e);
        }
    }

    /**
     * Proceedes a HTTP POST request.
     *
     * @param json        Request body, needs to be a json object correlating to the contentType.
     * @param api         the REST API string.
     * @param contentType the Content-Type of the JSON Object.
     * @return the received JSON response body.
     * @throws IOException if the communication went wrong.
     */
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

    public String getResponse(String s, String api, String contentType) {


        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .url("https://testing.test-ram.m2m.telekom.com/" + api + "/" + s)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            String result = null;
            if (response.isSuccessful())
                result = response.body().string();
            return result;
        } catch (IOException e) {
            throw new CotSdkException("Error in requets", e);
        }
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

    public void delete(String id, String api) throws IOException {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .url("https://testing.test-ram.m2m.telekom.com/" + api + "/" + id)
                .delete()
                .build();

        Response response = client.newCall(request).execute();
    }
}
