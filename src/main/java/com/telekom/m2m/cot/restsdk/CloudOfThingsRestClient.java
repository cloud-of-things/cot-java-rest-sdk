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
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * Created by Patrick Steinert on 30.01.16.
 */
public class CloudOfThingsRestClient {

    private final Gson gson = GsonUtils.createGson();
    private final String encodedAuthString;
    private final String host;

    // It seems that not all responses from the server use the same style of line breaks.
    private static final String LINE_BREAK_PATTERN = "\\r\\n|\\n";

    protected OkHttpClient client;

    // This is an automatic clone of {@link #client}, which can have it's own, much longer, timeouts, and because we
    // don't want a real time server advice to change timeout behaviour for the whole application:
    protected OkHttpClient realTimeClient;
    // The read timeout for the realTimeClient. It should always be set by the caller, but we store it to detect changes:
    protected Integer realTimeTimeout = 60000;


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
     * Executes an HTTP POST request and parses the response Header.
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
            // We need to rethrow this in order to not loose the status code.
            throw e;
        } catch (Exception e) {
            throw new CotSdkException("Problem: " + e.getMessage(), e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }
    }


    // TODO: here the contentType is also used for the Accept header, which is dirty!
    public String doPostRequest(String json, String api, String contentType) {
        return doPostRequest(json, api, contentType, contentType);
    }


    /**
     * Executes an HTTP POST request and returns the response body as String.
     *
     * @param json
     *            Request body, needs to be a json object correlating to the
     *            contentType.
     * @param api
     *            the REST API string.
     * @param contentType
     *            the Content-Type of the JSON Object.
     * @param accept
     *            the Accept header for the request
     * @return the received JSON response body.
     */
    public String doPostRequest(String json, String api, String contentType, String accept) {

        RequestBody body = RequestBody.create(MediaType.parse(contentType), json);
        Request.Builder requestBuilder = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .url(host + "/" + api)
                .post(body);

        if (contentType != null) {
            requestBuilder.addHeader("Content-Type", contentType);
        }
        if (accept != null) {
            requestBuilder.addHeader("Accept", accept);
        }

        Response response = null;
        try {
            response = client.newCall(requestBuilder.build()).execute();
            if (!response.isSuccessful()) {
                final String err = getErrorMessage(response);
                throw new CotSdkException(response.code(), err);
            }

            String bodyContent = response.body().string();
            return bodyContent;
        } catch (IOException e) {
            throw new CotSdkException("Unexpected error during POST request.", e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }
    }


    /**
     * Executes an HTTP POST request and returns the response body as String.
     *
     * @param file Request body, i.e. the first and only form part.
     * @param name The name of the form field.
     * @param api the URL path (without leading /)
     * @param contentType a String with the Content-Type to set in header of the request.
     * @return the response body
     */
    public String doFormUpload(String file, String name, String api, String contentType) {
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(name, "", RequestBody.create(MultipartBody.FORM, file))
                .build();

        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .addHeader("Content-Type", contentType)
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
            String bodyContent = response.body().string();
            return bodyContent;
        } catch (IOException e) {
            throw new CotSdkException("Unexpected error during POST request.", e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }
    }


    /**
     * Do a real time polling request, i.e. the connect call. We use a different client for this call,
     * because it's supposed to have very long timeouts, unsuitable for regular requests.
     * Regular real time polling and smart rest real time polling share the same client (and therefore timeout)
     * because it is to be expected that a client will probably use either one of them, but not both.
     *
     * @param json
     *            Request body, needs to be a json object correlating to the
     *            contentType.
     * @param api
     *            the REST API string.
     * @param contentType
     *            the Content-Type of the JSON Object.
     * @param timeout the new timeout for real time requests. null = don't change the current timeout
     *
     * @return the received JSON response body.
     */
    public String doRealTimePollingRequest(String json, String api, String contentType, Integer timeout) {

        RequestBody body = RequestBody.create(MediaType.parse(contentType), json);
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .addHeader("Content-Type", contentType)
                .addHeader("Accept", contentType)
                .url(host + "/" + api)
                .post(body)
                .build();

        // For the first request, or any subsequent request that wants to change the timeout, we need to get a new client:
        if ((realTimeClient == null) || ((timeout != null) && (!timeout.equals(realTimeTimeout)))) {
            realTimeTimeout = (timeout != null) ? timeout : realTimeTimeout;
            realTimeClient = client.newBuilder().readTimeout(realTimeTimeout, TimeUnit.MILLISECONDS).build();
        }

        Response response = null;
        try {
            response = realTimeClient.newCall(request).execute();
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
     * Executes an HTTP POST request and returns the response body as String.
     * Method will throw an exception if the response code is indicating
     * an unsuccessful request.
     *
     * @param json
     *            Request body, needs to be a json object.
     * @param api
     *            the REST API string.
     * @return the received JSON response body.
     * TODO: check if this method is redundant enough to delete it
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
            return responseBody.split(LINE_BREAK_PATTERN);
        } catch (IOException e) {
            throw new CotSdkException("Unexpected error during POST request.", e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }
    }

    /**
     * Do a SmartREST real time polling request, i.e. the connect call. We use a different client for this call,
     * because it's supposed to have very long timeouts, unsuitable for regular requests.
     * Regular real time polling and smart rest real time polling share the same client (and therefore timeout)
     * because it is to be expected that a client will probably use either one of them, but not both.
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

        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .addHeader("X-Id", xId)
                .url(host + "/cep/realtime") // Same real time endpoint handles smart and regular requests.
                .post(body)
                .build();

        // For the first request, or any subsequent request that wants to change the timeout, we need to get a new client:
        if ((realTimeClient == null) || ((timeout != null) && (!timeout.equals(realTimeTimeout)))) {
            realTimeTimeout = (timeout != null) ? timeout : realTimeTimeout;
            realTimeClient = client.newBuilder().readTimeout(realTimeTimeout, TimeUnit.MILLISECONDS).build();
        }

        Response response = null;
        try {
            response = realTimeClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new CotSdkException(response.code(), "Unexpected response code for POST request.");
            }
            String responseBody = response.body().string();
            return responseBody.split(LINE_BREAK_PATTERN);
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
            return responseBody.split(LINE_BREAK_PATTERN);
        } catch (IOException e) {
            throw new CotSdkException("Unexpected error during POST request.", e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }
    }


    public String getResponse(String id, String api, String accept) {
        Request.Builder requestBuilder = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .url(host + "/" + api + "/" + id);

        if (accept != null) {
            requestBuilder.addHeader("Accept", accept);
        }

        Response response = null;
        try {
            response = client.newCall(requestBuilder.build()).execute();
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

    public String getResponse(String api) {
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
        } catch (CotSdkException e) {
            // We need to rethrow this in order to not loose the status code.
            throw e;
        } catch (Exception e) {
            throw new CotSdkException("Error in request", e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }
    }


    /**
     * Execute a PUT request.
     *
     * @param data the body to send
     * @param path the URL path (without leading '/')
     * @param contentType the Content-Type header
     * @return the response body, if there was one (empty string otherwise)
     */
    public String doPutRequest(String data, String path, String contentType) {
        return doPutRequest(data, path, contentType, "*/*");
    }


    /**
     * Execute a PUT request.
     *
     * @param data the body to send
     * @param path the URL path (without leading '/')
     * @param contentType the Content-Type header
     * @param accept the Accept header (may be null)
     * @return the response body, if there was one (empty string otherwise)
     */
    public String doPutRequest(String data, String path, String contentType, String accept) {
        RequestBody requestBody = RequestBody.create(MediaType.parse(contentType), data);
        Request.Builder requestBuilder = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .addHeader("Content-Type", contentType)
                .url(host + "/" + path)
                .put(requestBody);

        if (accept != null) {
            requestBuilder.addHeader("Accept", accept);
        }

        Request request = requestBuilder.build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new CotSdkException(response.code(), "Requested returned error code");
            }
            ResponseBody responseBody = response.body();
            return (responseBody == null) ? "" : responseBody.string();
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
        } catch( CotSdkException e) {
            // We need to rethrow this in order to not loose the status code.
            throw e;
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
            // We need to rethrow this in order to not loose the status code.
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
