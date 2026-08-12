package com.example.myauthapp.user.error;
import org.springframework.http.HttpStatus;
import com.example.myauthapp.common.error.AppError;

public enum UserError implements AppError{

    UNAUTHORIZED(
        HttpStatus.UNAUTHORIZED,
        "UNAUTHORIZED",
        "認証されていません"
    ),

    USER_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "USER_NOT_FOUND",
        "ユーザーが見つかりません"
    );

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    UserError(HttpStatus httpStatus,
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