package com.example.myauthapp.web.token.dto;

public class TokenValidationDetail {

    private final String field;
    private final String message;

    public TokenValidationDetail(String field, String message) {
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