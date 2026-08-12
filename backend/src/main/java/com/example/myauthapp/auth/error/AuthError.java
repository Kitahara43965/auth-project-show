package com.example.myauthapp.auth.error;
import org.springframework.http.HttpStatus;
import com.example.myauthapp.common.error.AppError;

public enum AuthError implements AppError{

    EMAIL_ALREADY_EXISTS(
        HttpStatus.CONFLICT,
        "EMAIL_ALREADY_EXISTS",
        "このメールアドレスは既に使われています"
    ),

    INVALID_CREDENTIALS(
        HttpStatus.UNAUTHORIZED,
        "INVALID_CREDENTIALS",
        "メールアドレスまたはパスワードが正しくありません"
    ),
    
    AUTH_NO_USER(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "AUTH_NO_USER",
        "ユーザーが存在しません"
    ),

    AUTH_NO_EMAIL(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "AUTH_NO_EMAIL",
        "emailが存在しません"
    ),
    
    DATA_INTEGRITY_VIOLATION(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "DATA_INTEGRITY_VIOLATION",
        "データの整合性エラーが発生しました"
    );



    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    AuthError(HttpStatus httpStatus,
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