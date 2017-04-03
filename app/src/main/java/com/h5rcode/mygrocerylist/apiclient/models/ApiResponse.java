package com.h5rcode.mygrocerylist.apiclient.models;

public class ApiResponse<T> {

    private final int _responseStatusCode;
    private final T _responseBody;

    public ApiResponse(int responseStatus, T responseBody) {
        _responseStatusCode = responseStatus;
        _responseBody = responseBody;
    }

    public int getResponseStatusCode() {
        return _responseStatusCode;
    }

    public T getResponseBody() {
        return _responseBody;
    }
}
