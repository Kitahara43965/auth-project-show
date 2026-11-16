package com.example.myauthapp.user.error;
import com.example.myauthapp.web.user.error.UserError;

public enum MeUserError implements UserError{

    ME_USER_USER_NOT_FOUND(
        "ME_USER_USER_NOT_FOUND",
        "ユーザーが見つかりません"
    );

    private final String errorCode;
    private final String errorMessage;

    MeUserError(
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