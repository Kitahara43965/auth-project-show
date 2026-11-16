package com.example.myauthapp.web.token.error;

public interface TokenError {
    String getErrorCode();
    String getErrorMessage();

    default String format(Object... args) {
        return String.format(getErrorMessage(), args);
    }
}