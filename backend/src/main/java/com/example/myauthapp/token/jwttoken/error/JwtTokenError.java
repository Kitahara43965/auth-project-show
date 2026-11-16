package com.example.myauthapp.token.jwttoken.error;

import com.example.myauthapp.web.token.error.TokenError;

public enum JwtTokenError implements TokenError{

    JWT_TOKEN_NO_USER(
        "JWT_TOKEN_NO_USER",
        "認証に失敗しました"
    ),

    JWT_TOKEN_NO_EMAIL(
        "JWT_TOKEN_NO_EMAIL",
        "認証に失敗しました"
    ),
    
    JWT_TOKEN_NO_TOKEN(
        "JWT_TOKEN_NO_TOKEN",
        "認証に失敗しました"
    );

    private final String errorCode;
    private final String errorMessage;

    JwtTokenError(
        String errorCode,
        String errorMessage
    ) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}