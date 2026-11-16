package com.example.myauthapp.web.auth.exception;

import java.util.Objects;

import com.example.myauthapp.web.auth.error.AuthError;

public class AuthException extends RuntimeException {

    private final AuthError authError;

    public AuthException(AuthError authError, Object... args) {
        super(authError.format(args));
        this.authError = Objects.requireNonNull(
            authError,
            "authError must not be null"
        );
    }


    public AuthError getAuthError() {
        return authError;
    }
}