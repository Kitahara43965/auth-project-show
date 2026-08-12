package com.example.myauthapp.common.exception;

import com.example.myauthapp.common.error.AppError;

public abstract class AppException extends RuntimeException {

    private final AppError error;

    protected AppException(AppError error) {
        this.error = error;
    }

    public AppError getAppError() {
        return error;
    }
}

