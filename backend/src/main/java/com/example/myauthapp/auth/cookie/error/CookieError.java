package com.example.myauthapp.auth.cookie.error;
import org.springframework.http.HttpStatus;
import com.example.myauthapp.common.error.AppError;

public enum CookieError implements AppError{

    COOKIE_CONFIGURATION_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "COOKIE_CONFIGURATION_ERROR",
        "認証設定に問題があります"
    ),

    COOKIE_OPERATION_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "COOKIE_OPERATION_ERROR",
        "認証Cookieの処理中にエラーが発生しました"
    ),

    COOKIE_VALUE_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "COOKIE_VALUE_ERROR",
        "Cookieの値にエラーが発生しました"
    ),
    
    COOKIE_TYPE_IS_NULL(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "COOKIE_TYPE_IS_NULL",
        "cookieTypeがnullです"
    );

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    CookieError(HttpStatus httpStatus,
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