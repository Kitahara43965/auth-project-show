package com.example.myauthapp.auth.error;

import com.example.myauthapp.web.auth.error.AuthError;

public enum FieldAuthError implements AuthError{

    EMAIL_ALREADY_EXISTS(
        "EMAIL_ALREADY_EXISTS",
        "このメールアドレスは既に使われています"
    ),

    INVALID_CREDENTIALS(
        "INVALID_CREDENTIALS",
        "メールアドレスまたはパスワードが正しくありません"
    ),

    AUTH_NO_USER(
        "AUTH_NO_USER",
        "ユーザーが存在しません"
    ),

    AUTH_NO_EMAIL(
        "AUTH_NO_EMAIL",
        "emailが存在しません"
    ),

    DATA_INTEGRITY_VIOLATION(
        "DATA_INTEGRITY_VIOLATION",
        "データの整合性エラーが発生しました"
    );

    private final String errorCode;
    private final String errorMessage;

    FieldAuthError(
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