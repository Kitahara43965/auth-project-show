package com.example.myauthapp.auth.token.error;
import org.springframework.http.HttpStatus;
import com.example.myauthapp.common.error.AppError;

public enum TokenHashError implements AppError{
    
    STRING_TOKEN_IS_NULL(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "STRING_TOKEN_IS_NULL",
        "Stringのトークンが存在しません"
    ),

    HASH_ALGORITHM_NOT_SUPPORTED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "HASH_ALGORITHM_NOT_SUPPORTED",
        "ハッシュアルゴリズムがサポートされていません"
    );

    TokenHashError(HttpStatus httpStatus,
            String errorCode,
            String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

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