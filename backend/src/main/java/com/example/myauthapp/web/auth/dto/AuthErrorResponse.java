package com.example.myauthapp.web.auth.dto;

import java.util.List;
import com.example.myauthapp.web.auth.dto.AuthValidationDetail;

public class AuthErrorResponse {

    private final String errorCode;
    private final String errorMessage;
    private final List<AuthValidationDetail> authValidationDetails;

    public AuthErrorResponse(
        String errorCode,
        String errorMessage
    ) {
        this(errorCode, errorMessage, null);
    }

    public AuthErrorResponse(
        String errorCode,
        String errorMessage,
        List<AuthValidationDetail> authValidationDetails
    ) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.authValidationDetails = authValidationDetails;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<AuthValidationDetail> getAuthValidationDetails() {
        return authValidationDetails;
    }
}