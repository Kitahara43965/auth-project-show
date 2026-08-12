package com.example.myauthapp.auth.token.error;
import org.springframework.http.HttpStatus;
import com.example.myauthapp.common.error.AppError;

public enum RefreshTokenError implements AppError{

    REFRESH_TOKEN_DOES_NOT_EXIST(
        HttpStatus.UNAUTHORIZED,
        "REFRESH_TOKEN_DOES_NOT_EXIST",
        "RefreshTokenが存在しません"
    ),

    STRING_REFRESH_TOKEN_DOES_NOT_EXIST(
        HttpStatus.UNAUTHORIZED,
        "STRING_REFRESH_TOKEN_DOES_NOT_EXIST",
        "StringRefreshTokenが存在しません"
    ),
    
    REFRESH_TOKEN_IS_EXPIRED(
        HttpStatus.UNAUTHORIZED,
        "REFRESH_TOKEN_IS_EXPIRED",
        "RefreshTokenが期限切れです"
    ),

    REFRESH_TOKEN_IS_REVOKED(
        HttpStatus.UNAUTHORIZED,
        "REFRESH_TOKEN_IS_REVOKED",
        "RefreshTokenが失効しています"
    ),

    REUSE_DETECTED(
        HttpStatus.UNAUTHORIZED,
        "REUSE_DETECTED",
        "セッションに問題が検出されました。再度ログインしてください"
    ),
    
    REFRESH_TOKEN_NO_USER(
        HttpStatus.UNAUTHORIZED,
        "REFRESH_TOKEN_NO_USER",
        "refresh token作成時にuserが見つかりませんでした"
    );

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    RefreshTokenError(HttpStatus httpStatus,
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