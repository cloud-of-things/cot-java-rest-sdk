package com.telekom.m2m.cot.restsdk;

public enum HttpStatus {

    NOT_FOUND(404);

    private final int code;

    HttpStatus(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
