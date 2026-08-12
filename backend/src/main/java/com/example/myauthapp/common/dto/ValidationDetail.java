package com.example.myauthapp.common.dto;

public class ValidationDetail {

    private final String field;
    private final String message;

    public ValidationDetail(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}