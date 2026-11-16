package com.example.myauthapp.token.refreshtoken.error;
import com.example.myauthapp.web.token.error.TokenError;

public enum RefreshTokenFamilyTokenError implements TokenError{

    NO_REFRESH_TOKEN_FAMILIES_ARE_FOUND(
        "NO_REFRESH_TOKEN_FAMILIES_ARE_FOUND",
        "Error: No refresh token families are found."
    ),
    
    FAILED_TO_SAVE_REFRESH_TOKEN_FAMILY(
        "FAILED_TO_SAVE_REFRESH_TOKEN_FAMILY",
        "Error: Failed to save refresh token family. insertedRowCount = %d"
    );

    private final String errorCode;
    private final String errorMessage;

    RefreshTokenFamilyTokenError(
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