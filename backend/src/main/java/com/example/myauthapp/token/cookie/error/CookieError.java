package com.example.myauthapp.token.cookie.error;
import com.example.myauthapp.web.token.error.TokenError;

public enum CookieError implements TokenError{

    COOKIE_CONFIGURATION_ERROR(
        "COOKIE_CONFIGURATION_ERROR",
        "認証設定に問題があります"
    ),

    COOKIE_OPERATION_ERROR(
        "COOKIE_OPERATION_ERROR",
        "認証Cookieの処理中にエラーが発生しました"
    ),

    COOKIE_VALUE_ERROR(
        "COOKIE_VALUE_ERROR",
        "Cookieの値にエラーが発生しました"
    ),
    
    COOKIE_TYPE_IS_NULL(
        "COOKIE_TYPE_IS_NULL",
        "cookieTypeがnullです"
    );

    private final String errorCode;
    private final String errorMessage;

    CookieError(
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