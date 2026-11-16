package com.example.myauthapp.web.user.exception;

import java.util.Objects;

import com.example.myauthapp.web.user.error.UserError;

public class UserException extends RuntimeException {

    private final UserError userError;

    public UserException(UserError userError, Object... args) {
        super(userError.format(args));
        this.userError = Objects.requireNonNull(
            userError,
            "userError must not be null"
        );
    }

    public UserError getUserError() {
        return userError;
    }
}