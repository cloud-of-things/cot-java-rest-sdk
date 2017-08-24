package com.telekom.m2m.cot.restsdk;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.telekom.m2m.cot.restsdk.util.CotSdkException;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Patrick Steinert on 30.01.16.
 */
public class CloudOfThingsRestClient {

    private Gson gson = GsonUtils.createGson();
    private final String encodedAuthString;
    private final String host;

    protected OkHttpClient client;

    // This is an automatic clone of {@link #client}, which can have it's own timeouts, because we don't want
    // a real time server advice to change timeout behaviour for the whole application:
    protected OkHttpClient realTimeClient;
    // The read timeout for the realTimeClient:
    protected int realTimeTimeout = 10000;


    public CloudOfThingsRestClient(OkHttpClient okHttpClient, String host, String user, String password) {
        this.host = host;
        try {
            encodedAuthString = Base64.getEncoder().encodeToString((user + ":" + password).getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new CotSdkException("Error generating auth string.", e);
        }
        client = okHttpClient;
    }

    /**
     * Proceedes a HTTP POST request and parses the response Header.
     * Response header 'Location' will be split to get the ID of the object (mostly created).
     *
     * @param json
     *            Request body, needs to be a json object correlating to the
     *            contentType.
     * @param api
     *            the REST API string.
     * @param contentType
     *            the Content-Type of the JSON Object.
     * @return the id of the Object.
     */
    public String doRequestWithIdResponse(String json, String api, String contentType) {

        Response response = null;
        try {
            RequestBody body = RequestBody.create(MediaType.parse(contentType), json);
            Request request = new Request.Builder()
                    .addHeader("Authorization", "Basic " + encodedAuthString)
                    .addHeader("Content-Type", contentType)
                    .addHeader("Accept", contentType)
                    // .url(tenant + ".test-ram.m2m.telekom.com/" + api)
                    .url(host + "/" + api)
                    .post(body)
                    .build();
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                final String err = getErrorMessage(response);
                throw new CotSdkException(response.code(), err);
            }
            String location = response.header("Location");
            String result = null;
            if (location != null) {
                String[] pathParts = location.split("\\/");
                result = pathParts[pathParts.length - 1];
            }
            return result;
        } catch (CotSdkException e) {
            throw e;
        } catch (Exception e) {
            throw new CotSdkException("Problem: " + e.getMessage(), e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }
    }

    /**
     * Proceedes a HTTP POST request and returns the response body as String.
     *
     * @param json
     *            Request body, needs to be a json object correlating to the
     *            contentType.
     * @param api
     *            the REST API string.
     * @param contentType
     *            the Content-Type of the JSON Object.
     * @return the received JSON response body.
     */
    public String doPostRequest(String json, String api, String contentType) {

        RequestBody body = RequestBody.create(MediaType.parse(contentType), json);
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .addHeader("Content-Type", contentType)
                .addHeader("Accept", contentType)
                .url(host + "/" + api)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                final String err = getErrorMessage(response);
                throw new CotSdkException(response.code(), err);
            }

            return response.body().string();
        } catch (IOException e) {
            throw new CotSdkException("Unexpected error during POST request.", e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }
    }

    /**
     * Proceedes a HTTP POST request and returns the response body as String.
     * Method will throw an exception if the response code is indicating
     * an unsuccessful request.
     *
     * @param json
     *            Request body, needs to be a json object.
     * @param api
     *            the REST API string.
     * @return the received JSON response body.
     */
    public String doPostRequest(String json, String api) {

        RequestBody body = RequestBody.create(null, json);
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .url(host + "/" + api)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new CotSdkException(response.code(), "Unexpected response code for POST request.");
            }
            return response.body().string();
        } catch (IOException e) {
            throw new CotSdkException("Unexpected error during POST request.", e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }
    }


    /**
     * Do a SmartREST real time request.
     *
     * @param xId the X-Id for which this request shall be made. Cannot be null, because it's used by the server to
     *            detect that it is a SmartREST request in the first place.
     * @param lines a String with newline-separated lines for the request body
     *
     * @return the response body as an array of individual lines
     */
    public String[] doSmartRealTimeRequest(String xId, String lines) {
        RequestBody body = RequestBody.create(null, lines);

        Request.Builder builder = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .addHeader("X-Id", xId)
                .url(host + "/cep/realtime") // Same real time endpoint handles smart and regular requests.
                .post(body);

        Request request = builder.build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new CotSdkException(response.code(), "Unexpected response code for POST request.");
            }
            String responseBody = response.body().string();
            return responseBody.split("\\r\\n|\\n");
        } catch (IOException e) {
            throw new CotSdkException("Unexpected error during POST request.", e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }
    }

    /**
     * Do a SmartREST real time polling request, i.e. the connect call. We use a different client for this call,
     * because it's supposed to have very long timeouts, unsuitable for regular requests.
     *
     * @param xId the X-Id for which this request shall be made. Cannot be null, because it's used by the server to
     *            detect that it is a SmartREST request in the first place.
     * @param lines a String with newline-separated lines for the request body
     * @param timeout the new timeout for real time requests. null = don't change the current timeout
     *
     * @return the response body as an array of individual lines
     */
    public String[] doSmartRealTimePollingRequest(String xId, String lines, Integer timeout) {
        RequestBody body = RequestBody.create(null, lines);

        Request.Builder builder = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .addHeader("X-Id", xId)
                .url(host + "/cep/realtime") // Same real time endpoint handles smart and regular requests.
                .post(body);

        Request request = builder.build();

        // For the first request, or any subsequent request that wants to change the timeout, we need to get a new client:
        if ((realTimeClient == null) || ((timeout != null) && (timeout != realTimeTimeout))) {
            realTimeTimeout = timeout;
            realTimeClient = client.newBuilder().readTimeout(timeout, TimeUnit.MILLISECONDS).build();
        }

        Response response = null;
        try {
            response = realTimeClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new CotSdkException(response.code(), "Unexpected response code for POST request.");
            }
            String responseBody = response.body().string();
            return responseBody.split("\\r\\n|\\n");
        } catch (IOException e) {
            throw new CotSdkException("Unexpected error during POST request.", e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }
    }

    /**
     * Do a SmartREST-request.
     *
     * @param xId the X-Id for which this request shall be made.
     *            Can be null, omitting the X-Id header, to allow for multiple X-Id ("15,myxid").
     * @param lines a String with newline-separated lines for the request body
     * @param transientMode whether to use "X-Cumulocity-Processing-Mode: TRANSIENT" (false: PERSISTENT).
     *
     * @return the response body as an array of individual lines
     */
    public String[] doSmartRequest(String xId, String lines, boolean transientMode) {
        RequestBody body = RequestBody.create(null, lines);

        Request.Builder builder = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .addHeader("X-Id", xId)
                .url(host + "/s") // SmartRest-endpoint is always just "/s".
                .post(body);
        if (transientMode) {
            // PERSISTENT is the default, so we don't specify it.
            builder.addHeader("X-Cumulocity-Processing-Mode", "TRANSIENT");
        }
        Request request = builder.build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new CotSdkException(response.code(), "Unexpected response code for POST request.");
            }
            String responseBody = response.body().string();
            return responseBody.split("\\r\\n|\\n");
        } catch (IOException e) {
            throw new CotSdkException("Unexpected error during POST request.", e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }
    }

    public String getResponse(String id, String api, String contentType) {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .url(host + "/" + api + "/" + id)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            String result = null;
            if (response.isSuccessful()) {
                result = response.body().string();
            } else {
                if (response.code() != HttpURLConnection.HTTP_NOT_FOUND) {
                    throw new CotSdkException(response.code(), "Error in request.");
                }
            }
            return result;
        } catch (Exception e) {
            throw new CotSdkException("Error in request", e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }

    }

    // TODO: check why the contentType is not necessary, because experiments seemed to indicate that it is...
    public String getResponse(String api, String contentType) {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .url(host + "/" + api)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            String result;
            if (response.isSuccessful()) {
                result = response.body().string();
            } else {
                final String err = getErrorMessage(response);
                throw new CotSdkException(response.code(), err);
            }
            return result;
        } catch (Exception e) {
            throw new CotSdkException("Error in request", e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }

    }

    public void doPutRequest(String json, String api, String contentType) {
        RequestBody body = RequestBody.create(MediaType.parse(contentType), json);
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .addHeader("Content-Type", contentType)
                .addHeader("Accept", contentType)
                .url(host + "/" + api)
                .put(body)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new CotSdkException(response.code(), "Requested returned error code");
            }
        } catch (Exception e) {
            throw new CotSdkException("Error in request", e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }
    }

    public void doPutRequest(String json, String id, String api, String contentType) {
        RequestBody body = RequestBody.create(MediaType.parse(contentType), json);
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .addHeader("Content-Type", contentType)
                .addHeader("Accept", contentType)
                .url(host + "/" + api + "/" + id)
                .put(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (Exception e) {
            throw new CotSdkException("Error in request", e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }

    }

    public void delete(String id, String api) {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .url(host + "/" + api + "/" + id)
                .delete()
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new CotSdkException(response.code(), "Error in delete with ID '" + id + "' (see https://http.cat/" + response.code() + ")");
            }
        } catch (Exception e) {
            throw new CotSdkException("Error in request", e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }
    }

    public void deleteBy(final String filter, final String api) {
        final Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .url(host + "/" + api + "?" + filter)
                .delete()
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new CotSdkException(response.code(), "Error in delete by criteria '" + filter + "'");
            }
        } catch (CotSdkException e) {
            throw e;
        } catch (Exception e) {
            throw new CotSdkException("Error in request: " + e.getMessage(), e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }
    }

    public void delete(String url) {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .url(url)
                .delete()
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new CotSdkException(response.code(), "Error in delete with URL '" + url + "' (see https://http.cat/" + response.code() + ")");
            }
        } catch (Exception e) {
            throw new CotSdkException("Error in request", e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }

    }

    private String getErrorMessage(final Response response) throws IOException {
        final JsonObject o = gson.fromJson(response.body().string(), JsonObject.class);
        String errorMessage = "Request failed.";
        if (o.has("error")) {
            errorMessage += " Platform provided details: '" + o.get("error") + "'";
        }

        return errorMessage;
    }

    private void closeResponseBodyIfResponseAndBodyNotNull(final Response response) {
        if (response != null && response.body() != null) {
            response.body().close();
        }
    }

}
