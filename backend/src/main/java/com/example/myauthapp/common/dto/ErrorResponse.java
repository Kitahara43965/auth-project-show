package com.example.myauthapp.common.dto;

import java.util.List;

public class ErrorResponse {

    private final String errorCode;
    private final String errorMessage;
    private final List<ValidationDetail> validationDetails;

    public ErrorResponse(
            String errorCode,
            String errorMessage
    ) {
        this(errorCode, errorMessage, null);
    }

    public ErrorResponse(
            String errorCode,
            String errorMessage,
            List<ValidationDetail> validationDetails
    ) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.validationDetails = validationDetails;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<ValidationDetail> getValidationDetails() {
        return validationDetails;
    }
}