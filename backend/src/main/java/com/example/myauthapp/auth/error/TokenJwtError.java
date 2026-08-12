package com.example.myauthapp.auth.error;
import org.springframework.http.HttpStatus;
import com.example.myauthapp.common.error.AppError;

public enum TokenJwtError implements AppError{

    TOKEN_JWT_NO_USER(
        HttpStatus.UNAUTHORIZED,
        "TOKEN_JWT_NO_USER",
        "認証に失敗しました"
    ),

    TOKEN_JWT_NO_EMAIL(
        HttpStatus.UNAUTHORIZED,
        "TOKEN_JWT_NO_EMAIL",
        "認証に失敗しました"
    ),
    
    TOKEN_JWT_NO_TOKEN(
        HttpStatus.UNAUTHORIZED,
        "TOKEN_JWT_NO_TOKEN",
        "認証に失敗しました"
    );

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    TokenJwtError(HttpStatus httpStatus,
            String errorCode,
            String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}