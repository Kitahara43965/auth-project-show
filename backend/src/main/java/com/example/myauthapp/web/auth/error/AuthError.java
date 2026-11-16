package com.example.myauthapp.web.auth.error;

public interface AuthError {
    String getErrorCode();
    String getErrorMessage();

    default String format(Object... args) {
        return String.format(getErrorMessage(), args);
    }
}