package com.example.myauthapp.web.user.error;

public interface UserError {
    String getErrorCode();
    String getErrorMessage();

    default String format(Object... args) {
        return String.format(getErrorMessage(), args);
    }
}