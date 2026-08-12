package com.example.myauthapp.common.error;

import org.springframework.http.HttpStatus;

public enum ValidationError implements AppError{
    
    VALIDATION_ERROR(
        HttpStatus.BAD_REQUEST,
        "VALIDATION_ERROR",
        "入力内容が不正です"
    );

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    ValidationError(HttpStatus httpStatus,
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