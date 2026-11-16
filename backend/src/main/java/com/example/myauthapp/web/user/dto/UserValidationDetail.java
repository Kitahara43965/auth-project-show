package com.example.myauthapp.web.user.dto;

public class UserValidationDetail {

    private final String field;
    private final String message;

    public UserValidationDetail(String field, String message) {
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