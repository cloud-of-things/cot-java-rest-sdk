package com.telekom.m2m.cot.restsdk;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.telekom.m2m.cot.restsdk.smartrest.SmartRequest;
import com.telekom.m2m.cot.restsdk.smartrest.SmartResponse;
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

    protected OkHttpClient client;

    // This is an automatic clone of {@link #client}, which can have it's own, much longer, timeouts, and because we
    // don't want a real time server advice to change timeout behaviour for the whole application:
    protected OkHttpClient realTimeClient;
    // The read timeout for the realTimeClient. It should always be set by the caller, but we store it to detect changes:
    protected Integer realTimeTimeout = 60000;


    /**
     * @param okHttpClient HTTP client that is used to interact with the API.
     * @param host URL to connect to. Must contain scheme and host, e.g. https://username.int2-ram.m2m.telekom.com
     * @param user Username for authentication.
     * @param password Password for authentication.
     */
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
     * @param accept
     *            the accept header of the response JSON Object.
     * @return the id of the Object.
     */
    public String doRequestWithIdResponse(String json, String api, String contentType, String accept) {

        Response response = null;
        try {
            RequestBody body = RequestBody.create(MediaType.parse(contentType), json);
            Request request = new Request.Builder()
                    .addHeader("Authorization", "Basic " + encodedAuthString)
                    .addHeader("Content-Type", contentType)
                    .addHeader("Accept", accept)
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
     * Executes a multipart form upload of a string and returns the response body as String.
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
     * Executes an HTTP POST request and returns the response body as String.
     *
     * @param files array of byte[], one for each form field.
     * @param names the names of the form field, same order as files.
     * @param api the URL path (without leading /)
     * @return the ID from the Location header (for newly created objects), or null if there's no Location header.
     */
    public String doFormUpload(byte[][] files, String[] names, String api) {
        if (files.length != names.length) {
            throw new CotSdkException("Need to have the same number of files and names to upload (actual: "+files.length+" != "+names.length);
        }

        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        for (int i=0; i<names.length; i++) {
            bodyBuilder.addFormDataPart(names[i], "", RequestBody.create(MultipartBody.FORM, files[i]));
        }

        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .addHeader("Content-Type", "text/foo")
                .url(host + "/" + api)
                .post(bodyBuilder.build())
                .build();

        Response response = null;
        try {
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
        } catch (InterruptedIOException e) {
            // That's ok and normal. There just weren't any new notifications.
            return null;
        } catch (IOException e) {
            throw new CotSdkException("Unexpected error during POST request.", e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }
    }

    /**
     * Do a SmartREST real time request.
     *
     * @param smartRequest the request with meta information and lines to send to platform.
     *
     * @return the SmartResponse
     */
    public SmartResponse doSmartRealTimeRequest(SmartRequest smartRequest) {
        RequestBody body = RequestBody.create(null, smartRequest.getBody());

        Request.Builder builder = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .addHeader("X-Id", smartRequest.getXId())
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
            return new SmartResponse(responseBody);
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
     * @param smartRequest the request with meta information and lines to send to platform.
     * @param timeout the new timeout for real time requests. null = don't change the current timeout
     *
     * @return the SmartResponse
     */
    public SmartResponse doSmartRealTimePollingRequest(SmartRequest smartRequest, Integer timeout) {
        RequestBody body = RequestBody.create(null, smartRequest.getBody());

        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .addHeader("X-Id", smartRequest.getXId())
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
            return new SmartResponse(responseBody);
        } catch (InterruptedIOException e) {
            // That's ok and normal. There just weren't any new notifications.
            return null;
        } catch (IOException e) {
            throw new CotSdkException("Unexpected error during POST request.", e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }
    }

    /**
     * Do a SmartREST-request.
     *
     * @param smartRequest the request with meta information and lines to send to platform.
     *
     * @return the SmartResponse
     */
    public SmartResponse doSmartRequest(SmartRequest smartRequest) {
        RequestBody body = RequestBody.create(null, smartRequest.getBody());

        Request.Builder builder = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .url(host + "/s") // SmartRest-endpoint is always just "/s".
                .post(body);

        //TODO: do we need to not send the header at all, in that case?
        if (smartRequest.getXId() != null && !smartRequest.getXId().isEmpty()) {
            builder.addHeader("X-Id", smartRequest.getXId());
        }

        // PERSISTENT processing mode is the default, so we don't need to handle it in the else-case.
        if (smartRequest.getProcessingMode() != null) {
            builder.addHeader("X-Cumulocity-Processing-Mode", smartRequest.getProcessingMode().name());
        }
        Request request = builder.build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new CotSdkException(response.code(), "Unexpected response code for POST request.");
            }
            return new SmartResponse(response.body().string());
        } catch (IOException e) {
            throw new CotSdkException("Unexpected error during POST request.", e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }
    }

    /**
     * Executes an HTTP GET request and returns the response body as String.
     *
     * @param id id of managed object
     * @param api api name e.g. measurement
     * @param accept accept header for request
     * @return the response from cloud of things
     */
    public String getResponse(String id, String api, String accept) {
        byte[] result = getResponseInBytes(id, api, accept);
        if (result != null){
            return new String(result, Charset.forName("UTF-8"));
        }
        return null;
    }

    /**
     * Executes an HTTP GET request and returns the response body as byte array.
     *
     * @param id id of managed object
     * @param api api name e.g. measurement
     * @param accept accept header for request
     * @return the response from cloud of things
     */
    public byte[] getResponseInBytes(String id, String api, String accept){
        Request.Builder requestBuilder = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .url(host + "/" + removeTrailingSlash(api) + "/" + id);

        if (accept != null) {
            requestBuilder.addHeader("Accept", accept);
        }

        Response response = null;
        try {
            response = client.newCall(requestBuilder.build()).execute();
            if (response.isSuccessful()) {
                return response.body().bytes();
            }
            if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                return null;
            }
            String message = String.format("Error in request. API: %s, id: %s, accepted content type: %s",
                    api, id, accept);
            throw new CotSdkException(response.code(), message);
        } catch (CotSdkException e) {
            throw e;
        } catch (Exception e) {
            String message = String.format("Error in request. API: %s, id: %s, accepted content type: %s, message: %s",
                    api, id, accept, e.getMessage());
            throw new CotSdkException(message, e);
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

    /**
     * Execute a PUT request that will result in a new or changed ID.
     *
     * @param data the body to send in bytes
     * @param path the URL path (without leading '/')
     * @param contentType the Content-Type header
     * @return the ID from the Location header (for newly created objects), or null if there's no Location header.
     */
    public String doPutRequestWithIdResponseInBytes(byte[] data, String path, String contentType) {
        RequestBody requestBody = RequestBody.create(MediaType.parse(contentType), data);
        Request.Builder requestBuilder = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .addHeader("Content-Type", contentType)
                .url(host + "/" + path)
                .put(requestBody);

        Request request = requestBuilder.build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new CotSdkException(response.code(), "Requested returned error code");
            }
            String location = response.header("Location");
            String result = null;
            if (location != null) {
                String[] pathParts = location.split("\\/");
                result = pathParts[pathParts.length - 1];
            }
            return result;
        } catch (Exception e) {
            throw new CotSdkException("Error in request", e);
        } finally {
            closeResponseBodyIfResponseAndBodyNotNull(response);
        }
    }


    /**
     * Execute a PUT request that will result in a new or changed ID.
     *
     * @param data the body to send
     * @param path the URL path (without leading '/')
     * @param contentType the Content-Type header
     * @return the ID from the Location header (for newly created objects), or null if there's no Location header.
     */
    public String doPutRequestWithIdResponse(String data, String path, String contentType) {
       return doPutRequestWithIdResponseInBytes(data.getBytes(Charset.forName("UTF-8")), path, contentType);
    }


    public void delete(String id, String api) {
        Request request = new Request.Builder()
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .url(host + "/" + removeTrailingSlash(api) + "/" + id)
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
                .url(host + "/" + removeTrailingSlash(api) + "?" + filter)
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
        String errorMessage = "Request failed.";
        String body = "";
        try {
            body = response.body().string();
            final JsonElement e = gson.fromJson(body, JsonElement.class);
            if (e instanceof JsonObject) {
                JsonObject o = (JsonObject) e;
                if (o.has("error")) {
                    errorMessage += " Platform provided details: '" + o.get("error") + "'";
                }
            } else if (e instanceof JsonPrimitive) {
                JsonPrimitive p = (JsonPrimitive) e;
                errorMessage += " Platform provided details: '" + p + "'";
            }
        } catch (JsonSyntaxException ex) {
            errorMessage += " " + body;
        } catch (NullPointerException ex) {
            errorMessage += " Response body was empty.";
        }

        return errorMessage;
    }

    private void closeResponseBodyIfResponseAndBodyNotNull(final Response response) {
        if (response != null && response.body() != null) {
            response.body().close();
        }
    }

    private String removeTrailingSlash(String path) {
        Objects.requireNonNull(path);

        if(path.endsWith("/")) {
            path = path.substring(0, path.length()-1);
        }

        return path;
    }
}
