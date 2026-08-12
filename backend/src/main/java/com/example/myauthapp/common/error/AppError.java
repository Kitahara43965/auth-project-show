package com.example.myauthapp.common.error;

import org.springframework.http.HttpStatus;

public interface AppError {
    HttpStatus getHttpStatus();
    String getErrorCode();
    String getErrorMessage();
}