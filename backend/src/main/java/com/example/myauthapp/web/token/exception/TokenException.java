package com.example.myauthapp.web.token.exception;

import java.util.Objects;

import com.example.myauthapp.web.token.error.TokenError;

public class TokenException extends RuntimeException {

    private final TokenError tokenError;

    public TokenException(TokenError tokenError, Object... args) {
        super(tokenError.format(args));
        this.tokenError = Objects.requireNonNull(
            tokenError,
            "tokenError must not be null"
        );
    }

    public TokenError getTokenError() {
        return tokenError;
    }
}