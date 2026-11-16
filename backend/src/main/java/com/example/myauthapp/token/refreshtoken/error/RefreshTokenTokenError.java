package com.example.myauthapp.token.refreshtoken.error;
import com.example.myauthapp.web.token.error.TokenError;

public enum RefreshTokenTokenError implements TokenError{

    REFRESH_TOKEN_DOES_NOT_EXIST(
        "REFRESH_TOKEN_DOES_NOT_EXIST",
        "RefreshTokenが存在しません"
    ),

    STRING_REFRESH_TOKEN_DOES_NOT_EXIST(
        "STRING_REFRESH_TOKEN_DOES_NOT_EXIST",
        "StringRefreshTokenが存在しません"
    ),
    
    REFRESH_TOKEN_IS_EXPIRED(
        "REFRESH_TOKEN_IS_EXPIRED",
        "RefreshTokenが期限切れです"
    ),

    REFRESH_TOKEN_IS_REVOKED(
        "REFRESH_TOKEN_IS_REVOKED",
        "RefreshTokenが失効しています"
    ),

    REFRESH_TOKEN_REUSE_IS_DETECTED(
        "REFRESH_TOKEN_REUSE_IS_DETECTED",
        "セッションに問題が検出されました。再度ログインしてください"
    ),
    
    REFRESH_TOKEN_HAS_NO_USER(
        "REFRESH_TOKEN_HAS_NO_USER",
        "refresh token作成時にuserが見つかりませんでした"
    ),
    
    NO_REFRESH_TOKENS_ARE_FOUND(
        "NO_REFRESH_TOKENS_ARE_FOUND",
        "Error: No refresh tokens are found."
    ),
    
    FAILED_TO_SAVE_REFRESH_TOKEN(
        "FAILED_TO_SAVE_REFRESH_TOKEN",
        "Error: failed to save refresh token."
    );

    private final String errorCode;
    private final String errorMessage;

    RefreshTokenTokenError(
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