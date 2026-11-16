package com.example.myauthapp.user.error;
import com.example.myauthapp.web.user.error.UserError;

public enum UserDTOUserError implements UserError{

    NO_USERS_ARE_FOUND(
        "NO_USERS_ARE_FOUND",
        "UserDTOが見つかりません"
    ),
    
    NO_USER_IDS_ARE_FOUND(
        "NO_USER_IDS_ARE_FOUND",
        "UserIdが見つかりません"
    );

    private final String errorCode;
    private final String errorMessage;

    UserDTOUserError(
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