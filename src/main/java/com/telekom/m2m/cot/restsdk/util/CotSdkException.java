package com.telekom.m2m.cot.restsdk.util;

/**
 * Cloud of Things SDK Exception wraps Exception in Runtime Exceptions.
 * <p/>
 * Created by breucking on 02.02.16.
 */
public class CotSdkException extends RuntimeException {

    private int httpStatusCode = 0;

    public CotSdkException(String string) {
        super(string);
    }

    public CotSdkException(String string, Throwable t) {
        super(string, t);
    }

    public CotSdkException(int httpStatusCode, String string) {
        super(string);
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatus() {
        return httpStatusCode;
    }
}
